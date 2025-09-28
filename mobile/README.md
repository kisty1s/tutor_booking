# Mobile Clients Overview

This folder contains starting points for the Flutter (Dart) application and a reusable Java Android module that communicate with the existing Spring Boot backend.

## Backend Endpoints Recap

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/bookings`
- `POST /api/v1/bookings/{id}/confirm`
- `POST /api/v1/bookings/{id}/cancel`
- `POST /api/v1/bookings/{id}/complete`
- `GET /api/v1/bookings/tutor/{tutorProfileId}`
- `GET /api/v1/bookings/student/{studentProfileId}`

All non-auth endpoints require a Bearer JWT.

## Flutter Client (Dart)

Draft project structure under `mobile/flutter_app/`:

```
flutter_app/
  lib/
    api/
      api_client.dart
      auth_service.dart
      booking_service.dart
    models/
      auth_models.dart
      booking_models.dart
    storage/jwt_storage.dart
    app.dart
    main.dart
  pubspec.yaml
```

- Uses `dio` for HTTP, `json_serializable` for models, `shared_preferences` to store JWT.
- Provides `AuthService` and `BookingService` wrappers around the REST endpoints.
- Handles 401/403 by clearing tokens and redirecting to login.

Run the generator once Flutter/Dart is installed:

```
flutter pub get
flutter pub run build_runner build --delete-conflicting-outputs
```

## Android Java Module

Draft project under `mobile/android-client/`:

```
android-client/
  build.gradle
  settings.gradle
  gradlew / gradlew.bat
  src/main/java/com/giasu/tutor/mobile/
    ApiConfig.java
    auth/AuthApi.java
    auth/AuthRepository.java
    booking/BookingApi.java
    booking/BookingRepository.java
    model/AuthModels.java
    model/BookingModels.java
    retrofit/RetrofitProvider.java
```

- Uses Retrofit + OkHttp + Gson for networking.
- Exposes repositories returning `CompletableFuture` (Java 11+) or RxJava (optional) for easy integration with native Android or bridging to Flutter via Platform Channels.
- Provides interceptor to inject JWT and transparently refresh/clear on 401.

To publish for other modules:

```
./gradlew publishToMavenLocal
```

## Integration Options

- **Flutter only**: Call Dart services directly.
- **Hybrid**: Flutter via MethodChannel interacts with the Java module (e.g., for shared logic, encryption, or offline caching).

## Next Steps Checklist

1. Copy/rename these templates into real Flutter/Android projects.
2. Wire UI (login, register, booking list/detail) to the services.
3. Implement simple state management (Provider/Bloc) on Flutter.
4. Configure CI to run `flutter test` and Java unit tests.
5. Keep API contracts in sync with the Spring Boot backend (consider generating clients from OpenAPI spec once available).