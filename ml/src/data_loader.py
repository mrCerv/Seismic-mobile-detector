import math
import os
from pathlib import Path
from typing import Dict, List, Optional, Tuple

import h5py
import numpy as np
import pandas as pd
import tensorflow as tf
from sklearn.model_selection import train_test_split

from augmentation import apply_augmentation
from preprocessing import preprocess_hybrid_data

# ─────────────────────────────────────────────────────────────────────────────
# Constants
# ─────────────────────────────────────────────────────────────────────────────

# PGA thresholds (m/s²) used to bin STEAD samples into 0-5 intensity classes
_STEAD_INTENSITY_BINS = [0.0, 0.001, 0.01, 0.1, 0.3, 1.0]

# Phyphox CSV subdirectory convention:
#   data/raw/phyphox/earthquake/  -> is_earthquake=1
#   data/raw/phyphox/noise/       -> is_earthquake=0
PHYPHOX_EQ_SUBDIR = "earthquake"
PHYPHOX_NOISE_SUBDIR = "noise"

_PHYPHOX_ACCEL_COLS = [
    "linear_acceleration_x",
    "linear_acceleration_y",
    "linear_acceleration_z",
]
_PHYPHOX_GYRO_COLS = ["gyroscope_x", "gyroscope_y", "gyroscope_z"]
_PHYPHOX_ALL_COLS = _PHYPHOX_ACCEL_COLS + _PHYPHOX_GYRO_COLS


# ─────────────────────────────────────────────────────────────────────────────
# STEAD helpers
# ─────────────────────────────────────────────────────────────────────────────

def _pga_to_intensity(pga: float) -> int:
    """Map PGA (m/s²) to a 0-5 intensity class index."""
    for i, boundary in enumerate(reversed(_STEAD_INTENSITY_BINS)):
        if pga >= boundary:
            return 5 - i
    return 0


def _load_stead_waveform(filepath: str, group_path: str, key: str) -> Tuple[np.ndarray, dict]:
    """Load a single waveform+label from an open STEAD HDF5 file."""
    with h5py.File(filepath, "r") as f:
        is_eq = 1 if "earthquake" in group_path else 0
        ds = f[group_path][key]

        waveform = ds["waveforms"][:] if "waveforms" in ds else ds[:]
        if waveform.ndim == 1:
            waveform = waveform.reshape(-1, 1)
        waveform = waveform[:1000, :3]
        if waveform.shape[0] < 1000:
            waveform = np.pad(waveform, ((0, 1000 - waveform.shape[0]), (0, 0)))

        data = np.zeros((1000, 6), dtype=np.float32)
        data[:, :3] = waveform.astype(np.float32)

        attrs = dict(ds.attrs) if hasattr(ds, "attrs") else {}
        pga_val = float(attrs.get("p_peak_ground_velocity", attrs.get("PGA", 0.0)))
        freq_val = float(attrs.get("dominant_frequency", attrs.get("snr_db", 1.0)))
        dur_val = float(attrs.get("coda_end_sample", 100)) / 100.0

        label = {
            "is_earthquake": np.array([is_eq], dtype=np.float32),
            "intensity": _pga_to_intensity(pga_val),
            "pga": np.array([pga_val], dtype=np.float32),
            "dominant_freq": np.array([freq_val], dtype=np.float32),
            "duration": np.array([dur_val], dtype=np.float32),
        }

    data = preprocess_hybrid_data(data)
    return data, label


def index_stead_files(stead_paths: List[str]) -> Dict[str, List[Tuple[str, str, str]]]:
    """
    Build a fast-access index of STEAD samples without loading waveforms.

    Returns a dict with keys 'earthquake' and 'noise', each containing a list
    of (filepath, group_path, key) tuples.
    """
    index: Dict[str, List[Tuple[str, str, str]]] = {"earthquake": [], "noise": []}

    for path in stead_paths:
        try:
            with h5py.File(path, "r") as f:
                if "earthquake/local" in f:
                    for key in f["earthquake/local"].keys():
                        index["earthquake"].append((path, "earthquake/local", key))
                if "non_earthquake/noise" in f:
                    for key in f["non_earthquake/noise"].keys():
                        index["noise"].append((path, "non_earthquake/noise", key))
        except Exception as e:
            print(f"  [WARN] Could not index {path}: {e}")

    return index


def sample_stead_balanced(
    stead_index: Dict[str, List[Tuple[str, str, str]]],
    n_earthquake: int,
    n_noise: int,
    seed: int = 42,
) -> List[Tuple[np.ndarray, dict]]:
    """
    Randomly sample n_earthquake earthquake events and n_noise noise events
    from STEAD.  This keeps class balance in line with the Phyphox dataset.

    Args:
        stead_index: output of index_stead_files()
        n_earthquake: number of earthquake samples to draw
        n_noise: number of noise samples to draw
        seed: random seed for reproducibility

    Returns:
        list of (data [1000,6], label) tuples, preprocessed
    """
    rng = np.random.default_rng(seed)

    eq_pool = stead_index["earthquake"]
    noise_pool = stead_index["noise"]

    n_eq = min(n_earthquake, len(eq_pool))
    n_noise_actual = min(n_noise, len(noise_pool))

    if n_eq < n_earthquake:
        print(f"  [WARN] Requested {n_earthquake} earthquake samples but STEAD only has {n_eq}")
    if n_noise_actual < n_noise:
        print(f"  [WARN] Requested {n_noise} noise samples but STEAD only has {n_noise_actual}")

    eq_chosen = [eq_pool[i] for i in rng.choice(len(eq_pool), n_eq, replace=False)]
    noise_chosen = [noise_pool[i] for i in rng.choice(len(noise_pool), n_noise_actual, replace=False)]

    samples = []
    for filepath, group_path, key in eq_chosen + noise_chosen:
        try:
            data, label = _load_stead_waveform(filepath, group_path, key)
            samples.append((data, label))
        except Exception as e:
            print(f"  [WARN] Skipping {key}: {e}")

    return samples


# ─────────────────────────────────────────────────────────────────────────────
# Phyphox helpers
# ─────────────────────────────────────────────────────────────────────────────

def _parse_phyphox_csv(filepath: str, is_earthquake: int) -> List[Tuple[np.ndarray, dict]]:
    """Parse one Phyphox CSV file into a list of 1000-sample windows."""
    df = pd.read_csv(filepath)
    df.columns = [c.strip().lower().replace(" ", "_") for c in df.columns]

    col_map = {}
    for target in _PHYPHOX_ALL_COLS:
        matches = [c for c in df.columns if target in c or c in target]
        if matches:
            col_map[target] = matches[0]

    missing = [c for c in _PHYPHOX_ALL_COLS if c not in col_map]
    if missing:
        raise ValueError(
            f"{filepath}: missing columns {missing}. Found: {list(df.columns)}\n"
            "Expected: time, linear_acceleration_x/y/z, gyroscope_x/y/z"
        )

    raw = df[[col_map[c] for c in _PHYPHOX_ALL_COLS]].to_numpy(dtype=np.float32)

    n_windows = len(raw) // 1000
    samples = []
    for i in range(n_windows):
        window = preprocess_hybrid_data(raw[i * 1000 : (i + 1) * 1000])
        pga_val = float(np.max(np.abs(window[:, :3]))) if is_earthquake else 0.0
        label = {
            "is_earthquake": np.array([is_earthquake], dtype=np.float32),
            "intensity": _pga_to_intensity(pga_val) if is_earthquake else 0,
            "pga": np.array([pga_val], dtype=np.float32),
            "dominant_freq": np.array([0.0], dtype=np.float32),
            "duration": np.array([10.0 if is_earthquake else 0.0], dtype=np.float32),
        }
        samples.append((window, label))
    return samples


def load_phyphox_dataset(phyphox_root: str) -> Tuple[List[Tuple[np.ndarray, dict]], int, int]:
    """
    Load all Phyphox CSV files from the structured directory layout:

        phyphox_root/
            earthquake/   <- CSV files of seismic events recorded by phone
            noise/        <- CSV files of ambient noise / non-seismic recordings

    Returns:
        (samples, n_earthquake, n_noise)
    """
    root = Path(phyphox_root)
    all_samples: List[Tuple[np.ndarray, dict]] = []
    n_eq = 0
    n_noise = 0

    eq_dir = root / PHYPHOX_EQ_SUBDIR
    if eq_dir.exists():
        for csv_file in sorted(eq_dir.glob("*.csv")):
            try:
                s = _parse_phyphox_csv(str(csv_file), is_earthquake=1)
                all_samples.extend(s)
                n_eq += len(s)
                print(f"  [Phyphox EQ]    {csv_file.name}: {len(s)} windows")
            except Exception as e:
                print(f"  [WARN] {csv_file.name}: {e}")
    else:
        print(f"  [WARN] {eq_dir} not found — no earthquake Phyphox samples")

    noise_dir = root / PHYPHOX_NOISE_SUBDIR
    if noise_dir.exists():
        for csv_file in sorted(noise_dir.glob("*.csv")):
            try:
                s = _parse_phyphox_csv(str(csv_file), is_earthquake=0)
                all_samples.extend(s)
                n_noise += len(s)
                print(f"  [Phyphox NOISE] {csv_file.name}: {len(s)} windows")
            except Exception as e:
                print(f"  [WARN] {csv_file.name}: {e}")
    else:
        print(f"  [WARN] {noise_dir} not found — no noise Phyphox samples")

    return all_samples, n_eq, n_noise


# Legacy single-file loader kept for backward compatibility
def load_phyphox_csv(filepath: str, window_size: int = 1000) -> List[Tuple[np.ndarray, dict]]:
    """Load one Phyphox CSV with is_earthquake=0 label (legacy interface)."""
    return _parse_phyphox_csv(filepath, is_earthquake=0)


# ─────────────────────────────────────────────────────────────────────────────
# Batch generator
# ─────────────────────────────────────────────────────────────────────────────

class SeismicDataGenerator(tf.keras.utils.Sequence):
    """Keras batch generator for (data [1000,6], label) sample lists."""

    def __init__(
        self,
        samples: List[Tuple[np.ndarray, dict]],
        batch_size: int = 32,
        augment: bool = False,
        n_intensity_classes: int = 6,
        shuffle: bool = True,
    ) -> None:
        self.samples = samples
        self.batch_size = batch_size
        self.augment = augment
        self.n_intensity_classes = n_intensity_classes
        self.shuffle = shuffle
        self.indices = np.arange(len(samples))
        if shuffle:
            np.random.shuffle(self.indices)

    def __len__(self) -> int:
        return math.ceil(len(self.samples) / self.batch_size)

    def __getitem__(self, idx: int):
        batch_idx = self.indices[idx * self.batch_size : (idx + 1) * self.batch_size]
        batch_x: List[np.ndarray] = []
        batch_y: Dict[str, List[np.ndarray]] = {
            "is_earthquake": [],
            "intensity": [],
            "pga": [],
            "dominant_freq": [],
            "duration": [],
        }

        for i in batch_idx:
            data, label = self.samples[i]
            if self.augment:
                data, label = apply_augmentation(data, label)

            batch_x.append(data)
            batch_y["is_earthquake"].append(label["is_earthquake"])
            oh = np.zeros(self.n_intensity_classes, dtype=np.float32)
            oh[int(label["intensity"])] = 1.0
            batch_y["intensity"].append(oh)
            batch_y["pga"].append(label["pga"])
            batch_y["dominant_freq"].append(label["dominant_freq"])
            batch_y["duration"].append(label["duration"])

        return np.array(batch_x, dtype=np.float32), {
            k: np.array(v, dtype=np.float32) for k, v in batch_y.items()
        }

    def on_epoch_end(self) -> None:
        if self.shuffle:
            np.random.shuffle(self.indices)


# ─────────────────────────────────────────────────────────────────────────────
# Train / val / test split with STEAD balancing
# ─────────────────────────────────────────────────────────────────────────────

def create_train_val_test_split(
    stead_paths: List[str],
    phyphox_root: str,
    val_ratio: float = 0.15,
    test_ratio: float = 0.15,
    batch_size: int = 32,
    balance_stead_to_phyphox: bool = True,
    seed: int = 42,
) -> Tuple[SeismicDataGenerator, SeismicDataGenerator, SeismicDataGenerator]:
    """
    Build stratified train/val/test generators from STEAD + Phyphox.

    When balance_stead_to_phyphox=True (default), STEAD is randomly
    undersampled so that it contributes exactly as many samples as Phyphox
    (matching per-class counts).  This prevents STEAD from dominating
    training and ensures the model generalises to smartphone sensor data.

    Args:
        stead_paths: list of STEAD .hdf5 file paths
        phyphox_root: root directory containing earthquake/ and noise/ subdirs
        val_ratio: fraction of total data for validation
        test_ratio: fraction of total data for test
        batch_size: batch size for all generators
        balance_stead_to_phyphox: if True, undersample STEAD to match Phyphox size
        seed: random seed for reproducibility

    Returns:
        (train_gen, val_gen, test_gen)
    """
    print("── Loading Phyphox dataset ──────────────────────────────────")
    phyphox_samples, n_phyphox_eq, n_phyphox_noise = load_phyphox_dataset(phyphox_root)
    n_phyphox = len(phyphox_samples)
    print(f"  Phyphox total: {n_phyphox} ({n_phyphox_eq} eq + {n_phyphox_noise} noise)")

    print("── Indexing STEAD files ─────────────────────────────────────")
    stead_index = index_stead_files(stead_paths)
    n_stead_eq = len(stead_index["earthquake"])
    n_stead_noise = len(stead_index["noise"])
    print(f"  STEAD index:  {n_stead_eq + n_stead_noise} ({n_stead_eq} eq + {n_stead_noise} noise)")

    if balance_stead_to_phyphox and n_phyphox > 0:
        # Sample from STEAD to match the Phyphox class distribution
        target_eq = n_phyphox_eq if n_phyphox_eq > 0 else n_phyphox // 2
        target_noise = n_phyphox_noise if n_phyphox_noise > 0 else n_phyphox // 2
        print(
            f"── Sampling STEAD (balanced): {target_eq} eq + {target_noise} noise ──────"
        )
        stead_samples = sample_stead_balanced(stead_index, target_eq, target_noise, seed=seed)
    else:
        # Load all STEAD samples (may be very large)
        print("── Loading ALL STEAD samples (no balancing) ────────────────")
        stead_samples = []
        for filepath, group_path, key in (
            stead_index["earthquake"] + stead_index["noise"]
        ):
            try:
                data, label = _load_stead_waveform(filepath, group_path, key)
                stead_samples.append((data, label))
            except Exception:
                continue

    all_samples = stead_samples + phyphox_samples
    if not all_samples:
        raise ValueError(
            "No samples loaded. Check that:\n"
            "  • STEAD .hdf5 files are in data/raw/stead/\n"
            "  • Phyphox CSVs are in data/raw/phyphox/earthquake/ and data/raw/phyphox/noise/\n"
            "See docs/DATASET.md for details."
        )

    n_stead = len(stead_samples)
    print(
        f"── Combined dataset: {len(all_samples)} samples "
        f"(STEAD {n_stead} + Phyphox {n_phyphox}) ──"
    )

    labels_for_strat = [int(s[1]["is_earthquake"][0]) for s in all_samples]

    train_val, test, lbl_tv, _ = train_test_split(
        all_samples,
        labels_for_strat,
        test_size=test_ratio,
        stratify=labels_for_strat,
        random_state=seed,
    )
    val_size_adj = val_ratio / (1.0 - test_ratio)
    train, val = train_test_split(
        train_val,
        test_size=val_size_adj,
        stratify=lbl_tv,
        random_state=seed,
    )

    print(f"  Train: {len(train)}  Val: {len(val)}  Test: {len(test)}")

    train_gen = SeismicDataGenerator(train, batch_size=batch_size, augment=True, shuffle=True)
    val_gen = SeismicDataGenerator(val, batch_size=batch_size, augment=False, shuffle=False)
    test_gen = SeismicDataGenerator(test, batch_size=batch_size, augment=False, shuffle=False)

    return train_gen, val_gen, test_gen
