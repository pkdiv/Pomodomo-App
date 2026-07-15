# Pomodoro (Android)

A lightweight, native-feeling Android wrapper around the Pomodoro web application
([apps.pkdiv.com/pomodoro](https://apps.pkdiv.com/pomodoro)).

The web app is the single source of truth for the timer and all features. This
repository contains only the Android-native shell that enhances the web
experience. It does **not** reimplement timer logic, session management, or any
business logic locally.

## Features

- Splash and loading screens
- Offline detection with a clean retry screen and automatic recovery when the
  connection returns
- Pull-to-refresh
- File picker and download handling
- Notification permission handling and haptic feedback
- Screen wake lock during sessions
- Deep link support
- Back-button handling that follows WebView history first
- Fullscreen and edge-to-edge display
- Dark mode compatibility

## Tech stack

- Kotlin + AndroidX + Material 3
- `WebView`-based UI (no third-party dependencies beyond AndroidX)
- Gradle Kotlin DSL

## Build

Requirements: Android SDK (compileSdk 35), JDK 17.

```bash
./gradlew assembleRelease
```

The release APK is produced at `app/build/outputs/apk/release/`. F-Droid builds
from source on its own infrastructure, so no signing keys are committed here.

## Project layout

```
app/
 ├── activities/      # MainActivity (WebView host, back handling)
 ├── webview/         # WebView client/config
 ├── network/         # Connectivity monitoring
 ├── notifications/   # Notification permission helper
 ├── permissions/     # Permission helpers
 ├── utils/           # Constants, haptics
 └── res/             # Layouts, themes, drawables
```

## Releases (GitHub)

A signed release APK is built and published to GitHub Releases automatically when
you push a tag matching `v*` (e.g. `v1.0.0`).

To enable signing, add these **repository secrets** (Settings → Secrets and
variables → Actions):

| Secret                | Description                                              |
| -------------------- | -------------------------------------------------------- |
| `SIGNING_KEY`        | Base64 of your release keystore (`.jks`/`.keystore`)     |
| `KEY_STORE_PASSWORD` | Keystore password                                        |
| `KEY_ALIAS`          | Key alias used to sign the app                           |
| `KEY_PASSWORD`       | Key password                                             |

Generate a keystore and base64-encode it:

```bash
keytool -genkey -v -keystore release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias pomodoro
base64 -w0 release-keystore.jks > signing_key.base64
# paste signing_key.base64 contents into the SIGNING_KEY secret
```

The signing config in `app/build.gradle.kts` only activates when these
environment variables are present (CI). Local builds and F-Droid's own build
produce an unsigned APK — F-Droid signs independently from source.

## License

[Apache-2.0](./LICENSE). The application code in this repository is free and
open source; the timer and feature logic run in the hosted web app.
