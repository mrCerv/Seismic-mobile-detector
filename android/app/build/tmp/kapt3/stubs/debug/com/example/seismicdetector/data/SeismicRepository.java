package com.example.seismicdetector.data;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\"\u0010\u0010\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\u00122\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u0086@\u00a2\u0006\u0002\u0010\u0015R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0016"}, d2 = {"Lcom/example/seismicdetector/data/SeismicRepository;", "", "detectionDao", "Lcom/example/seismicdetector/data/DetectionDao;", "(Lcom/example/seismicdetector/data/DetectionDao;)V", "recentDetections", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/example/seismicdetector/data/DetectionEntity;", "getRecentDetections", "()Lkotlinx/coroutines/flow/Flow;", "cleanupOldDetections", "", "maxAgeMillis", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveDetection", "result", "Lcom/example/seismicdetector/domain/DetectionResult$EarthquakeDetected;", "waveformPath", "", "(Lcom/example/seismicdetector/domain/DetectionResult$EarthquakeDetected;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class SeismicRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.example.seismicdetector.data.DetectionDao detectionDao = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.example.seismicdetector.data.DetectionEntity>> recentDetections = null;
    
    @javax.inject.Inject()
    public SeismicRepository(@org.jetbrains.annotations.NotNull()
    com.example.seismicdetector.data.DetectionDao detectionDao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.example.seismicdetector.data.DetectionEntity>> getRecentDetections() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object saveDetection(@org.jetbrains.annotations.NotNull()
    com.example.seismicdetector.domain.DetectionResult.EarthquakeDetected result, @org.jetbrains.annotations.Nullable()
    java.lang.String waveformPath, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object cleanupOldDetections(long maxAgeMillis, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}