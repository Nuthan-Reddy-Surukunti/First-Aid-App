# Location-Based Emergency Contacts Feature

## Overview
This feature enhances the emergency contacts section of the First Aid App by providing location-based emergency contacts for Indian states and union territories. Users can either use their device's GPS to automatically detect their location or manually select their state to view relevant emergency numbers.

## Features Implemented

### 1. GPS-Based Location Detection
- Uses Google Play Services Location API for accurate location detection
- Automatically determines the user's state based on GPS coordinates
- Uses Geocoder to reverse geocode coordinates to state names
- Handles permission requests gracefully with user-friendly dialogs

### 2. Manual State Selection
- Provides a fallback option for users who don't want to share location
- Shows a list of supported states for manual selection
- Includes 10 Indian states and union territories

### 3. Comprehensive Emergency Contacts Database
- **National Services** (available for all states):
  - 112: Integrated Emergency Helpline (ERSS)
  - 100: Police Emergency
  - 101: Fire Brigade
  - 108: Ambulance Services
  - 181: Women Domestic Abuse Helpline
  - 1091: Women General Helpline
  - 1070: Disaster Management Control Room
  - 14416: Mental Health Helpline (Tele-MANAS)
  - 1098: Child Helpline

- **State-Specific Services** for:
  - Andhra Pradesh
  - Karnataka
  - Kerala
  - Telangana
  - Tripura
  - Delhi (NCT)
  - Chandigarh
  - Ladakh
  - Lakshadweep
  - Andaman and Nicobar Islands

### 4. Smart Contact Filtering
- Shows national emergency numbers to all users
- When a state is selected, shows both national and state-specific contacts
- National contacts appear first, followed by state-specific ones
- Option to show all contacts regardless of state

## Technical Implementation

### Files Created/Modified

#### New Files:
1. **EmergencyContactsData.kt** - Contains all emergency contacts data
2. **LocationHelper.kt** - Utility class for GPS location detection
3. **dialog_select_state.xml** - UI dialog for state selection

#### Modified Files:
1. **AndroidManifest.xml** - Added location permissions
2. **EmergencyContact.kt** - Added state field
3. **ContactDao.kt** - Added queries for state-based filtering
4. **ContactsViewModel.kt** - Added state selection logic
5. **ContactsFragment.kt** - Added UI for state selection and location detection
6. **DataInitializer.kt** - Updated to load comprehensive contacts
7. **AppDatabase.kt** - Added migration for state field
8. **fragment_contacts.xml** - Added state selection button
9. **build.gradle.kts** - Added Google Play Services dependency
10. **libs.versions.toml** - Added location dependency version

### Database Schema Changes
- Added `state` field to `emergency_contacts` table (nullable TEXT)
- Migration from version 6 to 7 implemented
- Automatic migration applied on app update

### Permissions Required
- `ACCESS_FINE_LOCATION` - For accurate GPS location
- `ACCESS_COARSE_LOCATION` - For approximate location (fallback)

## Usage

### For Users:
1. Open the Contacts section
2. Tap the location icon (🗺️) in the top-right corner
3. Choose one of three options:
   - **Use My Location (GPS)**: Automatically detect your state
   - **Select State Manually**: Choose from a list
   - **Show All Contacts**: View contacts from all states

### For Developers:
```kotlin
// Get location helper instance
val locationHelper = LocationHelper(context)

// Check if permission is granted
if (locationHelper.hasLocationPermission()) {
    // Get current state
    val state = locationHelper.getCurrentState()
}

// Set state in ViewModel to filter contacts
viewModel.setSelectedState("Karnataka")

// Get all emergency contacts
val contacts = EmergencyContactsData.getAllEmergencyContacts()

// Get list of supported states
val states = EmergencyContactsData.getStatesList()
```

## Testing Recommendations

### Manual Testing:
1. Test location permission flow (grant/deny)
2. Test GPS location detection in different states
3. Test manual state selection
4. Verify national contacts appear for all states
5. Verify state-specific contacts appear correctly
6. Test "Show All Contacts" option
7. Test search functionality with new contacts
8. Test adding custom contacts still works

### Edge Cases:
- Location permission denied
- GPS unavailable/disabled
- Geocoder fails to resolve state
- User is in an unsupported state
- Database migration from older versions

## Future Enhancements
1. Add more Indian states and union territories
2. Cache last selected state in SharedPreferences
3. Add hospital and clinic contacts with distances
4. Integrate with Google Maps for directions
5. Add offline map data for emergency locations
6. Support multiple languages for contact descriptions
7. Add emergency contact quick dial widget
8. Implement contact sharing functionality

## Dependencies Added
- `com.google.android.gms:play-services-location:21.0.1`

## Database Version
- Updated from version 6 to version 7
- Migration handles addition of `state` field

## Security Considerations
- Location permission is requested only when needed
- User can always choose manual selection instead of GPS
- Location data is not stored or transmitted
- All contacts are public emergency services

## Accessibility
- All UI elements have proper content descriptions
- Dialogs are keyboard navigable
- High contrast colors maintained
- Screen reader compatible

## Performance
- Location detection is asynchronous and non-blocking
- Database queries optimized with proper indexing
- Contacts loaded lazily via LiveData
- No network calls required (all data is local)
