package com.example.seismicdetector.ui.home;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00006\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\u001a\u0010\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0007\u001a.\u0010\u0004\u001a\u00020\u00012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\b\b\u0002\u0010\b\u001a\u00020\tH\u0007\u001a\u0012\u0010\n\u001a\u00020\u00012\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0007\u001a&\u0010\r\u001a\u00020\u00012\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H\u0007\u00a8\u0006\u0013"}, d2 = {"DetectionCard", "", "detection", "Lcom/example/seismicdetector/domain/DetectionResult$EarthquakeDetected;", "HomeScreen", "onNavigateToSettings", "Lkotlin/Function0;", "onNavigateToHistory", "viewModel", "Lcom/example/seismicdetector/ui/home/MainViewModel;", "RealtimeGraphCard", "sensorData", "Lcom/example/seismicdetector/domain/SensorDataBatch;", "StatusCard", "isMonitoring", "", "sampleRate", "", "onToggleMonitoring", "app_debug"})
public final class HomeScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void HomeScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToSettings, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToHistory, @org.jetbrains.annotations.NotNull()
    com.example.seismicdetector.ui.home.MainViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void StatusCard(boolean isMonitoring, float sampleRate, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onToggleMonitoring) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void RealtimeGraphCard(@org.jetbrains.annotations.Nullable()
    com.example.seismicdetector.domain.SensorDataBatch sensorData) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void DetectionCard(@org.jetbrains.annotations.NotNull()
    com.example.seismicdetector.domain.DetectionResult.EarthquakeDetected detection) {
    }
}