package com.example.seismicdetector.domain;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\u0017\u001a\u00020\t2\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\u0016\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001dH\u0086@\u00a2\u0006\u0002\u0010\u001eR\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\t0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u001a\u0010\u0014\u001a\u00020\u000bX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\r\"\u0004\b\u0016\u0010\u000fR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001f"}, d2 = {"Lcom/example/seismicdetector/domain/EarthquakeDetector;", "", "preprocessor", "Lcom/example/seismicdetector/domain/RealtimePreprocessor;", "mlModel", "Lcom/example/seismicdetector/domain/SeismicMLModel;", "(Lcom/example/seismicdetector/domain/RealtimePreprocessor;Lcom/example/seismicdetector/domain/SeismicMLModel;)V", "_detectionFlow", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lcom/example/seismicdetector/domain/DetectionResult;", "confidenceThreshold", "", "getConfidenceThreshold", "()F", "setConfidenceThreshold", "(F)V", "detectionFlow", "Lkotlinx/coroutines/flow/Flow;", "getDetectionFlow", "()Lkotlinx/coroutines/flow/Flow;", "minPgaThreshold", "getMinPgaThreshold", "setMinPgaThreshold", "evaluatePrediction", "output", "Lcom/example/seismicdetector/domain/ModelOutput;", "processBatch", "", "batch", "Lcom/example/seismicdetector/domain/SensorDataBatch;", "(Lcom/example/seismicdetector/domain/SensorDataBatch;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class EarthquakeDetector {
    @org.jetbrains.annotations.NotNull()
    private final com.example.seismicdetector.domain.RealtimePreprocessor preprocessor = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.seismicdetector.domain.SeismicMLModel mlModel = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableSharedFlow<com.example.seismicdetector.domain.DetectionResult> _detectionFlow = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<com.example.seismicdetector.domain.DetectionResult> detectionFlow = null;
    private float confidenceThreshold = 0.7F;
    private float minPgaThreshold = 0.05F;
    
    @javax.inject.Inject()
    public EarthquakeDetector(@org.jetbrains.annotations.NotNull()
    com.example.seismicdetector.domain.RealtimePreprocessor preprocessor, @org.jetbrains.annotations.NotNull()
    com.example.seismicdetector.domain.SeismicMLModel mlModel) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.example.seismicdetector.domain.DetectionResult> getDetectionFlow() {
        return null;
    }
    
    public final float getConfidenceThreshold() {
        return 0.0F;
    }
    
    public final void setConfidenceThreshold(float p0) {
    }
    
    public final float getMinPgaThreshold() {
        return 0.0F;
    }
    
    public final void setMinPgaThreshold(float p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object processBatch(@org.jetbrains.annotations.NotNull()
    com.example.seismicdetector.domain.SensorDataBatch batch, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final com.example.seismicdetector.domain.DetectionResult evaluatePrediction(com.example.seismicdetector.domain.ModelOutput output) {
        return null;
    }
}