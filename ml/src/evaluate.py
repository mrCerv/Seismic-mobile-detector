"""Evaluation script: computes metrics and saves report + plots."""

import json
import os
import sys
from pathlib import Path

import matplotlib
matplotlib.use("Agg")  # non-interactive backend for headless environments
import matplotlib.pyplot as plt
import numpy as np
import tensorflow as tf
import yaml
from sklearn.metrics import (
    ConfusionMatrixDisplay,
    auc,
    confusion_matrix,
    f1_score,
    precision_score,
    recall_score,
    roc_curve,
)

sys.path.insert(0, str(Path(__file__).parent))
from data_loader import create_train_val_test_split

CONFIG_PATH = Path(__file__).parents[1] / "configs" / "training_config.yaml"
MODEL_PATH = Path(__file__).parents[1] / "models" / "best_model.keras"
RESULTS_DIR = Path(__file__).parents[1] / "results"

INTENSITY_LABELS = [
    "Not Perceived",
    "Instrumental",
    "Weak",
    "Light",
    "Moderate",
    "Strong",
]


def _rmse(y_true: np.ndarray, y_pred: np.ndarray) -> float:
    return float(np.sqrt(np.mean((y_true - y_pred) ** 2)))


def _mae(y_true: np.ndarray, y_pred: np.ndarray) -> float:
    return float(np.mean(np.abs(y_true - y_pred)))


def evaluate(model: tf.keras.Model, test_gen) -> dict:
    """Run model on the test generator and collect raw predictions."""
    all_x, true_labels = {}, {
        "is_earthquake": [],
        "intensity": [],
        "pga": [],
        "dominant_freq": [],
        "duration": [],
    }
    pred_labels = {k: [] for k in true_labels}

    for batch_x, batch_y in test_gen:
        preds = model.predict(batch_x, verbose=0)
        # preds order: is_earthquake, intensity, pga, dominant_freq, duration
        pred_labels["is_earthquake"].extend(preds[0].flatten().tolist())
        pred_labels["intensity"].extend(preds[1].tolist())
        pred_labels["pga"].extend(preds[2].flatten().tolist())
        pred_labels["dominant_freq"].extend(preds[3].flatten().tolist())
        pred_labels["duration"].extend(preds[4].flatten().tolist())

        true_labels["is_earthquake"].extend(batch_y["is_earthquake"].flatten().tolist())
        true_labels["intensity"].extend(np.argmax(batch_y["intensity"], axis=1).tolist())
        true_labels["pga"].extend(batch_y["pga"].flatten().tolist())
        true_labels["dominant_freq"].extend(batch_y["dominant_freq"].flatten().tolist())
        true_labels["duration"].extend(batch_y["duration"].flatten().tolist())

    return true_labels, pred_labels


def compute_classification_metrics(y_true: np.ndarray, y_prob: np.ndarray) -> dict:
    y_pred = (y_prob >= 0.5).astype(int)
    fpr, tpr, _ = roc_curve(y_true, y_prob)
    roc_auc = auc(fpr, tpr)
    return {
        "precision": float(precision_score(y_true, y_pred, zero_division=0)),
        "recall": float(recall_score(y_true, y_pred, zero_division=0)),
        "f1": float(f1_score(y_true, y_pred, zero_division=0)),
        "auc_roc": float(roc_auc),
    }, fpr, tpr


def plot_confusion_matrix(y_true: np.ndarray, y_pred_int: np.ndarray, out_path: Path) -> None:
    cm = confusion_matrix(y_true, y_pred_int, labels=list(range(6)))
    disp = ConfusionMatrixDisplay(confusion_matrix=cm, display_labels=INTENSITY_LABELS)
    fig, ax = plt.subplots(figsize=(9, 7))
    disp.plot(ax=ax, xticks_rotation=45, colorbar=False)
    ax.set_title("Intensity Confusion Matrix")
    fig.tight_layout()
    fig.savefig(out_path, dpi=150)
    plt.close(fig)


def plot_roc_curve(fpr: np.ndarray, tpr: np.ndarray, roc_auc: float, out_path: Path) -> None:
    fig, ax = plt.subplots(figsize=(7, 6))
    ax.plot(fpr, tpr, label=f"AUC = {roc_auc:.3f}")
    ax.plot([0, 1], [0, 1], "k--", linewidth=0.8)
    ax.set_xlabel("False Positive Rate")
    ax.set_ylabel("True Positive Rate")
    ax.set_title("ROC Curve — is_earthquake")
    ax.legend(loc="lower right")
    fig.tight_layout()
    fig.savefig(out_path, dpi=150)
    plt.close(fig)


def main() -> None:
    RESULTS_DIR.mkdir(parents=True, exist_ok=True)

    cfg = yaml.safe_load(open(CONFIG_PATH))
    d = cfg["data"]
    repo_root = Path(__file__).parents[2]

    stead_dir = repo_root / d["stead_dir"]
    stead_paths = sorted(stead_dir.glob("*.hdf5")) + sorted(stead_dir.glob("*.h5"))
    phyphox_root = str(repo_root / d["phyphox_dir"])

    _, _, test_gen = create_train_val_test_split(
        stead_paths=[str(p) for p in stead_paths],
        phyphox_root=phyphox_root,
        val_ratio=d["val_ratio"],
        test_ratio=d["test_ratio"],
        batch_size=cfg["training"]["batch_size"],
    )

    print(f"Loading model from {MODEL_PATH} ...")
    model = tf.keras.models.load_model(str(MODEL_PATH))

    true_labels, pred_labels = evaluate(model, test_gen)

    y_true_eq = np.array(true_labels["is_earthquake"])
    y_prob_eq = np.array(pred_labels["is_earthquake"])

    cls_metrics, fpr, tpr = compute_classification_metrics(y_true_eq, y_prob_eq)

    y_true_int = np.array(true_labels["intensity"])
    y_pred_int = np.argmax(pred_labels["intensity"], axis=1)
    intensity_f1 = float(f1_score(y_true_int, y_pred_int, average="macro", zero_division=0))

    reg_metrics = {}
    for head in ("pga", "dominant_freq", "duration"):
        yt = np.array(true_labels[head])
        yp = np.array(pred_labels[head])
        reg_metrics[head] = {"mae": _mae(yt, yp), "rmse": _rmse(yt, yp)}

    report = {
        "is_earthquake": cls_metrics,
        "intensity": {"macro_f1": intensity_f1},
        "regression": reg_metrics,
    }

    report_path = RESULTS_DIR / "evaluation_report.json"
    with open(report_path, "w") as f:
        json.dump(report, f, indent=2)
    print(f"Report saved to {report_path}")

    plot_confusion_matrix(y_true_int, y_pred_int, RESULTS_DIR / "confusion_matrix.png")
    plot_roc_curve(fpr, tpr, cls_metrics["auc_roc"], RESULTS_DIR / "roc_curve.png")
    print("Plots saved to", RESULTS_DIR)

    # Print summary
    print("\n=== Evaluation Summary ===")
    print(f"is_earthquake  precision={cls_metrics['precision']:.3f}  "
          f"recall={cls_metrics['recall']:.3f}  "
          f"F1={cls_metrics['f1']:.3f}  "
          f"AUC={cls_metrics['auc_roc']:.3f}")
    print(f"intensity      macro-F1={intensity_f1:.3f}")
    for head, m in reg_metrics.items():
        print(f"{head:<15} MAE={m['mae']:.4f}  RMSE={m['rmse']:.4f}")


if __name__ == "__main__":
    main()
