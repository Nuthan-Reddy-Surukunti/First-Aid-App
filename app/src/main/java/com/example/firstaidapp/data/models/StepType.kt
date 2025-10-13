package com.example.firstaidapp.data.models

enum class StepType {
    ASSESSMENT,      // Check condition/responsiveness
    EMERGENCY_CALL,  // Call 911 or emergency services
    POSITIONING,     // Position the person
    ACTION,          // Perform the first aid action
    MONITORING,      // Monitor vital signs
    SAFETY,          // Safety precautions
    FOLLOW_UP,       // Post-treatment care

    // Additional values used in FirstAidGuidesRepository
    CHECK,           // Assessment/checking steps
    CALL,            // Emergency calling steps
    REPEAT           // Repeated actions
}
