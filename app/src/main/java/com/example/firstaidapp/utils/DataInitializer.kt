package com.example.firstaidapp.utils

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.example.firstaidapp.data.database.AppDatabase
import com.example.firstaidapp.data.models.*
import com.example.firstaidapp.utils.Constants.EMERGENCY_NUMBER_IN
import com.example.firstaidapp.utils.Constants.POISON_CONTROL_IN
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

object DataInitializer {

    private const val TAG = "DataInitializer"
    private val isInitialized = AtomicBoolean(false)

    /**
     * Public non-blocking entry used by older callers: launches the suspending initializer on IO.
     */
    fun initializeData(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            initializeDataBlocking(context.applicationContext)
        }
    }

    /**
     * Suspends until initialization completes. This is the entrypoint used by WorkManager.
     */
    suspend fun initializeDataBlocking(context: Context) {
        if (isInitialized.getAndSet(true)) {
            Log.i(TAG, "initializeData: Already initialized or in progress, skipping.")
            return
        }

        withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "initializeData: starting initialization on IO thread (blocking)")
                val database = AppDatabase.getDatabase(context)
                val prefs = context.getSharedPreferences("first_aid_prefs", Context.MODE_PRIVATE)

                val guidesCount = try { database.guideDao().getGuidesCount() } catch (e: Exception) { Log.e(TAG, "count guides failed", e); 0 }
                val contactsCount = try { database.contactDao().getContactsCount() } catch (e: Exception) { Log.e(TAG, "count contacts failed", e); 0 }

                Log.i(TAG, "initializeData: guidesCount=$guidesCount, contactsCount=$contactsCount")

                val expectedGuidesCount = 19
                val expectedContactsCount = 40 // Updated to include state-specific contacts
                val needsFullReinitialization = guidesCount < expectedGuidesCount || contactsCount < expectedContactsCount

                if (needsFullReinitialization) {
                    Log.i(TAG, "initializeData: forcing reinitialization - found $guidesCount guides (expected $expectedGuidesCount), $contactsCount contacts (expected $expectedContactsCount)")

                    database.guideDao().deleteAllGuides()
                    database.contactDao().deleteAllContacts()

                    try {
                        Log.i(TAG, "initializeData: initializing all $expectedGuidesCount guides")
                        initializeGuides(database)
                        Log.i(TAG, "initializeData: guides initialized")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error initializing guides", e)
                        isInitialized.set(false)
                        return@withContext
                    }

                    try {
                        Log.i(TAG, "initializeData: initializing contacts")
                        initializeContacts(database)
                        Log.i(TAG, "initializeData: contacts initialized")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error initializing contacts", e)
                    }

                    prefs.edit(commit = true) { putBoolean("data_initialized", true) }
                    Log.i(TAG, "initializeData: marked data_initialized=true")
                } else {
                    Log.i(TAG, "initializeData: all guides and contacts already present ($guidesCount/$expectedGuidesCount guides, $contactsCount/$expectedContactsCount contacts)")
                }

            } catch (e: Throwable) {
                Log.e(TAG, "initializeData: unexpected error", e)
                isInitialized.set(false) // Reset on failure to allow retry
            }
        }
    }

    private suspend fun initializeGuides(database: AppDatabase) {
        // Use the data source that contains YouTube links
        val guides = com.example.firstaidapp.data.repository.FirstAidGuidesData.getAllFirstAidGuides()

        for ((index, guide) in guides.withIndex()) {
            try {
                Log.i(TAG, "insertGuide: inserting ${guide.id} (index=${index}) with YouTube link: ${guide.youtubeLink}")
                database.guideDao().insertGuide(guide)
                delay(50)
            } catch (e: Exception) {
                Log.e(TAG, "insertGuide: failed to insert ${guide.id}", e)
            }
        }

        Log.i(TAG, "initializeGuides: completed inserting ${guides.size} guides with YouTube links")
    }

    private suspend fun initializeContacts(database: AppDatabase) {
        val contacts = com.example.firstaidapp.data.repository.EmergencyContactsData.getAllEmergencyContacts()

        for ((index, contact) in contacts.withIndex()) {
            try {
                Log.i(TAG, "insertContact: inserting ${contact.name} (index=${index}) for state: ${contact.state}")
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
                GuideStep(id = "cpr_step_1", guideId = "cpr_guide", stepNumber = 1, title = "Check Responsiveness", description = "Tap shoulders and shout 'Are you okay?'", stepType = StepType.CHECK, isCritical = true),
                GuideStep(id = "cpr_step_2", guideId = "cpr_guide", stepNumber = 2, title = "Call for Help", description = "Call 112 immediately", stepType = StepType.CALL, isCritical = true),
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
                GuideStep(id = "bleeding_step_1", guideId = "bleeding_guide", stepNumber = 1, title = "Call Emergency", description = "Call 112 for severe bleeding", stepType = StepType.CALL, isCritical = true),
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
                GuideStep(id = "fractures_step_2", guideId = "fractures_guide", stepNumber = 2, title = "Call Emergency", description = "Call 112 for obvious fractures", stepType = StepType.EMERGENCY_CALL),
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
                GuideStep(id = "poisoning_step_1", guideId = "poisoning_guide", stepNumber = 1, title = "Call Poison Control", description = "Call National Poison Information Centre immediately", stepType = StepType.CALL, isCritical = true)
            ),
            iconResName = "ic_poisoning",
            whenToCallEmergency = "If person is unconscious, having a seizure, or trouble breathing",
            warnings = listOf("Do not induce vomiting unless told to do so"),
            estimatedTimeMinutes = 5,
            difficulty = "Beginner"
        )
    }

    private fun createShockGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "shock_guide",
            title = "Treating for Shock",
            category = "Medical Emergency",
            severity = "CRITICAL",
            description = "Recognize and treat shock, a life-threatening condition.",
            steps = listOf(
                GuideStep(id = "shock_step_1", guideId = "shock_guide", stepNumber = 1, title = "Call Emergency", description = "Call 112 immediately", stepType = StepType.EMERGENCY_CALL, isCritical = true),
                GuideStep(id = "shock_step_2", guideId = "shock_guide", stepNumber = 2, title = "Lay Person Down", description = "Lay person down and elevate legs", stepType = StepType.ACTION, isCritical = true)
            ),
            iconResName = "ic_shock",
            whenToCallEmergency = "Any signs of shock (clammy skin, weak pulse, confusion)",
            estimatedTimeMinutes = 10,
            difficulty = "Intermediate"
        )
    }

    private fun createHeartAttackGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "heart_attack_guide",
            title = "Heart Attack Response",
            category = "Medical Emergency",
            severity = "CRITICAL",
            description = "Recognize the signs of a heart attack and take immediate action.",
            steps = listOf(
                GuideStep(id = "heart_attack_step_1", guideId = "heart_attack_guide", stepNumber = 1, title = "Call Emergency", description = "Call 112 immediately", stepType = StepType.EMERGENCY_CALL, isCritical = true),
                GuideStep(id = "heart_attack_step_2", guideId = "heart_attack_guide", stepNumber = 2, title = "Help Person Rest", description = "Have person sit down and rest", stepType = StepType.ACTION)
            ),
            iconResName = "ic_heart_attack",
            whenToCallEmergency = "Chest pain, shortness of breath, or other heart attack signs",
            warnings = listOf("Do not let the person drive to the hospital"),
            estimatedTimeMinutes = 5,
            difficulty = "Beginner"
        )
    }

    private fun createStrokeGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "stroke_guide",
            title = "Stroke (Brain Attack)",
            category = "Medical Emergency",
            severity = "CRITICAL",
            description = "Recognize and respond to the signs of a stroke using the F.A.S.T. method.",
            steps = listOf(
                GuideStep(id = "stroke_step_1", guideId = "stroke_guide", stepNumber = 1, title = "Call Emergency", description = "Call 112 immediately", stepType = StepType.EMERGENCY_CALL, isCritical = true),
                GuideStep(id = "stroke_step_2", guideId = "stroke_guide", stepNumber = 2, title = "Note the Time", description = "Note the time when first symptoms appeared", stepType = StepType.CHECK, isCritical = true)
            ),
            iconResName = "ic_stroke",
            whenToCallEmergency = "Any sign of stroke (face drooping, arm weakness, speech difficulty)",
            estimatedTimeMinutes = 5,
            difficulty = "Beginner"
        )
    }

    private fun createAllergicReactionGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "allergic_reaction_guide",
            title = "Anaphylaxis & Severe Allergic Reactions",
            category = "Medical Emergency",
            severity = "CRITICAL",
            description = "Administer an epinephrine auto-injector and provide care for a severe allergic reaction.",
            steps = listOf(
                GuideStep(id = "allergic_step_1", guideId = "allergic_reaction_guide", stepNumber = 1, title = "Use Epinephrine Auto-Injector", description = "Help person use their epinephrine auto-injector", stepType = StepType.ACTION, isCritical = true),
                GuideStep(id = "allergic_step_2", guideId = "allergic_reaction_guide", stepNumber = 2, title = "Call Emergency", description = "Call 112 even after using injector", stepType = StepType.EMERGENCY_CALL, isCritical = true)
            ),
            iconResName = "ic_allergy",
            whenToCallEmergency = "Difficulty breathing, swelling of lips or tongue",
            warnings = listOf("A second dose of epinephrine may be needed"),
            estimatedTimeMinutes = 10,
            difficulty = "Intermediate"
        )
    }

    private fun createSprainsStrainsGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "sprains_strains_guide",
            title = "Sprains and Strains",
            category = "Trauma",
            severity = "LOW",
            description = "Provide basic care for sprains and strains using the R.I.C.E. method.",
            steps = emptyList(),
            iconResName = "ic_sprain",
            whenToCallEmergency = "If you can't move the injured joint or it's numb",
            estimatedTimeMinutes = 20,
            difficulty = "Beginner"
        )
    }

    private fun createHypothermiaGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "hypothermia_guide",
            title = "Hypothermia Care",
            category = "Environmental",
            severity = "HIGH",
            description = "Warm someone who is dangerously cold and prevent further heat loss.",
            steps = emptyList(),
            iconResName = "ic_hypothermia",
            whenToCallEmergency = "Loss of consciousness, slow breathing, or weak pulse",
            warnings = listOf("Do not use direct heat like hot water or heating pads"),
            estimatedTimeMinutes = 30,
            difficulty = "Intermediate"
        )
    }

    private fun createHeatExhaustionGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "heat_exhaustion_guide",
            title = "Heat Exhaustion",
            category = "Environmental",
            severity = "MEDIUM",
            description = "Cool down someone who is overheating to prevent heatstroke.",
            steps = emptyList(),
            iconResName = "ic_heat_exhaustion",
            whenToCallEmergency = "If person's condition worsens or they become unconscious",
            estimatedTimeMinutes = 30,
            difficulty = "Beginner"
        )
    }

    private fun createSeizuresGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "seizures_guide",
            title = "Seizures & Epilepsy",
            category = "Medical Emergency",
            severity = "HIGH",
            description = "Protect someone having a seizure from injury.",
            steps = emptyList(),
            iconResName = "ic_seizure",
            whenToCallEmergency = "Seizure lasts more than 5 minutes or person is injured",
            warnings = listOf("Do not put anything in the person's mouth"),
            estimatedTimeMinutes = 10,
            difficulty = "Beginner"
        )
    }

    private fun createBitesStingsGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "bites_stings_guide",
            title = "Insect Bites & Stings",
            category = "Trauma",
            severity = "LOW",
            description = "Provide comfort and care for common insect bites and stings.",
            steps = emptyList(),
            iconResName = "ic_bites_stings",
            whenToCallEmergency = "Signs of a severe allergic reaction (anaphylaxis)",
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
            description = "Help someone use their inhaler and manage an asthma attack.",
            steps = emptyList(),
            iconResName = "ic_asthma",
            whenToCallEmergency = "If the person's inhaler is not helping or they can't speak",
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
            description = "Recognize and help with low blood sugar (hypoglycemia).",
            steps = emptyList(),
            iconResName = "ic_diabetic",
            whenToCallEmergency = "If the person becomes unresponsive or can't swallow",
            warnings = listOf("Do not give insulin unless you are trained to do so"),
            estimatedTimeMinutes = 15,
            difficulty = "Intermediate"
        )
    }

    private fun createDrowningGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "drowning_guide",
            title = "Drowning Response",
            category = "Life-Threatening",
            severity = "CRITICAL",
            description = "Provide rescue breaths and care after pulling someone from the water.",
            steps = emptyList(),
            iconResName = "ic_drowning",
            whenToCallEmergency = "Always call for any drowning incident",
            estimatedTimeMinutes = 5,
            difficulty = "Intermediate"
        )
    }

    private fun createNosebleedsGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "nosebleeds_guide",
            title = "Nosebleeds",
            category = "Trauma",
            severity = "LOW",
            description = "Simple steps to stop a common nosebleed.",
            steps = emptyList(),
            iconResName = "ic_nosebleed",
            whenToCallEmergency = "If bleeding doesn't stop after 20 minutes",
            estimatedTimeMinutes = 20,
            difficulty = "Beginner"
        )
    }

    private fun createEyeInjuriesGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "eye_injuries_guide",
            title = "Eye Injuries",
            category = "Trauma",
            severity = "MEDIUM",
            description = "Care for chemical splashes or foreign objects in the eye.",
            steps = emptyList(),
            iconResName = "ic_eye_injury",
            whenToCallEmergency = "For any major eye injury or vision loss",
            warnings = listOf("Do not rub the eye"),
            estimatedTimeMinutes = 15,
            difficulty = "Intermediate"
        )
    }
}
