"""Convert a trained Keras model to TFLite (float32 or INT8-quantized)."""

import os
import shutil
import sys
from pathlib import Path
from typing import Optional

import numpy as np
import tensorflow as tf

sys.path.insert(0, str(Path(__file__).parent))
from model import build_model

MODEL_DIR = Path(__file__).parents[1] / "models"
TFLITE_SRC = MODEL_DIR / "seismic_model.tflite"
ANDROID_ASSETS = Path(__file__).parents[2] / "android" / "app" / "src" / "main" / "assets"

KERAS_MODEL_PATH = MODEL_DIR / "best_model.keras"


def _representative_dataset_gen(n_samples: int = 100):
    """Yield random representative inputs for INT8 calibration."""
    for _ in range(n_samples):
        yield [np.random.randn(1, 1000, 6).astype(np.float32)]


def convert_to_tflite(
    keras_model_path: Path = KERAS_MODEL_PATH,
    quantize_int8: bool = False,
    output_path: Path = TFLITE_SRC,
) -> Path:
    """
    Convert a saved Keras model to TFLite.

    Args:
        keras_model_path: Path to the .keras model file.
        quantize_int8: If True, apply full-integer (INT8) post-training quantization.
        output_path: Destination .tflite file path.
    """
    print(f"Loading model from {keras_model_path} ...")
    model = tf.keras.models.load_model(str(keras_model_path))

    converter = tf.lite.TFLiteConverter.from_keras_model(model)

    if quantize_int8:
        converter.optimizations = [tf.lite.Optimize.DEFAULT]
        converter.representative_dataset = _representative_dataset_gen
        converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS_INT8]
        converter.inference_input_type = tf.int8
        converter.inference_output_type = tf.int8
        print("INT8 quantization enabled.")

    tflite_model = converter.convert()

    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_bytes(tflite_model)
    size_kb = output_path.stat().st_size / 1024
    print(f"TFLite model saved to {output_path}  ({size_kb:.1f} KB)")

    # Verify input/output shapes
    interpreter = tf.lite.Interpreter(model_path=str(output_path))
    interpreter.allocate_tensors()
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()
    print(f"Input shape  : {input_details[0]['shape']}")
    print("Output shapes:")
    for od in output_details:
        print(f"  {od['name']:<30} {od['shape']}")

    # Expected: input [1, 1000, 6], 5 outputs
    expected_input = [1, 1000, 6]
    actual_input = list(input_details[0]["shape"])
    assert actual_input == expected_input, (
        f"Unexpected input shape {actual_input}, expected {expected_input}"
    )
    assert len(output_details) == 5, (
        f"Expected 5 output tensors, got {len(output_details)}"
    )

    return output_path


def copy_to_android(tflite_path: Path, assets_dir: Path = ANDROID_ASSETS) -> Path:
    assets_dir.mkdir(parents=True, exist_ok=True)
    dest = assets_dir / "seismic_model.tflite"
    shutil.copy2(tflite_path, dest)
    print(f"Copied to Android assets: {dest}")
    return dest


def main(quantize_int8: bool = False) -> None:
    MODEL_DIR.mkdir(parents=True, exist_ok=True)

    tflite_path = convert_to_tflite(
        keras_model_path=KERAS_MODEL_PATH,
        quantize_int8=quantize_int8,
        output_path=TFLITE_SRC,
    )
    copy_to_android(tflite_path)


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser(description="Convert trained model to TFLite.")
    parser.add_argument("--int8", action="store_true", help="Apply INT8 quantization.")
    args = parser.parse_args()
    main(quantize_int8=args.int8)
