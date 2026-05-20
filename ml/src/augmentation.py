import numpy as np


def add_gaussian_noise(data: np.ndarray, snr_db: float) -> np.ndarray:
    """Add white Gaussian noise calibrated to a target SNR (dB) per channel."""
    result = np.empty_like(data)
    for ch in range(data.shape[1]):
        signal = data[:, ch]
        signal_power = np.mean(signal ** 2)
        # Avoid dividing by zero for flat (all-zero) channels
        if signal_power < 1e-10:
            result[:, ch] = signal
            continue
        snr_linear = 10 ** (snr_db / 10.0)
        noise_power = signal_power / snr_linear
        noise = np.random.normal(0.0, np.sqrt(noise_power), size=signal.shape)
        result[:, ch] = signal + noise
    return result


def scale_amplitude(data: np.ndarray, factor_range: tuple = (0.7, 1.3)) -> np.ndarray:
    """Randomly scale all channels by the same factor drawn from factor_range."""
    factor = np.random.uniform(factor_range[0], factor_range[1])
    return data * factor


def time_shift(data: np.ndarray, max_shift: int = 50) -> np.ndarray:
    """Circularly shift the signal along the time axis by a random amount."""
    shift = np.random.randint(-max_shift, max_shift + 1)
    return np.roll(data, shift, axis=0)


def apply_augmentation(data: np.ndarray, label: dict) -> tuple:
    """Randomly apply a combination of augmentations; each has 50% probability."""
    if np.random.rand() < 0.5:
        snr_db = np.random.uniform(10.0, 30.0)
        data = add_gaussian_noise(data, snr_db)
    if np.random.rand() < 0.5:
        data = scale_amplitude(data)
    if np.random.rand() < 0.5:
        data = time_shift(data)
    return data, label
