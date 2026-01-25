package com.example.seismicdetector.ui.home;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\b\u0010\u0010\u001a\u00020\u0011H\u0002J\u0006\u0010\u0012\u001a\u00020\u0011R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006\u0013"}, d2 = {"Lcom/example/seismicdetector/ui/home/MainViewModel;", "Landroidx/lifecycle/ViewModel;", "sensorManager", "Lcom/example/seismicdetector/domain/SeismicSensorManager;", "detector", "Lcom/example/seismicdetector/domain/EarthquakeDetector;", "preferencesManager", "Lcom/example/seismicdetector/data/PreferencesManager;", "(Lcom/example/seismicdetector/domain/SeismicSensorManager;Lcom/example/seismicdetector/domain/EarthquakeDetector;Lcom/example/seismicdetector/data/PreferencesManager;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/example/seismicdetector/ui/home/HomeUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "refreshMonitoringState", "", "toggleMonitoring", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class MainViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.seismicdetector.domain.SeismicSensorManager sensorManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.seismicdetector.domain.EarthquakeDetector detector = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.seismicdetector.data.PreferencesManager preferencesManager = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.example.seismicdetector.ui.home.HomeUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.example.seismicdetector.ui.home.HomeUiState> uiState = null;
    
    @javax.inject.Inject()
    public MainViewModel(@org.jetbrains.annotations.NotNull()
    com.example.seismicdetector.domain.SeismicSensorManager sensorManager, @org.jetbrains.annotations.NotNull()
    com.example.seismicdetector.domain.EarthquakeDetector detector, @org.jetbrains.annotations.NotNull()
    com.example.seismicdetector.data.PreferencesManager preferencesManager) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.example.seismicdetector.ui.home.HomeUiState> getUiState() {
        return null;
    }
    
    public final void toggleMonitoring() {
    }
    
    private final void refreshMonitoringState() {
    }
}