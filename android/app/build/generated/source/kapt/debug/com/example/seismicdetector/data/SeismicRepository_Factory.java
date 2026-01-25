package com.example.seismicdetector.data;

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
public final class SeismicRepository_Factory implements Factory<SeismicRepository> {
  private final Provider<DetectionDao> detectionDaoProvider;

  public SeismicRepository_Factory(Provider<DetectionDao> detectionDaoProvider) {
    this.detectionDaoProvider = detectionDaoProvider;
  }

  @Override
  public SeismicRepository get() {
    return newInstance(detectionDaoProvider.get());
  }

  public static SeismicRepository_Factory create(Provider<DetectionDao> detectionDaoProvider) {
    return new SeismicRepository_Factory(detectionDaoProvider);
  }

  public static SeismicRepository newInstance(DetectionDao detectionDao) {
    return new SeismicRepository(detectionDao);
  }
}
