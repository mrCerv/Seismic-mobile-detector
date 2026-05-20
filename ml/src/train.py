"""Training entry-point for the seismic CNN-1D model."""

import os
import sys
from pathlib import Path

import tensorflow as tf
import yaml

# Allow running from repo root or ml/src directly
sys.path.insert(0, str(Path(__file__).parent))

from data_loader import create_train_val_test_split
from model import build_model

CONFIG_PATH = Path(__file__).parents[1] / "configs" / "training_config.yaml"
MODEL_DIR = Path(__file__).parents[1] / "models"


def load_config(path: Path) -> dict:
    with open(path) as f:
        return yaml.safe_load(f)


def build_compiled_model(cfg: dict, loss_weights: dict) -> tf.keras.Model:
    model = build_model()
    model.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=cfg["training"]["learning_rate"]),
        loss={
            "is_earthquake": tf.keras.losses.BinaryCrossentropy(),
            "intensity": tf.keras.losses.CategoricalCrossentropy(),
            "pga": tf.keras.losses.MeanAbsoluteError(),
            "dominant_freq": tf.keras.losses.MeanAbsoluteError(),
            "duration": tf.keras.losses.MeanAbsoluteError(),
        },
        loss_weights=loss_weights,
        metrics={
            "is_earthquake": [tf.keras.metrics.AUC(name="auc"), "accuracy"],
            "intensity": ["accuracy"],
            "pga": [tf.keras.metrics.MeanAbsoluteError(name="mae")],
            "dominant_freq": [tf.keras.metrics.MeanAbsoluteError(name="mae")],
            "duration": [tf.keras.metrics.MeanAbsoluteError(name="mae")],
        },
    )
    return model


def main() -> None:
    cfg = load_config(CONFIG_PATH)
    t = cfg["training"]
    d = cfg["data"]

    MODEL_DIR.mkdir(parents=True, exist_ok=True)
    best_model_path = MODEL_DIR / "best_model.keras"

    # Resolve data paths relative to repo root
    repo_root = Path(__file__).parents[2]
    stead_dir = repo_root / d["stead_path"]
    phyphox_dir = repo_root / d["phyphox_path"]

    stead_paths = sorted(stead_dir.glob("*.hdf5")) + sorted(stead_dir.glob("*.h5"))
    phyphox_paths = sorted(phyphox_dir.glob("*.csv"))

    if not stead_paths and not phyphox_paths:
        print(
            "WARNING: No data files found.  "
            f"Expected STEAD in {stead_dir} and Phyphox CSVs in {phyphox_dir}."
        )
        print("Exiting — please add data before training.")
        sys.exit(1)

    print(f"Found {len(stead_paths)} STEAD file(s) and {len(phyphox_paths)} Phyphox CSV(s).")

    train_gen, val_gen, _ = create_train_val_test_split(
        stead_paths=[str(p) for p in stead_paths],
        phyphox_paths=[str(p) for p in phyphox_paths],
        val_ratio=d["val_ratio"],
        test_ratio=d["test_ratio"],
        batch_size=t["batch_size"],
    )

    loss_weights = t["loss_weights"]
    model = build_compiled_model(cfg, loss_weights)
    model.summary()

    callbacks = [
        tf.keras.callbacks.EarlyStopping(
            monitor="val_loss",
            patience=t["early_stopping_patience"],
            restore_best_weights=True,
        ),
        tf.keras.callbacks.ReduceLROnPlateau(
            monitor="val_loss",
            factor=0.5,
            patience=5,
            min_lr=1e-6,
            verbose=1,
        ),
        tf.keras.callbacks.ModelCheckpoint(
            filepath=str(best_model_path),
            monitor="val_loss",
            save_best_only=True,
            verbose=1,
        ),
    ]

    history = model.fit(
        train_gen,
        validation_data=val_gen,
        epochs=t["epochs"],
        callbacks=callbacks,
    )

    # Training summary
    best_epoch = int(tf.argmin(history.history["val_loss"]).numpy())
    best_val_loss = history.history["val_loss"][best_epoch]
    print("\n=== Training Summary ===")
    print(f"Best epoch : {best_epoch + 1}")
    print(f"Best val_loss : {best_val_loss:.4f}")
    print(f"Model saved to : {best_model_path}")


if __name__ == "__main__":
    main()
