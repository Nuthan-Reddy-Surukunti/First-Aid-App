package com.example.firstaidapp.utils

import android.content.Context
import com.example.firstaidapp.data.database.AppDatabase
import com.example.firstaidapp.data.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataInitializer(private val context: Context) {

    fun initializeData() {
        val database = AppDatabase.getDatabase(context)
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        if (!prefs.getBoolean(Constants.KEY_DATA_INITIALIZED, false)) {
            CoroutineScope(Dispatchers.IO).launch {
                initializeGuides(database)
                initializeContacts(database)

                prefs.edit().putBoolean(Constants.KEY_DATA_INITIALIZED, true).apply()
            }
        }
    }

    private suspend fun initializeGuides(database: AppDatabase) {
        val guides = listOf(
            createCPRGuide(),
            createChokingGuide(),
            createBleedingGuide(),
            createBurnsGuide()
        )
        database.guideDao().insertAll(guides)
    }

    private suspend fun initializeContacts(database: AppDatabase) {
        val contacts = listOf(
            EmergencyContact(
                name = "Emergency Services",
                phoneNumber = "911",
                type = ContactType.EMERGENCY_SERVICE,
                isDefault = true
            ),
            EmergencyContact(
                name = "Poison Control",
                phoneNumber = "1-800-222-1222",
                type = ContactType.POISON_CONTROL,
                isDefault = true
            )
        )
        database.contactDao().insertAll(contacts)
    }

    private fun createCPRGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "cpr_adult",
            title = "CPR - Adult",
            category = Constants.CATEGORY_CPR,
            description = "Cardiopulmonary resuscitation (CPR) is a lifesaving technique for someone whose heart has stopped beating.",
            iconResName = "ic_cpr",
            severity = Constants.SEVERITY_CRITICAL,
            steps = listOf(
                GuideStep(stepNumber = 1, title = "Check Responsiveness", description = "Tap the person's shoulder and shout loudly 'Are you okay?'", imageResName = null, duration = "5 seconds"),
                GuideStep(stepNumber = 2, title = "Call 911", description = "If no response, immediately call 911 or ask someone else to do it", imageResName = null, duration = "10 seconds"),
                GuideStep(stepNumber = 3, title = "Position the Person", description = "Place the person on their back on a firm surface", imageResName = null, duration = "5 seconds"),
                GuideStep(stepNumber = 4, title = "Hand Placement", description = "Place the heel of one hand on the center of the chest, place your other hand on top", imageResName = null, duration = "5 seconds"),
                GuideStep(stepNumber = 5, title = "Chest Compressions", description = "Push hard and fast, at least 2 inches deep, at a rate of 100-120 compressions per minute", imageResName = null, duration = "Continuous"),
                GuideStep(stepNumber = 6, title = "Rescue Breaths", description = "After 30 compressions, give 2 rescue breaths. Tilt head back, lift chin, pinch nose, and breathe into mouth", imageResName = null, duration = "After every 30 compressions")
            ),
            warnings = listOf(
                "Only perform CPR if you are trained",
                "Do not stop CPR until help arrives or the person starts breathing",
                "Push hard and fast - you may break ribs but this is normal"
            ),
            whenToCallEmergency = "Call 911 immediately if person is unresponsive and not breathing normally"
        )
    }

    private fun createChokingGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "choking_adult",
            title = "Choking - Adult",
            category = Constants.CATEGORY_CHOKING,
            description = "Emergency response for a conscious person who cannot breathe due to airway obstruction.",
            iconResName = "ic_choking",
            severity = Constants.SEVERITY_CRITICAL,
            steps = listOf(
                GuideStep(stepNumber = 1, title = "Recognize Choking", description = "Person cannot speak, cough, or breathe. May clutch throat with hands", imageResName = null, duration = "Immediate"),
                GuideStep(stepNumber = 2, title = "Ask Permission", description = "Ask 'Are you choking? Can I help you?'", imageResName = null, duration = "2 seconds"),
                GuideStep(stepNumber = 3, title = "Stand Behind", description = "Stand behind the person and wrap your arms around their waist", imageResName = null, duration = "3 seconds"),
                GuideStep(stepNumber = 4, title = "Make a Fist", description = "Make a fist with one hand and place it just above the person's navel", imageResName = null, duration = "2 seconds"),
                GuideStep(stepNumber = 5, title = "Abdominal Thrusts", description = "Grasp your fist with your other hand and give quick, upward thrusts", imageResName = null, duration = "Until object is expelled"),
                GuideStep(stepNumber = 6, title = "Continue Until Clear", description = "Repeat thrusts until object is dislodged or person becomes unconscious", imageResName = null, duration = "Continuous")
            ),
            warnings = listOf(
                "Do not perform on infants under 1 year old",
                "Be careful with pregnant women or obese individuals",
                "If person becomes unconscious, begin CPR"
            ),
            whenToCallEmergency = "Call 911 if object cannot be removed or person loses consciousness"
        )
    }

    private fun createBleedingGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "severe_bleeding",
            title = "Severe Bleeding",
            category = Constants.CATEGORY_BLEEDING,
            description = "Steps to control severe bleeding and prevent shock.",
            iconResName = "ic_bleeding",
            severity = Constants.SEVERITY_HIGH,
            steps = listOf(
                GuideStep(stepNumber = 1, title = "Ensure Safety", description = "Make sure the scene is safe before approaching", imageResName = null, duration = "5 seconds"),
                GuideStep(stepNumber = 2, title = "Protect Yourself", description = "Wear gloves if available to prevent infection", imageResName = null, duration = "5 seconds"),
                GuideStep(stepNumber = 3, title = "Apply Direct Pressure", description = "Place clean cloth on wound and press firmly", imageResName = null, duration = "10 minutes"),
                GuideStep(stepNumber = 4, title = "Elevate", description = "If possible, raise the injured area above heart level", imageResName = null, duration = "Continuous"),
                GuideStep(stepNumber = 5, title = "Apply Bandage", description = "Secure the cloth with a bandage, maintaining pressure", imageResName = null, duration = "1 minute"),
                GuideStep(stepNumber = 6, title = "Monitor", description = "Check for signs of shock: pale skin, rapid breathing, weakness", imageResName = null, duration = "Continuous")
            ),
            warnings = listOf(
                "Do not remove embedded objects",
                "Do not lift the cloth to check bleeding - keep pressure constant",
                "Watch for signs of shock"
            ),
            whenToCallEmergency = "Call 911 for severe bleeding that doesn't stop after 10 minutes of pressure"
        )
    }

    private fun createBurnsGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "burns_treatment",
            title = "Burns Treatment",
            category = Constants.CATEGORY_BURNS,
            description = "First aid treatment for minor to moderate burns.",
            iconResName = "ic_burns",
            severity = Constants.SEVERITY_MEDIUM,
            steps = listOf(
                GuideStep(stepNumber = 1, title = "Stop the Burning", description = "Remove the person from the source of the burn", imageResName = null, duration = "Immediate"),
                GuideStep(stepNumber = 2, title = "Cool the Burn", description = "Run cool (not cold) water over the burn for 10-20 minutes", imageResName = null, duration = "10-20 minutes"),
                GuideStep(stepNumber = 3, title = "Remove Jewelry", description = "Remove rings, watches, or tight items before swelling begins", imageResName = null, duration = "1 minute"),
                GuideStep(stepNumber = 4, title = "Cover the Burn", description = "Apply a sterile, non-stick bandage or clean cloth", imageResName = null, duration = "2 minutes"),
                GuideStep(stepNumber = 5, title = "Pain Relief", description = "Give over-the-counter pain reliever if needed", imageResName = null, duration = "As directed"),
                GuideStep(stepNumber = 6, title = "Monitor", description = "Watch for signs of infection: increased pain, redness, swelling", imageResName = null, duration = "Daily")
            ),
            warnings = listOf(
                "Do not use ice - it can cause more damage",
                "Do not apply butter, oils, or ointments",
                "Do not break blisters",
                "Do not remove clothing stuck to the burn"
            ),
            whenToCallEmergency = "Call 911 for burns larger than 3 inches, on face/hands/feet/genitals, or if caused by chemicals or electricity"
        )
    }
}
