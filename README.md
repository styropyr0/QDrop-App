# QDrop – QA Build Distribution App

**QDrop** is an Android application built with **Jetpack Compose**, designed for seamless **QA build distribution**.
It allows testers and teams to manage, download, and update builds - **without relying on Google Play**.

All builds are stored securely on **Cloudflare R2**, and build metadata (like version, label, changelog, etc.) is fetched from **Firebase Realtime Database**.

---

## Overview

QDrop provides a fast, elegant, and private way to share internal builds.
You can filter, view changelogs, download updates, and even **install new versions directly from within the app**.

---

## Screenshots

![Mobile App Screens](screenshots/scr.png)

---

## Features

* **Advanced Filtering** — Filter builds by app name, version, label, or environment (e.g., Staging, Production).
* **In-App Updates** — Download and install new APK versions without needing Google Play.
* **Realtime Sync** — Automatically fetches build information from Firebase Realtime Database.
* **Cloudflare R2 Integration** — Stores and serves APKs with fast and secure access.
* **Detailed Changelogs** — Each build includes notes and fixes for easy QA tracking.
* **Multi-App Support** — Manage builds from multiple apps or organizations under one account.
* **Direct Build Metadata Access** — Each build includes version, file size, uploader, and timestamp.
* **Jetpack Compose UI** — Fully modern and responsive interface with smooth animations and filters.
* **Private Distribution** — No public app store dependencies; everything stays within your QA environment.

---

## Project Structure

```
app/
├── src/
│    ├── main/
│    │    ├── java/com/styropyr0/qdrop/    # Kotlin source files (Compose UI, ViewModels, repository, etc.)
│    │    ├── res/                         # Resources (icons, themes, strings, etc.)
│    │    ├── AndroidManifest.xml
│    │    └── google-services.json          # Firebase config file (add your own)
├── build.gradle                            # Module-level Gradle file
├── proguard-rules.pro                      # Optional for release builds
build.gradle                                # Project-level Gradle file
gradle.properties                           # Gradle properties
settings.gradle                             # Settings file
README.md                                   # This documentation file
.gitignore
```

---

## Setup Instructions

### 1. Add Firebase Configuration

Add your **`google-services.json`** file inside the `app/` directory.
You can download it from your [Firebase Console](https://console.firebase.google.com/).

Example dummy configuration:

```json
{
  "project_info": {
    "project_number": "123456789012",
    "firebase_url": "https://your-project-id-default-rtdb.region.firebasedatabase.app",
    "project_id": "your-project-id",
    "storage_bucket": "your-project-id.appspot.com"
  },
  "client": [
    {
      "client_info": {
        "mobilesdk_app_id": "1:123456789012:android:abcdef1234567890",
        "android_client_info": {
          "package_name": "com.example.app"
        }
      },
      "api_key": [
        {
          "current_key": "YOUR_FIREBASE_API_KEY"
        }
      ]
    }
  ],
  "configuration_version": "1"
}
```

---

### 2. Configure Firebase Realtime Database Rules

For testing:

```json
{
  "rules": {
    ".read": "true",
    ".write": "true"
  }
}
```

For production, restrict access to authenticated or specific users only.

---

### 3. Configure Cloudflare R2

QDrop uses Cloudflare R2 for hosting APK files.

1. Go to **Cloudflare Dashboard → R2**
2. Create a bucket (e.g., `qdrop-builds`)
3. Generate **Access Key ID** and **Secret Access Key**
4. Enable **Public Access** or use presigned URLs for private access
5. Update the R2 endpoint and keys in your web app (QDrop Web) configuration

---

## Running the App

1. Clone the repository:

   ```bash
   git clone https://github.com/styropyr0/QDrop-App.git
   cd QDrop-App
   ```

2. Add your Firebase `google-services.json` in the `app/` directory.

3. Open the project in **Android Studio**.

4. Build and run the app.

5. Sign in or enter your organization ID to fetch builds.

---

## Security Notes

* Never commit your `google-services.json` or R2 credentials to GitHub.
* Restrict Firebase and Cloudflare access in production.
* Use dummy or example config files for open-source versions.

---

## How It Works

1. **QDrop Web App** uploads APKs to **Cloudflare R2** and metadata to **Firebase**.
2. **QDrop Android App** reads that metadata, lists all builds, and provides filters.
3. Testers select a build → the app downloads and installs it directly.

This architecture eliminates dependency on Google Play or any external app distribution service.

---

## License

MIT License — see the [LICENSE](LICENSE) file for details.
