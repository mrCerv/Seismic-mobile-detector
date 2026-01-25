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
public final class SeismicMLModel_Factory implements Factory<SeismicMLModel> {
  private final Provider<Context> contextProvider;

  public SeismicMLModel_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SeismicMLModel get() {
    return newInstance(contextProvider.get());
  }

  public static SeismicMLModel_Factory create(Provider<Context> contextProvider) {
    return new SeismicMLModel_Factory(contextProvider);
  }

  public static SeismicMLModel newInstance(Context context) {
    return new SeismicMLModel(context);
  }
}
