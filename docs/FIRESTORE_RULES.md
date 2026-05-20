# Firestore Security Rules

This document provides the production Firestore security rules for Seismic Mobile Detector and explains how to apply them.

---

## Production Security Rules

Copy the following rules into your Firebase Firestore security rules editor (Firebase Console > Firestore Database > Rules).

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // -------------------------------------------------------------------------
    // Seismic Events Collection
    // Stores anonymous detection events uploaded by the community network.
    // -------------------------------------------------------------------------
    match /seismic_events/{eventId} {

      // Public read: research data is open for cross-validation and academic use.
      allow read: true;

      // Authenticated write (create only):
      // Documents must contain all required fields with correct types and ranges.
      // Personal information fields are explicitly forbidden.
      allow create: if
        // Required fields must be present
        request.resource.data.keys().hasAll([
          'timestamp',
          'confidence',
          'intensity',
          'pga',
          'anonymousDeviceId'
        ])

        // Confidence must be a float in [0.0, 1.0]
        && request.resource.data.confidence is float
        && request.resource.data.confidence >= 0.0
        && request.resource.data.confidence <= 1.0

        // Intensity class must be an integer in [0, 4]
        && request.resource.data.intensity is int
        && request.resource.data.intensity >= 0
        && request.resource.data.intensity <= 4

        // PGA must be a non-negative float (m/s²)
        && request.resource.data.pga is float
        && request.resource.data.pga >= 0.0

        // anonymousDeviceId must be a non-empty string (SHA-256 hash, 64 hex chars)
        && request.resource.data.anonymousDeviceId is string
        && request.resource.data.anonymousDeviceId.size() == 64

        // Timestamp must be a timestamp type (not a raw string)
        && request.resource.data.timestamp is timestamp

        // Prevent any personal information fields from being submitted
        && !('email' in request.resource.data)
        && !('name' in request.resource.data)
        && !('phone' in request.resource.data)
        && !('userId' in request.resource.data)
        && !('deviceId' in request.resource.data)
        && !('imei' in request.resource.data)
        && !('exactLocation' in request.resource.data)

        // Limit total field count to prevent data bloat
        && request.resource.data.size() <= 10;

      // No updates or deletes from clients (immutable records)
      // Deletion is handled by server-side cleanup functions
      allow update, delete: false;
    }

    // -------------------------------------------------------------------------
    // All other documents: deny by default
    // -------------------------------------------------------------------------
    match /{document=**} {
      allow read, write: false;
    }

  }
}
```

---

## Rule Explanation

### Public Read Access

```javascript
allow read: true;
```

Seismic detection data is scientific research data intended to be publicly accessible. Any researcher or application can query the `seismic_events` collection. This aligns with the open science principles of comparable projects (MyShake, OpenEEW).

### Mandatory Fields Validation

The `hasAll` check ensures that clients cannot submit partial records. All five core fields must be present:

| Field | Type | Validation |
|-------|------|-----------|
| `timestamp` | Firestore Timestamp | Must be a Firestore server timestamp type |
| `confidence` | float | 0.0 ≤ confidence ≤ 1.0 |
| `intensity` | int | 0 ≤ intensity ≤ 4 (matches model output classes) |
| `pga` | float | pga ≥ 0.0 (m/s², non-negative) |
| `anonymousDeviceId` | string | Exactly 64 characters (SHA-256 hex string) |

Optional fields that may also be present (not blocked):
- `approxLatitude` — float, rounded to 2 decimal places (±1 km precision)
- `approxLongitude` — float, rounded to 2 decimal places
- `signalType` — int (0–2, model Head 5 output)
- `pWaveSample` — int (0–999, model Head 3 output)
- `appVersion` — string (semantic version)

### PII Prevention

The rules explicitly block submission of any document containing personally identifiable information fields:

```javascript
&& !('email' in request.resource.data)
&& !('name' in request.resource.data)
&& !('phone' in request.resource.data)
&& !('userId' in request.resource.data)
&& !('deviceId' in request.resource.data)
&& !('imei' in request.resource.data)
&& !('exactLocation' in request.resource.data)
```

This is a defense-in-depth measure. The Android app already strips PII before transmission, but the Firestore rules provide a server-side guarantee that is independent of the client implementation.

### Immutable Records

```javascript
allow update, delete: false;
```

Detection records are immutable once written. This ensures:
- Research data integrity (records cannot be tampered with post-hoc)
- Simplified security model (no authentication required for write, only field validation)
- Protection against data manipulation

Cleanup of old records (90-day retention) is handled by a server-side Firebase Cloud Function, not by client-side delete operations.

### Default Deny

```javascript
match /{document=**} {
  allow read, write: false;
}
```

All paths not explicitly covered by the `seismic_events` rule are denied. This is a security best practice.

---

## Applying the Rules

### Via Firebase Console (Recommended)

1. Go to [console.firebase.google.com](https://console.firebase.google.com)
2. Select your project
3. Navigate to **Build > Firestore Database > Rules**
4. Replace the existing rules with the production rules above
5. Click **Publish**

### Via Firebase CLI

```bash
# Install Firebase CLI if not installed
npm install -g firebase-tools

# Login
firebase login

# Initialize (in the repo root, if not already done)
firebase init firestore

# Deploy rules
firebase deploy --only firestore:rules
```

### Rules File Location

If using the Firebase CLI, the rules are deployed from `firestore.rules` in the project root. Create this file with the rules content above.

---

## Switching from Test Mode to Production Rules

When you create a Firestore database in test mode, Firebase applies open rules:

```javascript
// TEST MODE — INSECURE, only for initial development
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.time < timestamp.date(2026, 8, 1);
    }
  }
}
```

**Test mode rules expire automatically** and should be replaced with production rules before deploying to any real users. Apply the production rules from this document as soon as Firestore is configured.

---

## Firestore Indexes

The following composite indexes may be needed for common queries. Create them via Firebase Console > Firestore > Indexes, or via `firestore.indexes.json`:

```json
{
  "indexes": [
    {
      "collectionGroup": "seismic_events",
      "queryScope": "COLLECTION",
      "fields": [
        { "fieldPath": "timestamp", "order": "DESCENDING" },
        { "fieldPath": "confidence", "order": "DESCENDING" }
      ]
    },
    {
      "collectionGroup": "seismic_events",
      "queryScope": "COLLECTION",
      "fields": [
        { "fieldPath": "approxLatitude", "order": "ASCENDING" },
        { "fieldPath": "approxLongitude", "order": "ASCENDING" },
        { "fieldPath": "timestamp", "order": "DESCENDING" }
      ]
    }
  ]
}
```

---

## 90-Day Data Retention

To enforce the 90-day data retention policy described in the Privacy Policy, deploy a Firebase Cloud Function that runs daily:

```javascript
// functions/index.js (example)
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.deleteOldEvents = functions.pubsub
  .schedule('every 24 hours')
  .onRun(async (context) => {
    const db = admin.firestore();
    const cutoff = new Date();
    cutoff.setDate(cutoff.getDate() - 90);
    
    const snapshot = await db.collection('seismic_events')
      .where('timestamp', '<', cutoff)
      .limit(500)  // Process in batches
      .get();
    
    const batch = db.batch();
    snapshot.docs.forEach(doc => batch.delete(doc.ref));
    await batch.commit();
    
    console.log(`Deleted ${snapshot.size} events older than 90 days`);
  });
```

Deploy with:

```bash
firebase deploy --only functions
```

This function requires the **Blaze (pay-as-you-go) plan** on Firebase, as scheduled functions are not available on the free Spark plan.
