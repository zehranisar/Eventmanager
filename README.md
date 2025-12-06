# Event Manager - Android Application

A comprehensive Android application for managing events, designed for both students and administrators. The app allows users to register for events, set reminders, and manage their event participation.

## Features

### For Students
- **User Authentication**: Login, Sign Up, and Forgot Password functionality
- **Browse Events**: View upcoming events with details
- **Event Registration**: Register for events directly from the app
- **Reminders**: Set reminders for upcoming events
- **My Events**: View registered events and manage participation
- **Profile Management**: View and update profile information

### For Administrators
- **Admin Dashboard**: Comprehensive dashboard with event statistics
- **Create Events**: Add new events with detailed information
- **Manage Events**: Update and delete events
- **View Registrations**: See all event registrations and attendees
- **User Management**: Manage user registrations

## Technical Stack

- **Language**: Java
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Build Tool**: Gradle with Kotlin DSL
- **Architecture**: MVC Pattern
- **API Communication**: Retrofit 2.9.0
- **JSON Parsing**: Gson 2.10.1
- **UI Components**: Material Design Components

## Project Structure

```
Eventmanager/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/eventmanager/
│   │       │   ├── adapters/          # RecyclerView adapters
│   │       │   ├── api/               # API services and models
│   │       │   │   ├── request/       # API request models
│   │       │   │   └── response/      # API response models
│   │       │   ├── models/            # Data models
│   │       │   └── utils/             # Utility classes
│   │       ├── res/                   # Resources (layouts, drawables, values)
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts
├── backend/                           # Backend API (Django)
├── gradle/
└── build.gradle.kts
```

## Setup Instructions

### Prerequisites
- Android Studio (latest version recommended)
- JDK 11 or higher
- Android SDK (API 24+)
- Backend server running (Django REST API)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/zehranisar/EventManager.git
   cd EventManager
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Configure API Endpoint**
   - The app connects to a Django backend API
   - Update the API base URL in `app/src/main/java/com/example/eventmanager/api/ApiConfig.java`
   - See `API_CONNECTION_GUIDE.md` for detailed connection setup

4. **Setup ADB Reverse (for real device testing)**
   ```bash
   adb reverse tcp:8000 tcp:8000
   ```
   Or use the provided batch file: `start-adb-reverse.bat`

5. **Build and Run**
   - Sync Gradle files
   - Build the project
   - Run on an emulator or connected device

## API Connection Setup

The app needs to connect to a Django backend server. See the detailed guides:
- `API_CONNECTION_GUIDE.md` - Complete API connection setup instructions
- `ADB_REVERSE_GUIDE.md` - ADB reverse port forwarding guide for device testing

### Connection Methods:
1. **Android Emulator**: Automatically uses `10.0.2.2` (no setup needed)
2. **Real Device via USB**: Requires ADB reverse port forwarding
3. **Real Device via WiFi**: Configure with your computer's IP address

## API Endpoints

The app uses the following main API endpoints:
- `/auth/register/` - User registration
- `/auth/login/` - User login
- `/auth/forgot-password/` - Password reset
- `/events/` - Get all events
- `/events/{id}/` - Get event details
- `/events/{id}/register/` - Register for event
- `/events/{id}/set-reminder/` - Set reminder
- `/dashboard/` - User dashboard
- `/admin/dashboard/` - Admin dashboard

## Dependencies

Key dependencies used in this project:
- AndroidX AppCompat
- Material Design Components
- Retrofit & OkHttp for networking
- Gson for JSON parsing
- Navigation Component
- RecyclerView & CardView

## Build Variants

- **Debug**: Development build with logging enabled
- **Release**: Production build (ProGuard disabled by default)

## Contributing

This project follows a structured 5-day upload plan. See `UPLOAD_PLAN.md` for the detailed upload schedule.

## License

This project is part of an educational/portfolio project.

## Support

For issues related to:
- **API Connection**: Check `API_CONNECTION_GUIDE.md`
- **ADB Setup**: Check `ADB_REVERSE_GUIDE.md`
- **Build Issues**: Ensure all dependencies are synced in Android Studio

## Author

Developed as part of the Event Manager project.

