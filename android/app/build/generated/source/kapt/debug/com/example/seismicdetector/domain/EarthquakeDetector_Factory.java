package com.example.seismicdetector.domain;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class EarthquakeDetector_Factory implements Factory<EarthquakeDetector> {
  private final Provider<RealtimePreprocessor> preprocessorProvider;

  private final Provider<SeismicMLModel> mlModelProvider;

  public EarthquakeDetector_Factory(Provider<RealtimePreprocessor> preprocessorProvider,
      Provider<SeismicMLModel> mlModelProvider) {
    this.preprocessorProvider = preprocessorProvider;
    this.mlModelProvider = mlModelProvider;
  }

  @Override
  public EarthquakeDetector get() {
    return newInstance(preprocessorProvider.get(), mlModelProvider.get());
  }

  public static EarthquakeDetector_Factory create(
      Provider<RealtimePreprocessor> preprocessorProvider,
      Provider<SeismicMLModel> mlModelProvider) {
    return new EarthquakeDetector_Factory(preprocessorProvider, mlModelProvider);
  }

  public static EarthquakeDetector newInstance(RealtimePreprocessor preprocessor,
      SeismicMLModel mlModel) {
    return new EarthquakeDetector(preprocessor, mlModel);
  }
}
