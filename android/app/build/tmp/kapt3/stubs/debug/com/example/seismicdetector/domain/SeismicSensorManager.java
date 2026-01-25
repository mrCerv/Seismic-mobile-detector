package com.example.seismicdetector.domain;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0014\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010&\u001a\u00020\'H\u0002J\u001a\u0010(\u001a\u00020\'2\b\u0010)\u001a\u0004\u0018\u00010\t2\u0006\u0010*\u001a\u00020\u000bH\u0016J\u0012\u0010+\u001a\u00020\'2\b\u0010,\u001a\u0004\u0018\u00010-H\u0016J\b\u0010.\u001a\u00020\'H\u0002J\u0006\u0010/\u001a\u00020\'J\u0006\u00100\u001a\u00020\'R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0010\u001a\u00020\u0011X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\u000e\u0010\u0016\u001a\u00020\u000bX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u001b\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u0019@BX\u0086\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u000e\u0010\u001d\u001a\u00020\u001eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00070!\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u000e\u0010$\u001a\u00020%X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00061"}, d2 = {"Lcom/example/seismicdetector/domain/SeismicSensorManager;", "Landroid/hardware/SensorEventListener;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "_sensorDataFlow", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lcom/example/seismicdetector/domain/SensorDataBatch;", "accelerometer", "Landroid/hardware/Sensor;", "bufferSize", "", "bufferX", "", "bufferY", "bufferZ", "currentSampleRate", "", "getCurrentSampleRate", "()F", "setCurrentSampleRate", "(F)V", "emitInterval", "headIndex", "isBufferFull", "", "<set-?>", "isMonitoring", "()Z", "lastTimestamp", "", "sampleCounter", "sensorDataFlow", "Lkotlinx/coroutines/flow/SharedFlow;", "getSensorDataFlow", "()Lkotlinx/coroutines/flow/SharedFlow;", "sensorManager", "Landroid/hardware/SensorManager;", "emitCurrentWindow", "", "onAccuracyChanged", "sensor", "accuracy", "onSensorChanged", "event", "Landroid/hardware/SensorEvent;", "resetBuffers", "startMonitoring", "stopMonitoring", "app_debug"})
public final class SeismicSensorManager implements android.hardware.SensorEventListener {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final android.hardware.SensorManager sensorManager = null;
    @org.jetbrains.annotations.Nullable()
    private final android.hardware.Sensor accelerometer = null;
    private final int bufferSize = 1000;
    @org.jetbrains.annotations.NotNull()
    private final float[] bufferX = null;
    @org.jetbrains.annotations.NotNull()
    private final float[] bufferY = null;
    @org.jetbrains.annotations.NotNull()
    private final float[] bufferZ = null;
    private int headIndex = 0;
    private boolean isBufferFull = false;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableSharedFlow<com.example.seismicdetector.domain.SensorDataBatch> _sensorDataFlow = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.SharedFlow<com.example.seismicdetector.domain.SensorDataBatch> sensorDataFlow = null;
    private int sampleCounter = 0;
    private final int emitInterval = 100;
    private boolean isMonitoring = false;
    private float currentSampleRate = 0.0F;
    private long lastTimestamp = 0L;
    
    @javax.inject.Inject()
    public SeismicSensorManager(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.SharedFlow<com.example.seismicdetector.domain.SensorDataBatch> getSensorDataFlow() {
        return null;
    }
    
    public final boolean isMonitoring() {
        return false;
    }
    
    public final float getCurrentSampleRate() {
        return 0.0F;
    }
    
    public final void setCurrentSampleRate(float p0) {
    }
    
    public final void startMonitoring() {
    }
    
    public final void stopMonitoring() {
    }
    
    private final void resetBuffers() {
    }
    
    @java.lang.Override()
    public void onSensorChanged(@org.jetbrains.annotations.Nullable()
    android.hardware.SensorEvent event) {
    }
    
    private final void emitCurrentWindow() {
    }
    
    @java.lang.Override()
    public void onAccuracyChanged(@org.jetbrains.annotations.Nullable()
    android.hardware.Sensor sensor, int accuracy) {
    }
}