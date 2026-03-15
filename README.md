## Vietnamese Lunar Calendar - Android Application

__A comprehensive Vietnamese lunar calendar application with event management and notification system__

### Overview

Vietnamese Lunar Calendar is a feature-rich Android application designed to help users manage their schedules according to both solar and lunar calendars. The app provides accurate conversion between solar and lunar dates, event management with notifications, and special day tracking based on Vietnamese cultural traditions.

### Key Features

__📅 Dual Calendar System__

- Accurate conversion between Gregorian (solar) and Vietnamese lunar calendar
- Support for leap months and complex lunar calculations
- Date range from 1900 to 2100 with high precision

__🗓️ Event Management__

- Create, edit, and delete events with both solar and lunar date support
- Event categorization (Birthday, Anniversary, Work, Personal, Family, etc.)
- Optional notifications with 6-hour advance alerts
- Yearly recurring events based on lunar dates

__🌟 Special Day Tracking__

- Custom special day creation with lunar-based recurrence
- Automatic recognition of important lunar dates 
- Daily notifications at 6 PM for upcoming special days
- Special day indicators displayed on calendar grid

__🔔 Smart Notifications__

- Two-tier notification system for events and special days
- Configurable notification times
- Support for exact alarm scheduling (Android 12+ compatible)
- Automatic daily checks for special days

__📱 User Experience__

- Intuitive grid-based calendar interface
- Color-coded event types for easy identification
- Month and year selection with spinners
- Clean Material Design interface with Vietnamese cultural colors

### Technical Architecture

__Backend & Database__

- SQLite database with two main tables: `events` and `special_days`
- Custom lunar calendar algorithm with pre-calculated data from 1900-2100
- Efficient date conversion and event retrieval methods

__Android Components__

- Multiple Activities: MainActivity, AddEventActivity, EventListActivity, SpecialDayListActivity, NotificationSettingsActivity
- Custom adapters for RecyclerView and GridView
- BroadcastReceiver for alarm and notification handling
- Fragment-based dialogs for user interactions

__Key Classes__

- `LunarCalendar`: Core conversion algorithm between solar and lunar dates
- `DatabaseHelper`: SQLite database management with CRUD operations
- `NotificationHelper`: Event notification scheduling and management
- `SpecialDayNotificationHelper`: Special day notifications and daily checks
- `CalendarAdapter`: Custom grid adapter for calendar display
- `EventManager`: Business logic for event operations

### Technology Stack

- __Language__: Java
- __Platform__: Android SDK
- __Build System__: Gradle 8.5.2
- __Target SDK__: 34 (Android 14)
- __Minimum SDK__: 21 (Android 5.0)
- __Database__: SQLite
- __Architecture__: MVC pattern with separation of concerns

### Cultural Features

- Designed specifically for Vietnamese lunar calendar traditions
- Supports important Vietnamese cultural dates and celebrations
- Lunar-based event recurrence for traditional holidays
- Integration with Vietnamese holiday and festival calendar

### Installation & Usage

The application can be built and installed on Android devices running Android 5.0 (API level 21) or higher. The app requires permissions for notifications and exact alarm scheduling to function properly.

This application serves as a comprehensive solution for Vietnamese users who need to manage their schedules according to both modern Gregorian calendar and traditional lunar calendar systems.
