# Setup Guide

This guide covers the complete setup of Seismic Mobile Detector for development, including Android Studio configuration, Firebase project creation, Python ML environment, and dataset acquisition.

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Android Studio Setup](#android-studio-setup)
3. [Firebase Project Creation](#firebase-project-creation)
4. [Firestore Security Rules](#firestore-security-rules)
5. [Python ML Environment](#python-ml-environment)
6. [STEAD Dataset Download](#stead-dataset-download)
7. [Phyphox Data Format](#phyphox-data-format)
8. [Running Tests](#running-tests)

---

## Prerequisites

Before you begin, ensure you have the following installed:

| Tool | Minimum Version | Download |
|------|----------------|---------|
| Android Studio | Hedgehog (2023.1) or later | [developer.android.com/studio](https://developer.android.com/studio) |
| JDK | 17 | Bundled with Android Studio |
| Git | 2.x | [git-scm.com](https://git-scm.com) |
| Python | 3.9+ | [python.org](https://python.org) |
| Google account | — | For Firebase |

---

## Android Studio Setup

### 1. Clone the Repository

```bash
git clone https://github.com/[owner]/Seismic-mobile-detector.git
cd Seismic-mobile-detector
```

### 2. Open the Android Project

1. Launch Android Studio
2. Select **File > Open**
3. Navigate to and select the `android/` subdirectory (not the root)
4. Click **OK** and wait for Gradle sync to complete

The project uses Gradle 8.x with Kotlin DSL (`build.gradle.kts`). Gradle will download all dependencies automatically on first sync.

### 3. Install Required SDK Components

If Android Studio prompts about missing SDK components:

1. Go to **File > Project Structure > SDK Location**
2. Ensure Android SDK is set to SDK 34 (Android 14)
3. Go to **Tools > SDK Manager** and install:
   - Android SDK Platform 34
   - Android SDK Build-Tools 34
   - NDK (for the `armeabi-v7a` and `arm64-v8a` ABI filters)

### 4. Configure local.properties

The `android/local.properties` file must point to your Android SDK:

```properties
sdk.dir=/Users/yourname/Library/Android/sdk   # macOS
# or
sdk.dir=C:\\Users\\yourname\\AppData\\Local\\Android\\Sdk   # Windows
# or
sdk.dir=/home/yourname/Android/Sdk   # Linux
```

Android Studio typically creates this file automatically.

### 5. Add google-services.json

The app requires a `google-services.json` file from Firebase to build with Firebase dependencies. Without it, the Gradle build will fail.

**Option A — Use Firebase (recommended):** Follow the [Firebase Project Creation](#firebase-project-creation) section and download your `google-services.json`.

**Option B — Placeholder (local-only build):** Create a minimal placeholder file at `android/app/google-services.json`:

```json
{
  "project_info": { "project_id": "placeholder", "project_number": "000000000000" },
  "client": [{
    "client_info": { "mobilesdk_app_id": "1:000000000000:android:0000000000000000" },
    "oauth_client": [],
    "api_key": [{ "current_key": "" }],
    "services": { "appinvite_service": { "other_platform_oauth_client": [] } }
  }],
  "configuration_version": "1"
}
```

Note: Community network features will not function with the placeholder.

### 6. Place the TFLite Model

The app expects a TFLite model at `android/app/src/main/assets/seismic_model.tflite`.

- **After training:** Copy `output/seismic_model.tflite` from the ML pipeline
- **For UI development:** Generate a mock model:
  ```bash
  python scripts/create_mock_model.py
  cp output/seismic_model.tflite android/app/src/main/assets/
  ```

### 7. Build and Run

- Connect a physical Android device (API 26+) via USB with developer options and USB debugging enabled
- Click the **Run** button (green triangle) in Android Studio
- Select your device from the device picker

**Important:** The seismic monitoring service requires a physical device with real sensor hardware. Android emulators do not have accelerometers or gyroscopes.

---

## Firebase Project Creation

### Step 1 — Create a Firebase Project

1. Go to [console.firebase.google.com](https://console.firebase.google.com)
2. Click **Add project**
3. Enter a project name (e.g., `seismic-detector-dev`)
4. Choose whether to enable Google Analytics (optional for this project)
5. Click **Create project** and wait for provisioning

### Step 2 — Add an Android App

1. In your new project, click the Android icon (**Add app**)
2. Enter the package name: `com.example.seismicdetector`
3. Enter a nickname (e.g., `Seismic Detector`)
4. SHA-1 certificate (optional for Firestore, required for Google Sign-In if added later):
   ```bash
   # Generate debug SHA-1
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey \
     -storepass android -keypass android
   ```
5. Click **Register app**

### Step 3 — Download google-services.json

1. On the next screen, click **Download google-services.json**
2. Save it to `android/app/google-services.json`
3. Click **Next** through the remaining setup screens (SDK setup is already in the project)

### Step 4 — Enable Firestore Database

1. In the Firebase console sidebar, go to **Build > Firestore Database**
2. Click **Create database**
3. Choose **Start in test mode** (you will replace rules later)
4. Select the closest Firestore location:
   - `eur3` (europe-west) — recommended for European users / GDPR compliance
   - `us-central` — if primarily US-based users
5. Click **Enable**

### Step 5 — Enable Required APIs (if prompted)

Firebase may ask you to enable the Cloud Firestore API in Google Cloud Console. Follow the prompts to enable it.

---

## Firestore Security Rules

After initial setup in test mode, apply production security rules to protect the database.

1. In the Firebase console, go to **Firestore Database > Rules**
2. Replace the existing rules with the content from [FIRESTORE_RULES.md](FIRESTORE_RULES.md)
3. Click **Publish**

See [FIRESTORE_RULES.md](FIRESTORE_RULES.md) for the full rules and explanation.

---

## Python ML Environment

### 1. Create a Virtual Environment

```bash
cd Seismic-mobile-detector

# Create virtual environment
python -m venv venv

# Activate it
source venv/bin/activate          # macOS / Linux
# or
venv\Scripts\activate             # Windows
```

### 2. Install Dependencies

```bash
pip install --upgrade pip
pip install -r ml/requirements.txt
```

Key dependencies:
- `tensorflow>=2.15.0` — Model training and TFLite conversion
- `h5py>=3.8.0` — Reading STEAD HDF5 files
- `numpy`, `scipy`, `pandas` — Signal processing and data manipulation
- `scikit-learn` — Metrics and preprocessing utilities
- `matplotlib` — Visualization

### 3. Verify Installation

```bash
python -c "import tensorflow as tf; print(tf.__version__)"
python -c "import h5py; print(h5py.__version__)"
```

---

## STEAD Dataset Download

The STEAD (Stanford Earthquake Dataset) is the primary training dataset. It is approximately **70 GB** in HDF5 format.

### Automated Download

```bash
bash scripts/download_stead.sh
```

This script downloads from the Northern California Earthquake Data Center (NCEDC):

- **Direct URL:** `http://ncedc.org/ftp/outgoing/giaw/STEAD/`
- Files:
  - `chunk1.hdf5` + `chunk1.csv` (~17 GB)
  - `chunk2.hdf5` + `chunk2.csv` (~18 GB)
  - `merge.hdf5` + `merge.csv` — Full merged dataset (~35 GB)

### Manual Download

If the automated script fails, download manually from NCEDC:

1. Go to [ncedc.org/ftp/outgoing/giaw/STEAD/](http://ncedc.org/ftp/outgoing/giaw/STEAD/)
2. Download the HDF5 and CSV files for the chunks you want
3. Place them in `data/raw/stead/`

### Dataset Structure

```
data/raw/stead/
  chunk1.hdf5       # Seismic waveforms
  chunk1.csv        # Metadata (earthquake/noise labels, magnitude, distance, etc.)
  chunk2.hdf5
  chunk2.csv
```

The HDF5 files contain two groups:
- `earthquake/local` — Earthquake recordings (P-wave and S-wave arrivals labeled)
- `non_earthquake/noise` — Ambient noise recordings

See [docs/DATASET.md](DATASET.md) for full schema details.

### Citation Requirement

If you publish research using STEAD data, cite:

> Mousavi, S.M., Sheng, Y., Zhu, W., Beroza, G.C. (2019). STEAD: A large seismological dataset for AI. *IEEE Access*, 7, 179464–179476. DOI: 10.1109/ACCESS.2019.2947848

---

## Phyphox Data Format

[Phyphox](https://phyphox.org/) (RWTH Aachen University) is used to collect custom training data from smartphones.

### Phyphox Setup

1. Install Phyphox from the Play Store or [phyphox.org](https://phyphox.org)
2. Open Phyphox and create a custom experiment with:
   - **Sensor 1:** Linear Acceleration (`TYPE_LINEAR_ACCELERATION`) at 100 Hz
   - **Sensor 2:** Gyroscope at 100 Hz
3. Export data as CSV after recording

### Expected CSV Format

```csv
time,acc_x,acc_y,acc_z,gyro_x,gyro_y,gyro_z
0.000,0.012,-0.034,9.812,0.001,-0.002,0.000
0.010,0.015,-0.031,9.810,0.001,-0.001,0.000
...
```

| Column | Unit | Sensor |
|--------|------|--------|
| `time` | seconds | Timestamp (relative) |
| `acc_x` | m/s² | Linear acceleration X |
| `acc_y` | m/s² | Linear acceleration Y |
| `acc_z` | m/s² | Linear acceleration Z |
| `gyro_x` | rad/s | Gyroscope X (roll rate) |
| `gyro_y` | rad/s | Gyroscope Y (pitch rate) |
| `gyro_z` | rad/s | Gyroscope Z (yaw rate) |

### Converting Phyphox Data

```bash
python scripts/convert_phyphox.py \
  --input data/raw/phyphox/my_recording.csv \
  --output data/processed/phyphox_converted.npy \
  --label earthquake   # or 'noise'
```

See [docs/DATASET.md](DATASET.md) for the complete collection protocol and labeling guidelines.

---

## Running Tests

### Python ML Tests

```bash
# Activate virtual environment first
source venv/bin/activate

# Run all Python tests
python -m pytest tests/ -v

# Run only ML pipeline tests
python -m pytest tests/integration/test_ml_pipeline.py -v

# Run with coverage
python -m pytest tests/ --cov=ml/src --cov-report=html
```

### Android Unit Tests

```bash
cd android
./gradlew test
```

Or in Android Studio: right-click on the `test` directory > **Run Tests**.

### Android Instrumented Tests

Requires a connected device or emulator:

```bash
cd android
./gradlew connectedAndroidTest
```

### Full Workflow Tests

```bash
python -m pytest tests/e2e/test_full_workflow.py -v
```

---

## Troubleshooting

### Gradle sync fails with "google-services.json not found"

Add the `google-services.json` file as described in [Step 3 of Firebase Setup](#firebase-project-creation) or use the placeholder JSON from [Step 6 of Android Studio Setup](#6-place-the-tflite-model).

### TFLite model not found at runtime

Ensure `seismic_model.tflite` exists at `android/app/src/main/assets/seismic_model.tflite`. Either train the model or generate a mock with `python scripts/create_mock_model.py`.

### Sensors not available

The app requires a physical Android device. If running on an emulator, sensor-dependent features will throw an exception. Check logcat for `SensorManager: sensor not available` messages.

### STEAD download slow or failing

The NCEDC FTP server can be slow. Consider using a download manager that supports resumable transfers (`wget -c`). The `download_stead.sh` script uses `wget` with resume support.
