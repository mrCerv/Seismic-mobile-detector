package com.example.seismicdetector.ui;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class SeismicNotificationManager_Factory implements Factory<SeismicNotificationManager> {
  private final Provider<Context> contextProvider;

  public SeismicNotificationManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SeismicNotificationManager get() {
    return newInstance(contextProvider.get());
  }

  public static SeismicNotificationManager_Factory create(Provider<Context> contextProvider) {
    return new SeismicNotificationManager_Factory(contextProvider);
  }

  public static SeismicNotificationManager newInstance(Context context) {
    return new SeismicNotificationManager(context);
  }
}
