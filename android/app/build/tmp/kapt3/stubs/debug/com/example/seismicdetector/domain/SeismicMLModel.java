package com.example.seismicdetector.domain;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u0014\n\u0002\b\u0005\n\u0002\u0010%\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u00162\u0006\u0010\u0017\u001a\u00020\fJ\b\u0010\u0018\u001a\u00020\u0019H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\rR\u0016\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\rR\u0016\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\rR\u0016\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\rR\u001a\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00010\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\r\u00a8\u0006\u001a"}, d2 = {"Lcom/example/seismicdetector/domain/SeismicMLModel;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "inputBuffer", "Ljava/nio/ByteBuffer;", "kotlin.jvm.PlatformType", "interpreter", "Lorg/tensorflow/lite/Interpreter;", "outputDuration", "", "", "[[F", "outputFreq", "outputIntensity", "outputIsEarthquake", "outputMap", "", "", "outputPga", "doInference", "Lcom/example/seismicdetector/domain/ModelOutput;", "preprocessedData", "initializeInterpreter", "", "app_debug"})
public final class SeismicMLModel {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.Nullable()
    private org.tensorflow.lite.Interpreter interpreter;
    private final java.nio.ByteBuffer inputBuffer = null;
    @org.jetbrains.annotations.NotNull()
    private final float[][] outputIsEarthquake = null;
    @org.jetbrains.annotations.NotNull()
    private final float[][] outputIntensity = null;
    @org.jetbrains.annotations.NotNull()
    private final float[][] outputPga = null;
    @org.jetbrains.annotations.NotNull()
    private final float[][] outputFreq = null;
    @org.jetbrains.annotations.NotNull()
    private final float[][] outputDuration = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.Integer, java.lang.Object> outputMap = null;
    
    @javax.inject.Inject()
    public SeismicMLModel(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    private final void initializeInterpreter() {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.seismicdetector.domain.ModelOutput doInference(@org.jetbrains.annotations.NotNull()
    float[] preprocessedData) {
        return null;
    }
}