# STEAD Dataset — Come inserire i dati

Scarica i file HDF5 del dataset STEAD e inseriscili qui:

```
data/raw/stead/
├── chunk1.hdf5   (o qualsiasi nome — lo script cerca *.hdf5 e *.h5)
├── chunk2.hdf5
└── ...
```

## Struttura HDF5 attesa

STEAD usa questa struttura interna (standard):

```
chunk.hdf5
├── earthquake/
│   └── local/
│       ├── <event_id>/
│       │   ├── waveforms   shape (6000, 3)  — canali Z, N, E a 100 Hz
│       │   └── attrs: p_peak_ground_velocity, coda_end_sample, ...
│       └── ...
└── non_earthquake/
    └── noise/
        ├── <noise_id>/
        │   └── waveforms   shape (6000, 3)
        └── ...
```

## Download

Il dataset STEAD è disponibile su:
- **NCEDC**: https://ncedc.org/ncedc/doi/10.7932/STEAD
- **GitHub** (metadati + link): https://github.com/smousavi05/STEAD

Il dataset completo è ~70 GB. Per sviluppo iniziale è sufficiente 1-2 chunk (~1 GB).

## Citazione (obbligatoria se usi questi dati in ricerca)

```
Mousavi, S.M., Sheng, Y., Zhu, W., Beroza G.C., (2019).
STanford EArthquake Dataset (STEAD): A Global Data Set of Seismic Signals for AI.
IEEE Access, doi:10.1109/ACCESS.2019.2947848
```

## Note sul campionamento

`scripts/prepare_dataset.py` campiona casualmente da STEAD per pareggiare
la dimensione del tuo dataset Phyphox. Non è necessario usare tutti i file HDF5:
anche un singolo chunk è sufficiente se contiene abbastanza campioni.
