# Privacy Policy / Informativa sulla Privacy

**Seismic Mobile Detector**
*Last updated / Ultimo aggiornamento: 2026-05-20*

---

## Privacy Policy (English)

### 1. Introduction

This Privacy Policy describes how Seismic Mobile Detector ("the Application", "we", "us") collects, uses, stores, and protects information when you use the application. This policy is compliant with the **General Data Protection Regulation (GDPR) — Regulation (EU) 2016/679** and applicable Italian data protection law.

The Application is a citizen science and academic research platform. Our data collection is minimal, strictly anonymous, and limited to what is necessary for seismic research purposes.

---

### 2. Data Controller

The data controller is the Seismic Mobile Detector open-source project. For privacy matters, contact the project via GitHub Issues (label: `privacy`).

---

### 3. Data We Collect

#### 3.1 What We Collect

When you enable the **community network feature** (opt-in), the following data is transmitted to our research database upon detection of a potential seismic event:

| Field | Description | Purpose |
|-------|-------------|---------|
| `timestamp` | UTC timestamp of detection (millisecond precision) | Event correlation across devices |
| `confidence` | ML model confidence score (0.0–1.0) | Research quality filtering |
| `intensity` | Estimated seismic intensity class | Severity classification |
| `pga` | Peak Ground Acceleration in m/s² | Quantitative research metric |
| `approx_location` | Geographic coordinates rounded to ±1 km (~0.01 degree) | Geographic clustering of events |
| `anonymousDeviceId` | One-way hash of a random UUID generated at first install | Deduplication across events from the same device |

The `anonymousDeviceId` is a cryptographic hash (SHA-256) of a random UUID generated when the app is first installed. It is not derived from any device identifier (IMEI, MAC address, advertising ID, etc.) and cannot be used to identify or track you.

#### 3.2 What We Do NOT Collect

The following data is **never** collected, transmitted, or stored:

- Full name, email address, phone number, or any identity information
- Exact GPS coordinates (only rounded approximations to ±1 km)
- Device identifiers (IMEI, serial number, advertising ID, Android ID)
- IP addresses (stripped at the Firestore security rules level; Firestore does not log request IPs in shared databases)
- Contacts, photos, files, or any other app data
- Sensor data beyond what is listed above (raw accelerometer/gyroscope waveforms are processed locally and discarded)
- Location history or movement patterns

---

### 4. Legal Basis for Processing

The processing of the data described in Section 3.1 is based on:

- **Article 6(1)(f) GDPR** — Legitimate interest: the legitimate interest of the project in advancing public scientific knowledge about seismic activity through citizen science, which does not override your fundamental rights and freedoms.
- **Article 89(1) GDPR** — Special derogations for scientific research: appropriate safeguards are in place (anonymization, minimal data collection, opt-in consent, right to object).

If you are located in Italy, processing also complies with **D.Lgs. 196/2003** (Codice in materia di protezione dei dati personali) as amended.

---

### 5. How We Use the Data

Data collected through the community network is used exclusively for:

1. **Seismic event correlation** — Cross-validating detections from multiple devices to reduce false positives and estimate event epicenters
2. **Scientific research** — Academic studies on distributed earthquake detection methodologies
3. **Model improvement** — Training and evaluating future versions of the on-device ML model
4. **Public seismic datasets** — Aggregated, anonymized datasets may be published for open scientific use

Data is **never** used for:
- Advertising or commercial purposes
- User profiling or behavioral analysis
- Sale or transfer to third parties for non-research purposes

---

### 6. Data Storage and Retention

#### 6.1 Storage Location

Data is stored in **Google Firebase Firestore**, hosted on Google Cloud infrastructure. Storage may be within the European Economic Area (EEA) or transferred to the United States under Google's Standard Contractual Clauses (Art. 46 GDPR).

Google's privacy policy applies to the infrastructure layer: https://policies.google.com/privacy

#### 6.2 Retention Period

- Individual detection records are retained for **90 days** from the date of recording, after which they are automatically deleted by a scheduled cleanup function.
- Aggregated, fully anonymized statistical summaries (no device identifiers, no coordinates) may be retained indefinitely for longitudinal research.

---

### 7. Data Sharing

Anonymous detection data may be shared with:

- **Academic partners and research institutions** for scientific studies on seismic detection
- **Other citizen science platforms** (e.g., comparable projects in the spirit of OpenEEW) under open data agreements
- **Public scientific repositories** in fully aggregated, de-identified form

Data is **never** sold to, or shared with, advertising networks, commercial entities, or government agencies for non-scientific purposes.

---

### 8. Your Rights (GDPR)

As a data subject under GDPR, you have the following rights:

| Right | How to Exercise |
|-------|----------------|
| **Right of access** (Art. 15) | Request a copy of data associated with your `anonymousDeviceId` via GitHub Issues (label: `privacy`) |
| **Right to erasure** (Art. 17) | Go to Settings > Data Sharing > Delete My Data, or open a GitHub Issue with label `privacy` |
| **Right to object** (Art. 21) | Disable community network in Settings > Data Sharing > Community Network (OFF) |
| **Right to data portability** (Art. 20) | Request export of your data via GitHub Issues |
| **Right to restrict processing** (Art. 18) | Contact us via GitHub Issues (label: `privacy`) |

**Opt-out:** The community network feature is **opt-in** and disabled by default. You can disable it at any time in Settings. Disabling it stops all future data transmission immediately.

**Note:** Because data is anonymized and not linked to any personal identifier, we cannot guarantee identification of all records associated with a particular device if the app is reinstalled (which generates a new `anonymousDeviceId`).

---

### 9. Cookies and Tracking

The Application does not use cookies, tracking pixels, or any third-party analytics SDKs (e.g., Google Analytics, Firebase Analytics, Crashlytics). No behavioral tracking is performed.

---

### 10. Children's Privacy

This Application is not directed at children under the age of 16. We do not knowingly collect data from children. If you believe a child has used this application and transmitted data, please contact us via GitHub Issues.

---

### 11. Changes to This Policy

We may update this Privacy Policy. Changes will be published in this file with an updated "Last updated" date. For significant changes, a notice will be added to the CHANGELOG.md.

---

### 12. Contact

For all privacy-related inquiries:
- Open a GitHub Issue with the label `privacy`
- For urgent GDPR requests, mention "GDPR REQUEST" in the issue title

---

## Informativa sulla Privacy (Italiano)

### 1. Introduzione

La presente Informativa sulla Privacy descrive come Seismic Mobile Detector ("l'Applicazione") raccoglie, utilizza, conserva e protegge le informazioni quando si utilizza l'applicazione. Questa policy è conforme al **Regolamento (UE) 2016/679** (GDPR) e alla normativa italiana in materia di protezione dei dati personali (**D.Lgs. 196/2003** come modificato dal D.Lgs. 101/2018).

### 2. Dati Raccolti

La funzione di rete comunitaria (attivabile dall'utente) trasmette i seguenti dati anonimi in caso di rilevamento di un potenziale evento sismico: timestamp UTC, punteggio di confidenza del modello ML, classe di intensità stimata, Accelerazione Massima del Suolo (PGA), coordinate geografiche arrotondate (±1 km), identificatore dispositivo anonimo (hash SHA-256 di UUID casuale generato all'installazione).

**Non vengono mai raccolti:** nome, email, numero di telefono, coordinate GPS esatte, identificatori di dispositivo (IMEI, ID pubblicità), indirizzi IP, dati di contatti, foto o file.

### 3. Base Giuridica del Trattamento

Il trattamento si basa su:
- **Art. 6(1)(f) GDPR** — Interesse legittimo per finalità di ricerca scientifica
- **Art. 89(1) GDPR** — Deroghe per la ricerca scientifica, con garanzie appropriate

### 4. Conservazione dei Dati

I dati vengono conservati per **90 giorni** su Firebase Firestore (Google Cloud), poi eliminati automaticamente. I riepiloghi aggregati e completamente anonimi possono essere conservati a lungo termine per ricerca longitudinale.

### 5. I Tuoi Diritti

Puoi esercitare i diritti di cui agli artt. 15-22 GDPR (accesso, cancellazione, opposizione, portabilità, limitazione) aprendo un Issue su GitHub con etichetta `privacy`. Puoi disattivare in qualsiasi momento la rete comunitaria in Impostazioni > Condivisione Dati.

### 6. Contatto

Per richieste relative alla privacy: apri un Issue su GitHub con etichetta `privacy`.

---

*Per la versione completa in italiano, fare riferimento alla versione inglese di cui sopra, che costituisce il testo ufficiale della policy.*
