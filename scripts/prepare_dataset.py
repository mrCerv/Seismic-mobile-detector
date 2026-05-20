"""
Dataset preparation script for Seismic Mobile Detector.

This script:
  1. Scans data/raw/phyphox/ for CSV files (in earthquake/ and noise/ subdirs)
  2. Counts Phyphox samples per class
  3. Randomly samples the same number of events from STEAD HDF5 files
  4. Saves the balanced, preprocessed dataset to data/processed/
  5. Prints a statistics report

Usage:
    python scripts/prepare_dataset.py
    python scripts/prepare_dataset.py --phyphox-dir data/raw/phyphox --stead-dir data/raw/stead --out data/processed --seed 42

See docs/DATASET.md for the expected file layout.
"""

import argparse
import glob
import os
import sys
from pathlib import Path

import numpy as np

# Allow running from repo root without installing the package
sys.path.insert(0, str(Path(__file__).parent.parent / "ml" / "src"))

from data_loader import (
    PHYPHOX_EQ_SUBDIR,
    PHYPHOX_NOISE_SUBDIR,
    index_stead_files,
    load_phyphox_dataset,
    sample_stead_balanced,
)
from sklearn.model_selection import train_test_split


# ─────────────────────────────────────────────────────────────────────────────
# CLI
# ─────────────────────────────────────────────────────────────────────────────

def parse_args():
    parser = argparse.ArgumentParser(description="Prepare balanced STEAD + Phyphox dataset")
    parser.add_argument("--phyphox-dir", default="data/raw/phyphox",
                        help="Root dir with earthquake/ and noise/ subdirs (default: data/raw/phyphox)")
    parser.add_argument("--stead-dir", default="data/raw/stead",
                        help="Dir containing STEAD .hdf5 files (default: data/raw/stead)")
    parser.add_argument("--out", default="data/processed",
                        help="Output directory for processed dataset (default: data/processed)")
    parser.add_argument("--seed", type=int, default=42, help="Random seed (default: 42)")
    parser.add_argument("--val-ratio", type=float, default=0.15)
    parser.add_argument("--test-ratio", type=float, default=0.15)
    parser.add_argument("--no-balance", action="store_true",
                        help="Load all STEAD samples without undersampling (not recommended if Phyphox is small)")
    return parser.parse_args()


# ─────────────────────────────────────────────────────────────────────────────
# Main
# ─────────────────────────────────────────────────────────────────────────────

def main():
    args = parse_args()

    phyphox_dir = Path(args.phyphox_dir)
    stead_dir = Path(args.stead_dir)
    out_dir = Path(args.out)

    # ── Validate inputs ──────────────────────────────────────────────────────
    if not phyphox_dir.exists():
        print(f"[ERROR] Phyphox directory not found: {phyphox_dir}")
        print("  Create it and place CSV files as described in docs/DATASET.md")
        sys.exit(1)

    stead_files = sorted(glob.glob(str(stead_dir / "*.hdf5"))) + \
                  sorted(glob.glob(str(stead_dir / "*.h5")))
    if not stead_files:
        print(f"[ERROR] No STEAD .hdf5/.h5 files found in {stead_dir}")
        print("  Download STEAD and place files as described in docs/DATASET.md")
        sys.exit(1)

    out_dir.mkdir(parents=True, exist_ok=True)

    print("=" * 60)
    print("  Seismic Mobile Detector — Dataset Preparation")
    print("=" * 60)
    print(f"  Phyphox dir : {phyphox_dir}")
    print(f"  STEAD dir   : {stead_dir}  ({len(stead_files)} files)")
    print(f"  Output dir  : {out_dir}")
    print(f"  Seed        : {args.seed}")
    print()

    # ── Load Phyphox ─────────────────────────────────────────────────────────
    print("── Phyphox ─────────────────────────────────────────────────")
    phyphox_samples, n_phyphox_eq, n_phyphox_noise = load_phyphox_dataset(str(phyphox_dir))

    if not phyphox_samples:
        print("\n[ERROR] No Phyphox samples found.")
        print(f"  Place CSV files in:")
        print(f"    {phyphox_dir / PHYPHOX_EQ_SUBDIR}/  (earthquake recordings)")
        print(f"    {phyphox_dir / PHYPHOX_NOISE_SUBDIR}/  (ambient noise recordings)")
        print("  See docs/DATASET.md for the expected CSV format.")
        sys.exit(1)

    n_phyphox = len(phyphox_samples)
    print(f"\n  Phyphox: {n_phyphox} windows  ({n_phyphox_eq} earthquake, {n_phyphox_noise} noise)")

    # ── Index STEAD ───────────────────────────────────────────────────────────
    print("\n── STEAD indexing ──────────────────────────────────────────")
    stead_index = index_stead_files(stead_files)
    n_stead_eq = len(stead_index["earthquake"])
    n_stead_noise = len(stead_index["noise"])
    print(f"  STEAD index: {n_stead_eq + n_stead_noise} events "
          f"({n_stead_eq} earthquake, {n_stead_noise} noise) across {len(stead_files)} files")

    # ── Sample STEAD to match Phyphox ─────────────────────────────────────────
    if args.no_balance:
        print("\n── Loading ALL STEAD samples (--no-balance) ────────────────")
        from data_loader import _load_stead_waveform
        stead_samples = []
        for filepath, group_path, key in stead_index["earthquake"] + stead_index["noise"]:
            try:
                data, label = _load_stead_waveform(filepath, group_path, key)
                stead_samples.append((data, label))
            except Exception:
                continue
    else:
        # Match Phyphox class distribution
        target_eq = n_phyphox_eq if n_phyphox_eq > 0 else n_phyphox // 2
        target_noise = n_phyphox_noise if n_phyphox_noise > 0 else n_phyphox // 2

        print(f"\n── Sampling STEAD to match Phyphox: {target_eq} eq + {target_noise} noise ──")
        stead_samples = sample_stead_balanced(
            stead_index, target_eq, target_noise, seed=args.seed
        )

    n_stead = len(stead_samples)
    print(f"  STEAD sampled: {n_stead}")

    # ── Combine and split ────────────────────────────────────────────────────
    all_samples = stead_samples + phyphox_samples
    n_total = len(all_samples)

    labels = [int(s[1]["is_earthquake"][0]) for s in all_samples]
    n_total_eq = sum(labels)
    n_total_noise = n_total - n_total_eq

    print(f"\n── Combined: {n_total} samples  ({n_total_eq} eq, {n_total_noise} noise) ──")

    train_val, test, lbl_tv, _ = train_test_split(
        all_samples, labels,
        test_size=args.test_ratio,
        stratify=labels,
        random_state=args.seed,
    )
    val_adj = args.val_ratio / (1.0 - args.test_ratio)
    train, val = train_test_split(
        train_val,
        test_size=val_adj,
        stratify=lbl_tv,
        random_state=args.seed,
    )

    print(f"  Split: train={len(train)}, val={len(val)}, test={len(test)}")

    # ── Save to .npz files ───────────────────────────────────────────────────
    print("\n── Saving processed dataset ────────────────────────────────")

    def save_split(samples, name):
        X = np.array([s[0] for s in samples], dtype=np.float32)
        y_is_eq = np.array([s[1]["is_earthquake"] for s in samples], dtype=np.float32).squeeze()
        y_intensity = np.array([s[1]["intensity"] for s in samples], dtype=np.int32)
        y_pga = np.array([s[1]["pga"] for s in samples], dtype=np.float32).squeeze()
        y_freq = np.array([s[1]["dominant_freq"] for s in samples], dtype=np.float32).squeeze()
        y_dur = np.array([s[1]["duration"] for s in samples], dtype=np.float32).squeeze()

        out_path = out_dir / f"{name}.npz"
        np.savez_compressed(
            out_path,
            X=X,
            is_earthquake=y_is_eq,
            intensity=y_intensity,
            pga=y_pga,
            dominant_freq=y_freq,
            duration=y_dur,
        )
        size_mb = out_path.stat().st_size / 1e6
        print(f"  {out_path}  ({len(samples)} samples, {size_mb:.1f} MB)")

    save_split(train, "train")
    save_split(val, "val")
    save_split(test, "test")

    # ── Report ───────────────────────────────────────────────────────────────
    print("\n── Statistics ──────────────────────────────────────────────")
    print(f"  {'Split':<10} {'Total':>7} {'Eq':>7} {'Noise':>7} {'Eq%':>7}")
    print("  " + "-" * 38)
    for split_name, split in [("train", train), ("val", val), ("test", test)]:
        n = len(split)
        eq = sum(int(s[1]["is_earthquake"][0]) for s in split)
        print(f"  {split_name:<10} {n:>7} {eq:>7} {n - eq:>7} {eq / n * 100:>6.1f}%")
    print()

    print("✓ Dataset ready. To train the model, run:")
    print("    python ml/src/train.py")
    print()
    print("  The train script will automatically load from data/processed/")
    print("  if the .npz files exist, skipping the raw data loading.")


if __name__ == "__main__":
    main()
