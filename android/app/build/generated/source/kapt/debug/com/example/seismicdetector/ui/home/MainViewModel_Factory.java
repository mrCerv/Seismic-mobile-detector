package com.example.seismicdetector.ui.home;

import com.example.seismicdetector.data.PreferencesManager;
import com.example.seismicdetector.domain.EarthquakeDetector;
import com.example.seismicdetector.domain.SeismicSensorManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class MainViewModel_Factory implements Factory<MainViewModel> {
  private final Provider<SeismicSensorManager> sensorManagerProvider;

  private final Provider<EarthquakeDetector> detectorProvider;

  private final Provider<PreferencesManager> preferencesManagerProvider;

  public MainViewModel_Factory(Provider<SeismicSensorManager> sensorManagerProvider,
      Provider<EarthquakeDetector> detectorProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    this.sensorManagerProvider = sensorManagerProvider;
    this.detectorProvider = detectorProvider;
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  @Override
  public MainViewModel get() {
    return newInstance(sensorManagerProvider.get(), detectorProvider.get(), preferencesManagerProvider.get());
  }

  public static MainViewModel_Factory create(Provider<SeismicSensorManager> sensorManagerProvider,
      Provider<EarthquakeDetector> detectorProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new MainViewModel_Factory(sensorManagerProvider, detectorProvider, preferencesManagerProvider);
  }

  public static MainViewModel newInstance(SeismicSensorManager sensorManager,
      EarthquakeDetector detector, PreferencesManager preferencesManager) {
    return new MainViewModel(sensorManager, detector, preferencesManager);
  }
}
