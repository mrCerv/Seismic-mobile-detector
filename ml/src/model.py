import tensorflow as tf


def build_model(input_length: int = 1000, n_channels: int = 6) -> tf.keras.Model:
    """CNN-1D multi-task model for seismic event detection and characterization."""
    inputs = tf.keras.Input(shape=(input_length, n_channels), name="seismic_input")

    # Block 1: 64 filters, kernel 7
    x = tf.keras.layers.Conv1D(64, 7, padding="same")(inputs)
    x = tf.keras.layers.BatchNormalization()(x)
    x = tf.keras.layers.ReLU()(x)
    x = tf.keras.layers.Conv1D(64, 7, padding="same")(x)
    x = tf.keras.layers.BatchNormalization()(x)
    x = tf.keras.layers.ReLU()(x)
    x = tf.keras.layers.MaxPooling1D(2)(x)

    # Block 2: 128 filters, kernel 5
    x = tf.keras.layers.Conv1D(128, 5, padding="same")(x)
    x = tf.keras.layers.BatchNormalization()(x)
    x = tf.keras.layers.ReLU()(x)
    x = tf.keras.layers.Conv1D(128, 5, padding="same")(x)
    x = tf.keras.layers.BatchNormalization()(x)
    x = tf.keras.layers.ReLU()(x)
    x = tf.keras.layers.MaxPooling1D(2)(x)

    # Block 3: 256 filters, kernel 3
    x = tf.keras.layers.Conv1D(256, 3, padding="same")(x)
    x = tf.keras.layers.BatchNormalization()(x)
    x = tf.keras.layers.ReLU()(x)
    x = tf.keras.layers.Conv1D(256, 3, padding="same")(x)
    x = tf.keras.layers.BatchNormalization()(x)
    x = tf.keras.layers.ReLU()(x)
    x = tf.keras.layers.GlobalAveragePooling1D()(x)

    # Shared dense layers
    x = tf.keras.layers.Dense(512, activation="relu")(x)
    x = tf.keras.layers.Dropout(0.3)(x)
    x = tf.keras.layers.Dense(256, activation="relu")(x)

    # Output heads
    is_earthquake = tf.keras.layers.Dense(1, activation="sigmoid", name="is_earthquake")(x)
    intensity = tf.keras.layers.Dense(6, activation="softmax", name="intensity")(x)
    pga = tf.keras.layers.Dense(1, activation="linear", name="pga")(x)
    dominant_freq = tf.keras.layers.Dense(1, activation="linear", name="dominant_freq")(x)
    duration = tf.keras.layers.Dense(1, activation="linear", name="duration")(x)

    model = tf.keras.Model(
        inputs=inputs,
        outputs=[is_earthquake, intensity, pga, dominant_freq, duration],
        name="seismic_cnn1d",
    )
    return model
