package com.example.seismicdetector.di;

import com.example.seismicdetector.data.AppDatabase;
import com.example.seismicdetector.data.DetectionDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideDetectionDaoFactory implements Factory<DetectionDao> {
  private final Provider<AppDatabase> databaseProvider;

  public AppModule_ProvideDetectionDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public DetectionDao get() {
    return provideDetectionDao(databaseProvider.get());
  }

  public static AppModule_ProvideDetectionDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new AppModule_ProvideDetectionDaoFactory(databaseProvider);
  }

  public static DetectionDao provideDetectionDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideDetectionDao(database));
  }
}
