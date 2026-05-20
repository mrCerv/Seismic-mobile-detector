# Legal Notice / Avviso Legale

**Seismic Mobile Detector**
Copyright 2026 Seismic Mobile Detector Contributors

---

## 1. Limitation of Liability and Disclaimer of Warranties

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT.

IN NO EVENT SHALL THE AUTHORS, COPYRIGHT HOLDERS, OR CONTRIBUTORS BE LIABLE FOR ANY CLAIM, DAMAGES, OR OTHER LIABILITY — WHETHER IN CONTRACT, TORT, OR OTHERWISE — ARISING FROM, OUT OF, OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

### 1.1 Life and Safety Decisions

**SEISMIC MOBILE DETECTOR IS NOT DESIGNED, CERTIFIED, OR INTENDED FOR USE IN SAFETY-CRITICAL APPLICATIONS.** This application must not be relied upon for:

- Decisions involving the safety of persons or property
- Civil protection, emergency management, or disaster response operations
- Professional seismic monitoring, structural engineering assessments, or insurance purposes
- Any application where failure or inaccuracy could result in injury, death, or significant property damage

Users must not make life-safety decisions based solely or primarily on information provided by this application. Official seismic alerts, civil protection authorities, and professional seismological institutions must always be the primary source for emergency decision-making.

### 1.2 No Warranty of Continuity

The distributed network features of this application depend on third-party cloud infrastructure (Google Firebase / Firestore). The project contributors make no warranty regarding uptime, availability, or continued operation of these services. The application may cease to function if third-party services are discontinued or modified.

---

## 2. Intellectual Property

### 2.1 Application License

Seismic Mobile Detector is open-source software distributed under the **Apache License, Version 2.0**. The full license text is available in the [LICENSE](../LICENSE) file at the root of this repository.

You are free to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the software under the terms of the Apache 2.0 License.

### 2.2 STEAD Dataset

This project uses the **Stanford Earthquake Dataset (STEAD)** for model training. The STEAD dataset is subject to its own terms of use. When using or publishing results derived from STEAD, you must cite:

> Mousavi, S.M., Sheng, Y., Zhu, W., Beroza, G.C. (2019).
> **STEAD: A large seismological dataset for AI**. *IEEE Access*, 7, pp. 179464–179476.
> DOI: [10.1109/ACCESS.2019.2947848](https://doi.org/10.1109/ACCESS.2019.2947848)

The STEAD dataset is hosted by the Northern California Earthquake Data Center (NCEDC). Users are responsible for complying with NCEDC's data usage policies when downloading and using the dataset.

### 2.3 Phyphox Data

Custom training data collected via the Phyphox application (RWTH Aachen University) is governed by Phyphox's terms of use. Data collected by individual contributors and submitted to this project is assumed to be freely licensed for research purposes unless otherwise stated.

### 2.4 Third-Party Libraries

This project incorporates the following open-source libraries:

| Library | License | Use |
|---------|---------|-----|
| TensorFlow Lite 2.15 | Apache 2.0 | On-device ML inference |
| Firebase / Firestore SDK | Apache 2.0 | Distributed event network |
| OSMDroid | Apache 2.0 | OpenStreetMap rendering |
| Dagger Hilt 2.51 | Apache 2.0 | Dependency injection |
| AndroidX Compose | Apache 2.0 | UI framework |
| MPAndroidChart | Apache 2.0 | Seismogram visualization |
| Apache Commons Math 3 | Apache 2.0 | Signal filtering |
| Room (AndroidX) | Apache 2.0 | Local database |
| NumPy / SciPy / pandas | BSD 3-Clause | Python ML pipeline |
| scikit-learn | BSD 3-Clause | ML utilities |
| h5py | BSD 3-Clause | HDF5 dataset loading |

Map data is provided by OpenStreetMap contributors under the [Open Database License (ODbL)](https://www.openstreetmap.org/copyright).

---

## 3. Data Collection and Privacy

### 3.1 Summary

This application may collect and transmit anonymous, non-personal seismic detection data for scientific research purposes. No personally identifiable information (PII) is collected. Full details are provided in the [Privacy Policy](PRIVACY.md).

### 3.2 GDPR Compliance

Data processing is carried out in compliance with **Regulation (EU) 2016/679** (General Data Protection Regulation — GDPR) and applicable Italian data protection law (**D.Lgs. 196/2003** as amended by D.Lgs. 101/2018).

The legal basis for processing is:
- **Art. 6(1)(f) GDPR** — Legitimate interests of the data controller for scientific research purposes
- **Art. 89 GDPR** — Safeguards and derogations relating to processing for scientific research purposes

### 3.3 Data Hosting

Anonymous detection data is stored on **Google Firebase / Cloud Firestore** infrastructure hosted within the European Economic Area (EEA) or transferred to Google LLC (US) under Standard Contractual Clauses pursuant to Art. 46 GDPR.

---

## 4. Governing Law

This legal notice and any disputes arising from the use of Seismic Mobile Detector shall be governed by:

- **Italian law** for users located in Italy (Codice Civile, D.Lgs. 206/2005 Consumer Code where applicable)
- **European Union law**, including GDPR, for matters of data protection throughout the EU
- The laws of the user's country of residence for users outside Italy, to the extent that mandatory consumer protection or data protection laws apply

For open-source licensing matters, the terms of the Apache License 2.0 govern regardless of jurisdiction.

---

## 5. Academic Use and Attribution

If you use Seismic Mobile Detector in academic research, publications, or educational materials, attribution is appreciated:

> Seismic Mobile Detector Contributors (2026). *Seismic Mobile Detector: A citizen science distributed seismic monitoring platform for Android*. Available at: https://github.com/[repository-url]. Licensed under Apache 2.0.

---

## 6. Contact

For questions regarding:

- **Research collaborations and academic partnerships:** Open an issue in the GitHub repository with the label `research-collaboration`
- **Data privacy and GDPR requests:** Open an issue with the label `privacy` or submit a data deletion request via the app settings
- **Security vulnerabilities:** See `SECURITY.md` (if present) or open a confidential GitHub Security Advisory

---

*Last updated: 2026-05-20*
*Version: 1.0.0*
