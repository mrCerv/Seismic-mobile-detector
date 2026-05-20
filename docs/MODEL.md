# ML Model Documentation

This document describes the CNN-1D model architecture used by Seismic Mobile Detector for on-device seismic event detection.

---

## Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Input Specification](#input-specification)
4. [Output Heads](#output-heads)
5. [Preprocessing Pipeline](#preprocessing-pipeline)
6. [Training Details](#training-details)
7. [Expected Performance Metrics](#expected-performance-metrics)
8. [Running Training](#running-training)
9. [TFLite Conversion](#tflite-conversion)

---

## Overview

The model is a **1-dimensional Convolutional Neural Network (CNN-1D)** designed to run efficiently on Android devices via TensorFlow Lite. It takes a 10-second window of 6-channel sensor data and simultaneously outputs seismic detection confidence, intensity class, P-wave arrival time, peak ground acceleration, and signal type classification.

Key design decisions:
- **On-device inference** — No cloud dependency for detection; the model runs entirely on-device
- **6-channel input** — Linear acceleration (3 channels) + gyroscope (3 channels) to reduce false positives from mechanical vibrations
- **Multi-head output** — Single forward pass provides all relevant seismic metrics
- **TFLite-compatible** — All operations use standard TFLite operators; no custom ops required

---

## Architecture

```
Input: [batch, 1000, 6]
         |
         |  (10 seconds * 100 Hz = 1000 timesteps, 6 sensor channels)
         v
+---------------------------+
|  Conv1D(32, kernel=7)     |  --> ReLU --> BatchNorm
|  Conv1D(32, kernel=7)     |  --> ReLU --> BatchNorm
|  MaxPool1D(stride=2)      |  --> [batch, 497, 32]
+---------------------------+
         |
         v
+---------------------------+
|  Conv1D(64, kernel=5)     |  --> ReLU --> BatchNorm
|  Conv1D(64, kernel=5)     |  --> ReLU --> BatchNorm
|  MaxPool1D(stride=2)      |  --> [batch, 247, 64]
+---------------------------+
         |
         v
+---------------------------+
|  Conv1D(128, kernel=3)    |  --> ReLU --> BatchNorm
|  Conv1D(128, kernel=3)    |  --> ReLU --> BatchNorm
|  MaxPool1D(stride=2)      |  --> [batch, 122, 128]
+---------------------------+
         |
         v
+---------------------------+
|  Conv1D(256, kernel=3)    |  --> ReLU --> BatchNorm
|  Conv1D(256, kernel=3)    |  --> ReLU --> BatchNorm
|  GlobalAvgPool1D          |  --> [batch, 256]
+---------------------------+
         |
    +---------+
    |  Shared |  Dense(256) --> ReLU --> Dropout(0.3)
    |  Trunk  |
    +---------+
    /    |    |    \    \
   /     |    |     \    \
HEAD1  HEAD2 HEAD3  HEAD4  HEAD5
```

### Parameter Count

Approximate total parameters: ~850K  
TFLite model size (float32): ~3.4 MB  
TFLite model size (int8 quantized): ~0.9 MB  
Inference time on mid-range Android (Snapdragon 778G): ~25 ms

---

## Input Specification

| Property | Value |
|----------|-------|
| Shape | `[1, 1000, 6]` (batch=1 for inference) |
| Data type | float32 |
| Time dimension | 1000 samples = 10 seconds at 100 Hz |
| Channel order | `[acc_x, acc_y, acc_z, gyro_x, gyro_y, gyro_z]` |

### Channel Descriptions

| Index | Name | Sensor | Unit |
|-------|------|--------|------|
| 0 | `acc_x` | `TYPE_LINEAR_ACCELERATION` | m/s² |
| 1 | `acc_y` | `TYPE_LINEAR_ACCELERATION` | m/s² |
| 2 | `acc_z` | `TYPE_LINEAR_ACCELERATION` | m/s² |
| 3 | `gyro_x` | `TYPE_GYROSCOPE` | rad/s |
| 4 | `gyro_y` | `TYPE_GYROSCOPE` | rad/s |
| 5 | `gyro_z` | `TYPE_GYROSCOPE` | rad/s |

`TYPE_LINEAR_ACCELERATION` is used (not `TYPE_ACCELEROMETER`) to eliminate the gravity component, which improves model performance on seismic signals.

---

## Output Heads

### Head 1 — Detection Confidence (sigmoid)

```
Shape: [1, 1]
Range: 0.0 to 1.0
Interpretation: Probability that the input window contains a seismic event
Threshold (default): 0.75
```

### Head 2 — Intensity Class (softmax, 5 classes)

```
Shape: [1, 5]
Classes:
  0: No event / below detection threshold
  1: Micro (Richter < 2.0) — typically imperceptible
  2: Minor (Richter 2.0–3.9) — felt by some
  3: Moderate (Richter 4.0–4.9) — widely felt, minor damage possible
  4: Strong+ (Richter >= 5.0) — strong shaking, potential damage
```

### Head 3 — P-wave Arrival Sample (regression, linear)

```
Shape: [1, 1]
Range: 0 to 999 (sample index within the 1000-sample window)
Unit: Sample index (divide by 100 for seconds from window start)
Note: Valid only when Head 1 confidence > 0.5
```

### Head 4 — Peak Ground Acceleration / PGA (regression, softplus)

```
Shape: [1, 1]
Unit: m/s²
Range: 0.0 to ~50.0 (softplus ensures non-negative output)
Note: Estimated from accelerometer channels; approximation only
```

### Head 5 — Signal Type Classification (softmax, 3 classes)

```
Shape: [1, 3]
Classes:
  0: Noise / ambient vibration (traffic, machinery, etc.)
  1: Earthquake P-wave + S-wave pattern
  2: Single-impulse mechanical event (drop, knock, slam)
```

---

## Preprocessing Pipeline

The preprocessing is applied identically during training and inference. The Android implementation mirrors the Python preprocessing pipeline.

### Step 1 — Detrending

Remove linear trend from each channel independently:

```python
from scipy.signal import detrend
signal = detrend(signal, axis=0)
```

This removes slow drift caused by device orientation changes.

### Step 2 — Bandpass Filtering

Apply a 4th-order Butterworth bandpass filter:

```python
from scipy.signal import butter, sosfilt
# Accelerometer channels (0, 1, 2):
sos = butter(4, [1.0, 45.0], btype='bandpass', fs=100.0, output='sos')
# Gyroscope channels (3, 4, 5):
sos = butter(4, [0.5, 45.0], btype='bandpass', fs=100.0, output='sos')
signal = sosfilt(sos, signal, axis=0)
```

Passband 1–45 Hz for accelerometer captures typical seismic frequencies.  
Passband 0.5–45 Hz for gyroscope is slightly wider to capture low-frequency rotational motion.

### Step 3 — Z-score Normalization

Normalize each channel independently:

```python
mean = signal.mean(axis=0)
std = signal.std(axis=0)
std = np.where(std < 1e-8, 1e-8, std)  # Avoid division by zero
signal = (signal - mean) / std
```

Normalization is per-window and per-channel, not using global dataset statistics. This ensures the model generalizes to different device orientations and sensor calibrations.

### Android Implementation

The preprocessing is implemented in `RealtimePreprocessor.kt` using Apache Commons Math for the Butterworth filter. The preprocessing runs on a background thread to avoid blocking the sensor callback.

---

## Training Details

### Loss Function

Multi-task loss with weighted combination:

```python
total_loss = (
    1.5 * binary_crossentropy(y_detection, pred_detection) +
    0.8 * categorical_crossentropy(y_intensity, pred_intensity) +
    0.5 * mse(y_pwave_sample, pred_pwave) +
    0.4 * mse(y_pga, pred_pga) +
    0.3 * categorical_crossentropy(y_signal_type, pred_signal_type)
)
```

Detection confidence is weighted highest (1.5) as the primary task.

### Hyperparameters

| Parameter | Value |
|-----------|-------|
| Optimizer | Adam |
| Learning rate | 1e-3 (with ReduceLROnPlateau, factor=0.5, patience=5) |
| Batch size | 128 |
| Max epochs | 100 |
| Early stopping | patience=10, monitor=val_loss |
| Dropout rate | 0.3 |
| L2 regularization | 1e-4 on all Conv1D layers |

### Training Data Split

| Split | Fraction | Notes |
|-------|----------|-------|
| Train | 70% | |
| Validation | 15% | Used for early stopping and LR scheduling |
| Test | 15% | Held out; never used during training |

Stratified split on earthquake/noise label and intensity class.

### Class Imbalance Handling

STEAD contains approximately 60% earthquake and 40% noise traces. Class weights are applied:

```python
class_weight = {0: 1.0, 1: 1.4}  # Slight upweight for noise to reduce false positives
```

---

## Expected Performance Metrics

The following are reference values on the STEAD test set (15% held-out split). Actual performance on smartphone data (Phyphox) may differ.

### Detection Head (binary classification)

| Metric | Expected Value |
|--------|---------------|
| Precision | > 0.85 |
| Recall | > 0.80 |
| F1-score | > 0.82 |
| AUC-ROC | > 0.92 |

### Intensity Classification (5-class)

| Metric | Expected Value |
|--------|---------------|
| Accuracy | > 0.78 |
| Macro F1 | > 0.72 |

### P-wave Arrival (regression)

| Metric | Expected Value |
|--------|---------------|
| MAE | < 15 samples (< 0.15 seconds) |

### Notes on Real-World Performance

Performance on consumer smartphone data is expected to be lower than STEAD benchmarks due to:
- Different sensor hardware vs professional seismographs
- Noise sources not present in STEAD (phone vibration, movement)
- Variable device orientation

The Phyphox custom dataset helps bridge this gap. More smartphone-collected data improves real-world performance.

---

## Running Training

```bash
# Activate virtual environment
source venv/bin/activate

# Ensure STEAD data is in data/raw/stead/
ls data/raw/stead/

# Train with default configuration
python ml/src/train.py

# Train with custom configuration
python ml/src/train.py --config ml/configs/training_config.yaml

# Resume from checkpoint
python ml/src/train.py --resume output/checkpoints/best_model.keras
```

Training artifacts are saved to `output/`:
- `output/seismic_model.keras` — Full Keras model (for further training/evaluation)
- `output/seismic_model.tflite` — TFLite model (for Android deployment)
- `output/training_history.json` — Loss and metric curves
- `output/evaluation_report.json` — Test set metrics

---

## TFLite Conversion

### Standard (float32) Conversion

```bash
python ml/src/convert_tflite.py \
  --input output/seismic_model.keras \
  --output output/seismic_model.tflite \
  --quantization none
```

### INT8 Quantization (smaller model, faster inference)

```bash
python ml/src/convert_tflite.py \
  --input output/seismic_model.keras \
  --output output/seismic_model_int8.tflite \
  --quantization int8 \
  --calibration_data data/processed/calibration_set.npy
```

INT8 quantization requires a representative calibration dataset (100–500 samples). It reduces model size by ~75% and may increase inference speed on devices without a GPU, with a small accuracy trade-off (<1% F1 in practice).

### Deploy to Android

```bash
cp output/seismic_model.tflite android/app/src/main/assets/seismic_model.tflite
```

After copying, rebuild the Android app in Android Studio.

### Benchmark on Device

```bash
python scripts/benchmark_model.py --model output/seismic_model.tflite
```

This script runs inference 1000 times and reports mean and p95 inference time.
