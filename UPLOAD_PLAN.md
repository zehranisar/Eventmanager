# Event Manager - 5-Day GitHub Upload Plan

## Project Overview
Android Event Manager application with Django backend integration

## Day-by-Day Upload Plan

### **Day 1: Project Foundation & Configuration** ✅
**Goal**: Set up project structure, configuration files, and documentation

**Files to upload:**
- `.gitignore`
- `build.gradle.kts` (root)
- `settings.gradle.kts`
- `gradle.properties`
- `gradle/libs.versions.toml`
- `gradlew` and `gradlew.bat`
- `gradle/wrapper/` files
- `ADB_REVERSE_GUIDE.md`
- `API_CONNECTION_GUIDE.md`
- `app/.gitignore`
- `app/build.gradle.kts`
- `app/proguard-rules.pro`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/res/values/` (strings.xml, colors.xml, themes.xml)
- `app/src/main/res/xml/` (backup_rules.xml, data_extraction_rules.xml)
- `backend/` structure (initial setup)
- `start-adb-reverse.bat`

**Commit message**: "Day 1: Project foundation, build configuration, and documentation"

---

### **Day 2: Core Models, API Infrastructure & Utilities**
**Goal**: Upload data models, API services, request/response classes, and utility classes

**Files to upload:**
- `app/src/main/java/com/example/eventmanager/models/` (Event.java, User.java)
- `app/src/main/java/com/example/eventmanager/api/ApiConfig.java`
- `app/src/main/java/com/example/eventmanager/api/ApiService.java`
- `app/src/main/java/com/example/eventmanager/api/request/` (all request classes)
- `app/src/main/java/com/example/eventmanager/api/response/` (all response classes)
- `app/src/main/java/com/example/eventmanager/utils/SessionManager.java`
- `app/src/main/java/com/example/eventmanager/utils/SharedPreferencesHelper.java`

**Commit message**: "Day 2: Core models, API infrastructure, and utility classes"

---

### **Day 3: Authentication & Splash Screen**
**Goal**: Upload authentication flow and splash screen

**Files to upload:**
- `app/src/main/java/com/example/eventmanager/SplashActivity.java`
- `app/src/main/java/com/example/eventmanager/LoginActivity.java`
- `app/src/main/java/com/example/eventmanager/SignUpActivity.java`
- `app/src/main/java/com/example/eventmanager/ForgotPasswordActivity.java`
- `app/src/main/res/layout/activity_splash.xml`
- `app/src/main/res/layout/activity_login.xml`
- `app/src/main/res/layout/activity_signup.xml`
- `app/src/main/res/layout/activity_forgot_password.xml`
- `app/src/main/res/drawable/` (authentication-related drawables)
- `app/src/main/res/drawable/gradient_splash.xml`
- `app/src/main/res/drawable/gradient_background.xml`
- `app/src/main/res/drawable/input_background.xml`
- `app/src/main/res/drawable/button_primary.xml`
- `app/src/main/res/drawable/button_outlined.xml`
- `app/src/main/res/drawable/card_rounded.xml`

**Commit message**: "Day 3: Authentication activities and splash screen"

---

### **Day 4: Main Features & Event Management**
**Goal**: Upload main activities and event-related features

**Files to upload:**
- `app/src/main/java/com/example/eventmanager/MainActivity.java`
- `app/src/main/java/com/example/eventmanager/EventDetailActivity.java`
- `app/src/main/java/com/example/eventmanager/AddEventActivity.java`
- `app/src/main/java/com/example/eventmanager/MyEventsActivity.java`
- `app/src/main/java/com/example/eventmanager/RegisterEventActivity.java`
- `app/src/main/java/com/example/eventmanager/adapters/EventAdapter.java`
- `app/src/main/res/layout/activity_main.xml`
- `app/src/main/res/layout/activity_event_detail.xml`
- `app/src/main/res/layout/activity_add_event.xml`
- `app/src/main/res/layout/activity_my_events.xml`
- `app/src/main/res/layout/activity_register_event.xml`
- `app/src/main/res/layout/item_event.xml`
- `app/src/main/res/layout/item_event_registration.xml`
- `app/src/main/res/layout/item_registration_detail.xml`
- `app/src/main/res/layout/item_user_registration.xml`
- `app/src/main/res/layout/dialog_set_reminder.xml`
- `app/src/main/res/drawable/item-related drawables`
- `app/src/main/res/drawable/category_chip.xml`
- `app/src/main/res/drawable/category_background.xml`
- `app/src/main/res/menu/menu_main.xml`

**Commit message**: "Day 4: Main activities, event management, and adapters"

---

### **Day 5: Admin Features, Resources & Final Cleanup**
**Goal**: Upload admin features, remaining resources, and finalize

**Files to upload:**
- `app/src/main/java/com/example/eventmanager/AdminDashboardActivity.java`
- `app/src/main/res/layout/activity_admin_dashboard.xml`
- `app/src/main/res/menu/menu_admin_dashboard.xml`
- `app/src/main/res/drawable/ic_admin.xml`
- `app/src/main/res/drawable/ic_my_events.xml`
- `app/src/main/res/drawable/ic_logout.xml`
- `app/src/main/res/drawable/ic_student.xml`
- `app/src/main/res/drawable/ic_time.xml`
- `app/src/main/res/drawable/ic_reminder.xml`
- `app/src/main/res/drawable/ic_person.xml`
- `app/src/main/res/drawable/ic_lock.xml`
- `app/src/main/res/drawable/ic_email.xml`
- `app/src/main/res/drawable/ic_delete.xml`
- `app/src/main/res/drawable/ic_check_circle.xml`
- `app/src/main/res/drawable/ic_add_event.xml`
- `app/src/main/res/drawable/ic_location.xml`
- `app/src/main/res/drawable/ic_calendar.xml`
- `app/src/main/res/drawable/ic_event_logo.xml`
- `app/src/main/res/drawable/ic_edit.xml`
- `app/src/main/res/drawable/ic_back.xml`
- `app/src/main/res/drawable/circle_icon_background.xml`
- `app/src/main/res/drawable/button_accent.xml`
- `app/src/main/res/mipmap/` (all launcher icons)
- `app/src/main/res/values-night/themes.xml`
- `README.md` (if exists or create one)
- Test files (if any)
- Any remaining configuration files

**Commit message**: "Day 5: Admin dashboard, remaining resources, and project completion"

---

## Notes:
- Each day's work should be tested before committing
- Ensure `.gitignore` properly excludes build files and local.properties
- All sensitive information should be removed or moved to local.properties
- Backend files should be included if they're part of the project
- Documentation should be clear and up-to-date

