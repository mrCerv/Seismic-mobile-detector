import numpy as np
from scipy.signal import butter, sosfilt

def detrend_signal(data):
    """Removes linear trend from signal."""
    n = len(data)
    x = np.arange(n)
    slope, intercept = np.polyfit(x, data, 1)
    return data - (slope * x + intercept)

def apply_bandpass_filter(data, lowcut=0.5, highcut=25.0, fs=100.0, order=4):
    """Applies a Butterworth bandpass filter."""
    # Note: Using order/2 since bandpass doubles the order in scipy
    sos = butter(order // 2, [lowcut, highcut], btype='band', fs=fs, output='sos')
    return sosfilt(sos, data)

def z_score_normalize(data):
    """Applies Z-score normalization."""
    std = np.std(data)
    if std < 1e-6:
        return np.zeros_like(data)
    return (data - np.mean(data)) / std

def preprocess_seismic_data(data, fs=100.0):
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
