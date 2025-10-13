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

            val needsSeeding = !isInitializedPref || guidesCount == 0 || contactsCount == 0

            if (needsSeeding) {
                // Initialize guides
                try {
                    Log.i(TAG, "initializeData: initializing guides")
                    initializeGuides(database)
                    Log.i(TAG, "initializeData: guides initialized")
                } catch (e: Exception) {
                    Log.e(TAG, "Error initializing guides", e)
                    // Don't proceed to contacts if guides failed
                    return
                }

                // Initialize contacts
                try {
                    Log.i(TAG, "initializeData: initializing contacts")
                    initializeContacts(database)
                    Log.i(TAG, "initializeData: contacts initialized")
                } catch (e: Exception) {
                    Log.e(TAG, "Error initializing contacts", e)
                    // Continue, but still mark initialized to avoid retry loops
                }

                // Mark as initialized
                prefs.edit().putBoolean("data_initialized", true).apply()
                Log.i(TAG, "initializeData: marked data_initialized=true")
            } else {
                Log.i(TAG, "initializeData: seeding not required")
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
            createAllergicReactionGuide()
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

    private fun createCPRGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "cpr_guide",
            title = "CPR (Cardiopulmonary Resuscitation)",
            category = "Life-Threatening",
            severity = "CRITICAL",
            description = "Learn how to perform CPR to save a life when someone's heart stops beating.",
            steps = listOf(
                GuideStep(
                    id = "cpr_step_1",
                    guideId = "cpr_guide",
                    stepNumber = 1,
                    title = "Check Responsiveness",
                    description = "Tap shoulders and shout 'Are you okay?'",
                    detailedInstructions = "Place your hands on the person's shoulders and gently shake them. Speak loudly and clearly. Look for any signs of movement, breathing, or response to your voice.",
                    stepType = StepType.CHECK,
                    isCritical = true,
                    duration = "10 seconds"
                ),
                GuideStep(
                    id = "cpr_step_2",
                    guideId = "cpr_guide",
                    stepNumber = 2,
                    title = "Call for Help",
                    description = "Call 911 immediately or have someone else call",
                    detailedInstructions = "If the person is unresponsive, immediately call 911. If others are around, point to someone specific and say 'You, call 911 now!'",
                    stepType = StepType.CALL,
                    isCritical = true
                ),
                GuideStep(
                    id = "cpr_step_3",
                    guideId = "cpr_guide",
                    stepNumber = 3,
                    title = "Position Patient",
                    description = "Position the person on a firm surface, face up",
                    detailedInstructions = "Carefully roll the person onto their back on a firm surface. Tilt their head back slightly by lifting their chin.",
                    stepType = StepType.ACTION,
                    duration = "15 seconds"
                ),
                GuideStep(
                    id = "cpr_step_4",
                    guideId = "cpr_guide",
                    stepNumber = 4,
                    title = "Hand Placement",
                    description = "Place heel of one hand on center of chest, between nipples",
                    detailedInstructions = "Find the center of the chest between the nipples. Place the heel of one hand there, then place your other hand on top, interlocking fingers.",
                    stepType = StepType.ACTION,
                    isCritical = true,
                    duration = "10 seconds"
                ),
                GuideStep(
                    id = "cpr_step_5",
                    guideId = "cpr_guide",
                    stepNumber = 5,
                    title = "Chest Compressions",
                    description = "Push hard and fast at least 2 inches deep",
                    detailedInstructions = "Push straight down at least 2 inches deep. Allow complete chest recoil between compressions. Count out loud at 100-120 compressions per minute.",
                    stepType = StepType.ACTION,
                    isCritical = true,
                    warnings = listOf("Don't stop compressions unless absolutely necessary", "Expect to hear ribs crack - this is normal")
                ),
                GuideStep(
                    id = "cpr_step_6",
                    guideId = "cpr_guide",
                    stepNumber = 6,
                    title = "Continue Until Help Arrives",
                    description = "Keep doing compressions until emergency services arrive",
                    detailedInstructions = "Continue cycles of chest compressions. Don't stop until emergency services arrive and take over.",
                    stepType = StepType.REPEAT,
                    isCritical = true
                )
            ),
            iconResName = "ic_cpr",
            whenToCallEmergency = "Person is unresponsive and not breathing normally",
            warnings = listOf(
                "Only perform if person is unresponsive and not breathing normally",
                "Don't be afraid to push hard - broken ribs heal, but brain damage doesn't",
                "Don't stop CPR until professionals arrive"
            ),
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
                GuideStep(
                    id = "choking_step_1",
                    guideId = "choking_guide",
                    stepNumber = 1,
                    title = "Assess Situation",
                    description = "Ask 'Are you choking?' - if they can't speak, act immediately",
                    detailedInstructions = "A choking person may grab their throat, be unable to speak or cough effectively, make high-pitched sounds, or turn blue around lips and face.",
                    stepType = StepType.CHECK,
                    isCritical = true,
                    duration = "5 seconds"
                ),
                GuideStep(
                    id = "choking_step_2",
                    guideId = "choking_guide",
                    stepNumber = 2,
                    title = "Position Behind Person",
                    description = "Stand behind the person and wrap your arms around their waist",
                    stepType = StepType.ACTION,
                    duration = "5 seconds"
                ),
                GuideStep(
                    id = "choking_step_3",
                    guideId = "choking_guide",
                    stepNumber = 3,
                    title = "Abdominal Thrusts",
                    description = "Give quick upward thrusts into abdomen",
                    detailedInstructions = "Make a fist with one hand and place the thumb side against the person's belly, just above the navel. Grasp your fist with your other hand and perform quick, forceful upward and inward thrusts.",
                    stepType = StepType.ACTION,
                    isCritical = true,
                    warnings = listOf("Don't perform on pregnant women or infants", "If person becomes unconscious, start CPR")
                )
            ),
            iconResName = "ic_choking",
            whenToCallEmergency = "Person cannot speak, cough, or breathe",
            warnings = listOf(
                "Don't perform on pregnant women or infants",
                "Be prepared to start CPR if person becomes unconscious"
            ),
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
                GuideStep(
                    id = "bleeding_step_1",
                    guideId = "bleeding_guide",
                    stepNumber = 1,
                    title = "Call Emergency",
                    description = "Call 911 for severe bleeding",
                    stepType = StepType.CALL,
                    isCritical = true
                ),
                GuideStep(
                    id = "bleeding_step_2",
                    guideId = "bleeding_guide",
                    stepNumber = 2,
                    title = "Apply Direct Pressure",
                    description = "Apply direct pressure with clean cloth or bandage",
                    detailedInstructions = "Press firmly and don't lift to check bleeding. Add more layers if blood soaks through.",
                    stepType = StepType.ACTION,
                    isCritical = true,
                    warnings = listOf("Don't remove embedded objects", "Don't lift bandage to check bleeding")
                )
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
                GuideStep(id = "burns_step_1", guideId = "burns_guide", stepNumber = 1, title = "Remove from Source", description = "Remove person from heat source if safe to do so", stepType = StepType.SAFETY, isCritical = true),
                GuideStep(id = "burns_step_2", guideId = "burns_guide", stepNumber = 2, title = "Assess Severity", description = "Call 911 for severe burns", stepType = StepType.ASSESSMENT),
                GuideStep(id = "burns_step_3", guideId = "burns_guide", stepNumber = 3, title = "Cool the Burn", description = "Cool the burn with lukewarm water for 10-20 minutes", stepType = StepType.ACTION, isCritical = true),
                GuideStep(id = "burns_step_4", guideId = "burns_guide", stepNumber = 4, title = "Remove Jewelry", description = "Remove jewelry and loose clothing from burned area", stepType = StepType.ACTION),
                GuideStep(id = "burns_step_5", guideId = "burns_guide", stepNumber = 5, title = "Protect Blisters", description = "Don't break blisters", stepType = StepType.SAFETY),
                GuideStep(id = "burns_step_6", guideId = "burns_guide", stepNumber = 6, title = "Apply Bandage", description = "Apply loose, sterile bandage", stepType = StepType.ACTION),
                GuideStep(id = "burns_step_7", guideId = "burns_guide", stepNumber = 7, title = "Pain Management", description = "Give over-the-counter pain medication if conscious", stepType = StepType.ACTION),
                GuideStep(id = "burns_step_8", guideId = "burns_guide", stepNumber = 8, title = "Monitor Patient", description = "Keep person warm and monitor for shock", stepType = StepType.MONITORING)
            ),
            iconResName = "ic_burns",
            whenToCallEmergency = "Burns larger than palm size, white or charred skin, or difficulty breathing",
            warnings = listOf(
                "Don't use ice water",
                "Don't apply butter, oil, or home remedies",
                "Don't break blisters",
                "Seek medical care for burns larger than palm size"
            ),
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
                GuideStep(id = "fractures_step_1", guideId = "fractures_guide", stepNumber = 1, title = "Assess Safety", description = "Don't move the person unless in immediate danger", stepType = StepType.SAFETY, isCritical = true),
                GuideStep(id = "fractures_step_2", guideId = "fractures_guide", stepNumber = 2, title = "Call Emergency", description = "Call 911 for obvious fractures or if unsure", stepType = StepType.EMERGENCY_CALL),
                GuideStep(id = "fractures_step_3", guideId = "fractures_guide", stepNumber = 3, title = "Check Circulation", description = "Check for circulation below the injury", stepType = StepType.ASSESSMENT),
                GuideStep(id = "fractures_step_4", guideId = "fractures_guide", stepNumber = 4, title = "Immobilize Area", description = "Immobilize the area above and below the suspected fracture", stepType = StepType.ACTION, isCritical = true),
                GuideStep(id = "fractures_step_5", guideId = "fractures_guide", stepNumber = 5, title = "Apply Splints", description = "Use splints, slings, or padding to immobilize", stepType = StepType.ACTION),
                GuideStep(id = "fractures_step_6", guideId = "fractures_guide", stepNumber = 6, title = "Apply Cold", description = "Apply ice pack wrapped in cloth", stepType = StepType.ACTION),
                GuideStep(id = "fractures_step_7", guideId = "fractures_guide", stepNumber = 7, title = "Monitor Shock", description = "Monitor for signs of shock", stepType = StepType.MONITORING),
                GuideStep(id = "fractures_step_8", guideId = "fractures_guide", stepNumber = 8, title = "No Food/Drink", description = "Don't give food or drink in case surgery is needed", stepType = StepType.SAFETY)
            ),
            iconResName = "ic_fracture",
            whenToCallEmergency = "Obvious deformity, bone visible through skin, or severe pain",
            warnings = listOf(
                "Don't try to realign broken bones",
                "Don't move person with suspected spine injury",
                "Don't give medications unless trained",
                "Watch for signs of shock"
            ),
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
                GuideStep(id = "poisoning_step_1", guideId = "poisoning_guide", stepNumber = 1, title = "Call Poison Control", description = "Call Poison Control: 1-800-222-1222 immediately", stepType = StepType.EMERGENCY_CALL, isCritical = true),
                GuideStep(id = "poisoning_step_2", guideId = "poisoning_guide", stepNumber = 2, title = "Call 911", description = "Call 911 if person is unconscious or having trouble breathing", stepType = StepType.EMERGENCY_CALL, isCritical = true),
                GuideStep(id = "poisoning_step_3", guideId = "poisoning_guide", stepNumber = 3, title = "Identify Poison", description = "Try to identify the poison and amount consumed", stepType = StepType.ASSESSMENT),
                GuideStep(id = "poisoning_step_4", guideId = "poisoning_guide", stepNumber = 4, title = "Follow Instructions", description = "Follow Poison Control's instructions exactly", stepType = StepType.ACTION, isCritical = true),
                GuideStep(id = "poisoning_step_5", guideId = "poisoning_guide", stepNumber = 5, title = "Skin Contact", description = "If poison is on skin, remove contaminated clothing and rinse with water", stepType = StepType.ACTION),
                GuideStep(id = "poisoning_step_6", guideId = "poisoning_guide", stepNumber = 6, title = "Eye Contact", description = "If poison is in eyes, flush with water for 15-20 minutes", stepType = StepType.ACTION),
                GuideStep(id = "poisoning_step_7", guideId = "poisoning_guide", stepNumber = 7, title = "No Vomiting", description = "Don't induce vomiting unless instructed", stepType = StepType.SAFETY, isCritical = true),
                GuideStep(id = "poisoning_step_8", guideId = "poisoning_guide", stepNumber = 8, title = "Save Container", description = "Save poison container or take photo of label", stepType = StepType.ACTION),
                GuideStep(id = "poisoning_step_9", guideId = "poisoning_guide", stepNumber = 9, title = "Monitor Breathing", description = "Monitor breathing and be ready to start CPR", stepType = StepType.MONITORING)
            ),
            iconResName = "ic_poison",
            whenToCallEmergency = "Person is unconscious, having trouble breathing, or showing severe symptoms",
            warnings = listOf(
                "Don't induce vomiting unless told to do so",
                "Don't give milk or water unless instructed",
                "Don't try home remedies",
                "Keep poison container for identification"
            ),
            estimatedTimeMinutes = 0, // Immediate response
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
                GuideStep(id = "shock_step_2", guideId = "shock_guide", stepNumber = 2, title = "Position Patient", description = "Lay person down and elevate legs 12 inches if no spinal injury", stepType = StepType.POSITIONING, isCritical = true),
                GuideStep(id = "shock_step_3", guideId = "shock_guide", stepNumber = 3, title = "Keep Warm", description = "Keep person warm with blankets", stepType = StepType.ACTION),
                GuideStep(id = "shock_step_4", guideId = "shock_guide", stepNumber = 4, title = "Loosen Clothing", description = "Loosen tight clothing", stepType = StepType.ACTION),
                GuideStep(id = "shock_step_5", guideId = "shock_guide", stepNumber = 5, title = "No Food/Drink", description = "Don't give food or water", stepType = StepType.SAFETY, isCritical = true),
                GuideStep(id = "shock_step_6", guideId = "shock_guide", stepNumber = 6, title = "Monitor Vitals", description = "Monitor breathing and pulse", stepType = StepType.MONITORING, isCritical = true),
                GuideStep(id = "shock_step_7", guideId = "shock_guide", stepNumber = 7, title = "Prepare CPR", description = "Be prepared to perform CPR", stepType = StepType.SAFETY),
                GuideStep(id = "shock_step_8", guideId = "shock_guide", stepNumber = 8, title = "Keep Calm", description = "Keep person calm and still", stepType = StepType.ACTION)
            ),
            iconResName = "ic_shock",
            whenToCallEmergency = "Rapid weak pulse, pale cold skin, or confusion - this is life-threatening",
            warnings = listOf(
                "Don't move person if spinal injury suspected",
                "Don't give anything to drink",
                "Don't leave person alone",
                "This is life-threatening - call 911"
            ),
            estimatedTimeMinutes = 0, // Until help arrives
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
                GuideStep(id = "heart_attack_step_2", guideId = "heart_attack_guide", stepNumber = 2, title = "Give Aspirin", description = "Give aspirin if person is conscious and not allergic", stepType = StepType.ACTION, isCritical = true),
                GuideStep(id = "heart_attack_step_3", guideId = "heart_attack_guide", stepNumber = 3, title = "Position Comfort", description = "Have person sit down and rest", stepType = StepType.POSITIONING),
                GuideStep(id = "heart_attack_step_4", guideId = "heart_attack_guide", stepNumber = 4, title = "Loosen Clothing", description = "Loosen tight clothing", stepType = StepType.ACTION),
                GuideStep(id = "heart_attack_step_5", guideId = "heart_attack_guide", stepNumber = 5, title = "Monitor Vitals", description = "Monitor breathing and pulse", stepType = StepType.MONITORING, isCritical = true),
                GuideStep(id = "heart_attack_step_6", guideId = "heart_attack_guide", stepNumber = 6, title = "Prepare CPR", description = "Be prepared to perform CPR", stepType = StepType.SAFETY),
                GuideStep(id = "heart_attack_step_7", guideId = "heart_attack_guide", stepNumber = 7, title = "Keep Calm", description = "Keep person calm", stepType = StepType.ACTION),
                GuideStep(id = "heart_attack_step_8", guideId = "heart_attack_guide", stepNumber = 8, title = "Stay Present", description = "Don't leave person alone", stepType = StepType.MONITORING, isCritical = true)
            ),
            iconResName = "ic_heart_attack",
            whenToCallEmergency = "Chest pain, shortness of breath, or other heart attack symptoms",
            warnings = listOf(
                "Don't delay calling 911",
                "Don't drive to hospital yourself",
                "Don't give aspirin if allergic",
                "Women may have different symptoms"
            ),
            estimatedTimeMinutes = 0, // Until help arrives
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
                GuideStep(id = "stroke_step_2", guideId = "stroke_guide", stepNumber = 2, title = "Face Test", description = "F - Face: Ask person to smile, check for facial drooping", stepType = StepType.ASSESSMENT, isCritical = true),
                GuideStep(id = "stroke_step_3", guideId = "stroke_guide", stepNumber = 3, title = "Arms Test", description = "A - Arms: Ask person to raise both arms, check for weakness", stepType = StepType.ASSESSMENT, isCritical = true),
                GuideStep(id = "stroke_step_4", guideId = "stroke_guide", stepNumber = 4, title = "Speech Test", description = "S - Speech: Ask person to repeat a phrase, check for slurred speech", stepType = StepType.ASSESSMENT, isCritical = true),
                GuideStep(id = "stroke_step_5", guideId = "stroke_guide", stepNumber = 5, title = "Time Critical", description = "T - Time: If any signs present, call 911 immediately", stepType = StepType.EMERGENCY_CALL, isCritical = true),
                GuideStep(id = "stroke_step_6", guideId = "stroke_guide", stepNumber = 6, title = "Note Time", description = "Note time symptoms started", stepType = StepType.ASSESSMENT),
                GuideStep(id = "stroke_step_7", guideId = "stroke_guide", stepNumber = 7, title = "Keep Calm", description = "Keep person calm and comfortable", stepType = StepType.ACTION),
                GuideStep(id = "stroke_step_8", guideId = "stroke_guide", stepNumber = 8, title = "No Food/Drink", description = "Don't give food or water", stepType = StepType.SAFETY, isCritical = true),
                GuideStep(id = "stroke_step_9", guideId = "stroke_guide", stepNumber = 9, title = "Monitor", description = "Monitor breathing and consciousness", stepType = StepType.MONITORING)
            ),
            iconResName = "ic_stroke",
            whenToCallEmergency = "Any sign of facial drooping, arm weakness, or speech problems",
            warnings = listOf(
                "Time is critical - call 911 immediately",
                "Don't give aspirin (may worsen some strokes)",
                "Don't give food or water",
                "Note exact time symptoms started"
            ),
            estimatedTimeMinutes = 0, // Immediate response
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
                GuideStep(id = "allergic_reaction_step_2", guideId = "allergic_reaction_guide", stepNumber = 2, title = "Use EpiPen", description = "Use epinephrine auto-injector (EpiPen) if available", stepType = StepType.ACTION, isCritical = true),
                GuideStep(id = "allergic_reaction_step_3", guideId = "allergic_reaction_guide", stepNumber = 3, title = "Position Patient", description = "Have person lie down with legs elevated", stepType = StepType.POSITIONING),
                GuideStep(id = "allergic_reaction_step_4", guideId = "allergic_reaction_guide", stepNumber = 4, title = "Remove Allergen", description = "Remove or avoid allergen if known", stepType = StepType.SAFETY),
                GuideStep(id = "allergic_reaction_step_5", guideId = "allergic_reaction_guide", stepNumber = 5, title = "Monitor Breathing", description = "Monitor breathing closely", stepType = StepType.MONITORING, isCritical = true),
                GuideStep(id = "allergic_reaction_step_6", guideId = "allergic_reaction_guide", stepNumber = 6, title = "Prepare CPR", description = "Be prepared to perform CPR", stepType = StepType.SAFETY),
                GuideStep(id = "allergic_reaction_step_7", guideId = "allergic_reaction_guide", stepNumber = 7, title = "Give Antihistamine", description = "Give antihistamine if person is conscious", stepType = StepType.ACTION),
                GuideStep(id = "allergic_reaction_step_8", guideId = "allergic_reaction_guide", stepNumber = 8, title = "Keep Still", description = "Keep person calm and still", stepType = StepType.ACTION)
            ),
            iconResName = "ic_allergy",
            whenToCallEmergency = "Difficulty breathing, swelling of face/throat, or severe symptoms",
            warnings = listOf(
                "This can be fatal - call 911",
                "Use EpiPen if available and trained",
                "Don't assume mild symptoms won't worsen",
                "Second wave of symptoms can occur"
            ),
            estimatedTimeMinutes = 0, // Immediate response
            difficulty = "Intermediate"
        )
    }
}
