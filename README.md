# Second Brain 🧠📱

> **A personal "second brain" for Android** — save anything, ask questions, get answers via on-device RAG. Fully offline, private by design.

---

## 🚀 Overview

**Second Brain** is an intelligent, privacy-first Android application designed to index, search, and answer questions from your personal knowledge base using **on-device Retrieval-Augmented Generation (RAG)**.

- **100% On-Device & Offline**: No cloud APIs, zero data leaves your device.
- **Privacy by Design**: Your notes, documents, and vectors remain strictly local.
- **Fast Local Search**: Built on top of Room DB and local vector embeddings.

---

## 🛠️ Architecture & Tech Stack

The app follows **Modern Android Development (MAD)** standards and **MVVM Architecture**:

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3 Design
- **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Local Persistence**: [Room Database](https://developer.android.com/training/data-storage/room)
- **Asynchronous Logic**: Kotlin Coroutines & Flow
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture principles
- **Build System**: Gradle with Kotlin DSL & Version Catalogs (`libs.versions.toml`)

---

## 📁 Repository Structure

```
second-brain/
├── app/
│   ├── src/main/java/com/kaisarnajar/secondbrain/
│   │   ├── SecondBrainApp.kt           # @HiltAndroidApp Entry Point
│   │   ├── MainActivity.kt             # @AndroidEntryPoint ComponentActivity
│   │   ├── data/
│   │   │   └── local/                  # Room Entities, DAOs, & Database
│   │   ├── di/                         # Hilt Modules (DatabaseModule, etc.)
│   │   ├── ui/
│   │   │   ├── screens/
│   │   │   │   └── HomeScreen.kt       # Initial "Hello World" Compose Screen
│   │   │   └── theme/                  # Compose Colors, Typography, & Theme
│   │   └── viewmodel/                  # ViewModels (Upcoming)
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml              # Centralized Dependency Version Catalog
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🔑 Git Configuration (Repository-Local)

This repository is configured specifically for:
- **Author**: Kaisar Najar
- **Email**: kaisarnajar11114@gmail.com
- **Username**: kaisarnajar

---

## 📄 License

Copyright © 2026 Kaisar Najar. All rights reserved.
