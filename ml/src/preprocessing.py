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


def preprocess_seismic_data(data: np.ndarray, fs: float = 100.0) -> np.ndarray:
    """
    Full preprocessing pipeline: Detrend -> Bandpass -> Z-Score.
    Input shape: (N, 3) where columns are X, Y, Z.
    """
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

    Gyroscope uses a lower bandpass floor (0.1 Hz) because slow rotational drift
    can still carry meaningful low-frequency seismic information.
    """
    assert data.shape[1] == 6, f"Expected 6 channels, got {data.shape[1]}"
    processed = np.zeros_like(data, dtype=np.float32)

    # Channels 0-2: linear acceleration
    for i in range(3):
        ch = data[:, i].astype(np.float64)
        ch = detrend_signal(ch)
        ch = apply_bandpass_filter(ch, lowcut=0.5, highcut=25.0, fs=fs)
        ch = z_score_normalize(ch)
        processed[:, i] = ch

    # Channels 3-5: gyroscope
    for i in range(3, 6):
        ch = data[:, i].astype(np.float64)
        ch = detrend_signal(ch)
        ch = apply_bandpass_filter(ch, lowcut=0.1, highcut=25.0, fs=fs)
        ch = z_score_normalize(ch)
        processed[:, i] = ch

    return processed
