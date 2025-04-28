# Movies Android App

A modern Android application built with Jetpack Compose that allows users to browse and discover movies. The app follows clean architecture principles and uses the latest Android development technologies.

## Features

- Browse trending movies
- View detailed movie information
- Modern Material 3 design with dark/light theme support
- Responsive UI built with Jetpack Compose

## Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Architecture:** Clean Architecture with MVI
- **Dependency Injection:** Hilt
- **Networking:** Retrofit with OkHttp
- **Image Loading:** Coil
- **Navigation:** Jetpack Navigation Compose
- **Testing:** JUnit, Mockito, MockK
- **API:** The Movie Database (TMDB)

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/movies/
│   │   │   ├── data/           # Data layer (repositories, data sources)
│   │   │   ├── domain/         # Domain layer (use cases, models)
│   │   │   ├── ui/             # UI layer (screens, viewmodels)
│   │   │   └── utils/          # Utility classes
│   │   └── res/                # Resources
│   └── test/                   # Unit tests
```

## Setup

1. Clone the repository
2. Open the project in Android Studio
3. Create a `local.properties` file in the root directory and add your TMDB Access token:
   ```
   TMDB_ACCESS_TOKEN=your_access_token_here
   ```
4. Build and run the project

## Building the Project

The project uses Gradle for building. You can build the project using:

```bash
./gradlew build
```

For running the app:
```bash
./gradlew installDebug
```

## Testing

The project includes unit tests. To run the tests:

```bash
./gradlew test        # For unit tests
```

## Acknowledgments

- [The Movie Database (TMDB)](https://www.themoviedb.org/) for providing the movie data API
- [Android Jetpack](https://developer.android.com/jetpack) for the amazing Android development tools 