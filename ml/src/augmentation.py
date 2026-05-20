import numpy as np


def add_gaussian_noise(data: np.ndarray, snr_db: float) -> np.ndarray:
    """Add white Gaussian noise calibrated to a target SNR (dB) per channel."""
    result = np.empty_like(data)
    for ch in range(data.shape[1]):
        signal = data[:, ch]
        signal_power = np.mean(signal ** 2)
        if signal_power < 1e-10:
            result[:, ch] = signal
            continue
        snr_linear = 10 ** (snr_db / 10.0)
        noise_power = signal_power / snr_linear
        noise = np.random.normal(0.0, np.sqrt(noise_power), size=signal.shape)
        result[:, ch] = signal + noise
    return result


def time_shift(data: np.ndarray, max_shift: int = 50) -> np.ndarray:
    """Circularly shift the signal along the time axis by a random amount."""
    shift = np.random.randint(-max_shift, max_shift + 1)
    return np.roll(data, shift, axis=0)


def random_gyro_dropout(data: np.ndarray) -> np.ndarray:
    """Zero out gyroscope channels (3–5) to simulate STEAD-style seismograph data."""
    result = data.copy()
    result[:, 3:] = 0.0
    return result


def apply_augmentation(data: np.ndarray, label: dict) -> tuple:
    """Randomly apply augmentations; each has 50 % probability except gyro dropout (30 %)."""
    label = {k: (v.copy() if isinstance(v, np.ndarray) else v) for k, v in label.items()}

    if np.random.rand() < 0.5:
        snr_db = np.random.uniform(10.0, 30.0)
        data = add_gaussian_noise(data, snr_db)

    if np.random.rand() < 0.5:
        factor = np.float32(np.random.uniform(0.7, 1.3))
        data = data * factor
        # PGA scales linearly with amplitude — keep label consistent.
        label["pga"] = label["pga"] * factor

    if np.random.rand() < 0.5:
        data = time_shift(data)

    # Drop gyro channels to teach the model to handle STEAD samples (zero gyro).
    if np.random.rand() < 0.3:
        data = random_gyro_dropout(data)

    return data, label
