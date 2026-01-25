package com.example.seismicdetector.di;

import com.example.seismicdetector.domain.RealtimePreprocessor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class AppModule_ProvideRealtimePreprocessorFactory implements Factory<RealtimePreprocessor> {
  @Override
  public RealtimePreprocessor get() {
    return provideRealtimePreprocessor();
  }

  public static AppModule_ProvideRealtimePreprocessorFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RealtimePreprocessor provideRealtimePreprocessor() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideRealtimePreprocessor());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideRealtimePreprocessorFactory INSTANCE = new AppModule_ProvideRealtimePreprocessorFactory();
  }
}
