# Dataset Guide

This document describes the datasets used to train the Seismic Mobile Detector CNN-1D model, their structure, and how to collect and prepare custom smartphone data.

---

## Table of Contents

1. [STEAD Dataset](#stead-dataset)
2. [Phyphox Custom Dataset](#phyphox-custom-dataset)
3. [Data Pipeline](#data-pipeline)
4. [Labeling Guidelines](#labeling-guidelines)

---

## STEAD Dataset

### Description

The **Stanford Earthquake Dataset (STEAD)** is the primary training dataset. It is a large, labeled seismological dataset designed for machine learning applications in seismology.

| Property | Value |
|----------|-------|
| Total traces | ~1.2 million |
| Earthquake traces | ~714,000 |
| Noise traces | ~495,000 |
| Waveform duration | 60 seconds per trace |
| Sampling rate | 100 Hz |
| Channels | 3 (East, North, Vertical — professional seismographs) |
| Format | HDF5 + CSV metadata |
| Total size | ~70 GB |

### Citation

**Required citation for any academic use:**

> Mousavi, S.M., Sheng, Y., Zhu, W., Beroza, G.C. (2019).
> **STEAD: A large seismological dataset for AI**.
> *IEEE Access*, 7, pp. 179464–179476.
> DOI: [10.1109/ACCESS.2019.2947848](https://doi.org/10.1109/ACCESS.2019.2947848)

### Download

The dataset is hosted by the **Northern California Earthquake Data Center (NCEDC)**:

- **URL:** http://ncedc.org/ftp/outgoing/giaw/STEAD/
- **Files:**
  - `chunk1.hdf5` + `chunk1.csv` (~17 GB) — First chunk of waveforms
  - `chunk2.hdf5` + `chunk2.csv` (~18 GB) — Second chunk of waveforms
  - `merge.hdf5` + `merge.csv` (~35 GB) — Full merged dataset (recommended)

```bash
# Automated download via included script
bash scripts/download_stead.sh

# Manual download (resumable)
wget -c http://ncedc.org/ftp/outgoing/giaw/STEAD/merge.hdf5 -P data/raw/stead/
wget -c http://ncedc.org/ftp/outgoing/giaw/STEAD/merge.csv  -P data/raw/stead/
```

Expected download location: `data/raw/stead/`

### HDF5 Structure

```
merge.hdf5
  /earthquake/
    /local/
      <trace_name>   # e.g., "GS.OK001..HH_20170306_031009_ML3.1"
        [60000, 3]   # 60s * 100Hz = 6000 samples per channel, 3 channels (E, N, Z)
  /non_earthquake/
    /noise/
      <trace_name>
        [60000, 3]
```

### CSV Metadata

The CSV file contains metadata for each trace. Key columns:

| Column | Description |
|--------|-------------|
| `trace_name` | Identifier matching the HDF5 group name |
| `trace_category` | `earthquake_local` or `noise` |
| `trace_start_time` | UTC start time of the trace |
| `p_arrival_sample` | Sample index of P-wave arrival (earthquakes only) |
| `s_arrival_sample` | Sample index of S-wave arrival (earthquakes only) |
| `source_magnitude` | Earthquake magnitude (Richter/Mw) |
| `source_distance_km` | Distance from source to station (km) |
| `snr_db` | Signal-to-noise ratio in dB |

### Adaptation for Smartphone Input

STEAD traces are from professional seismographs (3 channels: E, N, Z). The model expects 6 channels (3-axis accelerometer + 3-axis gyroscope) from a smartphone.

The data loader (`ml/src/data_loader.py`) handles this adaptation:
- STEAD traces are used for channels 0–2 (accelerometer equivalent)
- Synthetic gyroscope data is generated for channels 3–5 during STEAD loading (zero-mean Gaussian noise scaled to typical smartphone gyroscope noise floor)
- This is a simplification; real gyroscope data from Phyphox improves performance

---

## Phyphox Custom Dataset

The **Phyphox dataset** is custom data collected using consumer smartphones with the [Phyphox](https://phyphox.org/) app (RWTH Aachen University). It bridges the domain gap between professional seismograph data (STEAD) and real smartphone sensor data.

### Purpose

- Provides real smartphone accelerometer + gyroscope data
- Captures smartphone-specific noise characteristics (electronic noise, microphone resonance, device vibration patterns)
- Includes true negative examples: drops, table knocks, traffic vibrations, construction

### Phyphox App Setup

1. Install Phyphox from the [Google Play Store](https://play.google.com/store/apps/details?id=de.rwth_aachen.phyphox) or [phyphox.org](https://phyphox.org/)
2. Open Phyphox and create a **custom experiment**:
   - Add sensor: **Linear Acceleration** (not plain Accelerometer) — rate: 100 Hz
   - Add sensor: **Gyroscope** — rate: 100 Hz
   - Buffer duration: 30–120 seconds
3. Alternatively, use Phyphox's built-in "Acceleration (without g)" experiment and note that it only captures 3 channels

### Recording Protocol

#### For Earthquake Events

- Record as soon as you feel shaking (retroactive recording is not possible; pre-trigger if you expect activity)
- Keep the phone on a hard surface (table, floor) for better ground coupling
- Record for at least 30 seconds before and after the event
- Note the time of the event (for cross-reference with INGV/USGS catalogs)
- Note phone orientation and placement

#### For Noise Examples (True Negatives)

Collect examples of common false positive sources:

| Scenario | Duration | Notes |
|----------|----------|-------|
| Phone drop on soft surface | 5–10 s | Include the drop moment |
| Phone drop on hard surface | 5–10 s | High-amplitude impulse |
| Table knock (fist) | 5–10 s | |
| Construction vibration | 30–60 s | Near construction sites |
| Heavy traffic / truck passing | 30–60 s | Phone on table near road |
| Washing machine cycle | 60 s | Rhythmic vibration |
| Walking with phone in pocket | 30 s | Mobile noise profile |
| Stationary / ambient | 60 s | Background noise baseline |

### Export Format

Export from Phyphox via **Export > CSV (comma, decimal point)**. The expected column layout:

```csv
time,acc_x,acc_y,acc_z,gyro_x,gyro_y,gyro_z
0.000,0.0124,-0.0341,0.0092,0.0011,-0.0020,0.0003
0.010,0.0149,-0.0318,0.0087,0.0010,-0.0018,0.0002
...
```

| Column | Sensor | Unit |
|--------|--------|------|
| `time` | — | seconds (relative to start) |
| `acc_x` | Linear Acceleration X | m/s² |
| `acc_y` | Linear Acceleration Y | m/s² |
| `acc_z` | Linear Acceleration Z | m/s² |
| `gyro_x` | Gyroscope X (roll rate) | rad/s |
| `gyro_y` | Gyroscope Y (pitch rate) | rad/s |
| `gyro_z` | Gyroscope Z (yaw rate) | rad/s |

**Note on axis orientation:** Phyphox uses the Android coordinate system (X = right, Y = up, Z = out of screen in portrait mode). Ensure the phone orientation is consistent across recordings, or document orientation per recording.

### Converting Phyphox Data

```bash
# Convert a single recording
python scripts/convert_phyphox.py \
  --input data/raw/phyphox/earthquake_20260115.csv \
  --output data/processed/ \
  --label earthquake \
  --window_size 1000 \
  --stride 500

# Convert all recordings in a directory
python scripts/convert_phyphox.py \
  --input data/raw/phyphox/ \
  --output data/processed/ \
  --label noise \
  --window_size 1000 \
  --stride 500
```

The converter:
1. Resamples to 100 Hz if the raw rate differs
2. Segments into 10-second (1000-sample) windows with configurable stride
3. Saves as NumPy `.npy` files with shape `[N, 1000, 6]`

---

## Data Pipeline

```
data/raw/stead/          data/raw/phyphox/
    merge.hdf5               *.csv
        |                      |
        v                      v
  data_loader.py         convert_phyphox.py
        |                      |
        +----------+-----------+
                   |
                   v
           preprocessing.py
           (detrend, bandpass, z-score)
                   |
                   v
         data/processed/
           train.npy, val.npy, test.npy
                   |
                   v
              train.py
```

### Running the Full Pipeline

```bash
# Step 1: Download STEAD (if not already done)
bash scripts/download_stead.sh

# Step 2: Validate dataset integrity
python scripts/validate_dataset.py

# Step 3: Clean and preprocess
python scripts/clean_dataset.py

# Step 4: Train
python ml/src/train.py
```

---

## Labeling Guidelines

### Earthquake vs Noise Decision Rules

When labeling custom Phyphox recordings:

| Observation | Label |
|------------|-------|
| Confirmed seismic event (INGV catalog match within ±5 min) | `earthquake` |
| Strong shaking with no catalog match | Investigate; likely `noise` |
| Drop, knock, impact | `noise` (impulse class) |
| Traffic, construction, machinery | `noise` (ambient class) |
| Stationary phone, normal environment | `noise` (background class) |

### Cross-Referencing with Seismic Catalogs

To verify whether a recording corresponds to a real earthquake:

1. **INGV (Italy):** https://terremoti.ingv.it — Search by time and area
2. **USGS:** https://earthquake.usgs.gov/earthquakes/search/
3. **EMSC:** https://www.emsc-csem.org

A match is considered valid if:
- Time difference < 5 minutes from catalog origin time
- The catalog magnitude is >= 2.0 (below this, most smartphones will not detect it)
- The station is within the expected shaking range for the magnitude (use magnitude-distance scaling rules: Mw 3.0 → ~100 km radius, Mw 4.0 → ~300 km radius)

### Quality Thresholds

Recordings are excluded from the training set if:
- Data duration < 10 seconds after trimming
- More than 5% of samples are missing or NaN
- The accelerometer appears clipped (|acc| > 19.6 m/s² sustained for > 100 samples — indicates sensor saturation)
- Sampling rate cannot be verified to be within ±5% of 100 Hz
