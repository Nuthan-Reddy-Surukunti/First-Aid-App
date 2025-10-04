# Requirements Document

## Introduction

The Emergency Guide System is the core functionality of the First Aid Guide Android application. This system provides users with quick access to life-saving first aid instructions through an intuitive, offline-first mobile interface. The system must work reliably during emergency situations when users are under stress and need clear, step-by-step guidance.

## Requirements

### Requirement 1

**User Story:** As a person responding to an emergency, I want to quickly find the appropriate first aid guide, so that I can provide immediate help to someone in need.

#### Acceptance Criteria

1. WHEN the user opens the app THEN the system SHALL display a home screen with 8-10 emergency categories within 2 seconds
2. WHEN the user taps on an emergency category THEN the system SHALL navigate to the detailed guide within 1 second
3. IF the device has no internet connection THEN the system SHALL still provide full functionality using local data
4. WHEN the user searches for a condition THEN the system SHALL return relevant results in less than 1 second

### Requirement 2

**User Story:** As someone providing first aid, I want clear step-by-step instructions with visual aids, so that I can follow the procedures correctly even under stress.

#### Acceptance Criteria

1. WHEN the user views a first aid guide THEN the system SHALL display numbered steps with concise instructions (2-3 sentences maximum)
2. WHEN the user is on a guide step THEN the system SHALL show a progress indicator displaying current step and total steps
3. WHEN the user navigates between steps THEN the system SHALL provide "Previous" and "Next" buttons that are easily tappable
4. IF a step has safety warnings THEN the system SHALL display them prominently with visual indicators
5. WHEN the user views any guide THEN the system SHALL always show a "Call Emergency Services" button

### Requirement 3

**User Story:** As someone in an emergency situation, I want hands-free voice guidance, so that I can receive instructions while keeping my hands free to provide aid.

#### Acceptance Criteria

1. WHEN the user activates voice mode THEN the system SHALL read each step aloud using text-to-speech
2. WHEN voice mode is active THEN the system SHALL highlight the current step being read
3. WHEN the user says "next step" or taps the screen THEN the system SHALL advance to the next instruction
4. WHEN the user says "repeat" THEN the system SHALL re-read the current step
5. IF the screen locks during voice mode THEN the system SHALL continue reading instructions using a foreground service
6. WHEN voice mode is active THEN the system SHALL provide pause and resume functionality

### Requirement 4

**User Story:** As someone performing CPR, I want audio-visual rhythm guidance, so that I can maintain the correct compression rate and count.

#### Acceptance Criteria

1. WHEN the user starts CPR mode THEN the system SHALL provide beats at 100-120 BPM with audio cues
2. WHEN CPR mode is active THEN the system SHALL display a pulsing visual indicator synchronized with the beat
3. WHEN the user performs compressions THEN the system SHALL count from 1 to 30 and then prompt for rescue breaths
4. WHEN 30 compressions are completed THEN the system SHALL play a different sound and display "Give 2 breaths"
5. WHEN the user completes a full cycle THEN the system SHALL increment the cycle counter
6. IF the user enables haptic feedback THEN the system SHALL provide vibration on each beat

### Requirement 5

**User Story:** As someone unsure about the emergency type, I want guided questions to help identify the appropriate first aid response, so that I can quickly determine the right course of action.

#### Acceptance Criteria

1. WHEN the user selects "Help Me Decide" THEN the system SHALL start with a clear yes/no question about the person's condition
2. WHEN the user answers a question THEN the system SHALL progress to the next relevant question or final recommendation
3. WHEN the decision tree is complete THEN the system SHALL recommend a specific first aid guide and provide quick action buttons
4. IF the user wants to go back THEN the system SHALL allow navigation to previous questions
5. WHEN the user reaches a conclusion THEN the system SHALL display the recommended guide with option to start immediately

### Requirement 6

**User Story:** As a frequent app user, I want to bookmark commonly needed guides, so that I can access them quickly without searching.

#### Acceptance Criteria

1. WHEN the user views any guide THEN the system SHALL display a bookmark icon
2. WHEN the user taps the bookmark icon THEN the system SHALL add the guide to favorites and update the icon state
3. WHEN the user accesses the favorites section THEN the system SHALL display all bookmarked guides
4. WHEN the user taps a bookmarked guide THEN the system SHALL open it directly
5. IF the user has no favorites THEN the system SHALL display an appropriate empty state message

### Requirement 7

**User Story:** As someone in an emergency, I want quick access to emergency contact numbers, so that I can call for professional help immediately.

#### Acceptance Criteria

1. WHEN the user accesses emergency contacts THEN the system SHALL display pre-loaded emergency numbers (102, 108, 100, 101, 112)
2. WHEN the user taps an emergency number THEN the system SHALL open the phone dialer with the number pre-filled
3. WHEN the user wants to add personal contacts THEN the system SHALL allow adding up to 3 emergency contacts with name, relationship, and phone number
4. WHEN the user manages personal contacts THEN the system SHALL provide options to add, edit, and delete contacts
5. IF the user has personal emergency contacts THEN the system SHALL display them prominently above general emergency numbers

### Requirement 8

**User Story:** As someone looking for specific first aid information, I want to search by condition, symptom, or body part, so that I can quickly find relevant guidance.

#### Acceptance Criteria

1. WHEN the user types in the search bar THEN the system SHALL show real-time results as they type
2. WHEN the user searches for a condition THEN the system SHALL match against guide titles, descriptions, and keywords
3. WHEN the user performs a search THEN the system SHALL store it in search history (last 5 searches)
4. WHEN no results are found THEN the system SHALL display helpful suggestions or alternative search terms
5. IF the user clears search history THEN the system SHALL remove all stored search terms