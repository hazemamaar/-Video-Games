# VideoGames Android App

A modern Android application for browsing and discovering video games using the RAWG API. Built with Jetpack Compose, following clean architecture principles with an offline-first approach.

## 📱 Features

- **Game Discovery**: Browse games by genre with pagination support
- **Search Functionality**: Search games by name 
- **Game Details**: View detailed information about each game
- **Offline Support**: Cached data available when offline
- **Genre Filtering**: Filter games by genre with visual chips
- **Material Design 3**: Modern, beautiful UI with Material3 theming

## 🏗️ Architecture

### **MVI (Model-View-Intent) Architecture**

This project follows the **MVI (Model-View-Intent)** architecture pattern, chosen for the following reasons:

1. **Unidirectional Data Flow**: Ensures predictable state management with a single source of truth
2. **Type Safety**: Sealed classes for State and Intent provide compile-time safety
3. **Testability**: Clear separation of concerns makes unit testing straightforward
4. **State Management**: BaseViewModel handles state transitions and intent processing automatically
5. **Reactive UI**: Jetpack Compose works seamlessly with StateFlow for reactive updates


## 🛠️ Tech Stack

### **Core Technologies**
- **Kotlin** 2.0.21 - Modern, concise programming language
- **Android Gradle Plugin** 8.9.0 - Build system
- **Jetpack Compose** 1.7.6 - Modern declarative UI toolkit
- **Material Design 3** 1.2.0 - Material Design components

### **Architecture Components**
- **Hilt** 2.51.1 - Dependency injection framework
- **ViewModel** - Lifecycle-aware state management
- **Navigation Compose** 2.8.0 - Type-safe navigation
- **Lifecycle Runtime KTX** 2.8.7 - Lifecycle-aware components

### **Networking**
- **Retrofit** 2.9.0 - Type-safe HTTP client
- **OkHttp** 4.12.0 - HTTP client with logging interceptor
- **Gson** 2.10.1 - JSON serialization/deserialization
- **Kotlin Serialization** 1.6.2 - Kotlin-native serialization (configured but using Gson for Retrofit)

### **Local Storage**
- **Room** 2.6.1 - SQLite database abstraction layer
- **Room KTX** - Coroutines support for Room

### **Image Loading**
- **Coil** 2.6.0 - Image loading library for Compose

### **Development Tools**
- **Kapt** - Annotation processing for Room and Hilt
- **Compose Compiler** - Kotlin compiler plugin for Compose

## 📦 Project Structure

```
app/src/main/java/com/example/videogames/
├── core/                          # Core utilities and base classes
│   ├── error/                     # Error handling
│   │   ├── ErrorHandler.kt
│   │   └── GeneralErrorHandlerImpl.kt
│   ├── mapper/                    # Base mapper interface
│   ├── network/                   # Network abstraction
│   ├── response/                  # Resource and RepositoryResult
│   ├── usecase/                   # Base use case
│   ├── viewmodel/                 # BaseViewModel for MVI
│   └── VideoGameException.kt      # Custom exception hierarchy
│
├── data/                          # Data layer
│   ├── local/                     # Local data source (Room)
│   │   ├── dao/                   # Data Access Objects
│   │   ├── entity/                # Room entities
│   │   ├── ILocalDataSource.kt
│   │   └── LocalDataSource.kt
│   ├── mapper/                    # Data mappers (DTO ↔ Entity ↔ Domain)
│   ├── remote/                    # Remote data source
│   │   ├── datasource/            # Remote data source implementation
│   │   ├── dto/                   # Data Transfer Objects
│   │   └── RawgApiService.kt      # Retrofit API interface
│   └── repository/                # Repository implementation
│
├── domain/                        # Domain layer (business logic)
│   ├── model/                     # Domain models
│   ├── repository/                # Repository interface
│   └── usecase/                   # Use cases
│
├── di/                            # Dependency injection modules
│   ├── DatabaseModule.kt
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
│
├── ui/                            # UI layer
│   ├── components/                # Reusable Compose components
│   ├── navigation/                # Navigation graph
│   ├── screens/                   # Screen implementations
│   └── theme/                     # Material3 theming
│
├── utils/                         # Utility classes
│   └── Constants.kt
│
├── MainActivity.kt
└── VideoGamesApp.kt              # Hilt Application class
```

## 🔄 Data Flow (MVI Pattern)

1. **User Action** → UI sends `Intent` via `viewModel.sendIntent()`
2. **Intent Processing** → `BaseViewModel.handleIntent()` processes the intent
3. **Business Logic** → UseCase executes business logic
4. **Data Fetching** → Repository fetches from Remote/Local data sources
5. **State Update** → ViewModel updates state via `setState()`
6. **UI Update** → Compose UI observes `viewState` and recomposes

## 🔌 API Integration

This app uses the **RAWG Video Games Database API**:
- Base URL: `https://api.rawg.io/api/`
- API Key: Configure in `Constants.kt` (replace `YOUR_API_KEY_HERE`)
- Get your free API key at: https://rawg.io/apidocs

## 🗄️ Database Schema

### Games Table
- `id` (Primary Key)
- `name`
- `imageUrl`
- `rating`
- `genres` (stored as comma-separated string)
- `releaseDate`
- `description`
- `page`
- `genreFilter`
- `lastUpdated`

### Genres Table
- `id` (Primary Key)
- `name`
- `slug`

## ⚙️ Configuration

### API Key Setup
1. Get your RAWG API key from https://rawg.io/apidocs
2. Update `Constants.kt`:
   ```kotlin
   const val API_KEY = "YOUR_API_KEY_HERE"
   ```

### Build Requirements
- **minSdk**: 24 (Android 7.0)
- **targetSdk**: 35 (Android 15)
- **compileSdk**: 35
- **Java Version**: 11

## 📝 Assumptions & Shortcuts

### **Assumptions Made:**
1. **API Key Management**: API key is stored in `Constants.kt` (should be moved to `local.properties` or BuildConfig for production)
2. **Error Handling**: Network errors are handled gracefully with user-friendly messages
3. **Offline Strategy**: App uses offline-first approach - shows cached data when available, updates from network when possible
4. **Pagination**: Simple page-based pagination
5. **Data Validation**: Basic validation on DTOs and entities (ID > 0, non-blank names)

### **Trade-offs & Shortcuts:**
1. **No Paging Library**: Implemented custom pagination instead of Jetpack Paging for simplicity
2. **Genre Storage**: Genres stored as comma-separated string in Room (could use separate junction table for better normalization)
3. **Error Messages**: Error messages are simplified - could be more detailed with specific error codes
4. **Caching Strategy**: Simple cache-aside pattern - could implement more sophisticated cache invalidation
5. **API Key Security**: API key is in source code (should use BuildConfig or secure storage)
6. **Image Caching**: Relies on Coil's default caching
7. **State Management**: Using sealed interfaces for state - could add more granular states for better UX

## 📄 License

This project is for educational purposes.

## 👤 Author

VideoGames Android App - Built with modern Android development practices.

---

**Note**: This project demonstrates modern Android development practices including MVI architecture, Jetpack Compose, Hilt dependency injection, and clean architecture principles.

