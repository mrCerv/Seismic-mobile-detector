# Phyphox Dataset — Come inserire i dati

Inserisci i tuoi file CSV esportati da Phyphox nelle sottocartelle in base alla classe:

```
data/raw/phyphox/
├── earthquake/          ← registrazioni durante eventi sismici reali
│   ├── evento_2024_01_12.csv
│   ├── evento_2024_03_08.csv
│   └── ...
└── noise/               ← registrazioni di rumore ambientale / non-sismico
    ├── camminata_01.csv
    ├── rumore_traffico.csv
    └── ...
```

## Formato CSV atteso

Il file deve contenere queste colonne (i nomi esatti possono variare leggermente):

| Colonna                   | Unità  | Descrizione                          |
|---------------------------|--------|--------------------------------------|
| `time`                    | s      | Timestamp relativo                   |
| `linear_acceleration_x`   | m/s²   | Accelerazione lineare asse X (senza g) |
| `linear_acceleration_y`   | m/s²   | Accelerazione lineare asse Y (senza g) |
| `linear_acceleration_z`   | m/s²   | Accelerazione lineare asse Z (senza g) |
| `gyroscope_x`             | rad/s  | Velocità angolare asse X              |
| `gyroscope_y`             | rad/s  | Velocità angolare asse Y              |
| `gyroscope_z`             | rad/s  | Velocità angolare asse Z              |

> **Frequenza di campionamento**: 100 Hz (impostare su Phyphox)

## Come esportare da Phyphox

1. Apri l'esperimento in Phyphox
2. Menu → **Esporta** → **CSV (comma separated)**
3. Rinomina il file in modo descrittivo (es. `terremoto_ml4_2024.csv`)
4. Copia nella sottocartella corretta (`earthquake/` o `noise/`)

## Suggerimento bilanciamento

Il dataset Phyphox è tipicamente piccolo rispetto a STEAD.
`scripts/prepare_dataset.py` campionerà automaticamente da STEAD lo stesso numero
di esempi del tuo dataset Phyphox, evitando che STEAD domini il training.

Esegui:
```bash
python scripts/prepare_dataset.py
```
