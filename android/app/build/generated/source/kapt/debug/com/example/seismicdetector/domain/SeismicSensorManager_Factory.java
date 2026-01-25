package com.example.seismicdetector.domain;

import android.content.Context;
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
public final class SeismicSensorManager_Factory implements Factory<SeismicSensorManager> {
  private final Provider<Context> contextProvider;

  public SeismicSensorManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SeismicSensorManager get() {
    return newInstance(contextProvider.get());
  }

  public static SeismicSensorManager_Factory create(Provider<Context> contextProvider) {
    return new SeismicSensorManager_Factory(contextProvider);
  }

  public static SeismicSensorManager newInstance(Context context) {
    return new SeismicSensorManager(context);
  }
}
