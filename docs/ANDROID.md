# Android Development Guide

This guide covers the Android app architecture, key components, development patterns, and how to extend the application.

---

## Table of Contents

1. [Project Structure](#project-structure)
2. [Key Components](#key-components)
3. [Dependency Injection (Hilt)](#dependency-injection-hilt)
4. [Architecture Overview](#architecture-overview)
5. [Adding New Screens](#adding-new-screens)
6. [Testing](#testing)
7. [Build Variants and Configuration](#build-variants-and-configuration)

---

## Project Structure

```
android/
  app/
    src/
      main/
        assets/
          seismic_model.tflite        # TFLite model (not committed; see SETUP.md)
        java/com/example/seismicdetector/
          data/
            AppDatabase.kt            # Room database definition
            DetectionDao.kt           # DAO for seismic event records
            DetectionEntity.kt        # Room entity for detection records
            PreferencesManager.kt     # DataStore preferences wrapper
            SeismicRepository.kt      # Repository (local DB + optional Firestore)
          di/
            AppModule.kt              # Hilt module: provides DB, sensors, model
          domain/
            EarthquakeDetector.kt     # Main detection orchestrator
            RealtimePreprocessor.kt   # Signal preprocessing (detrend, bandpass, z-score)
            SeismicMLModel.kt         # TFLite inference wrapper
            SeismicMonitoringService.kt # Foreground service for background monitoring
            SeismicSensorManager.kt   # SensorManager wrapper + circular buffer
          ui/
            home/
              HomeScreen.kt           # Main monitoring screen (Compose)
              MainViewModel.kt        # ViewModel for home screen
            history/
              HistoryScreen.kt        # Detection history list
            settings/
              SettingsScreen.kt       # User preferences
            SeismicNotificationManager.kt  # Notification channel + alerts
            theme/
              Theme.kt                # Material3 theme
          MainActivity.kt             # Single-activity entry point
          SeismicApp.kt               # Hilt @HiltAndroidApp application class
      test/
        java/com/example/seismicdetector/
          RealtimePreprocessorTest.kt # Unit tests for signal preprocessing
```

---

## Key Components

### SeismicSensorManager

**File:** `domain/SeismicSensorManager.kt`

Wraps Android's `SensorManager` to register `TYPE_LINEAR_ACCELERATION` and `TYPE_GYROSCOPE` listeners. Maintains a **circular buffer** of 1000 samples (10 seconds at 100 Hz) per sensor channel.

Key responsibilities:
- Sensor registration/unregistration lifecycle management
- Thread-safe circular buffer implementation
- Triggering the preprocessor when the buffer is full (or on a sliding window)
- Handling sensor accuracy changes

**Important:** Sensor callbacks run on the sensor's hardware thread. Heavy processing must be dispatched off this thread.

```kotlin
// Usage example
sensorManager.startListening()
sensorManager.sensorData.collect { window ->
    // window: FloatArray of shape [1000 * 6]
    val prediction = earthquakeDetector.processWindow(window)
}
```

### RealtimePreprocessor

**File:** `domain/RealtimePreprocessor.kt`

Applies the same preprocessing pipeline as the Python training code:
1. Linear detrend per channel
2. Butterworth bandpass filter (1–45 Hz for accel, 0.5–45 Hz for gyro) using Apache Commons Math
3. Z-score normalization per channel

The preprocessor exposes a single `preprocess(rawWindow: FloatArray): FloatArray` function. It runs synchronously but should be called from a background coroutine (`Dispatchers.Default`).

### SeismicMLModel

**File:** `domain/SeismicMLModel.kt`

Wraps the TFLite `Interpreter` to run inference:
- Loads `seismic_model.tflite` from `assets/`
- Optionally uses GPU delegate (`tensorflow-lite-gpu`) for faster inference
- Falls back to CPU if GPU is unavailable
- Accepts float32 input `[1, 1000, 6]` and returns 5 output tensors

```kotlin
val result: ModelOutput = mlModel.runInference(preprocessedWindow)
// result.confidence: Float (0.0 - 1.0)
// result.intensityClass: Int (0-4)
// result.pWaveSample: Float
// result.pga: Float
// result.signalType: Int (0-2)
```

### EarthquakeDetector

**File:** `domain/EarthquakeDetector.kt`

Main orchestrator that:
1. Receives raw sensor windows from `SeismicSensorManager`
2. Applies STA/LTA algorithm for rapid P-wave onset detection
3. Calls `RealtimePreprocessor` for full preprocessing
4. Calls `SeismicMLModel` for inference
5. Applies detection threshold and debouncing (prevents duplicate events within 30 seconds)
6. Emits detected events via a `StateFlow`

The STA/LTA (Short-Term Average / Long-Term Average) algorithm provides a fast first-pass filter before invoking the more expensive ML model. This reduces unnecessary inference calls and saves battery.

### SeismicMonitoringService

**File:** `domain/SeismicMonitoringService.kt`

A `Service` that runs as a **foreground service** to keep monitoring active when the app is in the background. It:
- Displays a persistent notification ("Seismic monitoring active")
- Starts/stops sensor listening based on user preferences
- Survives app backgrounding (but not force-kill or battery optimization)
- Handles wakelock for continuous monitoring

Users must grant notification permission (Android 13+) and should be advised to exempt the app from battery optimization for reliable background monitoring.

### SeismicRepository

**File:** `data/SeismicRepository.kt`

Data layer abstraction combining:
- **Local storage:** Room database (`AppDatabase`) for detection history
- **Remote storage:** Firebase Firestore for anonymous community uploads (only when user has consented and network is available)

Exposes a `Flow<List<DetectionEntity>>` for the UI layer to observe.

---

## Dependency Injection (Hilt)

The project uses **Dagger Hilt** for dependency injection. All dependencies are declared in `di/AppModule.kt`.

### Application Setup

`SeismicApp.kt` is annotated with `@HiltAndroidApp`:

```kotlin
@HiltAndroidApp
class SeismicApp : Application()
```

### Injecting into Activities and ViewModels

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() { ... }

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: SeismicRepository,
    private val earthquakeDetector: EarthquakeDetector
) : ViewModel() { ... }
```

### Adding a New Dependency

1. Add the class to `AppModule.kt`:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideMyNewService(context: Context): MyNewService {
        return MyNewService(context)
    }
}
```

2. Inject it where needed with `@Inject constructor(...)`.

### Hilt with Compose Navigation

Use `hiltViewModel()` in Compose screens:

```kotlin
@Composable
fun HomeScreen(viewModel: MainViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // ...
}
```

---

## Architecture Overview

The app follows **MVVM (Model-View-ViewModel)** with Clean Architecture principles:

```
UI Layer (Compose Screens)
    |  observes StateFlow/SharedFlow
    v
ViewModel Layer (Hilt ViewModels)
    |  calls use cases / repository
    v
Domain Layer (EarthquakeDetector, Preprocessor, MLModel)
    |  uses
    v
Data Layer (Repository -> Room DB + Firestore)
```

**Navigation:** Jetpack Compose Navigation with a bottom navigation bar (4 tabs: Home, History, Map, Settings).

**Threading model:**
- Sensor callbacks: hardware thread
- Preprocessing and inference: `Dispatchers.Default` (CPU-bound coroutines)
- Database operations: `Dispatchers.IO`
- UI updates: `Dispatchers.Main`

---

## Adding New Screens

### 1. Create the Composable

Create a new file in `ui/yourfeature/YourScreen.kt`:

```kotlin
@Composable
fun YourScreen(
    viewModel: YourViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Compose UI here
}
```

### 2. Create the ViewModel

```kotlin
@HiltViewModel
class YourViewModel @Inject constructor(
    private val repository: SeismicRepository
) : ViewModel() {
    val uiState: StateFlow<YourUiState> = /* ... */
}
```

### 3. Add Navigation Route

In `MainActivity.kt`, add a route to the Compose Navigation graph:

```kotlin
composable("your_route") {
    YourScreen()
}
```

### 4. Add to Bottom Navigation Bar

Update the bottom nav bar items in `MainActivity.kt` to include a new tab if needed.

---

## Testing

### Unit Tests

Unit tests live in `app/src/test/`. They run on the JVM (no device needed).

Example: `RealtimePreprocessorTest.kt` tests the signal preprocessing pipeline with synthetic waveforms.

```bash
# Run unit tests
cd android
./gradlew test
```

### Instrumented Tests

Instrumented tests live in `app/src/androidTest/`. They require a connected device or emulator.

```bash
cd android
./gradlew connectedAndroidTest
```

### Testing the ViewModel

Use `kotlinx-coroutines-test` and Hilt testing utilities:

```kotlin
@HiltAndroidTest
class MainViewModelTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @Test
    fun testDetectionState() = runTest {
        // ...
    }
}
```

### Mocking the ML Model

For UI tests that don't require real ML inference, inject a mock `SeismicMLModel` that returns predetermined outputs. Use Hilt's `@TestInstallIn` to replace the production module.

---

## Build Variants and Configuration

### Build Types

| Type | Minify | Debug | Description |
|------|--------|-------|-------------|
| `debug` | No | Yes | Development, with debug symbols |
| `release` | No (currently) | No | Production build |

To enable minification for release, set `isMinifyEnabled = true` in `build.gradle.kts` and verify ProGuard rules for TFLite and Firebase.

### Key Dependencies Summary

| Dependency | Version | Purpose |
|-----------|---------|---------|
| TensorFlow Lite | 2.15.0 | ML inference |
| TFLite GPU Delegate | 2.15.0 | GPU acceleration |
| Hilt | 2.51.1 | Dependency injection |
| Room | 2.6.1 | Local database |
| Compose BOM | 2024.04.01 | UI framework |
| Navigation Compose | 2.7.7 | Screen navigation |
| MPAndroidChart | 3.1.0 | Seismogram chart |
| Apache Commons Math | 3.6.1 | Butterworth filter |
| Firebase Firestore | (via BOM) | Cloud data sync |
| OSMDroid | (latest) | OpenStreetMap |

### ProGuard Notes for Release Builds

If minification is enabled, add these rules to `proguard-rules.pro`:

```proguard
# TensorFlow Lite
-keep class org.tensorflow.lite.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }

# Apache Commons Math
-keep class org.apache.commons.math3.** { *; }
```
