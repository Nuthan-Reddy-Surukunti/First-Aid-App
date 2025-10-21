package com.example.firstaidapp.data.models

enum class StepType {
    CHECK,          // Assessment or checking steps
    CALL,           // Emergency calling steps
    ACTION,         // Physical action steps
    SAFETY,         // Safety-related steps
    REPEAT,         // Steps that need to be repeated
    EMERGENCY_CALL, // Specific emergency calling
    WAIT,           // Waiting steps
    OBSERVE,        // Observation steps
    ASSESSMENT,     // Assessment steps
    POSITIONING,    // Positioning steps
    MONITORING,     // Monitoring steps
    FOLLOW_UP       // Follow-up steps
}
