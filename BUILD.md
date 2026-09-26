# BillNova — Build & GitHub Actions APK Guide

This guide walks you through pushing the project to **GitHub**, running the automated **GitHub Actions** workflow, downloading the compiled **APK**, and installing it on an Android device.

---

## 📋 Table of Contents
1. [Push Project to GitHub](#1-push-project-to-github)
2. [Automated APK Build via GitHub Actions](#2-automated-apk-build-via-github-actions)
3. [How to Download Your APK](#3-how-to-download-your-apk)
4. [Installing the APK on Android](#4-installing-the-apk-on-android)
5. [Local Build via Command Line](#5-local-build-via-command-line)

---

## 1. Push Project to GitHub

### Step 1.1: Create a New GitHub Repository
1. Go to [github.com/new](https://github.com/new).
2. Enter a repository name (e.g., `billnova` or `nexusflow`).
3. Set the repository to **Public** or **Private**.
4. **Do not** check "Initialize this repository with a README" (your repository already has one).
5. Click **Create repository**.
6. Copy the repository URL (e.g., `https://github.com/YOUR_USERNAME/billnova.git`).

### Step 1.2: Initialize and Push Your Code
Open your terminal in the root folder of this project and run:

```bash
# 1. Initialize Git (if not already initialized)
git init

# 2. Configure default branch to main
git branch -M main

# 3. Add all files to staging
git add .

# 4. Commit the files
git commit -m "feat: initial commit with BillNova billing app and CI/CD workflow"

# 5. Link your GitHub remote (replace with your actual repository URL)
git remote add origin https://github.com/YOUR_USERNAME/billnova.git

# 6. Push code to GitHub
git push -u origin main
```

> **Note:** If prompted for GitHub credentials, use your GitHub username and a **Personal Access Token (classic)** with `repo` scope, or use the GitHub CLI (`gh auth login`).

---

## 2. Automated APK Build via GitHub Actions

A pre-configured GitHub Actions workflow is included at `.github/workflows/build-apk.yml`.

### Option A: Automatic Trigger
The workflow triggers automatically whenever you:
- `git push` new commits to `main` or `master`.
- Open a Pull Request targeting `main` or `master`.

### Option B: Manual Trigger (Run Workflow Button)
You can trigger a build on demand anytime:
1. Open your repository on GitHub in your web browser.
2. Click on the **Actions** tab at the top.
3. In the left sidebar, click on **Build Android APK**.
4. Click the **Run workflow** dropdown button on the right.
5. Select branch `main` and click the green **Run workflow** button.
6. The workflow will start running in ~5–10 seconds. Click on the run to view live build logs.

---

## 3. How to Download Your APK

Once the workflow finishes (typically takes 2–3 minutes):

1. Go to the **Actions** tab on your GitHub repository.
2. Click on the latest workflow run (marked with a green checkmark `✔ Build Android APK`).
3. Scroll down to the bottom of the page to the **Artifacts** section.
4. Click on **`BillNova-Debug-APK`**.
5. Your browser will download a ZIP archive containing `app-debug.apk`.
6. Extract the ZIP file on your computer or phone to get `app-debug.apk`.

---

## 4. Installing the APK on Android

1. **Transfer the APK to your phone:**
   - Send `app-debug.apk` via Google Drive, WhatsApp, Telegram, USB cable, or directly download it from GitHub on your phone's browser (request desktop site if downloading GitHub Actions artifacts directly on mobile).
2. **Install:**
   - Tap on `app-debug.apk` on your phone.
   - If prompted: **"For your security, your phone is not allowed to install unknown apps from this source"**, tap **Settings** and toggle **"Allow from this source"**.
   - Tap **Install**.
3. **Launch:**
   - Tap **Open** or launch **BillNova** from your app drawer.

---

## 5. Local Build via Command Line

If you want to build the APK locally on your machine without GitHub Actions:

### Prerequisites:
- Java Development Kit (JDK) 21 installed.
- Android SDK installed (`ANDROID_HOME` configured).

### Build Command:
```bash
# Ensure Gradle wrapper is executable
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# Output APK path:
# app/build/outputs/apk/debug/app-debug.apk
```

To run on a connected USB device:
```bash
./gradlew installDebug
```

---

## 📁 Repository Structure Overview

```
├── .github/
│   └── workflows/
│       └── build-apk.yml      # CI/CD Workflow for automated APK generation
├── app/
│   ├── build.gradle.kts       # App-level dependencies & build rules
│   ├── google-services.json   # Firebase configuration
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/  # Jetpack Compose UI, Room DB, Repositories, ViewModels
│       └── res/               # Colors, themes, icons, logos, drawables
├── gradle/
│   ├── libs.versions.toml     # Version catalog
│   └── wrapper/               # Gradle wrapper runtime binaries
├── gradlew                    # Unix Gradle wrapper launcher
├── gradlew.bat                # Windows Gradle wrapper launcher
├── metadata.json              # Platform metadata
├── build.gradle.kts           # Root Gradle configuration
├── settings.gradle.kts        # Project settings & repositories
├── BUILD.md                   # This guide
└── README.md                  # Project overview
```
