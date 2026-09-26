# Invoice Flex — GitHub Setup & Automated APK Build Guide

This guide walks you through pushing the project to **GitHub**, triggering the automated **GitHub Actions CI/CD workflow**, downloading the compiled **APK**, and installing it on any Android phone or tablet.

---

## 📋 Table of Contents
1. [Push Project to GitHub](#1-push-project-to-github)
2. [Automated APK Build via GitHub Actions](#2-automated-apk-build-via-github-actions)
3. [How to Download Your APK](#3-how-to-download-your-apk)
4. [Installing the APK on Your Android Device](#4-installing-the-apk-on-your-android-device)
5. [Local Build via Command Line](#5-local-build-via-command-line)

---

## 1. Push Project to GitHub

### Step 1.1: Create a New GitHub Repository
1. Open [github.com/new](https://github.com/new) in your browser.
2. Set the repository name (e.g., `invoice-flex`).
3. Choose **Public** or **Private**.
4. **Do NOT** initialize with a README, `.gitignore`, or license (the project already contains all required files).
5. Click **Create repository**.
6. Copy your repository URL (e.g., `https://github.com/YOUR_USERNAME/invoice-flex.git`).

### Step 1.2: Initialize and Push Your Code
Run the following commands in the project root directory:

```bash
# 1. Initialize Git repository
git init

# 2. Set main as the default branch
git branch -M main

# 3. Stage all files
git add .

# 4. Commit all files
git commit -m "feat: complete Invoice Flex app with GitHub Actions APK workflow"

# 5. Add your remote repository (replace YOUR_USERNAME and invoice-flex with your repo URL)
git remote add origin https://github.com/YOUR_USERNAME/invoice-flex.git

# 6. Push code to GitHub
git push -u origin main
```

---

## 2. Automated APK Build via GitHub Actions

The workflow file is located at `.github/workflows/build-apk.yml`.

### Option A: Automatic Trigger
The build runs automatically whenever you:
- `git push` to `main` or `master`.
- Open a Pull Request to `main` or `master`.

### Option B: Manual Trigger (Run Workflow Button)
You can build an APK anytime on demand:
1. Open your repository on GitHub.
2. Click on the **Actions** tab in the top navigation bar.
3. In the left sidebar, click **Build Android APK**.
4. Click the **Run workflow** dropdown on the right side.
5. Select branch `main` and click the green **Run workflow** button.
6. The action will run and compile the APK in ~2 to 3 minutes.

---

## 3. How to Download Your APK

Once the workflow run completes with a green checkmark (✔):

1. Go to the **Actions** tab on your GitHub repository.
2. Click on the latest workflow run: **`Build Android APK`**.
3. Scroll down to the bottom to find the **Artifacts** section.
4. Click on **`InvoiceFlex-Debug-APK`**.
5. Your browser will download a ZIP archive containing `app-debug.apk`.
6. Extract the ZIP file to get `app-debug.apk`.

---

## 4. Installing the APK on Your Android Device

1. **Transfer the APK to your phone:**
   - Send `app-debug.apk` to your phone via Google Drive, WhatsApp, Telegram, USB, or download it directly from GitHub on your phone's browser.
2. **Install:**
   - Tap on `app-debug.apk`.
   - If prompted: *"For your security, your phone is not allowed to install unknown apps from this source"*, tap **Settings** and enable **"Allow from this source"**.
   - Tap **Install**.
3. **Open:**
   - Tap **Open** or find **Invoice Flex** in your app drawer.

---

## 5. Local Build via Command Line (Optional)

If you have Android Studio / JDK 21 installed on your computer:

```bash
# Make gradlew executable
chmod +x gradlew

# Build Debug APK
./gradlew assembleDebug

# Output APK location:
# app/build/outputs/apk/debug/app-debug.apk
```
