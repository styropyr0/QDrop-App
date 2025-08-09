# QDrop – QA Build Distribution App

QDrop is an Android application built with **Jetpack Compose** for distributing QA (Quality Assurance) builds to testers.
It uses:

* **Firebase Realtime Database** → Stores metadata for each build (name, version, download URL, etc.).
* **Supabase Storage** → Hosts APK files and provides public download links for testers.

---

## Introduction

The app fetches build metadata from Firebase and displays it in a clean UI for testers.
When a tester selects a build, the app downloads the APK directly from Supabase and installs it.

---

## What You Need to Change Before Running

1. **`google-services.json`**

    * This file contains your Firebase configuration.
    * A dummy file (`google-services.example.json`) is provided for reference.
    * Download your own `google-services.json` from the Firebase Console and place it inside the `app/` directory.

2. **Firebase Realtime Database Rules**

    * Configure rules to allow only intended users to read/write.
    * Example (public read/write for testing only — **do not use in production**):

      ```json
      {
        "rules": {
          ".read": "true",
          ".write": "true"
        }
      }
      ```

3. **Supabase Project Settings**

    * Create a bucket in Supabase Storage (e.g., `qdrop-apps`).
    * Enable public access if testers should be able to download APKs without logging in.
    * Note the **project URL** and **public anon key** for uploading/downloading files.
    * Ensure your APKs follow the allowed file type (`.apk`) and size limit.

---

## Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com/).
2. Create a new project.
3. Enable **Realtime Database**.
4. Download the `google-services.json` and add it to your `app/` folder.
5. Set security rules according to your needs.

---

## Supabase Setup

1. Go to [Supabase](https://supabase.com/).
2. Create a new project.
3. Create a new storage bucket for APK files.
4. Make it public if you want direct download links.
5. Use your **Supabase Project URL** and **Anon Key** in your app logic for file access.

---

## Running the App

1. Clone this repository.
2. Place your real `google-services.json` inside the `app/` directory.
3. Configure your Supabase bucket and permissions.
4. Open the project in Android Studio.
5. Build and run the app on your device or emulator.

---

## Security Notes

* Do not commit your real `google-services.json` to a public repository.
* Restrict Firebase and Supabase rules in production.
* For open-source distribution, provide example configs instead of real credentials.
