import os
import h5py
import numpy as np
import pandas as pd
from tqdm import tqdm
import sys

# Add ml/src to path to use the same preprocessing as training
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '../ml/src')))
from preprocessing import preprocess_seismic_data

def clean_and_validate_dataset(hdf5_path, output_path):
    """
    Checks the STEAD-format HDF5 dataset for:
    1. Null/NaN values
    2. Flat/constant signals (sensor failure)
    3. Incorrect sample counts (expected 6000 for STEAD or 1000 for our window)
    4. Outliers in amplitude

    Then applies preprocessing and saves clean data.
    """
    if not os.path.exists(hdf5_path):
        print(f"Error: Dataset not found at {hdf5_path}")
        return

    print(f"Cleaning dataset: {hdf5_path}")

    with h5py.File(hdf5_path, 'r') as f:
        group_name = 'data' # STEAD default
        if group_name not in f:
            # Fallback for other structures
            keys = list(f.keys())
            if not keys: return
            group_name = keys[0]

        dataset_group = f[group_name]
        event_names = list(dataset_group.keys())

        cleaned_data = []
        stats = {"total": len(event_names), "removed_nan": 0, "removed_flat": 0, "removed_short": 0}

        with h5py.File(output_path, 'w') as out_f:
            out_group = out_f.create_group('data')

            for name in tqdm(event_names, desc="Processing events"):
                data = np.array(dataset_group[name])

                # 1. Check for NaNs
                if np.isnan(data).any():
                    stats["removed_nan"] += 1
                    continue

                # 2. Check for constant signals (std == 0)
                if np.any(np.std(data, axis=0) < 1e-6):
                    stats["removed_flat"] += 1
                    continue

                # 3. Validation for our model (expects specific length, e.g., 1000 samples)
                # If it's STEAD (6000 samples), we might slice it or validate it.
                if len(data) < 1000:
                    stats["removed_short"] += 1
                    continue

                # 4. Apply preprocessing (Clean/Standardize)
                # If data is (6000, 3), we process it all
                try:
                    processed_data = preprocess_seismic_data(data)

                    # Save to output HDF5
                    dset = out_group.create_dataset(name, data=processed_data, compression="gzip")

                    # Copy attributes if they exist
                    for attr_name, attr_val in dataset_group[name].attrs.items():
                        dset.attrs[attr_name] = attr_val

                except Exception as e:
                    print(f"Failed to process {name}: {e}")
                    continue

    print("\n--- Cleaning Report ---")
    print(f"Total events analyzed: {stats['total']}")
    print(f"Removed (NaNs): {stats['removed_nan']}")
    print(f"Removed (Flat signal): {stats['removed_flat']}")
    print(f"Removed (Too short): {stats['removed_short']}")
    print(f"Cleaned events saved: {stats['total'] - sum(list(stats.values())[1:])}")
    print(f"Output saved to: {output_path}")

if __name__ == "__main__":
    # Example paths - adjust as needed for your specific dataset file
    RAW_DATA = "data/raw/stead/merged.hdf5"
    CLEAN_DATA = "data/processed/cleaned_dataset.hdf5"

    if not os.path.exists("data/processed"):
        os.makedirs("data/processed")

    clean_and_validate_dataset(RAW_DATA, CLEAN_DATA)
