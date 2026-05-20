"""
Create a mock TFLite model with random weights for Android development.

No training is required — the model is converted immediately after construction.
This lets Android developers iterate on the inference pipeline before real
weights are available.
"""

import sys
from pathlib import Path

import numpy as np
import tensorflow as tf

# Resolve ml/src relative to this script
ML_SRC = Path(__file__).parents[1] / "ml" / "src"
sys.path.insert(0, str(ML_SRC))

from model import build_model

ANDROID_ASSETS = (
    Path(__file__).parents[1] / "android" / "app" / "src" / "main" / "assets"
)
OUTPUT_PATH = ANDROID_ASSETS / "seismic_model.tflite"


def main() -> None:
    ANDROID_ASSETS.mkdir(parents=True, exist_ok=True)

    model = build_model(input_length=1000, n_channels=6)

    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    tflite_model = converter.convert()

    OUTPUT_PATH.write_bytes(tflite_model)
    size_kb = OUTPUT_PATH.stat().st_size / 1024

    # Verify shapes via interpreter
    interpreter = tf.lite.Interpreter(model_path=str(OUTPUT_PATH))
    interpreter.allocate_tensors()
    input_shape = list(interpreter.get_input_details()[0]["shape"])
    output_shapes = [list(od["shape"]) for od in interpreter.get_output_details()]

    print(f"Mock model created at {OUTPUT_PATH}")
    print(f"  Size    : {size_kb:.1f} KB")
    print(f"  Input   : {input_shape}")
    print(f"  Outputs : {output_shapes}")


if __name__ == "__main__":
    main()
