package com.example.seismicdetector.domain;

import com.example.seismicdetector.data.SeismicRepository;
import com.example.seismicdetector.ui.SeismicNotificationManager;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class SeismicMonitoringService_MembersInjector implements MembersInjector<SeismicMonitoringService> {
  private final Provider<SeismicSensorManager> sensorManagerProvider;

  private final Provider<EarthquakeDetector> detectorProvider;

  private final Provider<SeismicRepository> repositoryProvider;

  private final Provider<SeismicNotificationManager> notificationManagerProvider;

  public SeismicMonitoringService_MembersInjector(
      Provider<SeismicSensorManager> sensorManagerProvider,
      Provider<EarthquakeDetector> detectorProvider, Provider<SeismicRepository> repositoryProvider,
      Provider<SeismicNotificationManager> notificationManagerProvider) {
    this.sensorManagerProvider = sensorManagerProvider;
    this.detectorProvider = detectorProvider;
    this.repositoryProvider = repositoryProvider;
    this.notificationManagerProvider = notificationManagerProvider;
  }

  public static MembersInjector<SeismicMonitoringService> create(
      Provider<SeismicSensorManager> sensorManagerProvider,
      Provider<EarthquakeDetector> detectorProvider, Provider<SeismicRepository> repositoryProvider,
      Provider<SeismicNotificationManager> notificationManagerProvider) {
    return new SeismicMonitoringService_MembersInjector(sensorManagerProvider, detectorProvider, repositoryProvider, notificationManagerProvider);
  }

  @Override
  public void injectMembers(SeismicMonitoringService instance) {
    injectSensorManager(instance, sensorManagerProvider.get());
    injectDetector(instance, detectorProvider.get());
    injectRepository(instance, repositoryProvider.get());
    injectNotificationManager(instance, notificationManagerProvider.get());
  }

  @InjectedFieldSignature("com.example.seismicdetector.domain.SeismicMonitoringService.sensorManager")
  public static void injectSensorManager(SeismicMonitoringService instance,
      SeismicSensorManager sensorManager) {
    instance.sensorManager = sensorManager;
  }

  @InjectedFieldSignature("com.example.seismicdetector.domain.SeismicMonitoringService.detector")
  public static void injectDetector(SeismicMonitoringService instance,
      EarthquakeDetector detector) {
    instance.detector = detector;
  }

  @InjectedFieldSignature("com.example.seismicdetector.domain.SeismicMonitoringService.repository")
  public static void injectRepository(SeismicMonitoringService instance,
      SeismicRepository repository) {
    instance.repository = repository;
  }

  @InjectedFieldSignature("com.example.seismicdetector.domain.SeismicMonitoringService.notificationManager")
  public static void injectNotificationManager(SeismicMonitoringService instance,
      SeismicNotificationManager notificationManager) {
    instance.notificationManager = notificationManager;
  }
}
