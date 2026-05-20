# Seismic Mobile Detector

A citizen science distributed seismic monitoring platform for Android. Uses smartphone accelerometers and gyroscopes with on-device CNN-1D inference (TensorFlow Lite) to detect seismic events and aggregate anonymous detections via Firebase Firestore into a distributed sensor network.

[![Android Build](https://github.com/[owner]/Seismic-mobile-detector/actions/workflows/android-build.yml/badge.svg)](https://github.com/[owner]/Seismic-mobile-detector/actions/workflows/android-build.yml)
[![Python Tests](https://github.com/[owner]/Seismic-mobile-detector/actions/workflows/python-tests.yml/badge.svg)](https://github.com/[owner]/Seismic-mobile-detector/actions/workflows/python-tests.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

---

## Important Disclaimer

**This is a research tool, NOT a professional civil protection system.**

Seismic Mobile Detector does not replace official seismic alert systems (INGV, DPC, USGS, or equivalent). False positives from mechanical vibrations are common. Never rely on this app for safety-critical decisions.

See [DISCLAIMER.md](DISCLAIMER.md) for the full disclaimer (bilingual IT/EN).

---

## Overview

```
+------------------+     +------------------+     +------------------+
|  Phone Sensors   | --> |  Preprocessing   | --> | CNN-1D Model     |
|                  |     |                  |     |                  |
| Linear Accel.    |     | Detrend          |     | TFLite (offline) |
| (TYPE_LINEAR_    |     | Bandpass filter  |     | [1000 x 6] input |
|  ACCELERATION)   |     | Z-score norm.    |     | 5-head output    |
| Gyroscope        |     | 10s @ 100Hz      |     |                  |
+------------------+     +------------------+     +--------+---------+
                                                           |
                              +----------------------------+
                              |
               +--------------v--------------+     +-----------------+
               |      Local Detection        |     |  Firestore      |
               |                             | --> |  Upload         |
               | STA/LTA P-wave detection    |     | (anonymous)     |
               | Room DB storage             |     +-----------------+
               | Notification trigger        |             |
               +-----------------------------+     +-----------------+
                                                   | Community       |
                                                   | Correlation     |
                                                   | Multi-device    |
                                                   | validation      |
                                                   +-----------------+
```

The app runs continuous background monitoring via a foreground service. Sensor data is buffered in a 10-second sliding window at 100 Hz, preprocessed on-device, and fed to the CNN-1D model. Confirmed detections are stored locally and (optionally) uploaded anonymously to Firebase for cross-device correlation.

---

## Features

- **Real-time seismic monitoring** — Linear acceleration (no gravity) + gyroscope, 6 channels at 100 Hz
- **On-device CNN-1D inference** — TensorFlow Lite 2.15, offline-capable with no cloud dependency for detection
- **P-wave early detection** — STA/LTA (Short-Term Average / Long-Term Average) algorithm for rapid onset detection
- **Distributed community network** — Anonymous event aggregation via Firebase Firestore for multi-device validation
- **Real-time event map** — OpenStreetMap (OSMDroid) showing community detections in your area
- **Scientific data export** — Export detection history as CSV or JSON for academic use
- **Privacy by design** — Community network is opt-in; no PII is ever collected

---

## Requirements

### Android App
- Android 8.0+ (API level 26+)
- Physical device with accelerometer and gyroscope sensors (emulators will not work for seismic monitoring)
- ~50 MB storage for app + model
- Internet connection (only required for community network features)

### ML Training (optional)
- Python 3.9+
- ~70 GB disk space for STEAD dataset
- 8 GB RAM minimum (16 GB recommended)
- GPU optional but recommended for training (CPU training is slow)

### Firebase (optional, for community network)
- Google account
- Firebase project (free Spark tier sufficient for development)

---

## Quick Start

### 1. Android Development

```bash
# Clone the repository
git clone https://github.com/[owner]/Seismic-mobile-detector.git
cd Seismic-mobile-detector

# Open the android/ directory in Android Studio
# File > Open > select the android/ folder
```

**Note:** The app will build and run without Firebase, but community network features will be disabled. For a complete setup, follow the Firebase steps below.

For detailed Android setup instructions, see [docs/ANDROID.md](docs/ANDROID.md).

### 2. Firebase Setup (Required for Network Features)

1. Go to [console.firebase.google.com](https://console.firebase.google.com) and create a new project
2. Enable **Firestore Database** (start in test mode, then apply security rules)
3. Add an Android app with package name `com.example.seismicdetector`
4. Download `google-services.json` and place it at `android/app/google-services.json`
5. Deploy Firestore security rules from [docs/FIRESTORE_RULES.md](docs/FIRESTORE_RULES.md)

See [docs/SETUP.md](docs/SETUP.md) for step-by-step Firebase configuration.

### 3. ML Training

```bash
# Create and activate a virtual environment
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate

# Install dependencies
pip install -r ml/requirements.txt

# Download the STEAD dataset (~70 GB, takes a while)
bash scripts/download_stead.sh

# Train the model
python ml/src/train.py

# Convert to TFLite
python ml/src/convert_tflite.py

# Copy model to Android assets
cp output/seismic_model.tflite android/app/src/main/assets/
```

### 4. Mock Model (for UI development without training)

If you want to work on the Android UI without training a model:

```bash
python scripts/create_mock_model.py
# Outputs: output/seismic_model.tflite (random weights, correct shape)
```

---

## Architecture

For detailed architecture documentation:

- **Android components** — [docs/ANDROID.md](docs/ANDROID.md)
- **ML model architecture** — [docs/MODEL.md](docs/MODEL.md)
- **Dataset details** — [docs/DATASET.md](docs/DATASET.md)
- **Full setup guide** — [docs/SETUP.md](docs/SETUP.md)

---

## Dataset

This project trains on two datasets:

### STEAD (Stanford Earthquake Dataset)
The primary training dataset. ~1.2 million seismic signal traces from global seismic networks.

> Mousavi, S.M., Sheng, Y., Zhu, W., Beroza, G.C. (2019). **STEAD: A large seismological dataset for AI**. *IEEE Access*, 7, 179464–179476. DOI: [10.1109/ACCESS.2019.2947848](https://doi.org/10.1109/ACCESS.2019.2947848)

### Phyphox Dataset (Custom)
Custom smartphone recordings collected with the [Phyphox](https://phyphox.org/) app (RWTH Aachen University) to bridge the gap between professional seismic stations and consumer smartphone sensors. See [docs/DATASET.md](docs/DATASET.md) for collection protocol.

---

## Comparable Projects

Seismic Mobile Detector is inspired by and comparable to:

- [MyShake](https://myshake.berkeley.edu/) — UC Berkeley
- [Did You Feel It? (DYFI)](https://earthquake.usgs.gov/data/dyfi/) — USGS
- [OpenEEW](https://openeew.com/) — IBM / Grillo

---

## Legal and Ethical Use

This application is intended **exclusively for research and educational purposes**.

- [DISCLAIMER.md](DISCLAIMER.md) — Important safety disclaimer (bilingual IT/EN)
- [docs/LEGAL.md](docs/LEGAL.md) — Full legal notice, IP, third-party licenses
- [docs/PRIVACY.md](docs/PRIVACY.md) — GDPR-compliant privacy policy

---

## Contributing

Contributions are welcome. Please:

1. Fork the repository and create a feature branch
2. Follow the existing code style (Kotlin + Jetpack Compose for Android, Python PEP 8 for ML)
3. Add tests for new functionality
4. Open a pull request with a clear description of your changes

For major changes, open an issue first to discuss the approach.

See [docs/CONTRIBUTING.txt](docs/CONTRIBUTING.txt) for more details.

---

## License

Apache License 2.0 — see [LICENSE](LICENSE) for the full text.

Copyright 2026 Seismic Mobile Detector Contributors
