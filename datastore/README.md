# Core Datastore Module

A powerful and flexible data storage solution for Kotlin Multiplatform projects.

## Features

- Type-safe data storage with generics support
- Caching mechanism with LRU implementation
- Data validation system
- JSON serialization support
- User preferences management
- Exception handling
- Dependency injection support with Koin
- Coroutines Flow integration for reactive programming

## Architecture

The module is organized into several key components:

### Contracts
- `DataStore`: Base interface for data storage operations
- `TypedDataStore`: Interface for type-safe data storage with key-value pairs
- `CacheableDataStore`: Interface for data storage with caching capabilities

### Strategy
- `TypeHandler`: Interface for handling type conversions
- `PrimitiveTypeHandlers`: Implementations for primitive types (String, Int, Boolean, etc.)

### Serialization
- `SerializationStrategy`: Interface for data serialization/deserialization
- `JsonSerializationStrategy`: JSON implementation using kotlinx.serialization

### Validation
- `DataStoreValidator`: Interface for data validation
- `DefaultDataStoreValidator`: Basic implementation that always returns valid

### Cache
- `CacheManager`: Interface for cache management
- `LRUCacheManager`: LRU (Least Recently Used) cache implementation

### Repository
- `UserPreferencesRepository`: Interface for user preferences management
- `DefaultUserPreferencesRepository`: Implementation using MultiplatformSettings

## Usage

### Basic Usage

```kotlin
// Create a data store for a custom type
@Serializable
data class UserData(val name: String, val age: Int)

// Create serializer and validator
val serializer = JsonSerializationStrategy(UserData.serializer())
val validator = DefaultDataStoreValidator<UserData>()

// Get DataStore instance from factory
val dataStore = dataStoreFactory.createDataStore(serializer, validator)

// Store data
dataStore.setData(UserData("John", 30))

// Get data as Flow
dataStore.getData().collect { userData ->
    println("User: ${userData.name}, Age: ${userData.age}")
}
```

### Using Typed DataStore

```kotlin
// Create a typed data store
val typedDataStore = dataStoreFactory.createTypedDataStore<UserData, String>(
    serializer,
    validator
)

// Store data with key
typedDataStore.setDataForKey("user1", UserData("John", 30))

// Get data by key
typedDataStore.getDataForKey("user1").collect { userData ->
    println("User: ${userData?.name}, Age: ${userData?.age}")
}
```

### Using Cacheable DataStore

```kotlin
// Create a cacheable data store
val cacheManager = LRUCacheManager<String, UserData>()
val cacheableDataStore = dataStoreFactory.createCacheableDataStore(
    serializer,
    validator,
    cacheManager
)

// Store and cache data
cacheableDataStore.setDataForKey("user1", UserData("John", 30))

// Get data from cache if available
cacheableDataStore.getCachedData("user1").collect { userData ->
    println("User: ${userData?.name}, Age: ${userData?.age}")
}
```

### Using User Preferences

```kotlin
// Get UserPreferencesRepository from DI
val userPreferences = get<UserPreferencesRepository>()

// Store preferences
userPreferences.setString("theme", "dark")
userPreferences.setBoolean("notifications", true)

// Get preferences
userPreferences.getString("theme").collect { theme ->
    println("Current theme: $theme")
}
```

## Dependencies

```kotlin
dependencies {
    implementation("com.russhwolf:multiplatform-settings:1.0.0")
    implementation("com.russhwolf:multiplatform-settings-serialization:1.0.0")
    implementation("com.russhwolf:multiplatform-settings-coroutines:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")
    implementation("io.insert-koin:koin-core:3.4.0")
}
```

## Testing

The module includes comprehensive test cases for all components. Run the tests using:

```bash
./gradlew :core-base:datastore:test
```

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the Mozilla Public License 2.0 - see the LICENSE file for details. 