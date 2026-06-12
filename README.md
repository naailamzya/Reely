# Reely

**Reely** is a modern Android application designed for movie enthusiasts. It serves as a comprehensive movie discovery platform, providing users with up-to-date information on the latest cinematic releases, trending titles, and all-time favorites. Powered by the TMDB API, Reely delivers a seamless and immersive browsing experience.

## Key Features

- **Home Dashboard**: Explore movies across various categories, including Now Playing, Trending, and Top Rated.
- **Smart Discovery**: Find your favorite movies quickly with a responsive and intuitive search feature.
- **Comprehensive Movie Details**: Access in-depth information such as storylines, user ratings, genres, release dates, budgets, revenues, and full cast lists.
- **Integrated Trailers**: Watch official movie trailers directly within the app via the integrated YouTube Player.
- **Personal Watchlist**: Save movies you want to watch later using the local database (Room) for offline access.
- **Aesthetic Themes**: Choose between **Night Cinema** (Dark) and **Soft Cinema** (Light) modes to suit your viewing preference.

## Tech Stack & Libraries

The app is built using modern Android development practices to ensure performance and maintainability:

- **Architecture**: MVVM (Model-View-ViewModel) for a clean separation of concerns.
- **Networking**: [Retrofit 2](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/) for reliable API communication.
- **Local Database**: [Room Persistence Library](https://developer.android.com/training/data-storage/room) for reactive local data storage.
- **Image Loading**: [Glide](https://github.com/bumptech/glide) for efficient image processing and caching.
- **UI Components**: Material Design 3 (M3), View Binding, RecyclerView, ViewPager2, and Shimmer Effects.
- **Video Playback**: [Android YouTube Player](https://github.com/PierfrancescoSoffritti/android-youtube-player) for seamless trailer streaming.

## Project Structure

```text
com.reely
├── activities/    # UI Activities (Splash, Login, Main, Detail, etc.)
├── fragments/     # Main Navigation Fragments (Home, Discover, Watchlist, Profile)
├── viewmodel/     # UI State and Logic Management
├── repository/    # Single Source of Truth for API & Local Database
├── api/           # Retrofit Configuration & Endpoint Definitions
├── database/      # Room Database, Entities, and DAOs
├── models/        # Data classes (POJO) for API and Database mapping
├── adapters/      # RecyclerView Adapters for movies and cast members
└── utils/         # Helper classes (Constants, NetworkUtils, ThemeManager)
```

## Getting Started

1. **Clone the Repository**
   ```bash
   git clone https://github.com/naailamzya/reely.git
   ```

2. **Configure TMDB API Key**
   The application uses `BuildConfig` to manage the API Key. Update your key in `app/build.gradle.kts`:
   ```kotlin
   buildConfigField("String", "TMDB_API_KEY", "\"YOUR_API_KEY_HERE\"")
   ```

3. **Build and Run**
   - Open the project in Android Studio (Koala or newer).
   - Ensure you are using JDK 17.
   - Sync Gradle and run the app on an emulator or physical device (Min SDK 24).

---
*Developed by [Naailamzya](https://github.com/naailamzya)**
