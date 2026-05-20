# Changelog

All notable changes to Seismic Mobile Detector will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.0] - 2026-05-20

### Added

- Complete CNN-1D ML pipeline with hybrid STEAD + Phyphox dataset support
- Firebase Firestore integration for distributed community detection network
- Real-time seismic event map (OpenStreetMap via OSMDroid)
- P-wave early detection via STA/LTA (Short-Term Average / Long-Term Average) algorithm
- Scientific data export — CSV and JSON formats for academic use
- First-launch legal disclaimer and data sharing consent flow
- Community event correlation — cross-device validation reduces false positives
- 6-channel sensor input: linear acceleration (m/s²) + gyroscope (rad/s) at 100 Hz
- Multi-head CNN output: detection confidence, intensity class, P-wave arrival sample, PGA estimate, signal type classification
- On-device preprocessing pipeline: detrend → bandpass filter (Butterworth) → z-score normalization
- Room database for local detection history with full CRUD support
- Background monitoring via foreground service with persistent notification
- GPU delegate support for TFLite inference (falls back to CPU automatically)
- Anonymized detection upload: SHA-256 hashed device UUID, coordinates rounded to ±1 km
- 90-day automatic data retention via Firestore cleanup function
- Full GDPR-compliant privacy policy and opt-in data sharing consent
- Bilingual (Italian/English) legal disclaimer
- Complete project documentation: README, SETUP, MODEL, ANDROID, DATASET, LEGAL, PRIVACY, FIRESTORE_RULES
- Apache 2.0 license

### Changed

- Sensor input changed from `TYPE_ACCELEROMETER` (includes gravity) to `TYPE_LINEAR_ACCELERATION` (gravity-compensated) + `TYPE_GYROSCOPE`
- Model input shape updated from `[1000, 3]` to `[1000, 6]` to accommodate 6-channel sensor input
- Navigation updated to bottom navigation bar with 4 tabs (Home, History, Map, Settings)
- Notification text updated to support Italian locale ("TERREMOTO RILEVATO")

### Fixed

- Circular buffer ordering bug in `SeismicSensorManager` — oldest sample was written to position 0 instead of correct circular position, causing window misalignment
- Preprocessor Z-score normalization divide-by-zero on flat signal windows (now guarded with epsilon = 1e-8)

---

## [0.1.0] - 2026-01-01

### Added

- Initial Android prototype with local-only detection
- TFLite inference interface (`SeismicMLModel.kt`)
- Room database for local storage of detection events
- Background monitoring foreground service
- Basic Compose UI (single screen)
- Placeholder ML model with correct input/output shape
- Initial Python ML training scaffolding
- Project structure with `android/`, `ml/`, `scripts/`, `data/`, `tests/` directories

---

## Versioning Policy

- **Patch version (x.y.Z):** Bug fixes, documentation updates, dependency patches
- **Minor version (x.Y.0):** New features, backward-compatible changes
- **Major version (X.0.0):** Breaking changes (model incompatibility, API changes, database schema migrations)
