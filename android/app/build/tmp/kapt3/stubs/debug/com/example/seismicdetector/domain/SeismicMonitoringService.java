package com.example.seismicdetector.domain;

@dagger.hilt.android.AndroidEntryPoint()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000l\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\b\u0007\u0018\u0000 42\u00020\u0001:\u00014B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010%\u001a\u00020&2\u0006\u0010\'\u001a\u00020(H\u0002J\u0014\u0010)\u001a\u0004\u0018\u00010*2\b\u0010+\u001a\u0004\u0018\u00010,H\u0016J\b\u0010-\u001a\u00020&H\u0016J\b\u0010.\u001a\u00020&H\u0016J\"\u0010/\u001a\u0002002\b\u0010+\u001a\u0004\u0018\u00010,2\u0006\u00101\u001a\u0002002\u0006\u00102\u001a\u000200H\u0016J\b\u00103\u001a\u00020&H\u0002R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0006\u001a\u00020\u00078\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001e\u0010\f\u001a\u00020\r8\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001e\u0010\u0012\u001a\u00020\u00138\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001e\u0010\u0018\u001a\u00020\u00198\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u001b\"\u0004\b\u001c\u0010\u001dR\u000e\u0010\u001e\u001a\u00020\u001fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020!X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\"\u001a\b\u0018\u00010#R\u00020$X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u00065"}, d2 = {"Lcom/example/seismicdetector/domain/SeismicMonitoringService;", "Landroid/app/Service;", "()V", "collectingJob", "Lkotlinx/coroutines/Job;", "detectionJob", "detector", "Lcom/example/seismicdetector/domain/EarthquakeDetector;", "getDetector", "()Lcom/example/seismicdetector/domain/EarthquakeDetector;", "setDetector", "(Lcom/example/seismicdetector/domain/EarthquakeDetector;)V", "notificationManager", "Lcom/example/seismicdetector/ui/SeismicNotificationManager;", "getNotificationManager", "()Lcom/example/seismicdetector/ui/SeismicNotificationManager;", "setNotificationManager", "(Lcom/example/seismicdetector/ui/SeismicNotificationManager;)V", "repository", "Lcom/example/seismicdetector/data/SeismicRepository;", "getRepository", "()Lcom/example/seismicdetector/data/SeismicRepository;", "setRepository", "(Lcom/example/seismicdetector/data/SeismicRepository;)V", "sensorManager", "Lcom/example/seismicdetector/domain/SeismicSensorManager;", "getSensorManager", "()Lcom/example/seismicdetector/domain/SeismicSensorManager;", "setSensorManager", "(Lcom/example/seismicdetector/domain/SeismicSensorManager;)V", "serviceJob", "Lkotlinx/coroutines/CompletableJob;", "serviceScope", "Lkotlinx/coroutines/CoroutineScope;", "wakeLock", "Landroid/os/PowerManager$WakeLock;", "Landroid/os/PowerManager;", "handleDetection", "", "result", "Lcom/example/seismicdetector/domain/DetectionResult$EarthquakeDetected;", "onBind", "Landroid/os/IBinder;", "intent", "Landroid/content/Intent;", "onCreate", "onDestroy", "onStartCommand", "", "flags", "startId", "startMonitoring", "Companion", "app_debug"})
public final class SeismicMonitoringService extends android.app.Service {
    @javax.inject.Inject()
    public com.example.seismicdetector.domain.SeismicSensorManager sensorManager;
    @javax.inject.Inject()
    public com.example.seismicdetector.domain.EarthquakeDetector detector;
    @javax.inject.Inject()
    public com.example.seismicdetector.data.SeismicRepository repository;
    @javax.inject.Inject()
    public com.example.seismicdetector.ui.SeismicNotificationManager notificationManager;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CompletableJob serviceJob = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope serviceScope = null;
    @org.jetbrains.annotations.Nullable()
    private android.os.PowerManager.WakeLock wakeLock;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job collectingJob;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job detectionJob;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String WAKE_LOCK_TAG = "SeismicDetector::ServiceWakeLock";
    @org.jetbrains.annotations.NotNull()
    public static final com.example.seismicdetector.domain.SeismicMonitoringService.Companion Companion = null;
    
    public SeismicMonitoringService() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.seismicdetector.domain.SeismicSensorManager getSensorManager() {
        return null;
    }
    
    public final void setSensorManager(@org.jetbrains.annotations.NotNull()
    com.example.seismicdetector.domain.SeismicSensorManager p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.seismicdetector.domain.EarthquakeDetector getDetector() {
        return null;
    }
    
    public final void setDetector(@org.jetbrains.annotations.NotNull()
    com.example.seismicdetector.domain.EarthquakeDetector p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.seismicdetector.data.SeismicRepository getRepository() {
        return null;
    }
    
    public final void setRepository(@org.jetbrains.annotations.NotNull()
    com.example.seismicdetector.data.SeismicRepository p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.seismicdetector.ui.SeismicNotificationManager getNotificationManager() {
        return null;
    }
    
    public final void setNotificationManager(@org.jetbrains.annotations.NotNull()
    com.example.seismicdetector.ui.SeismicNotificationManager p0) {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public android.os.IBinder onBind(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent) {
        return null;
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    @java.lang.Override()
    public int onStartCommand(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent, int flags, int startId) {
        return 0;
    }
    
    private final void startMonitoring() {
    }
    
    private final void handleDetection(com.example.seismicdetector.domain.DetectionResult.EarthquakeDetected result) {
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/example/seismicdetector/domain/SeismicMonitoringService$Companion;", "", "()V", "WAKE_LOCK_TAG", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}