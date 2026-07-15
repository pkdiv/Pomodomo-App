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

## License

[Apache-2.0](./LICENSE). The application code in this repository is free and
open source; the timer and feature logic run in the hosted web app.
