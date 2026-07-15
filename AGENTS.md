# AGENTS.md

# Project Overview

This repository contains the Android wrapper for the Pomodoro web application.

The web application already exists in a separate repository and is the single source of truth.

This repository **must not** reimplement, duplicate, or fork the web application's functionality.

Its sole responsibility is to provide a high-quality Android experience around the existing web app.

---

# Primary Goal

Deliver a native-feeling Android application while keeping maintenance as close to zero as possible.

The Android app should primarily:

* Launch the hosted web application
* Integrate with Android features where appropriate
* Handle Android lifecycle correctly
* Provide a polished user experience
* Stay lightweight

The web application remains responsible for:

* UI
* Business logic
* Timer functionality
* Styling
* Feature implementation

---

# Target Website

```text
https://apps.pkdiv.com/pomodoro
```

Never duplicate web functionality inside the Android application unless explicitly requested.

---

# Architecture

Preferred architecture:

```text
Android App
│
├── MainActivity
├── WebView
├── Android integrations
│
├── Notifications
├── Splash Screen
├── Network monitoring
├── Permission handling
├── Keep Screen Awake
├── Haptic Feedback
└── Deep Links
```

The WebView is the application.

Native code should only enhance the experience.

---

# Technology Stack

* Kotlin
* Android Studio
* Android SDK
* AndroidX
* Material 3

Avoid unnecessary third-party libraries.

---

# Responsibilities

The Android layer may implement:

* Splash screen
* Loading screen
* Pull-to-refresh (if appropriate)
* Offline detection
* Network recovery
* Download handling
* File picker support
* Notification permissions
* Haptic feedback
* Screen wake lock
* Deep links
* Back button handling
* Fullscreen mode
* Edge-to-edge display

The Android layer should **not** implement:

* Timer logic
* Session management
* User settings
* Business logic
* Pomodoro calculations

Those belong to the web application.

---

# WebView Guidelines

Always enable:

* JavaScript
* DOM Storage
* Local Storage
* IndexedDB
* Cookies
* Dark mode compatibility

Handle:

* SSL errors appropriately
* File uploads
* External links
* Downloads
* Permissions

Never expose unnecessary native interfaces to JavaScript.

---

# Security

Always follow Android WebView security best practices.

* Only allow trusted domains.
* Prevent navigation to unknown hosts unless explicitly intended.
* Disable debugging in release builds.
* Do not expose unrestricted JavaScript interfaces.
* Validate all intents before opening external applications.

Security should take precedence over convenience.

---

# Offline Behaviour

If the website cannot be reached:

* Show a clean offline screen.
* Provide a retry option.
* Automatically recover when connectivity returns.

Do not display Android crash dialogs for network failures.

---

# Notifications

Native notifications may be used for:

* Timer completion
* Reminder notifications

Notification scheduling should integrate with the web application where possible rather than duplicating timer logic.

---

# Back Navigation

Preferred behavior:

* Navigate WebView history first.
* Exit only when there is no further history.

Avoid confusing navigation flows.

---

# UI

The Android UI should remain minimal.

Native UI should only exist for:

* Splash screen
* Error screens
* Permission dialogs
* Offline state

Avoid building duplicate navigation or settings screens.

---

# Performance

Prioritize:

* Fast startup
* Low memory usage
* Smooth scrolling
* Minimal APK size

Avoid heavy frameworks.

---

# Code Style

* Prefer idiomatic Kotlin.
* Keep classes focused.
* Favor composition over inheritance.
* Use descriptive names.
* Avoid unnecessary abstractions.

---

# Dependencies

Before adding a dependency, ask:

1. Is this available in AndroidX?
2. Can the Android SDK already do this?
3. Does this significantly improve the user experience?

If not, avoid adding it.

---

# Project Structure

```text
app/
 ├── activities/
 ├── webview/
 ├── permissions/
 ├── notifications/
 ├── network/
 ├── utils/
 └── theme/
```

Keep packages focused.

---

# AI Agent Guidelines

When implementing changes:

* Assume the website is the source of truth.
* Never recreate web features in native code.
* Keep the wrapper lightweight.
* Prefer Android SDK features over third-party libraries.
* Minimize maintenance burden.
* Preserve compatibility with future website updates.
* Explain architectural changes before implementing them.
* If multiple approaches exist, choose the simplest solution that maintains a polished Android experience.

When in doubt, enhance the Android experience rather than replacing web functionality.
