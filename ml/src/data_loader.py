import math
import os
from pathlib import Path
from typing import List, Optional, Tuple

import h5py
import numpy as np
import pandas as pd
import tensorflow as tf
from sklearn.model_selection import train_test_split

from augmentation import apply_augmentation
from preprocessing import preprocess_hybrid_data

# ─────────────────────────────────────────────────────────────────────────────
# STEAD helpers
# ─────────────────────────────────────────────────────────────────────────────

# STEAD intensity labels map to approximate MMI bins 0-5
_STEAD_INTENSITY_BINS = [0.0, 0.001, 0.01, 0.1, 0.3, 1.0]  # PGA m/s² boundaries


def _pga_to_intensity(pga: float) -> int:
    """Map PGA (m/s²) to a 0-5 intensity class index."""
    for i, boundary in enumerate(reversed(_STEAD_INTENSITY_BINS)):
        if pga >= boundary:
            return 5 - i
    return 0


def load_stead_sample(filepath: str, idx: int) -> Tuple[np.ndarray, dict]:
    """
    Load one sample from a STEAD HDF5 file.

    STEAD stores 3-component seismograph data (Z, N, E).  We map those to the
    first three channels (linear-acceleration proxies) and fill channels 3-5
    (gyroscope) with zeros so the array matches the model's expected [1000, 6]
    shape.

    Groups checked in order: 'earthquake/local', 'non_earthquake/noise'.
    """
    with h5py.File(filepath, "r") as f:
        # Determine group and whether it is an earthquake
        if "earthquake/local" in f:
            group = f["earthquake/local"]
            is_eq = 1
        elif "non_earthquake/noise" in f:
            group = f["non_earthquake/noise"]
            is_eq = 0
        else:
            raise KeyError(f"No recognised group in {filepath}")

        keys = list(group.keys())
        key = keys[idx % len(keys)]
        ds = group[key]

        # waveforms: shape (6000, 3) at 100 Hz — trim / pad to 1000 samples
        waveform = ds["waveforms"][:] if "waveforms" in ds else ds[:]
        if waveform.ndim == 1:
            waveform = waveform.reshape(-1, 1)
        waveform = waveform[:1000, :3]  # keep first 1000 samples, first 3 channels
        if waveform.shape[0] < 1000:
            waveform = np.pad(waveform, ((0, 1000 - waveform.shape[0]), (0, 0)))

        # Build 6-channel array; gyro channels stay zero
        data = np.zeros((1000, 6), dtype=np.float32)
        data[:, :3] = waveform

        # Labels — use metadata when available, fall back to defaults
        attrs = dict(ds.attrs) if hasattr(ds, "attrs") else {}
        pga_val = float(attrs.get("p_peak_ground_velocity", attrs.get("PGA", 0.0)))
        dominant_freq_val = float(attrs.get("snr_db", attrs.get("dominant_frequency", 1.0)))
        duration_val = float(attrs.get("coda_end_sample", 100)) / 100.0  # convert samples→s

        intensity_cls = _pga_to_intensity(pga_val)

        label = {
            "is_earthquake": np.array([is_eq], dtype=np.float32),
            "intensity": intensity_cls,
            "pga": np.array([pga_val], dtype=np.float32),
            "dominant_freq": np.array([dominant_freq_val], dtype=np.float32),
            "duration": np.array([duration_val], dtype=np.float32),
        }

    data = preprocess_hybrid_data(data)
    return data, label


# ─────────────────────────────────────────────────────────────────────────────
# Phyphox helpers
# ─────────────────────────────────────────────────────────────────────────────

# Expected CSV columns exported by Phyphox
_PHYPHOX_ACCEL_COLS = [
    "linear_acceleration_x",
    "linear_acceleration_y",
    "linear_acceleration_z",
]
_PHYPHOX_GYRO_COLS = ["gyroscope_x", "gyroscope_y", "gyroscope_z"]
_PHYPHOX_ALL_COLS = _PHYPHOX_ACCEL_COLS + _PHYPHOX_GYRO_COLS


def load_phyphox_csv(filepath: str, window_size: int = 1000) -> List[Tuple[np.ndarray, dict]]:
    """
    Read a Phyphox CSV export and return a list of (data, label) windows.

    Phyphox exports contain: time, linear_acceleration_{x,y,z}, gyroscope_{x,y,z}.
    Windows are non-overlapping.  The label is a stub (is_earthquake=0 by
    default; real labels must be assigned externally before training).
    """
    df = pd.read_csv(filepath)
    df.columns = [c.strip().lower().replace(" ", "_") for c in df.columns]

    # Flexible column matching (Phyphox may use slightly different names)
    col_map = {}
    for target in _PHYPHOX_ALL_COLS:
        matches = [c for c in df.columns if target in c or c in target]
        if matches:
            col_map[target] = matches[0]

    missing = [c for c in _PHYPHOX_ALL_COLS if c not in col_map]
    if missing:
        raise ValueError(f"Phyphox CSV missing columns: {missing}. Found: {list(df.columns)}")

    raw = df[[col_map[c] for c in _PHYPHOX_ALL_COLS]].to_numpy(dtype=np.float32)

    n_windows = len(raw) // window_size
    samples = []
    for i in range(n_windows):
        window = raw[i * window_size : (i + 1) * window_size, :]
        window = preprocess_hybrid_data(window)
        label = {
            "is_earthquake": np.array([0], dtype=np.float32),
            "intensity": 0,
            "pga": np.array([0.0], dtype=np.float32),
            "dominant_freq": np.array([0.0], dtype=np.float32),
            "duration": np.array([0.0], dtype=np.float32),
        }
        samples.append((window, label))
    return samples


# ─────────────────────────────────────────────────────────────────────────────
# Batch generator
# ─────────────────────────────────────────────────────────────────────────────

class SeismicDataGenerator(tf.keras.utils.Sequence):
    """
    Keras batch generator that mixes STEAD and Phyphox samples.

    Each epoch shuffles the sample list.  During training, augmentation is
    applied with apply_augmentation().
    """

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
        batch_x, batch_y = [], {
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
            # One-hot encode intensity
            intensity_oh = np.zeros(self.n_intensity_classes, dtype=np.float32)
            intensity_oh[int(label["intensity"])] = 1.0
            batch_y["intensity"].append(intensity_oh)
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
# Train / val / test split
# ─────────────────────────────────────────────────────────────────────────────

def _collect_stead_samples(
    stead_paths: List[str], max_per_file: Optional[int] = None
) -> List[Tuple[np.ndarray, dict]]:
    samples = []
    for path in stead_paths:
        with h5py.File(path, "r") as f:
            groups = []
            if "earthquake/local" in f:
                groups.append(("earthquake/local", 1))
            if "non_earthquake/noise" in f:
                groups.append(("non_earthquake/noise", 0))
            for group_path, _ in groups:
                keys = list(f[group_path].keys())
                if max_per_file:
                    keys = keys[:max_per_file]
                for k_idx in range(len(keys)):
                    try:
                        data, label = load_stead_sample(path, k_idx)
                        samples.append((data, label))
                    except Exception:
                        continue
    return samples


def create_train_val_test_split(
    stead_paths: List[str],
    phyphox_paths: List[str],
    val_ratio: float = 0.15,
    test_ratio: float = 0.15,
    batch_size: int = 32,
    max_stead_per_file: Optional[int] = None,
) -> Tuple[SeismicDataGenerator, SeismicDataGenerator, SeismicDataGenerator]:
    """
    Stratified split across STEAD + Phyphox samples.

    Stratification is performed on is_earthquake to keep class balance.
    Returns (train_gen, val_gen, test_gen).
    """
    stead_samples = _collect_stead_samples(stead_paths, max_stead_per_file)

    phyphox_samples: List[Tuple[np.ndarray, dict]] = []
    for path in phyphox_paths:
        phyphox_samples.extend(load_phyphox_csv(path))

    all_samples = stead_samples + phyphox_samples
    if not all_samples:
        raise ValueError("No samples loaded — check stead_paths and phyphox_paths.")

    labels_for_strat = [int(s[1]["is_earthquake"][0]) for s in all_samples]

    train_val, test, lbl_tv, _ = train_test_split(
        all_samples,
        labels_for_strat,
        test_size=test_ratio,
        stratify=labels_for_strat,
        random_state=42,
    )
    val_size_adjusted = val_ratio / (1.0 - test_ratio)
    train, val = train_test_split(
        train_val,
        test_size=val_size_adjusted,
        stratify=lbl_tv,
        random_state=42,
    )

    train_gen = SeismicDataGenerator(train, batch_size=batch_size, augment=True, shuffle=True)
    val_gen = SeismicDataGenerator(val, batch_size=batch_size, augment=False, shuffle=False)
    test_gen = SeismicDataGenerator(test, batch_size=batch_size, augment=False, shuffle=False)

    return train_gen, val_gen, test_gen
