import numpy as np
from scipy.signal import butter, sosfilt


def detrend_signal(data: np.ndarray) -> np.ndarray:
    """Removes linear trend from signal."""
    n = len(data)
    x = np.arange(n)
    slope, intercept = np.polyfit(x, data, 1)
    return data - (slope * x + intercept)


def apply_bandpass_filter(
    data: np.ndarray,
    lowcut: float = 0.5,
    highcut: float = 25.0,
    fs: float = 100.0,
    order: int = 4,
) -> np.ndarray:
    """Applies a Butterworth bandpass filter."""
    # Note: Using order/2 since bandpass doubles the order in scipy
    sos = butter(order // 2, [lowcut, highcut], btype="band", fs=fs, output="sos")
    return sosfilt(sos, data)


def z_score_normalize(data: np.ndarray) -> np.ndarray:
    """Applies Z-score normalization."""
    std = np.std(data)
    if std < 1e-6:
        return np.zeros_like(data)
    return (data - np.mean(data)) / std


def compute_dominant_freq(signal: np.ndarray, fs: float = 100.0) -> float:
    """FFT-based dominant frequency (Hz) in the 0.5–25 Hz seismic band."""
    n = len(signal)
    if n == 0:
        return 1.0
    fft_mag = np.abs(np.fft.rfft(signal.astype(np.float64)))
    freqs = np.fft.rfftfreq(n, d=1.0 / fs)
    mask = (freqs >= 0.5) & (freqs <= 25.0)
    if not mask.any():
        return 1.0
    dominant = float(freqs[mask][np.argmax(fft_mag[mask])])
    return dominant if dominant > 0.0 else 1.0


def preprocess_seismic_data(data: np.ndarray, fs: float = 100.0) -> np.ndarray:
    """Full preprocessing pipeline for 3-channel seismic data: Detrend -> Bandpass -> Z-Score."""
    processed = np.zeros_like(data)
    for i in range(3):
        channel = data[:, i]
        channel = detrend_signal(channel)
        channel = apply_bandpass_filter(channel, fs=fs)
        channel = z_score_normalize(channel)
        processed[:, i] = channel
    return processed


def preprocess_hybrid_data(data: np.ndarray, fs: float = 100.0) -> np.ndarray:
    """
    Preprocessing for 6-channel hybrid data (linear acceleration + gyroscope).

    Channel layout:
      0-2: linear acceleration (m/s²) — detrend + bandpass(0.5-25 Hz) + z-score
      3-5: gyroscope (rad/s)          — detrend + bandpass(0.1-25 Hz) + z-score

    Zero-valued channels (e.g. STEAD gyro padding) pass through as zeros via
    the z_score_normalize guard (std < 1e-6 → zeros).
    """
    assert data.shape == (1000, 6), f"Expected (1000, 6), got {data.shape}"
    processed = np.zeros_like(data, dtype=np.float32)

    for i in range(3):
        ch = data[:, i].astype(np.float64)
        ch = detrend_signal(ch)
        ch = apply_bandpass_filter(ch, lowcut=0.5, highcut=25.0, fs=fs)
        processed[:, i] = z_score_normalize(ch)

    for i in range(3, 6):
        ch = data[:, i].astype(np.float64)
        ch = detrend_signal(ch)
        ch = apply_bandpass_filter(ch, lowcut=0.1, highcut=25.0, fs=fs)
        processed[:, i] = z_score_normalize(ch)

    return processed
