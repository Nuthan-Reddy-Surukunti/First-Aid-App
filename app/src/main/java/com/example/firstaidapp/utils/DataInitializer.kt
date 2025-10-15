package com.example.firstaidapp.utils

import android.content.Context
import android.util.Log
import com.example.firstaidapp.data.database.AppDatabase
import com.example.firstaidapp.data.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DataInitializer(private val context: Context) {

    private val TAG = "DataInitializer"

    /**
     * Public non-blocking entry used by older callers: launches the suspending initializer on IO.
     */
    fun initializeData() {
        CoroutineScope(Dispatchers.IO).launch {
            initializeDataBlocking()
        }
    }

    /**
     * Suspends until initialization completes. This is the entrypoint used by WorkManager.
     */
    suspend fun initializeDataBlocking() {
        try {
            Log.i(TAG, "initializeData: starting initialization on IO thread (blocking)")
            val database = AppDatabase.getDatabase(context)
            val prefs = context.getSharedPreferences("first_aid_prefs", Context.MODE_PRIVATE)

            val isInitializedPref = prefs.getBoolean("data_initialized", false)

            // Also check actual table contents to handle destructive migrations or cleared app data
            val guidesCount = try { database.guideDao().getGuidesCount() } catch (e: Exception) { Log.e(TAG, "count guides failed", e); 0 }
            val contactsCount = try { database.contactDao().getContactsCount() } catch (e: Exception) { Log.e(TAG, "count contacts failed", e); 0 }

            Log.i(TAG, "initializeData: pref=$isInitializedPref, guidesCount=$guidesCount, contactsCount=$contactsCount")

            // Force reinitialization if we don't have all 20 guides
            val expectedGuidesCount = 20
            val needsFullReinitialization = guidesCount < expectedGuidesCount

            if (needsFullReinitialization) {
                Log.i(TAG, "initializeData: forcing reinitialization - found $guidesCount guides, expected $expectedGuidesCount")

                // Clear existing data and reinitialize
                database.guideDao().deleteAllGuides()
                if (contactsCount == 0) {
                    database.contactDao().deleteAllContacts()
                }

                // Reset preferences
                prefs.edit().putBoolean("data_initialized", false).apply()

                // Initialize all guides
                try {
                    Log.i(TAG, "initializeData: initializing all $expectedGuidesCount guides")
                    initializeGuides(database)
                    Log.i(TAG, "initializeData: guides initialized")
                } catch (e: Exception) {
                    Log.e(TAG, "Error initializing guides", e)
                    return
                }

                // Initialize contacts if needed
                if (contactsCount == 0) {
                    try {
                        Log.i(TAG, "initializeData: initializing contacts")
                        initializeContacts(database)
                        Log.i(TAG, "initializeData: contacts initialized")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error initializing contacts", e)
                    }
                }

                // Mark as initialized
                prefs.edit().putBoolean("data_initialized", true).apply()
                Log.i(TAG, "initializeData: marked data_initialized=true")
            } else {
                Log.i(TAG, "initializeData: all guides already present ($guidesCount/$expectedGuidesCount)")
            }

        } catch (e: Throwable) {
            // Catch Throwable because process-kill related issues might throw Errors
            Log.e(TAG, "initializeData: unexpected error", e)
        }
    }

    private suspend fun initializeGuides(database: AppDatabase) {
        val guides = listOf(
            createCPRGuide(),
            createChokingGuide(),
            createBleedingGuide(),
            createBurnsGuide(),
            createFracturesGuide(),
            createPoisoningGuide(),
            createShockGuide(),
            createHeartAttackGuide(),
            createStrokeGuide(),
            createAllergicReactionGuide(),
            // Adding the missing 10 guides
            createSprainsStrainsGuide(),
            createHypothermiaGuide(),
            createHeatExhaustionGuide(),
            createSeizuresGuide(),
            createBitesStingsGuide(),
            createAsthmaAttackGuide(),
            createDiabeticEmergenciesGuide(),
            createDrowningGuide(),
            createNosebleedsGuide(),
            createEyeInjuriesGuide()
        )

        // Insert guides in small batches to avoid heavy DB pressure at startup
        for ((index, guide) in guides.withIndex()) {
            try {
                Log.i(TAG, "insertGuide: inserting ${guide.id} (index=${index})")
                database.guideDao().insertGuide(guide)
                // Small delay to yield and reduce immediate pressure
                delay(50)
            } catch (e: Exception) {
                Log.e(TAG, "insertGuide: failed to insert ${guide.id}", e)
            }
        }

        Log.i(TAG, "initializeGuides: completed inserting ${guides.size} guides")
    }

    private suspend fun initializeContacts(database: AppDatabase) {
        val contacts = listOf(
            EmergencyContact(
                name = "Emergency Services",
                phoneNumber = "911",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Call for immediate emergency assistance"
            ),
            EmergencyContact(
                name = "Poison Control Center",
                phoneNumber = "1-800-222-1222",
                type = ContactType.POISON_CONTROL,
                relationship = "Emergency",
                notes = "24/7 poison emergency hotline"
            ),
            EmergencyContact(
                name = "Local Fire Department",
                phoneNumber = "911",
                type = ContactType.FIRE_DEPARTMENT,
                relationship = "Emergency",
                notes = "Fire and rescue services"
            ),
            EmergencyContact(
                name = "Local Police",
                phoneNumber = "911",
                type = ContactType.POLICE,
                relationship = "Emergency",
                notes = "Police emergency services"
            )
        )

        // Insert contacts one by one and yield
        for ((index, contact) in contacts.withIndex()) {
            try {
                Log.i(TAG, "insertContact: inserting ${contact.name} (index=${index})")
                database.contactDao().insertContact(contact)
                delay(30)
            } catch (e: Exception) {
                Log.e(TAG, "insertContact: failed to insert ${contact.name}", e)
            }
        }

        Log.i(TAG, "initializeContacts: completed inserting ${contacts.size} contacts")
    }

    // Guide creation functions for all 20 guides
    private fun createCPRGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "cpr_guide",
            title = "CPR (Cardiopulmonary Resuscitation)",
            category = "Life-Threatening",
            severity = "CRITICAL",
            description = "Learn how to perform CPR to save a life when someone's heart stops beating.",
            steps = listOf(
                GuideStep(id = "cpr_step_1", guideId = "cpr_guide", stepNumber = 1, title = "Check Responsiveness", description = "Tap shoulders and shout 'Are you okay?'", stepType = StepType.CHECK, isCritical = true),
                GuideStep(id = "cpr_step_2", guideId = "cpr_guide", stepNumber = 2, title = "Call for Help", description = "Call 911 immediately", stepType = StepType.CALL, isCritical = true),
                GuideStep(id = "cpr_step_3", guideId = "cpr_guide", stepNumber = 3, title = "Position Patient", description = "Position on firm surface, face up", stepType = StepType.ACTION),
                GuideStep(id = "cpr_step_4", guideId = "cpr_guide", stepNumber = 4, title = "Hand Placement", description = "Place heel of hand on center of chest", stepType = StepType.ACTION, isCritical = true),
                GuideStep(id = "cpr_step_5", guideId = "cpr_guide", stepNumber = 5, title = "Chest Compressions", description = "Push hard and fast at least 2 inches deep", stepType = StepType.ACTION, isCritical = true),
                GuideStep(id = "cpr_step_6", guideId = "cpr_guide", stepNumber = 6, title = "Continue Until Help Arrives", description = "Keep doing compressions until emergency services arrive", stepType = StepType.REPEAT, isCritical = true)
            ),
            iconResName = "ic_cpr",
            whenToCallEmergency = "Person is unresponsive and not breathing normally",
            warnings = listOf("Only perform if person is unresponsive", "Don't be afraid to push hard"),
            estimatedTimeMinutes = 0,
            difficulty = "Intermediate"
        )
    }

    private fun createChokingGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "choking_guide",
            title = "Choking Emergency",
            category = "Respiratory",
            severity = "CRITICAL",
            description = "Quick response to save someone who is choking and cannot breathe.",
            steps = listOf(
                GuideStep(id = "choking_step_1", guideId = "choking_guide", stepNumber = 1, title = "Assess Situation", description = "Ask 'Are you choking?' - if they can't speak, act immediately", stepType = StepType.CHECK, isCritical = true),
                GuideStep(id = "choking_step_2", guideId = "choking_guide", stepNumber = 2, title = "Position Behind Person", description = "Stand behind and wrap arms around waist", stepType = StepType.ACTION),
                GuideStep(id = "choking_step_3", guideId = "choking_guide", stepNumber = 3, title = "Abdominal Thrusts", description = "Give quick upward thrusts into abdomen", stepType = StepType.ACTION, isCritical = true)
            ),
            iconResName = "ic_choking",
            whenToCallEmergency = "Person cannot speak, cough, or breathe",
            warnings = listOf("Don't perform on pregnant women or infants"),
            estimatedTimeMinutes = 2,
            difficulty = "Beginner"
        )
    }

    private fun createBleedingGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "bleeding_guide",
            title = "Severe Bleeding Control",
            category = "Trauma",
            severity = "HIGH",
            description = "Stop severe bleeding to prevent shock and save a life.",
            steps = listOf(
                GuideStep(id = "bleeding_step_1", guideId = "bleeding_guide", stepNumber = 1, title = "Call Emergency", description = "Call 911 for severe bleeding", stepType = StepType.CALL, isCritical = true),
                GuideStep(id = "bleeding_step_2", guideId = "bleeding_guide", stepNumber = 2, title = "Apply Direct Pressure", description = "Apply direct pressure with clean cloth", stepType = StepType.ACTION, isCritical = true)
            ),
            iconResName = "ic_bleeding",
            whenToCallEmergency = "Heavy, continuous bleeding or blood spurting from wound",
            estimatedTimeMinutes = 10,
            difficulty = "Intermediate"
        )
    }

    private fun createBurnsGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "burns_guide",
            title = "Burns Treatment",
            category = "Trauma",
            severity = "MEDIUM",
            description = "Proper treatment of burns to prevent infection and promote healing.",
            steps = listOf(
                GuideStep(id = "burns_step_1", guideId = "burns_guide", stepNumber = 1, title = "Remove from Source", description = "Remove person from heat source", stepType = StepType.SAFETY, isCritical = true),
                GuideStep(id = "burns_step_2", guideId = "burns_guide", stepNumber = 2, title = "Cool the Burn", description = "Cool with lukewarm water for 10-20 minutes", stepType = StepType.ACTION, isCritical = true),
                GuideStep(id = "burns_step_3", guideId = "burns_guide", stepNumber = 3, title = "Apply Bandage", description = "Apply loose, sterile bandage", stepType = StepType.ACTION)
            ),
            iconResName = "ic_burns",
            whenToCallEmergency = "Burns larger than palm size or white/charred skin",
            warnings = listOf("Don't use ice water", "Don't apply butter or oil"),
            estimatedTimeMinutes = 20,
            difficulty = "Beginner"
        )
    }

    private fun createFracturesGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "fractures_guide",
            title = "Broken Bones & Fractures",
            category = "Trauma",
            severity = "MEDIUM",
            description = "Immobilize and stabilize suspected fractures to prevent further injury.",
            steps = listOf(
                GuideStep(id = "fractures_step_1", guideId = "fractures_guide", stepNumber = 1, title = "Don't Move Person", description = "Don't move unless in immediate danger", stepType = StepType.SAFETY, isCritical = true),
                GuideStep(id = "fractures_step_2", guideId = "fractures_guide", stepNumber = 2, title = "Call Emergency", description = "Call 911 for obvious fractures", stepType = StepType.EMERGENCY_CALL),
                GuideStep(id = "fractures_step_3", guideId = "fractures_guide", stepNumber = 3, title = "Immobilize Area", description = "Immobilize above and below fracture", stepType = StepType.ACTION, isCritical = true)
            ),
            iconResName = "ic_fracture",
            whenToCallEmergency = "Obvious deformity or bone visible through skin",
            warnings = listOf("Don't try to realign broken bones"),
            estimatedTimeMinutes = 15,
            difficulty = "Intermediate"
        )
    }

    private fun createPoisoningGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "poisoning_guide",
            title = "Poisoning Emergency",
            category = "Medical Emergency",
            severity = "HIGH",
            description = "Respond quickly to poisoning emergencies and prevent absorption.",
            steps = listOf(
                GuideStep(id = "poisoning_step_1", guideId = "poisoning_guide", stepNumber = 1, title = "Call Poison Control", description = "Call Poison Control: 1-800-222-1222", stepType = StepType.EMERGENCY_CALL, isCritical = true),
                GuideStep(id = "poisoning_step_2", guideId = "poisoning_guide", stepNumber = 2, title = "Follow Instructions", description = "Follow Poison Control's instructions exactly", stepType = StepType.ACTION, isCritical = true)
            ),
            iconResName = "ic_poison",
            whenToCallEmergency = "Person is unconscious or having trouble breathing",
            warnings = listOf("Don't induce vomiting unless told to do so"),
            estimatedTimeMinutes = 0,
            difficulty = "Intermediate"
        )
    }

    private fun createShockGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "shock_guide",
            title = "Medical Shock Treatment",
            category = "Medical Emergency",
            severity = "CRITICAL",
            description = "Recognize and treat shock to maintain blood flow to vital organs.",
            steps = listOf(
                GuideStep(id = "shock_step_1", guideId = "shock_guide", stepNumber = 1, title = "Call Emergency", description = "Call 911 immediately", stepType = StepType.EMERGENCY_CALL, isCritical = true),
                GuideStep(id = "shock_step_2", guideId = "shock_guide", stepNumber = 2, title = "Position Patient", description = "Lay down and elevate legs 12 inches", stepType = StepType.POSITIONING, isCritical = true),
                GuideStep(id = "shock_step_3", guideId = "shock_guide", stepNumber = 3, title = "Keep Warm", description = "Keep person warm with blankets", stepType = StepType.ACTION)
            ),
            iconResName = "ic_shock",
            whenToCallEmergency = "Rapid weak pulse, pale cold skin, or confusion",
            warnings = listOf("Don't give anything to drink", "This is life-threatening"),
            estimatedTimeMinutes = 0,
            difficulty = "Intermediate"
        )
    }

    private fun createHeartAttackGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "heart_attack_guide",
            title = "Heart Attack Emergency",
            category = "Medical Emergency",
            severity = "CRITICAL",
            description = "Quick recognition and response to heart attack symptoms.",
            steps = listOf(
                GuideStep(id = "heart_attack_step_1", guideId = "heart_attack_guide", stepNumber = 1, title = "Call Emergency", description = "Call 911 immediately", stepType = StepType.EMERGENCY_CALL, isCritical = true),
                GuideStep(id = "heart_attack_step_2", guideId = "heart_attack_guide", stepNumber = 2, title = "Give Aspirin", description = "Give aspirin if conscious and not allergic", stepType = StepType.ACTION, isCritical = true),
                GuideStep(id = "heart_attack_step_3", guideId = "heart_attack_guide", stepNumber = 3, title = "Keep Calm", description = "Keep person calm and still", stepType = StepType.ACTION)
            ),
            iconResName = "ic_heart_attack",
            whenToCallEmergency = "Chest pain, shortness of breath, or other heart attack symptoms",
            warnings = listOf("Don't delay calling 911", "Don't drive to hospital yourself"),
            estimatedTimeMinutes = 0,
            difficulty = "Beginner"
        )
    }

    private fun createStrokeGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "stroke_guide",
            title = "Stroke Emergency (FAST)",
            category = "Medical Emergency",
            severity = "CRITICAL",
            description = "Use FAST method to identify stroke and get immediate medical help.",
            steps = listOf(
                GuideStep(id = "stroke_step_1", guideId = "stroke_guide", stepNumber = 1, title = "FAST Test", description = "Use FAST test to identify stroke", stepType = StepType.ASSESSMENT, isCritical = true),
                GuideStep(id = "stroke_step_2", guideId = "stroke_guide", stepNumber = 2, title = "Face Test", description = "Ask person to smile, check for drooping", stepType = StepType.ASSESSMENT, isCritical = true),
                GuideStep(id = "stroke_step_3", guideId = "stroke_guide", stepNumber = 3, title = "Call 911", description = "Call 911 immediately if any signs present", stepType = StepType.EMERGENCY_CALL, isCritical = true)
            ),
            iconResName = "ic_stroke",
            whenToCallEmergency = "Any sign of facial drooping, arm weakness, or speech problems",
            warnings = listOf("Time is critical - call 911 immediately"),
            estimatedTimeMinutes = 0,
            difficulty = "Beginner"
        )
    }

    private fun createAllergicReactionGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "allergic_reaction_guide",
            title = "Severe Allergic Reaction (Anaphylaxis)",
            category = "Medical Emergency",
            severity = "HIGH",
            description = "Recognize and treat severe allergic reactions that can be life-threatening.",
            steps = listOf(
                GuideStep(id = "allergic_reaction_step_1", guideId = "allergic_reaction_guide", stepNumber = 1, title = "Call Emergency", description = "Call 911 for severe reactions", stepType = StepType.EMERGENCY_CALL, isCritical = true),
                GuideStep(id = "allergic_reaction_step_2", guideId = "allergic_reaction_guide", stepNumber = 2, title = "Use EpiPen", description = "Use epinephrine auto-injector if available", stepType = StepType.ACTION, isCritical = true),
                GuideStep(id = "allergic_reaction_step_3", guideId = "allergic_reaction_guide", stepNumber = 3, title = "Monitor Breathing", description = "Monitor breathing closely", stepType = StepType.MONITORING, isCritical = true)
            ),
            iconResName = "ic_allergy",
            whenToCallEmergency = "Difficulty breathing, swelling of face/throat",
            warnings = listOf("This can be fatal - call 911", "Use EpiPen if available"),
            estimatedTimeMinutes = 0,
            difficulty = "Intermediate"
        )
    }

    // The 10 additional guides
    private fun createSprainsStrainsGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "sprains_strains_guide",
            title = "Sprains and Strains",
            category = "Musculoskeletal",
            severity = "MEDIUM",
            description = "Treat sprains and strains with proper care to prevent further injury.",
            steps = listOf(
                GuideStep(id = "sprains_step_1", guideId = "sprains_strains_guide", stepNumber = 1, title = "Rest", description = "Stop using the injured area and rest", stepType = StepType.ACTION, isCritical = true),
                GuideStep(id = "sprains_step_2", guideId = "sprains_strains_guide", stepNumber = 2, title = "Ice", description = "Apply ice pack wrapped in cloth for 10-15 minutes", stepType = StepType.ACTION, isCritical = true),
                GuideStep(id = "sprains_step_3", guideId = "sprains_strains_guide", stepNumber = 3, title = "Compression", description = "Apply elastic bandage (not too tight)", stepType = StepType.ACTION),
                GuideStep(id = "sprains_step_4", guideId = "sprains_strains_guide", stepNumber = 4, title = "Elevation", description = "Elevate injured area above heart level", stepType = StepType.POSITIONING)
            ),
            iconResName = "ic_sprain",
            whenToCallEmergency = "Severe deformity, numbness, or inability to bear weight",
            warnings = listOf("Don't apply ice directly to skin"),
            estimatedTimeMinutes = 15,
            difficulty = "Beginner"
        )
    }

    private fun createHypothermiaGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "hypothermia_guide",
            title = "Hypothermia",
            category = "Environmental",
            severity = "HIGH",
            description = "Warm a person with hypothermia gradually to prevent dangerous complications.",
            steps = listOf(
                GuideStep(id = "hypothermia_step_1", guideId = "hypothermia_guide", stepNumber = 1, title = "Call Emergency", description = "Call 911 for severe hypothermia", stepType = StepType.EMERGENCY_CALL, isCritical = true),
                GuideStep(id = "hypothermia_step_2", guideId = "hypothermia_guide", stepNumber = 2, title = "Move to Warmth", description = "Move person to warm, dry location", stepType = StepType.SAFETY),
                GuideStep(id = "hypothermia_step_3", guideId = "hypothermia_guide", stepNumber = 3, title = "Warm Gradually", description = "Wrap in blankets, warm the core first", stepType = StepType.ACTION, isCritical = true)
            ),
            iconResName = "ic_hypothermia",
            whenToCallEmergency = "Severe shivering, confusion, or loss of consciousness",
            warnings = listOf("Don't warm too quickly", "Handle gently"),
            estimatedTimeMinutes = 30,
            difficulty = "Intermediate"
        )
    }

    private fun createHeatExhaustionGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "heat_exhaustion_guide",
            title = "Heat Exhaustion and Heatstroke",
            category = "Environmental",
            severity = "HIGH",
            description = "Cool down overheated person and prevent progression to life-threatening heatstroke.",
            steps = listOf(
                GuideStep(id = "heat_step_1", guideId = "heat_exhaustion_guide", stepNumber = 1, title = "Move to Cool Area", description = "Move person to cool, shaded area", stepType = StepType.SAFETY, isCritical = true),
                GuideStep(id = "heat_step_2", guideId = "heat_exhaustion_guide", stepNumber = 2, title = "Cool with Water", description = "Apply cool water to skin or use wet cloths", stepType = StepType.ACTION, isCritical = true),
                GuideStep(id = "heat_step_3", guideId = "heat_exhaustion_guide", stepNumber = 3, title = "Give Fluids", description = "Give cool water if conscious and alert", stepType = StepType.ACTION)
            ),
            iconResName = "ic_heat",
            whenToCallEmergency = "High fever, confusion, or hot dry skin",
            warnings = listOf("Don't give alcohol or caffeine"),
            estimatedTimeMinutes = 20,
            difficulty = "Beginner"
        )
    }

    private fun createSeizuresGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "seizures_guide",
            title = "Seizures",
            category = "Neurological",
            severity = "MEDIUM",
            description = "Protect someone having a seizure and know when to call for help.",
            steps = listOf(
                GuideStep(id = "seizures_step_1", guideId = "seizures_guide", stepNumber = 1, title = "Keep Person Safe", description = "Clear area of hard objects, cushion head", stepType = StepType.SAFETY, isCritical = true),
                GuideStep(id = "seizures_step_2", guideId = "seizures_guide", stepNumber = 2, title = "Time Seizure", description = "Note start time of seizure", stepType = StepType.ASSESSMENT),
                GuideStep(id = "seizures_step_3", guideId = "seizures_guide", stepNumber = 3, title = "Don't Restrain", description = "Don't hold person down or put anything in mouth", stepType = StepType.SAFETY, isCritical = true)
            ),
            iconResName = "ic_seizure",
            whenToCallEmergency = "Seizure lasts over 5 minutes or no known seizure history",
            warnings = listOf("Never put anything in person's mouth"),
            estimatedTimeMinutes = 10,
            difficulty = "Beginner"
        )
    }

    private fun createBitesStingsGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "bites_stings_guide",
            title = "Bites and Stings",
            category = "Environmental",
            severity = "MEDIUM",
            description = "Treat animal bites and insect stings to prevent infection and allergic reactions.",
            steps = listOf(
                GuideStep(id = "bites_step_1", guideId = "bites_stings_guide", stepNumber = 1, title = "Clean Wound", description = "Clean with soap and water", stepType = StepType.ACTION, isCritical = true),
                GuideStep(id = "bites_step_2", guideId = "bites_stings_guide", stepNumber = 2, title = "Apply Pressure", description = "Apply pressure to control bleeding", stepType = StepType.ACTION),
                GuideStep(id = "bites_step_3", guideId = "bites_stings_guide", stepNumber = 3, title = "Apply Cold", description = "Apply ice pack to reduce swelling", stepType = StepType.ACTION)
            ),
            iconResName = "ic_bite",
            whenToCallEmergency = "Signs of severe allergic reaction or deep puncture wounds",
            warnings = listOf("Watch for allergic reactions"),
            estimatedTimeMinutes = 15,
            difficulty = "Beginner"
        )
    }

    private fun createAsthmaAttackGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "asthma_attack_guide",
            title = "Asthma Attack",
            category = "Respiratory",
            severity = "HIGH",
            description = "Help someone having an asthma attack breathe easier.",
            steps = listOf(
                GuideStep(id = "asthma_step_1", guideId = "asthma_attack_guide", stepNumber = 1, title = "Help Sit Upright", description = "Help person sit upright and lean forward slightly", stepType = StepType.POSITIONING, isCritical = true),
                GuideStep(id = "asthma_step_2", guideId = "asthma_attack_guide", stepNumber = 2, title = "Use Inhaler", description = "Help person use their rescue inhaler", stepType = StepType.ACTION, isCritical = true),
                GuideStep(id = "asthma_step_3", guideId = "asthma_attack_guide", stepNumber = 3, title = "Call 911", description = "Call 911 if inhaler doesn't help", stepType = StepType.EMERGENCY_CALL, isCritical = true)
            ),
            iconResName = "ic_asthma",
            whenToCallEmergency = "Breathing doesn't improve with inhaler",
            warnings = listOf("Don't leave person alone"),
            estimatedTimeMinutes = 10,
            difficulty = "Beginner"
        )
    }

    private fun createDiabeticEmergenciesGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "diabetic_emergencies_guide",
            title = "Diabetic Emergencies",
            category = "Medical Emergency",
            severity = "HIGH",
            description = "Recognize and respond to diabetic emergencies (high or low blood sugar).",
            steps = listOf(
                GuideStep(id = "diabetic_step_1", guideId = "diabetic_emergencies_guide", stepNumber = 1, title = "Check Responsiveness", description = "Check if person is conscious and responsive", stepType = StepType.ASSESSMENT, isCritical = true),
                GuideStep(id = "diabetic_step_2", guideId = "diabetic_emergencies_guide", stepNumber = 2, title = "Give Sugar", description = "If conscious, give sugar (juice, candy)", stepType = StepType.ACTION, isCritical = true),
                GuideStep(id = "diabetic_step_3", guideId = "diabetic_emergencies_guide", stepNumber = 3, title = "Call 911", description = "Call 911 if unconscious or doesn't improve", stepType = StepType.EMERGENCY_CALL, isCritical = true)
            ),
            iconResName = "ic_diabetes",
            whenToCallEmergency = "Person is unconscious or doesn't improve after giving sugar",
            warnings = listOf("Don't give anything by mouth if unconscious"),
            estimatedTimeMinutes = 5,
            difficulty = "Beginner"
        )
    }

    private fun createDrowningGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "drowning_guide",
            title = "Drowning",
            category = "Life-Threatening",
            severity = "CRITICAL",
            description = "Rescue and resuscitate a drowning person safely.",
            steps = listOf(
                GuideStep(id = "drowning_step_1", guideId = "drowning_guide", stepNumber = 1, title = "Call for Help", description = "Call 911 and alert lifeguards", stepType = StepType.EMERGENCY_CALL, isCritical = true),
                GuideStep(id = "drowning_step_2", guideId = "drowning_guide", stepNumber = 2, title = "Rescue Safely", description = "Use reaching assist or throw flotation device", stepType = StepType.SAFETY, isCritical = true),
                GuideStep(id = "drowning_step_3", guideId = "drowning_guide", stepNumber = 3, title = "Start CPR", description = "Start CPR immediately if not breathing", stepType = StepType.ACTION, isCritical = true)
            ),
            iconResName = "ic_drowning",
            whenToCallEmergency = "Any near-drowning incident requires emergency medical evaluation",
            warnings = listOf("Don't become a victim yourself"),
            estimatedTimeMinutes = 0,
            difficulty = "Advanced"
        )
    }

    private fun createNosebleedsGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "nosebleeds_guide",
            title = "Nosebleeds",
            category = "Minor Injury",
            severity = "LOW",
            description = "Stop a nosebleed safely and effectively.",
            steps = listOf(
                GuideStep(id = "nosebleed_step_1", guideId = "nosebleeds_guide", stepNumber = 1, title = "Lean Forward", description = "Have person sit and lean slightly forward", stepType = StepType.POSITIONING, isCritical = true),
                GuideStep(id = "nosebleed_step_2", guideId = "nosebleeds_guide", stepNumber = 2, title = "Pinch Nostrils", description = "Pinch soft part of nose for 10-15 minutes", stepType = StepType.ACTION, isCritical = true),
                GuideStep(id = "nosebleed_step_3", guideId = "nosebleeds_guide", stepNumber = 3, title = "Apply Cold", description = "Apply cold compress to bridge of nose", stepType = StepType.ACTION)
            ),
            iconResName = "ic_nosebleed",
            whenToCallEmergency = "Bleeding doesn't stop after 20 minutes",
            warnings = listOf("Don't tilt head back"),
            estimatedTimeMinutes = 15,
            difficulty = "Beginner"
        )
    }

    private fun createEyeInjuriesGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "eye_injuries_guide",
            title = "Eye Injuries",
            category = "Trauma",
            severity = "MEDIUM",
            description = "Protect injured eyes from further damage.",
            steps = listOf(
                GuideStep(id = "eye_step_1", guideId = "eye_injuries_guide", stepNumber = 1, title = "Don't Touch", description = "Don't touch or rub the injured eye", stepType = StepType.SAFETY, isCritical = true),
                GuideStep(id = "eye_step_2", guideId = "eye_injuries_guide", stepNumber = 2, title = "Flush with Water", description = "For chemicals, flush with clean water for 15-20 minutes", stepType = StepType.ACTION, isCritical = true),
                GuideStep(id = "eye_step_3", guideId = "eye_injuries_guide", stepNumber = 3, title = "Cover Both Eyes", description = "Cover both eyes with sterile gauze", stepType = StepType.ACTION)
            ),
            iconResName = "ic_eye",
            whenToCallEmergency = "Any penetrating eye injury or chemical exposure",
            warnings = listOf("Don't remove embedded objects"),
            estimatedTimeMinutes = 10,
            difficulty = "Intermediate"
        )
    }

    /**
     * Check if we have the expected number of guides - updated for all 20 guides
     */
    suspend fun verifyDataIntegrity(): Boolean {
        return try {
            val database = AppDatabase.getDatabase(context)
            val guidesCount = database.guideDao().getGuidesCount()
            val expectedCount = 20
            Log.i(TAG, "verifyDataIntegrity: found $guidesCount guides, expected $expectedCount")
            guidesCount >= expectedCount
        } catch (e: Exception) {
            Log.e(TAG, "verifyDataIntegrity: error", e)
            false
        }
    }

    /**
     * Force a complete reinitialization - useful for testing or when data gets corrupted
     */
    suspend fun forceReinitialization() {
        try {
            Log.i(TAG, "forceReinitialization: starting complete data reset")
            val database = AppDatabase.getDatabase(context)
            val prefs = context.getSharedPreferences("first_aid_prefs", Context.MODE_PRIVATE)

            // Clear all existing data
            database.guideDao().deleteAllGuides()
            database.contactDao().deleteAllContacts()

            // Reset initialization flag
            prefs.edit().putBoolean("data_initialized", false).apply()

            // Force fresh initialization
            initializeDataBlocking()

            Log.i(TAG, "forceReinitialization: completed")
        } catch (e: Exception) {
            Log.e(TAG, "forceReinitialization: error", e)
        }
    }

    /**
     * Force immediate reinitialization for debugging - clears ALL data and preferences
     */
    suspend fun forceImmediateReinitialization() {
        try {
            Log.i(TAG, "forceImmediateReinitialization: starting COMPLETE reset")
            val database = AppDatabase.getDatabase(context)
            val prefs = context.getSharedPreferences("first_aid_prefs", Context.MODE_PRIVATE)

            // Clear ALL preferences
            prefs.edit().clear().apply()

            // Clear all database data
            database.guideDao().deleteAllGuides()
            database.contactDao().deleteAllContacts()

            Log.i(TAG, "forceImmediateReinitialization: cleared all data, now initializing...")

            // Force fresh initialization
            initializeGuides(database)
            initializeContacts(database)

            // Mark as initialized
            prefs.edit().putBoolean("data_initialized", true).apply()

            // Verify the count
            val finalCount = database.guideDao().getGuidesCount()
            Log.i(TAG, "forceImmediateReinitialization: completed with $finalCount guides")

        } catch (e: Exception) {
            Log.e(TAG, "forceImmediateReinitialization: error", e)
        }
    }
}

