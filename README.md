# IronCore Metrics

IronCore Metrics is a high-performance, AI-integrated health and fitness application designed for the Google Pixel 10 Pro XL and Wear OS. 

## Features

- **Advanced Workout Logging:** Detailed set and rep tracking with historical analysis.
- **AI-Powered Nutrition:** Connects to your Homelab AI backend to generate personalized meal plans based on your goals.
- **Health Connect Integration:** Syncs heart rate, steps, and activity data across all your devices.
- **Wear OS Companion:** Real-time workout tracking and heart rate monitoring on your wrist.
- **Media Integration:** Seamless control of Spotify and YouTube Music during your training sessions.
- **Premium Design:** Sophisticated dark theme using GitHub Space Gray, IBM Cobalt Blue, and Granite.

## Tech Stack

- **UI:** Jetpack Compose (Mobile & Wear OS)
- **Architecture:** MVVM + Clean Architecture
- **Dependency Injection:** Hilt
- **Database:** Room (Offline-first)
- **Networking:** Retrofit + OkHttp
- **AI:** Hybrid approach (On-Device AICore + Homelab Remote AI)

## Setup Instructions

1.  **Prerequisites:** Android Studio Jellyfish+ and Android SDK 35.
2.  **Cloning:** The project is located in `IronCoreMetrics/`.
3.  **AI Backend Configuration:**
    - Open `NetworkModule.kt` in the `app` module.
    - Update the `baseUrl` to point to your Homelab AI API IP address.
4.  **Health Connect:**
    - Ensure the Health Connect app is installed on your device or emulator.
    - Grant permissions when prompted within IronCore Metrics.

## Development Guide

### Project Structure
- `app/`: The primary mobile application module.
- `wear/`: The Wear OS companion module.
- `gradle/libs.versions.toml`: Centralized dependency management.

### Testing
- Run `./gradlew test` to execute unit tests.
- UI tests are located in `androidTest` directories.

## License
Free and open-source for personal use. Built with a focus on privacy and high performance.
