package com.example.seismicdetector.ui.home;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B1\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010\u0014\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u000b\u0010\u0015\u001a\u0004\u0018\u00010\tH\u00c6\u0003J5\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tH\u00c6\u0001J\u0013\u0010\u0017\u001a\u00020\u00032\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0019\u001a\u00020\u001aH\u00d6\u0001J\t\u0010\u001b\u001a\u00020\u001cH\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\rR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0013\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011\u00a8\u0006\u001d"}, d2 = {"Lcom/example/seismicdetector/ui/home/HomeUiState;", "", "isMonitoring", "", "currentSampleRate", "", "lastDetection", "Lcom/example/seismicdetector/domain/DetectionResult$EarthquakeDetected;", "sensorData", "Lcom/example/seismicdetector/domain/SensorDataBatch;", "(ZFLcom/example/seismicdetector/domain/DetectionResult$EarthquakeDetected;Lcom/example/seismicdetector/domain/SensorDataBatch;)V", "getCurrentSampleRate", "()F", "()Z", "getLastDetection", "()Lcom/example/seismicdetector/domain/DetectionResult$EarthquakeDetected;", "getSensorData", "()Lcom/example/seismicdetector/domain/SensorDataBatch;", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "", "toString", "", "app_debug"})
public final class HomeUiState {
    private final boolean isMonitoring = false;
    private final float currentSampleRate = 0.0F;
    @org.jetbrains.annotations.Nullable()
    private final com.example.seismicdetector.domain.DetectionResult.EarthquakeDetected lastDetection = null;
    @org.jetbrains.annotations.Nullable()
    private final com.example.seismicdetector.domain.SensorDataBatch sensorData = null;
    
    public HomeUiState(boolean isMonitoring, float currentSampleRate, @org.jetbrains.annotations.Nullable()
    com.example.seismicdetector.domain.DetectionResult.EarthquakeDetected lastDetection, @org.jetbrains.annotations.Nullable()
    com.example.seismicdetector.domain.SensorDataBatch sensorData) {
        super();
    }
    
    public final boolean isMonitoring() {
        return false;
    }
    
    public final float getCurrentSampleRate() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.seismicdetector.domain.DetectionResult.EarthquakeDetected getLastDetection() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.seismicdetector.domain.SensorDataBatch getSensorData() {
        return null;
    }
    
    public HomeUiState() {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    public final float component2() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.seismicdetector.domain.DetectionResult.EarthquakeDetected component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.seismicdetector.domain.SensorDataBatch component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.seismicdetector.ui.home.HomeUiState copy(boolean isMonitoring, float currentSampleRate, @org.jetbrains.annotations.Nullable()
    com.example.seismicdetector.domain.DetectionResult.EarthquakeDetected lastDetection, @org.jetbrains.annotations.Nullable()
    com.example.seismicdetector.domain.SensorDataBatch sensorData) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}