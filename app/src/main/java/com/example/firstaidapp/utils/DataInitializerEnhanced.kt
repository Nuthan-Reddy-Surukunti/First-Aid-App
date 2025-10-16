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

object DataInitializerEnhanced {

    private const val TAG = "DataInitializerEnhanced"
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

                val expectedGuidesCount = 20
                val needsFullReinitialization = guidesCount < expectedGuidesCount

                if (needsFullReinitialization) {
                    Log.i(TAG, "initializeData: forcing reinitialization - found $guidesCount guides, expected $expectedGuidesCount")

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
                    Log.i(TAG, "initializeData: all guides already present ($guidesCount/$expectedGuidesCount)")
                }

            } catch (e: Throwable) {
                Log.e(TAG, "initializeData: unexpected error", e)
                isInitialized.set(false) // Reset on failure to allow retry
            }
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

        for ((index, guide) in guides.withIndex()) {
            try {
                Log.i(TAG, "insertGuide: inserting ${guide.id} (index=${index})")
                database.guideDao().insertGuide(guide)
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
                name = "Emergency Services (All)",
                phoneNumber = EMERGENCY_NUMBER_IN,
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Unified emergency number for Police, Fire, and Medical emergencies in India"
            ),
            EmergencyContact(
                name = "Police",
                phoneNumber = "100",
                type = ContactType.POLICE,
                relationship = "Emergency",
                notes = "Police emergency services"
            ),
            EmergencyContact(
                name = "Fire Brigade",
                phoneNumber = "101",
                type = ContactType.FIRE_DEPARTMENT,
                relationship = "Emergency",
                notes = "Fire and rescue services"
            ),
            EmergencyContact(
                name = "Medical Emergency",
                phoneNumber = "108",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Medical emergency and ambulance services"
            ),
            EmergencyContact(
                name = "National Poison Information Centre",
                phoneNumber = POISON_CONTROL_IN,
                type = ContactType.POISON_CONTROL,
                relationship = "Emergency",
                notes = "24/7 poison emergency hotline in India"
            )
        )

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

    // CRITICAL GUIDES WITH COMPREHENSIVE DETAILED INSTRUCTIONS

    private fun createCPRGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "cpr_guide",
            title = "CPR (Cardiopulmonary Resuscitation)",
            category = "Life-Threatening",
            severity = "CRITICAL",
            description = "CPR is a life-saving emergency procedure performed when someone's heart stops beating or they stop breathing. CPR combines chest compressions with rescue breathing to maintain blood circulation and oxygen delivery to vital organs, particularly the brain. In cardiac arrest, brain damage can occur within 4-6 minutes without oxygen, making immediate CPR crucial for survival. Modern CPR focuses on high-quality chest compressions at the correct rate and depth. The procedure should be performed on anyone who is unresponsive and not breathing normally. CPR can double or triple the chances of survival when performed correctly and immediately.",
            steps = listOf(
                GuideStep(
                    id = "cpr_step_1",
                    guideId = "cpr_guide",
                    stepNumber = 1,
                    title = "Check Responsiveness and Breathing",
                    description = "Tap shoulders firmly and shout 'Are you okay?' Look for normal breathing for no more than 10 seconds.",
                    detailedInstructions = "Approach safely and tap the person's shoulders firmly while shouting 'Are you okay?' Look, listen, and feel for normal breathing for no more than 10 seconds. Normal breathing is regular and quiet. Gasping, agonal breathing, or snoring sounds are NOT normal breathing and require CPR. If responsive but having difficulty breathing, help them into a comfortable position and monitor while waiting for emergency services. If unresponsive and not breathing normally, begin CPR immediately.",
                    duration = "10-15 seconds",
                    tips = listOf("Tap firmly on shoulders", "Shout loudly", "Look for chest rising/falling", "Listen for breathing sounds"),
                    warnings = listOf("Don't perform CPR on conscious person", "Don't waste time checking pulse if untrained", "Gasping is not normal breathing"),
                    requiredTools = listOf("None required"),
                    stepType = StepType.CHECK,
                    isCritical = true
                ),
                GuideStep(
                    id = "cpr_step_2",
                    guideId = "cpr_guide",
                    stepNumber = 2,
                    title = "Call Emergency Services",
                    description = "Call 112 immediately. Request AED if available. Put phone on speaker for guidance.",
                    detailedInstructions = "Call 112 immediately or have someone else call. State clearly: 'I need an ambulance, someone is unconscious and not breathing.' Give exact location. If others present, designate someone to call and another to find an AED. Put phone on speaker to receive CPR instructions while performing compressions. Don't hang up unless instructed. The dispatcher will guide you through CPR if needed.",
                    duration = "30-60 seconds",
                    tips = listOf("Put phone on speaker", "Give exact location", "Say 'cardiac arrest' or 'not breathing'", "Ask someone to find AED"),
                    warnings = listOf("Don't delay CPR to make call", "Don't hang up unless told", "Don't assume someone else called"),
                    requiredTools = listOf("Phone", "AED if available"),
                    stepType = StepType.CALL,
                    isCritical = true
                ),
                GuideStep(
                    id = "cpr_step_3",
                    guideId = "cpr_guide",
                    stepNumber = 3,
                    title = "Position Person Properly",
                    description = "Place on firm, flat surface, face up. Tilt head back slightly to open airway.",
                    detailedInstructions = "Move person to firm, flat surface if not already there. Soft surfaces like beds reduce compression effectiveness. Position face-up with arms at sides. Tilt head back slightly by lifting chin and pushing forehead down to open airway. If neck injury suspected, use jaw thrust instead. Remove visible obstructions from mouth but don't do blind finger sweeps. Proper positioning is crucial for effective compressions.",
                    duration = "15-30 seconds",
                    tips = listOf("Use floor or hard surface", "Check for visible obstructions", "Tilt head back to open airway", "Position arms at sides"),
                    warnings = listOf("Don't move if spinal injury suspected unless necessary", "Don't do blind finger sweeps", "Avoid soft surfaces"),
                    requiredTools = listOf("Firm flat surface"),
                    stepType = StepType.ACTION
                ),
                GuideStep(
                    id = "cpr_step_4",
                    guideId = "cpr_guide",
                    stepNumber = 4,
                    title = "Correct Hand Placement",
                    description = "Place heel of hand on center of chest between nipples. Other hand on top, fingers interlocked.",
                    detailedInstructions = "Kneel beside person's chest. Place heel of one hand on center of chest between nipples, on lower half of breastbone. Place other hand on top, interlacing fingers. Keep fingers lifted off chest - only heel of bottom hand touches chest. Position shoulders directly over hands with arms straight. This allows effective use of body weight. Correct placement is critical - too high breaks ribs, too low damages organs.",
                    duration = "15-20 seconds",
                    tips = listOf("Use heel of hand only", "Interlock fingers", "Keep arms straight", "Shoulders over hands"),
                    warnings = listOf("Don't press on ribs or stomach", "Don't let fingers touch chest", "Don't lean on chest between compressions"),
                    requiredTools = listOf("None required"),
                    stepType = StepType.ACTION,
                    isCritical = true
                ),
                GuideStep(
                    id = "cpr_step_5",
                    guideId = "cpr_guide",
                    stepNumber = 5,
                    title = "Perform High-Quality Compressions",
                    description = "Push hard and fast at least 2 inches deep, 100-120 compressions per minute. Allow complete recoil.",
                    detailedInstructions = "Push straight down at least 2 inches (5cm) using your body weight. Compress at 100-120 per minute - count '1-and-2-and-3-and...' Allow complete chest recoil between compressions but don't lift hands off chest. Compression and relaxation phases should be equal. After 30 compressions, give 2 rescue breaths if trained, then resume compressions. If untrained in rescue breathing, provide continuous compressions. Switch rescuers every 2 minutes to prevent fatigue.",
                    duration = "Continuous until help arrives",
                    tips = listOf("Count out loud for rhythm", "Use full body weight", "Allow complete recoil", "Switch every 2 minutes if possible"),
                    warnings = listOf("Don't stop for more than 10 seconds", "Don't compress too shallow", "Don't lean on chest between compressions"),
                    requiredTools = listOf("None required"),
                    stepType = StepType.ACTION,
                    isCritical = true
                ),
                GuideStep(
                    id = "cpr_step_6",
                    guideId = "cpr_guide",
                    stepNumber = 6,
                    title = "Continue Until Help Arrives",
                    description = "Don't stop CPR until emergency services arrive, person breathes normally, or you're too exhausted.",
                    detailedInstructions = "Continue CPR until: emergency services arrive and take over, person starts breathing normally and regains consciousness, AED becomes available, or you become too exhausted to continue effectively. If person starts breathing normally, place in recovery position and monitor closely. Be ready to resume CPR if breathing stops. If multiple rescuers present, switch every 2 minutes with less than 10 seconds interruption. Follow dispatcher instructions throughout.",
                    duration = "Until help arrives (average 8-12 minutes)",
                    tips = listOf("Switch rescuers every 2 minutes", "Minimize interruptions under 10 seconds", "Monitor for normal breathing", "Stay until help arrives"),
                    warnings = listOf("Don't stop unless person clearly breathing normally", "Don't leave person alone", "Don't give up too early"),
                    requiredTools = listOf("None required"),
                    stepType = StepType.REPEAT,
                    isCritical = true
                )
            ),
            iconResName = "ic_cpr",
            whenToCallEmergency = "Person is unresponsive and not breathing normally",
            warnings = listOf("Only perform if person is unresponsive and not breathing", "Don't be afraid to push hard - broken ribs heal, brain damage doesn't", "Continue until professional help arrives"),
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
            description = "Choking occurs when a foreign object blocks the airway, preventing normal breathing. This is a medical emergency that can lead to unconsciousness and death within minutes if not treated immediately. The most common causes include food (especially in adults), small toys or objects (in children), and liquid aspiration. Recognition is critical - a choking person may make the universal choking sign (hands clutching throat), be unable to speak, cough weakly or not at all, make high-pitched sounds when trying to breathe, or turn blue around the lips and face. The Heimlich maneuver (abdominal thrusts) is the primary treatment for conscious choking victims.",
            steps = listOf(
                GuideStep(
                    id = "choking_step_1",
                    guideId = "choking_guide",
                    stepNumber = 1,
                    title = "Assess and Confirm Choking",
                    description = "Ask 'Are you choking?' If they can't speak or cough effectively, act immediately.",
                    detailedInstructions = "Quickly approach and ask loudly 'Are you choking?' A truly choking person cannot speak, may make the universal choking sign (hands clutching throat), and appears panicked. They may make high-pitched squeaking sounds or no sound at all when trying to breathe. If they can speak, cough forcefully, or cry, encourage continued coughing. If they cannot speak, make sounds, or cough effectively, or nod yes to your question, begin immediate treatment. Look for blue coloration around lips or face indicating severe oxygen deprivation.",
                    duration = "5-10 seconds",
                    tips = listOf("Ask loudly and clearly", "Look for universal choking sign", "Check if they can speak or cough", "Look for blue coloration"),
                    warnings = listOf("Don't waste time if signs are obvious", "Don't perform on someone who can speak or cough forcefully"),
                    requiredTools = listOf("None required"),
                    stepType = StepType.CHECK,
                    isCritical = true
                ),
                GuideStep(
                    id = "choking_step_2",
                    guideId = "choking_guide",
                    stepNumber = 2,
                    title = "Position Behind Person",
                    description = "Stand behind and wrap arms around waist, just above the navel.",
                    detailedInstructions = "Quickly move behind the choking person and wrap your arms around their waist. For adults and children over 1 year, position arms just above the navel and below rib cage. Place feet shoulder-width apart for stability. If person is much taller, have them sit or kneel. For pregnant women or very obese individuals, position arms around chest above breastbone instead. Ensure firm grip and stable stance before beginning thrusts. If person becomes unconscious, lower to ground and begin CPR.",
                    duration = "5-10 seconds",
                    tips = listOf("Stand firmly behind person", "Arms around waist, not ribs", "Use chair if person much taller", "Ensure stable footing"),
                    warnings = listOf("Don't position arms too high on ribs", "Don't attempt if person unconscious", "Modify position for pregnant women"),
                    requiredTools = listOf("Chair if needed for height difference"),
                    stepType = StepType.ACTION
                ),
                GuideStep(
                    id = "choking_step_3",
                    guideId = "choking_guide",
                    stepNumber = 3,
                    title = "Perform Abdominal Thrusts",
                    description = "Make a fist and give quick, upward thrusts into the abdomen.",
                    detailedInstructions = "Make a fist with one hand and place thumb side against person's abdomen, just above navel and below rib cage. Grasp fist with other hand and pull sharply inward and upward with quick, forceful thrusts. Each thrust should be separate, distinct movement aimed at creating pressure to expel object. Perform up to 5 thrusts, checking after each if object dislodged. Thrusts should be forceful enough to lift person's feet slightly off ground. If object doesn't dislodge after 5 thrusts, call emergency help if not done, continue thrusting until object expelled or person becomes unconscious.",
                    duration = "30-60 seconds for series of thrusts",
                    tips = listOf("Thumb side of fist against abdomen", "Quick, sharp upward thrusts", "Check after each thrust", "Each thrust should be forceful"),
                    warnings = listOf("Don't thrust too high into ribs", "Don't continue on unconscious person", "Don't be afraid to use significant force"),
                    requiredTools = listOf("None required"),
                    stepType = StepType.ACTION,
                    isCritical = true
                ),
                GuideStep(
                    id = "choking_step_4",
                    guideId = "choking_guide",
                    stepNumber = 4,
                    title = "If Unconscious, Begin CPR",
                    description = "If person becomes unconscious, lower to ground and start CPR immediately.",
                    detailedInstructions = "If person becomes unconscious during treatment, immediately lower them to ground on their back and begin CPR. Call 112 if not already done. Chest compressions of CPR may help dislodge object. Before giving rescue breaths, look in mouth and remove visible objects with fingers, but never do blind finger sweep. Give 30 chest compressions followed by 2 rescue breaths, checking mouth before each rescue breath attempt. Continue CPR cycles until object expelled, person starts breathing normally, or emergency services arrive.",
                    duration = "Continue until help arrives or object expelled",
                    tips = listOf("Lower person carefully to ground", "Check mouth before rescue breaths", "Use standard CPR technique", "Continue until help arrives"),
                    warnings = listOf("Don't do blind finger sweeps", "Don't continue abdominal thrusts on unconscious person", "Don't give up"),
                    requiredTools = listOf("Firm surface for CPR"),
                    stepType = StepType.ACTION,
                    isCritical = true
                )
            ),
            iconResName = "ic_choking",
            whenToCallEmergency = "Person cannot speak, cough, or breathe; if person becomes unconscious; or if choking continues after initial attempts",
            warnings = listOf("Don't perform on pregnant women or infants under 1 year", "Don't do blind finger sweeps", "Don't continue abdominal thrusts if person becomes unconscious"),
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
            description = "Severe bleeding can lead to shock and death within minutes if not controlled immediately. The human body contains approximately 5 liters of blood, and losing just 1-2 liters can be life-threatening. Arterial bleeding (bright red, spurting blood) is more dangerous than venous bleeding (dark red, flowing blood), but both require immediate attention. The primary goal is to stop bleeding while preventing shock and infection.",
            steps = listOf(
                GuideStep(
                    id = "bleeding_step_1",
                    guideId = "bleeding_guide",
                    stepNumber = 1,
                    title = "Immediate Direct Pressure",
                    description = "Apply firm, direct pressure to the wound immediately.",
                    detailedInstructions = """
                        1. Ensure scene safety and put on gloves if available (don't delay if no gloves)
                        2. Apply immediate direct pressure:
                           • Use clean cloth, gauze, or clothing if no medical supplies
                           • Place directly over the bleeding wound
                           • Press firmly with palm of hand or fingers
                           • If object embedded in wound, press around it (never remove)
                        3. Maintain continuous pressure:
                           • Don't lift to check if bleeding stopped
                           • Press firmly enough to compress blood vessels
                           • Use both hands if needed for large wounds
                           • If blood soaks through, add more material on top (don't remove first layer)
                        4. Position person appropriately:
                           • Have person lie down if possible
                           • Keep person calm and still
                           • Elevate legs if no spinal injury suspected
                        5. Call for emergency help immediately if:
                           • Bleeding is severe or won't stop
                           • Blood is spurting (arterial bleeding)
                           • Person shows signs of shock
                           • Wound is on torso, neck, or head
                    """.trimIndent(),
                    duration = "Continue until bleeding stops or help arrives",
                    tips = listOf("Don't remove first blood-soaked layer", "Use clean materials when possible", "Maintain firm continuous pressure", "Call for help immediately"),
                    warnings = listOf("Don't remove embedded objects", "Don't use tourniquets unless trained", "Don't lift pressure to peek"),
                    requiredTools = listOf("Clean cloths or gauze", "Gloves if available"),
                    stepType = StepType.ACTION,
                    isCritical = true
                ),
                GuideStep(
                    id = "bleeding_step_2",
                    guideId = "bleeding_guide",
                    stepNumber = 2,
                    title = "Wound Assessment and Positioning",
                    description = "Assess wound severity and position person to control bleeding.",
                    detailedInstructions = """
                        1. Assess bleeding type while maintaining pressure:
                           • Arterial: Bright red, spurting blood (life-threatening)
                           • Venous: Dark red, flowing blood (serious)
                           • Capillary: Slow oozing (less serious)
                        2. Elevate bleeding area if possible:
                           • Raise wounded limb above heart level
                           • Only if no fracture suspected
                           • Continue direct pressure while elevating
                           • Use pillows, blankets, or assistance to maintain elevation
                        3. Position person for shock prevention:
                           • Keep person lying down
                           • Elevate legs 8-12 inches (if no spinal injury)
                           • Keep head flat or slightly elevated
                           • Loosen tight clothing around neck and waist
                        4. Identify wounds requiring immediate emergency care:
                           • Deep wounds showing fat, muscle, or bone
                           • Wounds longer than 1 inch or gaping open
                           • Wounds on joints, hands, face, or genitals
                           • Any wound with continuous severe bleeding
                           • Puncture wounds (especially deep ones)
                        5. Special considerations:
                           • Neck wounds: support head, don't elevate
                           • Chest wounds: sit person up if conscious
                           • Abdominal wounds: bend knees up toward chest
                    """.trimIndent(),
                    duration = "5-10 minutes assessment while maintaining pressure",
                    tips = listOf("Elevate bleeding area above heart", "Position based on wound location", "Watch for signs of shock", "Don't elevate if fracture suspected"),
                    warnings = listOf("Don't elevate if spinal injury suspected", "Don't ignore location-specific positioning", "Don't delay pressure for positioning"),
                    requiredTools = listOf("Pillows or blankets for elevation"),
                    stepType = StepType.ASSESSMENT,
                    isCritical = true
                ),
                GuideStep(
                    id = "bleeding_step_3",
                    guideId = "bleeding_guide",
                    stepNumber = 3,
                    title = "Advanced Bleeding Control",
                    description = "Apply pressure bandages and additional bleeding control techniques.",
                    detailedInstructions = """
                        1. Apply pressure bandage once initial bleeding slows:
                           • Keep original blood-soaked materials in place
                           • Wrap firmly with roller bandage or strips of cloth
                           • Maintain pressure while wrapping
                           • Secure with tape or tie
                           • Check circulation below bandage (pulse, color, warmth)
                        2. Pressure point technique if direct pressure insufficient:
                           • Apply pressure to major arteries supplying the area:
                             - Arm bleeding: brachial artery (inside upper arm)
                             - Leg bleeding: femoral artery (groin area)
                             - Head/neck bleeding: carotid artery (side of neck)
                           • Use fingertips to press artery against underlying bone
                           • Maintain direct pressure on wound simultaneously
                        3. If bleeding continues through bandages:
                           • Don't remove original bandages
                           • Add more layers on top
                           • Increase direct pressure
                           • Consider pressure points
                           • Prepare for more aggressive measures if trained
                        4. Monitor bandage tightness:
                           • Check pulse below bandage every 10 minutes
                           • Look for blue/white color in fingers/toes
                           • Feel for warmth and sensation
                           • Loosen slightly if circulation compromised
                        5. Tourniquet use (only if trained and life-threatening):
                           • Last resort for severe limb bleeding
                           • Apply 2-3 inches above wound
                           • Tighten until bleeding stops
                           • Note time of application
                           • Never loosen once applied
                    """.trimIndent(),
                    duration = "10-15 minutes for proper bandaging",
                    tips = listOf("Don't remove blood-soaked materials", "Check circulation frequently", "Use pressure points if needed", "Only trained persons should use tourniquets"),
                    warnings = listOf("Don't make bandages too tight", "Don't use tourniquets without training", "Don't remove pressure once bleeding controlled"),
                    requiredTools = listOf("Roller bandages", "Medical tape", "Additional gauze"),
                    stepType = StepType.ACTION,
                    isCritical = true
                ),
                GuideStep(
                    id = "bleeding_step_4",
                    guideId = "bleeding_guide",
                    stepNumber = 4,
                    title = "Shock Prevention and Monitoring",
                    description = "Monitor for shock and provide supportive care until help arrives.",
                    detailedInstructions = """
                        1. Monitor for signs of shock continuously:
                           • Rapid, weak pulse (over 100 beats per minute)
                           • Pale, cold, clammy skin
                           • Rapid, shallow breathing
                           • Confusion or anxiety
                           • Weakness or dizziness
                           • Nausea or vomiting
                           • Decreased urine output
                        2. Shock prevention measures:
                           • Keep person lying down with legs elevated
                           • Maintain normal body temperature:
                             - Cover with blankets to prevent heat loss
                             - Don't overheat
                           • Reassure and keep calm
                           • Don't give food or water
                           • Monitor vital signs every 5 minutes
                        3. Ongoing wound care:
                           • Continue to monitor bleeding control
                           • Don't disturb bandages unless bleeding resumes
                           • Watch for signs of infection (later concern)
                           • Document time bleeding was controlled
                        4. Prepare for emergency responders:
                           • Gather information about:
                             - How injury occurred
                             - Time bleeding started
                             - Estimated blood loss
                             - Person's medical history if known
                             - Medications or allergies
                           • Stay with person until help arrives
                           • Be ready to assist with transport
                        5. If person becomes unconscious:
                           • Check for breathing and pulse
                           • Place in recovery position if breathing normally
                           • Be prepared to start CPR if needed
                           • Continue bleeding control measures
                    """.trimIndent(),
                    duration = "Until emergency help arrives",
                    tips = listOf("Monitor vital signs every 5 minutes", "Keep person warm but not hot", "Document all care provided", "Stay with person until help arrives"),
                    warnings = listOf("Don't give food or water", "Don't leave person alone", "Don't ignore signs of shock", "Be prepared for CPR if needed"),
                    requiredTools = listOf("Blankets", "Watch for timing", "Paper to document care"),
                    stepType = StepType.MONITORING,
                    isCritical = true
                )
            ),
            iconResName = "ic_bleeding",
            whenToCallEmergency = "Severe bleeding, arterial bleeding (spurting), deep wounds, signs of shock, or bleeding that won't stop with direct pressure",
            warnings = listOf("Don't remove embedded objects", "Don't use tourniquets without training", "Don't give food or water to shock victims", "Don't leave person alone"),
            estimatedTimeMinutes = 20,
            difficulty = "Intermediate"
        )
    }

    private fun createBurnsGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "burns_guide",
            title = "Burns Treatment",
            category = "Trauma",
            severity = "MEDIUM",
            description = "Burns are classified into three degrees based on severity and depth. First-degree burns affect only the outer skin layer (epidermis) and appear red without blisters. Second-degree burns damage both outer and underlying skin layers, causing redness, swelling, and blisters. Third-degree burns destroy all skin layers and may appear white, charred, or leathery. Immediate cooling with lukewarm running water is the most effective treatment for most burns. The goal is to stop the burning process, prevent infection, manage pain, and minimize scarring. Chemical and electrical burns require special considerations.",
            steps = listOf(
                GuideStep(
                    id = "burns_step_1",
                    guideId = "burns_guide",
                    stepNumber = 1,
                    title = "Remove from Heat Source",
                    description = "Immediately remove person from heat source and ensure scene safety.",
                    detailedInstructions = "First priority is stopping the burning process. Remove person from heat source - extinguish flames by having them stop, drop, and roll, or smother flames with blanket. For electrical burns, turn off power source before touching person. For chemical burns, remove contaminated clothing and flush with water. Ensure your own safety - don't become another victim. If clothing is stuck to burn, don't remove it - cut around it. Remove jewelry and tight clothing near burn before swelling begins. Time is critical - every second of continued burning worsens the injury.",
                    duration = "Immediate action required",
                    tips = listOf("Stop, drop, and roll for flames", "Turn off electricity before touching", "Remove jewelry before swelling", "Cut around stuck clothing"),
                    warnings = listOf("Don't touch person with electrical burns until power is off", "Don't pull off stuck clothing", "Don't waste time - act immediately"),
                    requiredTools = listOf("Blanket to smother flames if needed"),
                    stepType = StepType.SAFETY,
                    isCritical = true
                ),
                GuideStep(
                    id = "burns_step_2",
                    guideId = "burns_guide",
                    stepNumber = 2,
                    title = "Cool the Burn",
                    description = "Cool burn with lukewarm running water for 10-20 minutes.",
                    detailedInstructions = "Immediately cool burn with lukewarm (not cold) running water for 10-20 minutes. This stops burning process, reduces pain, and minimizes tissue damage. Hold burned area under gentle stream of water or use wet cloths if running water unavailable. Water should be lukewarm - cold water or ice can cause further tissue damage and hypothermia. For chemical burns, flush for at least 20 minutes to remove all chemical residue. Don't use ice, butter, oil, or home remedies. If burn covers large area, be careful not to cause hypothermia during cooling.",
                    duration = "10-20 minutes of cooling",
                    tips = listOf("Use lukewarm, not cold water", "Use wet cloths if no running water", "Flush chemical burns for 20+ minutes", "Be gentle with water pressure"),
                    warnings = listOf("Don't use ice or cold water", "Don't apply butter, oil, or home remedies", "Don't cause hypothermia with prolonged cooling"),
                    requiredTools = listOf("Lukewarm running water or wet cloths"),
                    stepType = StepType.ACTION,
                    isCritical = true
                ),
                GuideStep(
                    id = "burns_step_3",
                    guideId = "burns_guide",
                    stepNumber = 3,
                    title = "Assess Burn Severity",
                    description = "Determine if emergency care is needed based on burn size, depth, and location.",
                    detailedInstructions = "Assess burn severity to determine treatment needed. Call 112 for: burns larger than person's palm, burns on face/hands/feet/genitals/joints, third-degree burns (white/charred appearance), chemical or electrical burns, burns in elderly/infants, or burns with breathing difficulties. First-degree burns are red without blisters and can be treated at home. Second-degree burns have blisters and may need medical attention if large. Third-degree burns appear white, brown, or charred and always need emergency care. Consider percentage of body affected - burns covering more than 10% of body surface require emergency care.",
                    duration = "2-3 minutes assessment",
                    tips = listOf("Use palm size to estimate burn area", "Check for white or charred appearance", "Consider burn location", "Assess breathing difficulties"),
                    warnings = listOf("Don't underestimate electrical burns", "Don't delay calling for large burns", "Don't ignore burns on critical areas"),
                    requiredTools = listOf("None required for assessment"),
                    stepType = StepType.CHECK
                ),
                GuideStep(
                    id = "burns_step_4",
                    guideId = "burns_guide",
                    stepNumber = 4,
                    title = "Apply Loose Sterile Bandage",
                    description = "Cover burn with loose, sterile, non-stick bandage.",
                    detailedInstructions = "After cooling, gently pat area dry and cover with loose, sterile, non-stick bandage or clean cloth. Bandage should be loose enough not to put pressure on burn but secure enough to protect from infection. Use sterile gauze if available, or clean dry cloth. For large burns, use sheets or towels. Don't apply bandage tightly as swelling will occur. Don't use adhesive bandages directly on burn. For second-degree burns with blisters, don't break blisters as they protect underlying tissue from infection. Cover fingers/toes individually to prevent sticking together.",
                    duration = "5-10 minutes to bandage properly",
                    tips = listOf("Use non-stick sterile gauze if available", "Bandage loosely to allow for swelling", "Cover fingers/toes individually", "Don't break blisters"),
                    warnings = listOf("Don't apply bandage tightly", "Don't use adhesive directly on burn", "Don't break blisters intentionally"),
                    requiredTools = listOf("Sterile gauze or clean cloth", "Medical tape"),
                    stepType = StepType.ACTION
                ),
                GuideStep(
                    id = "burns_step_5",
                    guideId = "burns_guide",
                    stepNumber = 5,
                    title = "Pain Management and Monitoring",
                    description = "Manage pain and monitor for signs of infection or complications.",
                    detailedInstructions = "For minor burns, over-the-counter pain medications like ibuprofen or acetaminophen can help manage pain and reduce inflammation. Keep burn elevated above heart level if possible to reduce swelling. Monitor for signs of infection: increased pain, redness, swelling, warmth, pus, red streaking, or fever. Change bandages daily or when they become wet/dirty. For minor burns, healing typically takes 1-3 weeks. Seek medical attention if burn doesn't heal properly, shows signs of infection, or if person hasn't had tetanus shot in last 10 years.",
                    duration = "Ongoing monitoring during healing",
                    tips = listOf("Use OTC pain medication as directed", "Elevate burned area", "Change bandages when wet/dirty", "Monitor for infection signs"),
                    warnings = listOf("Don't ignore signs of infection", "Don't apply topical anesthetics without medical advice", "Don't expose healing burn to sun"),
                    requiredTools = listOf("Pain medication", "Clean bandages for changes"),
                    stepType = StepType.MONITORING
                )
            ),
            iconResName = "ic_burns",
            whenToCallEmergency = "Burns larger than palm size, burns on face/hands/feet/genitals, white/charred skin, chemical/electrical burns, or breathing difficulties",
            warnings = listOf("Don't use ice water", "Don't apply butter or oil", "Don't break blisters", "Don't underestimate electrical burns"),
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
            description = "A fracture is a break in the continuity of a bone. Fractures can be closed (skin intact) or open (bone protruding through skin). Signs include severe pain, swelling, deformity, inability to use the limb, and grinding sensation when bone ends rub together. The goals of first aid are to prevent further injury, reduce pain, and prevent shock. Immobilization is key - prevent movement of the injured area and the joints above and below the fracture. Open fractures have high infection risk and always require emergency care. Spinal fractures require special handling to prevent paralysis.",
            steps = listOf(
                GuideStep(
                    id = "fractures_step_1",
                    guideId = "fractures_guide",
                    stepNumber = 1,
                    title = "Assess Scene Safety and Injury",
                    description = "Ensure scene safety and assess for life-threatening injuries first.",
                    detailedInstructions = "First ensure scene safety and check for life-threatening injuries. Look for signs of fracture: obvious deformity, severe pain, swelling, inability to move limb, or bone visible through skin. Check circulation beyond injury - feel for pulse, check skin color and temperature, and ask if person can feel touch. Don't move person unless in immediate danger. If spinal injury suspected (fall from height, motor vehicle accident, head injury), don't move at all unless absolutely necessary. Call 112 for obvious fractures, open fractures, or if you suspect spinal injury.",
                    duration = "2-3 minutes assessment",
                    tips = listOf("Look for obvious deformity", "Check pulse beyond injury", "Don't move unless necessary", "Suspect spinal injury with certain mechanisms"),
                    warnings = listOf("Don't move person with suspected spinal injury", "Don't attempt to realign deformed limbs", "Don't ignore circulation checks"),
                    requiredTools = listOf("None for assessment"),
                    stepType = StepType.CHECK,
                    isCritical = true
                ),
                GuideStep(
                    id = "fractures_step_2",
                    guideId = "fractures_guide",
                    stepNumber = 2,
                    title = "Call Emergency Services",
                    description = "Call 112 for obvious fractures, open fractures, or suspected spinal injuries.",
                    detailedInstructions = "Call 112 immediately for: obvious bone deformity, bone visible through skin (open fracture), suspected spinal injury, multiple fractures, fracture with loss of circulation, or if person cannot be moved safely. Describe injury location, whether bone is visible, and person's condition. If emergency services delayed, you may need to provide prolonged care and immobilization. For simple closed fractures of arms or legs in stable patients, emergency transport may not be needed, but medical evaluation is still important.",
                    duration = "2-3 minutes for call",
                    tips = listOf("Describe injury clearly", "Mention if bone visible", "Give exact location", "Ask for estimated arrival time"),
                    warnings = listOf("Don't delay call for obvious fractures", "Don't assume simple fractures don't need medical care", "Don't move person to get better phone signal"),
                    requiredTools = listOf("Phone"),
                    stepType = StepType.CALL,
                    isCritical = true
                ),
                GuideStep(
                    id = "fractures_step_3",
                    guideId = "fractures_guide",
                    stepNumber = 3,
                    title = "Control Bleeding and Protect Open Fractures",
                    description = "If bone is visible, control bleeding and cover wound without pushing bone back in.",
                    detailedInstructions = "For open fractures, control bleeding with direct pressure around (not on) the protruding bone. Don't attempt to push bone back into wound. Cover wound and protruding bone with sterile dressing or clean cloth, being careful not to apply pressure on bone itself. Apply pressure around wound edges to control bleeding. Build up dressing around bone to support and protect it. Monitor for shock as open fractures can cause significant blood loss. Don't remove clothing stuck to wound - cut around it.",
                    duration = "5-10 minutes depending on bleeding",
                    tips = listOf("Apply pressure around, not on bone", "Build up dressing around protruding bone", "Cut around stuck clothing", "Monitor for shock"),
                    warnings = listOf("Don't push bone back into wound", "Don't apply direct pressure on protruding bone", "Don't remove stuck clothing"),
                    requiredTools = listOf("Sterile dressings or clean cloths", "Bandages"),
                    stepType = StepType.ACTION
                ),
                GuideStep(
                    id = "fractures_step_4",
                    guideId = "fractures_guide",
                    stepNumber = 4,
                    title = "Immobilize the Fracture",
                    description = "Splint the fracture to prevent movement, including joints above and below the break.",
                    detailedInstructions = "Immobilize fracture by splinting above and below the injury, including joints on both sides of fracture. Use rigid materials like boards, rolled newspapers, or inflatable splints. Pad splint with soft material to prevent pressure sores. Secure splint with bandages or tape, but not so tight as to cut off circulation. For arm fractures, use sling to support weight. For leg fractures, may need to splint to opposite leg. Don't attempt to straighten deformed limbs - splint in position found. Check circulation after splinting by feeling pulse and checking skin color beyond injury.",
                    duration = "10-15 minutes to properly splint",
                    tips = listOf("Include joints above and below fracture", "Pad rigid splints", "Use sling for arm injuries", "Check circulation after splinting"),
                    warnings = listOf("Don't try to straighten deformed limbs", "Don't splint so tight as to cut off circulation", "Don't move injured limb more than necessary"),
                    requiredTools = listOf("Rigid splinting material", "Padding", "Bandages or tape", "Sling material"),
                    stepType = StepType.ACTION,
                    isCritical = true
                ),
                GuideStep(
                    id = "fractures_step_5",
                    guideId = "fractures_guide",
                    stepNumber = 5,
                    title = "Monitor and Treat for Shock",
                    description = "Monitor vital signs, treat for shock, and manage pain until help arrives.",
                    detailedInstructions = "Monitor person for signs of shock: rapid pulse, pale skin, confusion, or weakness. Keep person lying down and comfortable. Cover with blanket to maintain body temperature. Elevate legs unless leg is fractured or spinal injury suspected. Don't give food or water in case surgery needed. Provide emotional support and reassurance. Regularly check circulation beyond splint - if fingers/toes become blue, cold, or numb, loosen splint slightly. Apply ice packs to injury if available, but don't apply directly to skin. Continue monitoring until emergency services arrive.",
                    duration = "Continuous until help arrives",
                    tips = listOf("Check circulation beyond splint regularly", "Apply ice with barrier to prevent frostbite", "Provide emotional support", "Keep person still"),
                    warnings = listOf("Don't give food or water", "Don't apply ice directly to skin", "Don't leave person alone"),
                    requiredTools = listOf("Blanket", "Ice packs", "Towel for ice barrier"),
                    stepType = StepType.MONITORING
                )
            ),
            iconResName = "ic_fracture",
            whenToCallEmergency = "Obvious deformity, bone visible through skin, suspected spinal injury, or loss of circulation beyond injury",
            warnings = listOf("Don't try to realign broken bones", "Don't move person with suspected spinal injury", "Don't ignore circulation problems"),
            estimatedTimeMinutes = 15,
            difficulty = "Intermediate"
        )
    }

    private fun createPoisoningGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "poisoning_guide",
            title = "Poisoning Emergency",
            category = "Toxic Exposure",
            severity = "HIGH",
            description = "Poisoning occurs when harmful substances are ingested, inhaled, absorbed through skin, or injected. Common sources include medications, household chemicals, carbon monoxide, and contaminated food. Recognition and immediate action are crucial as some poisons can cause irreversible damage or death within minutes.",
            steps = listOf(
                GuideStep(
                    id = "poisoning_step_1",
                    guideId = "poisoning_guide",
                    stepNumber = 1,
                    title = "Ensure Safety and Identify Poison",
                    description = "Remove person from source if safe. Try to identify the poison.",
                    detailedInstructions = "First ensure your safety - don't become another victim. If poison is in the air (gas leak, fumes), get person to fresh air immediately. If safe, try to identify what was taken - look for containers, pill bottles, or ask the person if conscious. Note time of exposure, amount taken, and person's symptoms. Don't delay treatment to identify poison if person is in distress. For inhaled poisons, move to fresh air immediately. For ingested poisons, quickly remove any remaining substance from their mouth if visible and safe to do so.",
                    duration = "1-2 minutes",
                    tips = listOf("Save poison container", "Note time of exposure", "Get to fresh air if gas poisoning"),
                    warnings = listOf("Don't expose yourself to poison", "Don't induce vomiting unless instructed"),
                    requiredTools = listOf("Poison container for identification"),
                    stepType = StepType.SAFETY,
                    isCritical = true
                ),
                GuideStep(
                    id = "poisoning_step_2",
                    guideId = "poisoning_guide",
                    stepNumber = 2,
                    title = "Call Poison Control",
                    description = "Call poison control at " + POISON_CONTROL_IN + " immediately.",
                    detailedInstructions = "Call poison control hotline immediately at " + POISON_CONTROL_IN + ". They will guide you on specific treatment. Have poison container ready to read ingredients. If person unconscious or having severe symptoms, call 112 first. Don't wait if person is deteriorating rapidly. Provide information about the person's age, weight, what was taken, how much, and when. Follow their exact instructions - they may tell you to induce vomiting or give specific antidotes.",
                    duration = "Immediate",
                    tips = listOf("Have poison container ready", "Follow their exact instructions"),
                    warnings = listOf("Don't induce vomiting unless told", "Don't give activated charcoal unless instructed"),
                    requiredTools = listOf("Phone"),
                    stepType = StepType.CALL,
                    isCritical = true
                ),
                GuideStep(
                    id = "poisoning_step_3",
                    guideId = "poisoning_guide",
                    stepNumber = 3,
                    title = "Monitor and Support",
                    description = "Monitor breathing and consciousness while waiting for help.",
                    detailedInstructions = "Monitor the person's breathing and consciousness. If unconscious, place in recovery position to prevent choking if they vomit. If breathing stops, begin CPR. For skin contact with poison, remove contaminated clothing and rinse skin with water for 15-20 minutes. For eye contact, flush eyes continuously with clean water for at least 20 minutes. Keep the person calm and comfortable. Do not give anything by mouth unless specifically instructed by poison control.",
                    duration = "Until help arrives",
                    tips = listOf("Use recovery position if unconscious", "Rinse skin/eyes if contaminated", "Keep person calm"),
                    warnings = listOf("Don't give food or drink unless instructed", "Don't leave person alone"),
                    requiredTools = listOf("Clean water for rinsing"),
                    stepType = StepType.MONITORING,
                    isCritical = true
                )
            ),
            iconResName = "ic_poison",
            whenToCallEmergency = "All suspected poisonings require immediate poison control or emergency services",
            warnings = listOf("Don't induce vomiting unless instructed", "Don't give activated charcoal without guidance"),
            estimatedTimeMinutes = 5,
            difficulty = "Beginner"
        )
    }

    private fun createShockGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "shock_guide",
            title = "Shock Treatment",
            category = "Life-Threatening",
            severity = "CRITICAL",
            description = "Shock occurs when the circulatory system fails to deliver adequate oxygen to vital organs. Signs include rapid pulse, pale skin, confusion, and weakness. It can be life-threatening if not treated immediately.",
            steps = listOf(
                GuideStep(
                    id = "shock_step_1",
                    guideId = "shock_guide",
                    stepNumber = 1,
                    title = "Call Emergency Services",
                    description = "Call 112 immediately for shock symptoms.",
                    detailedInstructions = "If the person shows signs of shock (pale, cold sweat, rapid pulse, altered consciousness), call emergency services immediately. Recognize shock signs: rapid weak pulse, pale or blue skin, confusion, weakness, nausea, or thirst. Call 112 immediately as shock can be fatal. Describe symptoms clearly to dispatcher.",
                    duration = "1-2 minutes",
                    tips = listOf("Describe all symptoms", "Mention rapid pulse", "Stay on line for guidance"),
                    warnings = listOf("Don't delay calling", "Shock can be fatal quickly"),
                    requiredTools = listOf("Phone"),
                    stepType = StepType.CALL,
                    isCritical = true
                ),
                GuideStep(
                    id = "shock_step_2",
                    guideId = "shock_guide",
                    stepNumber = 2,
                    title = "Position and Warm",
                    description = "Lay person down with legs elevated. Keep warm but don't overheat.",
                    detailedInstructions = "Lay the person flat on their back. If no head/neck injury is suspected, elevate the legs about 12 inches to improve blood flow to the heart and brain. Have person lie down on back. Elevate legs 8-12 inches unless spinal injury suspected. Keep person warm with blankets but don't overheat. Loosen tight clothing around neck and waist.",
                    duration = "2-3 minutes",
                    tips = listOf("Elevate legs unless contraindicated", "Use blankets for warmth", "Loosen restrictive clothing"),
                    warnings = listOf("Don't elevate legs if spinal injury suspected", "Don't overheat"),
                    requiredTools = listOf("Blankets", "Pillows for elevation"),
                    stepType = StepType.ACTION,
                    isCritical = true
                ),
                GuideStep(
                    id = "shock_step_3",
                    guideId = "shock_guide",
                    stepNumber = 3,
                    title = "Monitor and Support",
                    description = "Monitor breathing and consciousness until help arrives.",
                    detailedInstructions = "Control any bleeding and treat the cause (e.g., injury, allergic reaction). Cover them with a blanket to keep warm. Do not let them get chilled or too hot. Do not give food or drink (they may vomit). Monitor breathing and consciousness; if breathing stops, begin CPR. Stay with the person and reassure them.",
                    duration = "Until help arrives",
                    tips = listOf("Keep person warm", "Monitor breathing closely", "Provide reassurance"),
                    warnings = listOf("Don't give food or drink", "Don't leave person alone"),
                    requiredTools = listOf("Blankets"),
                    stepType = StepType.MONITORING,
                    isCritical = true
                )
            ),
            iconResName = "ic_shock",
            whenToCallEmergency = "Signs of shock: rapid pulse, pale skin, confusion, weakness",
            warnings = listOf("Don't give food or water", "Don't leave person alone"),
            estimatedTimeMinutes = 5,
            difficulty = "Intermediate"
        )
    }

    private fun createHeartAttackGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "heart_attack_guide",
            title = "Heart Attack",
            category = "Life-Threatening",
            severity = "CRITICAL",
            description = "Heart attack occurs when blood flow to heart muscle is blocked. Common symptoms include chest pain, shortness of breath, and arm pain. Immediate treatment can save life and limit heart damage.",
            steps = listOf(
                GuideStep(
                    id = "heart_attack_step_1",
                    guideId = "heart_attack_guide",
                    stepNumber = 1,
                    title = "Recognize Symptoms",
                    description = "Look for chest pain, shortness of breath, arm pain, nausea.",
                    detailedInstructions = "Common heart attack symptoms: chest pain or pressure, shortness of breath, pain in arms/jaw/neck, nausea, sweating, dizziness. Symptoms may be different in women and diabetics. Don't ignore mild symptoms. Recognize the signs of a heart attack (chest pain, shortness of breath, etc.) and call for emergency medical help immediately.",
                    duration = "1-2 minutes",
                    tips = listOf("Ask about chest pain", "Check for sweating", "Note arm or jaw pain"),
                    warnings = listOf("Don't dismiss mild symptoms", "Women may have different symptoms"),
                    requiredTools = listOf("None"),
                    stepType = StepType.CHECK,
                    isCritical = true
                ),
                GuideStep(
                    id = "heart_attack_step_2",
                    guideId = "heart_attack_guide",
                    stepNumber = 2,
                    title = "Call Emergency and Give Aspirin",
                    description = "Call 112 and give aspirin if person is conscious and not allergic.",
                    detailedInstructions = "Call 112 immediately. If person is conscious and not allergic to aspirin, give 300mg aspirin to chew (not swallow whole). Help person sit in comfortable position, usually sitting up slightly. Help the person to sit in a comfortable, slightly reclined position and reassure them to keep them calm.",
                    duration = "3-5 minutes",
                    tips = listOf("Give aspirin to chew", "Help person sit comfortably", "Stay calm"),
                    warnings = listOf("Don't give aspirin if allergic", "Don't delay calling emergency"),
                    requiredTools = listOf("Phone", "Aspirin if available"),
                    stepType = StepType.ACTION,
                    isCritical = true
                ),
                GuideStep(
                    id = "heart_attack_step_3",
                    guideId = "heart_attack_guide",
                    stepNumber = 3,
                    title = "Assist with Medication",
                    description = "Help with prescribed nitroglycerin if available.",
                    detailedInstructions = "If the person has been prescribed nitroglycerin, help them take it as directed (usually under the tongue). If they chew aspirin and have no allergy, give one adult dose (300 mg) to chew (this can help).",
                    duration = "2-3 minutes",
                    tips = listOf("Follow medication instructions", "Help with nitroglycerin placement"),
                    warnings = listOf("Only give prescribed medications", "Don't exceed recommended doses"),
                    requiredTools = listOf("Prescribed nitroglycerin"),
                    stepType = StepType.ACTION
                ),
                GuideStep(
                    id = "heart_attack_step_4",
                    guideId = "heart_attack_guide",
                    stepNumber = 4,
                    title = "Monitor and Prepare for CPR",
                    description = "Monitor breathing and be ready to perform CPR if needed.",
                    detailedInstructions = "Loosen tight clothing and monitor breathing. Loosen any tight clothing and monitor the person's breathing. Be prepared to perform CPR if they become unresponsive. Do not allow them to exert themselves. Do not give them food or drink (in case surgery is needed).",
                    duration = "Until help arrives",
                    tips = listOf("Keep person calm", "Monitor breathing closely", "Be ready for CPR"),
                    warnings = listOf("Don't let person exert themselves", "Don't give food or drink"),
                    requiredTools = listOf("None"),
                    stepType = StepType.MONITORING,
                    isCritical = true
                )
            ),
            iconResName = "ic_heart_attack",
            whenToCallEmergency = "Any suspected heart attack symptoms",
            warnings = listOf("Don't drive person to hospital yourself", "Time is critical - call 112"),
            estimatedTimeMinutes = 10,
            difficulty = "Intermediate"
        )
    }

    private fun createStrokeGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "stroke_guide",
            title = "Stroke Recognition & Response",
            category = "Life-Threatening",
            severity = "CRITICAL",
            description = "Stroke occurs when blood supply to brain is interrupted. Use FAST test: Face drooping, Arm weakness, Speech difficulty, Time to call emergency. Quick treatment can prevent permanent brain damage.",
            steps = listOf(
                GuideStep(
                    id = "stroke_step_1",
                    guideId = "stroke_guide",
                    stepNumber = 1,
                    title = "Perform FAST Test",
                    description = "Check Face, Arms, Speech, and note Time of onset.",
                    detailedInstructions = "Recognize stroke signs with FAST: Face droop, Arm weakness, Speech difficulty. FAST test: Face - ask person to smile, look for drooping. Arms - ask to raise both arms, check if one drifts down. Speech - ask to repeat simple phrase, listen for slurred speech. Time - note when symptoms started. If any test fails, suspect stroke.",
                    duration = "2-3 minutes",
                    tips = listOf("Ask person to smile", "Test both arms", "Listen to speech clearly"),
                    warnings = listOf("Don't dismiss mild symptoms", "Time is critical for treatment"),
                    requiredTools = listOf("None"),
                    stepType = StepType.CHECK,
                    isCritical = true
                ),
                GuideStep(
                    id = "stroke_step_2",
                    guideId = "stroke_guide",
                    stepNumber = 2,
                    title = "Call Emergency Immediately",
                    description = "Call 112 and note exact time symptoms started.",
                    detailedInstructions = "If you see any sign of a stroke, call for emergency medical help immediately. Time is critical. Call 112 immediately if FAST test positive. Tell dispatcher you suspect stroke and give exact time symptoms started. Keep person calm and lying down with head slightly elevated.",
                    duration = "2-3 minutes",
                    tips = listOf("Note exact time of symptom onset", "Keep person calm", "Elevate head slightly"),
                    warnings = listOf("Don't give food or water", "Don't give medications"),
                    requiredTools = listOf("Phone"),
                    stepType = StepType.CALL,
                    isCritical = true
                ),
                GuideStep(
                    id = "stroke_step_3",
                    guideId = "stroke_guide",
                    stepNumber = 3,
                    title = "Keep Person Comfortable",
                    description = "Position safely and monitor until help arrives.",
                    detailedInstructions = "Keep the person calm and still. Lay them down with their head and shoulders slightly elevated. Cover them with a blanket to keep them warm. Do not give the person any food, drink, or medication by mouth. Loosen tight clothing.",
                    duration = "Until help arrives",
                    tips = listOf("Slightly elevate head and shoulders", "Keep warm", "Loosen clothing"),
                    warnings = listOf("Don't give anything by mouth", "Don't let them sleep"),
                    requiredTools = listOf("Blanket", "Pillow"),
                    stepType = StepType.MONITORING,
                    isCritical = true
                ),
                GuideStep(
                    id = "stroke_step_4",
                    guideId = "stroke_guide",
                    stepNumber = 4,
                    title = "Monitor and Document",
                    description = "Track symptoms and note time for medical team.",
                    detailedInstructions = "If you know when the symptoms started, provide this information to the emergency responders. This is very important for treatment decisions. Monitor for changes in consciousness, breathing, or symptoms. Be ready to perform CPR if breathing stops.",
                    duration = "Until help arrives",
                    tips = listOf("Note exact time symptoms began", "Monitor breathing", "Track any changes"),
                    warnings = listOf("Don't move unnecessarily", "Don't give aspirin for stroke"),
                    requiredTools = listOf("Paper to note times"),
                    stepType = StepType.MONITORING
                )
            ),
            iconResName = "ic_stroke",
            whenToCallEmergency = "Any positive FAST test result",
            warnings = listOf("Time is brain - call immediately", "Don't give aspirin for stroke"),
            estimatedTimeMinutes = 5,
            difficulty = "Beginner"
        )
    }

    private fun createAllergicReactionGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "allergic_reaction_guide",
            title = "Allergic Reactions",
            category = "Respiratory",
            severity = "HIGH",
            description = "Allergic reactions range from mild skin reactions to life-threatening anaphylaxis. Severe reactions can cause breathing difficulty, swelling, and shock. Epinephrine auto-injector is first-line treatment for severe reactions.",
            steps = listOf(
                GuideStep(
                    id = "allergic_step_1",
                    guideId = "allergic_reaction_guide",
                    stepNumber = 1,
                    title = "Assess Severity",
                    description = "Determine if reaction is mild or severe (anaphylaxis).",
                    detailedInstructions = "If a known allergic person is showing signs of anaphylaxis (hives, swelling of face/tongue, difficulty breathing), act immediately. Mild: skin rash, itching, mild swelling. Severe (anaphylaxis): difficulty breathing, swelling of face/throat, rapid pulse, dizziness, full-body rash. Severe reactions are life-threatening emergencies.",
                    duration = "1-2 minutes",
                    tips = listOf("Check breathing difficulty", "Look for facial swelling", "Note skin changes"),
                    warnings = listOf("Anaphylaxis can be fatal quickly", "Don't underestimate severity"),
                    requiredTools = listOf("None"),
                    stepType = StepType.CHECK,
                    isCritical = true
                ),
                GuideStep(
                    id = "allergic_step_2",
                    guideId = "allergic_reaction_guide",
                    stepNumber = 2,
                    title = "Use Epinephrine if Available",
                    description = "For severe reactions, use epinephrine auto-injector if available.",
                    detailedInstructions = "If person has epinephrine auto-injector and shows severe symptoms, help them use it or use it for them. Inject epinephrine immediately (e.g. use their EpiPen auto-injector in the outer thigh as instructed). Inject into outer thigh muscle. Call 112 immediately after using epinephrine. Keep person lying down.",
                    duration = "2-3 minutes",
                    tips = listOf("Inject into outer thigh", "Hold for 10 seconds", "Call 112 after injection"),
                    warnings = listOf("Always call emergency after epinephrine use", "Don't hesitate to use if severe symptoms"),
                    requiredTools = listOf("Epinephrine auto-injector"),
                    stepType = StepType.ACTION,
                    isCritical = true
                ),
                GuideStep(
                    id = "allergic_step_3",
                    guideId = "allergic_reaction_guide",
                    stepNumber = 3,
                    title = "Position and Monitor",
                    description = "Position person appropriately and monitor for second reaction.",
                    detailedInstructions = "Have them lie down with legs elevated (unless breathing is difficult, then keep upright). Be ready to manage airway (if throat swelling, anaphylaxis can block airway). Have CPR ready in case of collapse. Administer a second dose of epinephrine after 5-10 minutes if symptoms worsen and no improvement.",
                    duration = "Until help arrives",
                    tips = listOf("Elevate legs unless breathing difficulty", "Be ready for second dose", "Monitor airway"),
                    warnings = listOf("Be prepared for CPR", "Reactions can be biphasic"),
                    requiredTools = listOf("Second epinephrine if available"),
                    stepType = StepType.MONITORING,
                    isCritical = true
                )
            ),
            iconResName = "ic_allergy",
            whenToCallEmergency = "Severe allergic reaction with breathing difficulty, facial swelling, or shock symptoms",
            warnings = listOf("Always call emergency after epinephrine use", "Reactions can worsen quickly"),
            estimatedTimeMinutes = 5,
            difficulty = "Intermediate"
        )
    }

    private fun createSprainsStrainsGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "sprains_strains_guide",
            title = "Sprains & Strains",
            category = "Trauma",
            severity = "LOW",
            description = "Sprains affect ligaments, strains affect muscles/tendons. Use RICE protocol: Rest, Ice, Compression, Elevation. Most heal with proper care, but severe injuries may need medical evaluation.",
            steps = listOf(
                GuideStep(
                    id = "sprains_step_1",
                    guideId = "sprains_strains_guide",
                    stepNumber = 1,
                    title = "Rest the Injury",
                    description = "Stop activity and rest the injured area.",
                    detailedInstructions = "Have the person stop using the injured joint and rest. Avoid putting weight on the injured area. Stop activity immediately. Avoid putting weight on injured area. Use crutches or sling if needed. Don't continue activity through pain as this can worsen injury.",
                    duration = "Immediate",
                    tips = listOf("Stop activity immediately", "Use support devices if needed"),
                    warnings = listOf("Don't continue activity through pain"),
                    requiredTools = listOf("Crutches or sling if available"),
                    stepType = StepType.ACTION
                ),
                GuideStep(
                    id = "sprains_step_2",
                    guideId = "sprains_strains_guide",
                    stepNumber = 2,
                    title = "Apply Ice",
                    description = "Apply ice to reduce swelling and pain.",
                    detailedInstructions = "Apply an ice pack wrapped in a cloth to the area for up to 10-20 minutes at a time, several times a day. Apply ice wrapped in towel for 15-20 minutes every 2-3 hours for first 48 hours. Do not apply ice directly to skin.",
                    duration = "20 minutes ice, every 2-3 hours",
                    tips = listOf("Wrap ice in towel", "Apply for 15-20 minutes", "Repeat every 2-3 hours"),
                    warnings = listOf("Don't apply ice directly to skin", "Don't apply for more than 20 minutes"),
                    requiredTools = listOf("Ice", "Towel"),
                    stepType = StepType.ACTION
                ),
                GuideStep(
                    id = "sprains_step_3",
                    guideId = "sprains_strains_guide",
                    stepNumber = 3,
                    title = "Apply Compression",
                    description = "Wrap with elastic bandage to reduce swelling.",
                    detailedInstructions = "Compress the injury with an elastic bandage, but not so tightly that it cuts off circulation. Apply elastic bandage for compression, snug but not so tight it cuts off circulation. Check that fingers or toes beyond bandage remain pink and warm.",
                    duration = "Ongoing during healing",
                    tips = listOf("Check circulation beyond bandage", "Wrap snugly but not too tight"),
                    warnings = listOf("Don't wrap too tightly", "Loosen if tingling or numbness"),
                    requiredTools = listOf("Elastic bandage"),
                    stepType = StepType.ACTION
                ),
                GuideStep(
                    id = "sprains_step_4",
                    guideId = "sprains_strains_guide",
                    stepNumber = 4,
                    title = "Elevate the Injured Area",
                    description = "Raise the injured limb above heart level when possible.",
                    detailedInstructions = "Elevate the injured limb above the level of the heart to help reduce swelling. Elevate injured area above heart level when possible. Use pillows or props to maintain elevation, especially when resting.",
                    duration = "As much as possible",
                    tips = listOf("Use pillows for support", "Maintain elevation when resting"),
                    warnings = listOf("Only if no fracture suspected"),
                    requiredTools = listOf("Pillows for support"),
                    stepType = StepType.ACTION
                )
            ),
            iconResName = "ic_sprain",
            whenToCallEmergency = "Severe deformity, complete inability to move, or signs of fracture",
            warnings = listOf("Don't ignore severe pain", "Seek medical care if no improvement in 48 hours"),
            estimatedTimeMinutes = 15,
            difficulty = "Beginner"
        )
    }

    private fun createHypothermiaGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "hypothermia_guide",
            title = "Hypothermia Treatment",
            category = "Environmental",
            severity = "HIGH",
            description = "Hypothermia occurs when body temperature drops below normal. Early signs include shivering and confusion. Severe hypothermia can cause unconsciousness and death. Gradual rewarming is key.",
            steps = listOf(
                GuideStep(
                    id = "hypothermia_step_1",
                    guideId = "hypothermia_guide",
                    stepNumber = 1,
                    title = "Move to Warm Environment",
                    description = "Get person out of cold environment gently.",
                    detailedInstructions = "Move the person out of the cold and into a warm, dry place. Gently move person to warm, dry shelter. Handle very gently as heart may be sensitive to sudden movements in severe hypothermia. Remove wet clothing carefully.",
                    duration = "5-10 minutes",
                    tips = listOf("Move gently", "Get to dry shelter", "Remove wet clothes"),
                    warnings = listOf("Handle very gently", "Don't use direct heat initially"),
                    requiredTools = listOf("Warm shelter", "Dry clothing"),
                    stepType = StepType.ACTION,
                    isCritical = true
                ),
                GuideStep(
                    id = "hypothermia_step_2",
                    guideId = "hypothermia_guide",
                    stepNumber = 2,
                    title = "Remove Wet Clothing",
                    description = "Replace wet clothing with dry, warm layers.",
                    detailedInstructions = "Gently remove any wet clothing and replace it with dry, warm layers or blankets. Cover their head. Be gentle - hypothermic people can go into cardiac arrest if moved roughly.",
                    duration = "5-10 minutes",
                    tips = listOf("Replace with dry clothes", "Cover the head", "Be very gentle"),
                    warnings = listOf("Don't move roughly", "Avoid sudden movements"),
                    requiredTools = listOf("Dry clothing", "Blankets"),
                    stepType = StepType.ACTION
                ),
                GuideStep(
                    id = "hypothermia_step_3",
                    guideId = "hypothermia_guide",
                    stepNumber = 3,
                    title = "Gradual Rewarming",
                    description = "Warm gradually using blankets and warm compresses.",
                    detailedInstructions = "Warm the center of the body first (chest, neck, head, and groin) using warm (not hot) compresses. Wrap in dry blankets. If conscious and able to swallow, give warm non-alcoholic, non-caffeinated drinks. Apply warm packs to chest, neck, and groin - not directly to skin. Do not rewarm too rapidly (no hot baths or heating pads).",
                    duration = "Ongoing until normal temperature",
                    tips = listOf("Use dry blankets", "Give warm drinks if conscious", "Apply heat to core areas"),
                    warnings = listOf("Don't use direct heat", "Don't give alcohol", "Don't rub or massage"),
                    requiredTools = listOf("Blankets", "Warm drinks", "Heating pads"),
                    stepType = StepType.ACTION
                ),
                GuideStep(
                    id = "hypothermia_step_4",
                    guideId = "hypothermia_guide",
                    stepNumber = 4,
                    title = "Monitor and Call for Help",
                    description = "Monitor condition and call emergency services if severe.",
                    detailedInstructions = "Call emergency services if core temperature is very low, or if the person is drowsy, confused, or not shivering (signs of moderate/severe hypothermia). Monitor breathing and be prepared for CPR if needed.",
                    duration = "Until help arrives",
                    tips = listOf("Watch for confusion", "Monitor breathing", "Note shivering status"),
                    warnings = listOf("Don't ignore mental changes", "Be ready for CPR"),
                    requiredTools = listOf("Phone"),
                    stepType = StepType.MONITORING,
                    isCritical = true
                )
            ),
            iconResName = "ic_hypothermia",
            whenToCallEmergency = "Severe hypothermia with altered mental status or unconsciousness",
            warnings = listOf("Don't rewarm too quickly", "Don't rub frostbitten areas"),
            estimatedTimeMinutes = 30,
            difficulty = "Intermediate"
        )
    }

    private fun createHeatExhaustionGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "heat_exhaustion_guide",
            title = "Heat Exhaustion & Heat Stroke",
            category = "Environmental",
            severity = "MEDIUM",
            description = "Heat exhaustion can progress to life-threatening heat stroke. Signs include heavy sweating, nausea, dizziness. Heat stroke involves high temperature and altered mental status.",
            steps = listOf(
                GuideStep(
                    id = "heat_step_1",
                    guideId = "heat_exhaustion_guide",
                    stepNumber = 1,
                    title = "Move to Cool Area",
                    description = "Get person to cool, shaded area immediately.",
                    detailedInstructions = "Move the person to a cool, shaded area or an air-conditioned room. Move person to cool, shaded, or air-conditioned area. Have them lie down with legs elevated. Remove excess clothing. Fan the person or use air conditioning.",
                    duration = "5-10 minutes",
                    tips = listOf("Find air conditioning if possible", "Elevate legs", "Remove excess clothing"),
                    warnings = listOf("Don't delay cooling", "Heat stroke can be fatal"),
                    requiredTools = listOf("Cool area", "Fan if available"),
                    stepType = StepType.ACTION,
                    isCritical = true
                ),
                GuideStep(
                    id = "heat_step_2",
                    guideId = "heat_exhaustion_guide",
                    stepNumber = 2,
                    title = "Cool the Skin",
                    description = "Apply cool water and remove excess clothing.",
                    detailedInstructions = "Remove any excess clothing. Cool the person's skin by fanning them or applying cool, wet cloths. Apply cool, wet cloths to skin or spray with cool water. If person is conscious and able to drink, give cool water or sports drinks. Monitor for improvement.",
                    duration = "15-30 minutes",
                    tips = listOf("Use cool, wet cloths", "Fan for air circulation", "Remove tight clothing"),
                    warnings = listOf("Don't use ice water", "Don't force fluids if nauseous"),
                    requiredTools = listOf("Cool water", "Cloths", "Fan"),
                    stepType = StepType.ACTION
                ),
                GuideStep(
                    id = "heat_step_3",
                    guideId = "heat_exhaustion_guide",
                    stepNumber = 3,
                    title = "Provide Fluids",
                    description = "Give cool water or electrolyte drinks if conscious.",
                    detailedInstructions = "If the person is conscious, give them sips of cool water or an electrolyte drink. Give cool fluids if conscious. Monitor improvement closely and watch for signs of heat stroke (confusion, high fever).",
                    duration = "Ongoing",
                    tips = listOf("Give small sips", "Use electrolyte drinks", "Monitor improvement"),
                    warnings = listOf("Don't give fluids if unconscious", "Don't give alcohol or caffeine"),
                    requiredTools = listOf("Cool water", "Electrolyte drinks"),
                    stepType = StepType.ACTION
                ),
                GuideStep(
                    id = "heat_step_4",
                    guideId = "heat_exhaustion_guide",
                    stepNumber = 4,
                    title = "Monitor and Seek Help",
                    description = "Monitor for improvement or signs of heat stroke.",
                    detailedInstructions = "Have the person lie down and elevate their feet slightly. If the person's symptoms do not improve or if they show signs of heatstroke (e.g., confusion, high fever), call for emergency medical help. Call emergency if no improvement with cooling.",
                    duration = "Until improvement or help arrives",
                    tips = listOf("Elevate feet", "Watch for confusion", "Monitor temperature"),
                    warnings = listOf("Heat stroke is life-threatening", "Don't ignore worsening symptoms"),
                    requiredTools = listOf("Phone if emergency needed"),
                    stepType = StepType.MONITORING,
                    isCritical = true
                )
            ),
            iconResName = "ic_heat",
            whenToCallEmergency = "High temperature with altered mental status, unconsciousness, or no improvement with cooling",
            warnings = listOf("Heat stroke is life-threatening", "Don't give fluids to unconscious person"),
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
            description = "Seizures involve uncontrolled electrical activity in the brain. Most seizures stop on their own within 2-3 minutes. The key is protecting the person from injury and providing proper post-seizure care.",
            steps = listOf(
                GuideStep(
                    id = "seizure_step_1",
                    guideId = "seizures_guide",
                    stepNumber = 1,
                    title = "Immediate Environmental Safety",
                    description = "Create a safe space and protect person from injury during seizure.",
                    detailedInstructions = """
                        1. Stay calm and remain with the person throughout the seizure
                        2. Note the time when seizure begins (crucial for emergency decisions)
                        3. Immediately clear the surrounding area:
                           • Move away sharp objects (knives, glass, tools)
                           • Remove hard furniture (chairs, tables, corners)
                           • Clear floor of obstacles they could hit
                           • Push back bystanders to give space
                        4. Protect the head:
                           • Gently place something soft under their head:
                             - Folded jacket, sweater, or towel
                             - Small pillow or cushion
                             - Your hands/arms if nothing else available
                           • Don't force head positioning - just cushion impact
                        5. Loosen tight clothing around neck and waist:
                           • Undo collar buttons, ties, belts
                           • Remove glasses if they're on
                           • Ensure nothing is restricting breathing
                        6. Do NOT attempt to:
                           • Hold down or restrain their movements
                           • Put anything in their mouth (tongue cannot be swallowed)
                           • Give food, water, or medication
                           • Try to wake them up or shout at them
                        7. Observe and document:
                           • What body parts are jerking
                           • If eyes are open or closed
                           • Any sounds they make
                           • Duration of different phases
                    """.trimIndent(),
                    duration = "During active seizure (typically 1-3 minutes)",
                    tips = listOf("Start timing immediately", "Move objects away, not the person", "Cushion head gently", "Stay calm and observe"),
                    warnings = listOf("Never restrain movements", "Never put objects in mouth", "Don't move person unless in immediate danger"),
                    requiredTools = listOf("Soft object for head cushion", "Watch or phone to time seizure"),
                    stepType = StepType.SAFETY,
                    isCritical = true
                ),
                GuideStep(
                    id = "seizure_step_2",
                    guideId = "seizures_guide",
                    stepNumber = 2,
                    title = "Post-Seizure Recovery Positioning",
                    description = "Position person safely after seizure stops and ensure airway is clear.",
                    detailedInstructions = """
                        1. Wait for seizure to completely stop before moving person
                        2. Check responsiveness:
                           • Gently call their name
                           • Tap shoulders lightly
                           • Note their level of consciousness
                        3. Place in recovery position if unconscious:
                           • Roll person onto their side (recovery position)
                           • Tilt head back slightly to open airway
                           • Ensure tongue doesn't block breathing
                           • Top leg bent to stabilize position
                        4. Check for breathing:
                           • Look for chest rising and falling
                           • Listen for breathing sounds
                           • Feel for breath on your cheek
                           • If not breathing normally, call 911 immediately
                        5. Check for injuries sustained during seizure:
                           • Head injuries from falling
                           • Cuts or bruises from objects
                           • Bitten tongue or cheek
                           • Dislocated joints (rare but possible)
                        6. Keep airway clear:
                           • Wipe away any saliva or vomit from mouth
                           • Don't stick fingers deep in mouth
                           • Turn head to side if vomiting occurs
                        7. Monitor vital signs:
                           • Check pulse at wrist or neck
                           • Monitor breathing rate and quality
                           • Note skin color (blue lips indicate oxygen problems)
                    """.trimIndent(),
                    duration = "First 5-10 minutes after seizure stops",
                    tips = listOf("Recovery position is key", "Keep airway clear", "Check for injuries", "Monitor breathing carefully"),
                    warnings = listOf("Don't leave person alone", "Don't give anything by mouth until fully alert", "Call 911 if breathing problems"),
                    requiredTools = listOf("Clean cloth for wiping mouth", "Phone for emergency if needed"),
                    stepType = StepType.ACTION,
                    isCritical = true
                ),
                GuideStep(
                    id = "seizure_step_3",
                    guideId = "seizures_guide",
                    stepNumber = 3,
                    title = "Post-Seizure Care and Reassurance",
                    description = "Provide comfort and orientation as person regains consciousness.",
                    detailedInstructions = """
                        1. Provide reassurance and comfort:
                           • Speak in calm, gentle voice
                           • Explain what happened: "You had a seizure, but you're safe now"
                           • Stay close and provide emotional support
                           • Don't overwhelm with questions initially
                        2. Allow natural recovery process:
                           • Person may be confused or disoriented (normal)
                           • Memory of seizure events may be missing
                           • May feel tired, embarrassed, or scared
                           • Recovery time varies from minutes to hours
                        3. Gradual reorientation:
                           • Ask simple questions: "What's your name?" "Do you know where you are?"
                           • Help them recognize familiar surroundings
                           • Remind them of date and time if confused
                           • Don't rush the process
                        4. Physical comfort measures:
                           • Help them sit up slowly when ready
                           • Offer to help clean up if they lost bladder control
                           • Provide tissues for saliva
                           • Help fix disheveled clothing
                        5. Assess for additional needs:
                           • Ask about any pain or discomfort
                           • Check if they need their regular seizure medication
                           • Determine if they need medical evaluation
                           • Contact family/friends if appropriate
                        6. Do not allow them to:
                           • Drive until fully recovered (at least 24 hours)
                           • Return to dangerous activities (swimming, heights, machinery)
                           • Be alone until completely alert
                           • Make important decisions while confused
                    """.trimIndent(),
                    duration = "15-60 minutes post-seizure",
                    tips = listOf("Be patient with confusion", "Provide gentle reassurance", "Don't rush recovery", "Stay with them until alert"),
                    warnings = listOf("No driving for 24 hours minimum", "No dangerous activities", "Don't leave alone until fully recovered"),
                    requiredTools = listOf("Tissues", "Clean clothes if needed", "Contact information for family"),
                    stepType = StepType.ACTION
                ),
                GuideStep(
                    id = "seizure_step_4",
                    guideId = "seizures_guide",
                    stepNumber = 4,
                    title = "Ongoing Monitoring and Documentation",
                    description = "Continue monitoring and document seizure details for medical follow-up.",
                    detailedInstructions = """
                        1. Continue monitoring for complications:
                           • Watch for signs of another seizure
                           • Monitor for changes in consciousness level
                           • Check for delayed injuries becoming apparent
                           • Observe for signs of infection if cuts occurred
                        2. Document seizure details for medical team:
                           • Exact time seizure started and stopped
                           • Duration of different phases
                           • Body parts affected (which limbs jerked)
                           • Eye movements or position
                           • Breathing changes during seizure
                           • Any injuries sustained
                           • How long confusion lasted after
                           • Any triggers you noticed (stress, flashing lights, missed medication)
                        3. Medication management (if known epileptic):
                           • Help them take prescribed post-seizure medication if ordered
                           • Check if they missed their regular seizure medication
                           • Don't give medication unless specifically prescribed for seizures
                        4. When to seek immediate medical care:
                           • First-time seizure (always needs evaluation)
                           • Seizure lasted longer than 5 minutes
                           • Person was injured during seizure
                           • Multiple seizures without full recovery between
                           • Difficulty breathing after seizure
                           • Person doesn't return to normal consciousness
                           • Signs of head injury
                           • Pregnancy
                        5. Follow-up care planning:
                           • Arrange safe transportation home (no driving)
                           • Ensure someone stays with them for next few hours
                           • Schedule follow-up with their doctor
                           • Review seizure action plan if they have one
                    """.trimIndent(),
                    duration = "Several hours post-seizure",
                    tips = listOf("Document everything", "Watch for complications", "Ensure safe transportation", "Plan follow-up care"),
                    warnings = listOf("First-time seizures need medical evaluation", "Don't ignore head injuries", "No driving until cleared by doctor"),
                    requiredTools = listOf("Pen and paper for documentation", "Phone for medical contacts"),
                    stepType = StepType.MONITORING,
                    isCritical = true
                )
            ),
            iconResName = "ic_seizure",
            whenToCallEmergency = "Seizure lasts longer than 5 minutes, first-time seizure, injury during seizure, difficulty breathing after seizure, or person doesn't regain normal consciousness",
            warnings = listOf("Never put objects in mouth during seizure", "Don't restrain movements", "Don't leave person alone during recovery", "First-time seizures always need medical evaluation"),
            estimatedTimeMinutes = 30,
            difficulty = "Intermediate"
        )
    }

    private fun createBitesStingsGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "bites_stings_guide",
            title = "Bites & Stings",
            category = "Trauma",
            severity = "LOW",
            description = "Animal bites and insect stings can range from minor irritations to life-threatening emergencies. Animal bites carry high infection risk, while insect stings can cause severe allergic reactions (anaphylaxis). Proper wound care and monitoring for systemic reactions are essential.",
            steps = listOf(
                GuideStep(
                    id = "bites_step_1",
                    guideId = "bites_stings_guide",
                    stepNumber = 1,
                    title = "Immediate Assessment and Safety",
                    description = "Assess the type of bite/sting and ensure scene safety.",
                    detailedInstructions = """
                        1. Ensure scene safety:
                           • Remove person from danger (aggressive animal, bee swarm)
                           • Identify the animal or insect if possible (for medical treatment)
                           • Look for multiple stings or bites
                        2. Assess the wound:
                           • Animal bite: Check depth, location, and bleeding
                           • Insect sting: Look for embedded stinger
                           • Snake bite: Look for fang marks, swelling, discoloration
                           • Spider bite: Note any distinctive markings
                        3. Check for immediate allergic reaction signs:
                           • Difficulty breathing or wheezing
                           • Swelling of face, lips, or throat
                           • Widespread rash or hives
                           • Rapid pulse or dizziness
                           • Nausea or vomiting
                        4. Priority assessment:
                           • Life-threatening allergic reaction = Call 911 immediately
                           • Deep animal bite = Needs medical attention
                           • Venomous bite/sting = Call poison control and 911
                           • Minor insect sting = Local treatment
                    """.trimIndent(),
                    duration = "2-5 minutes assessment",
                    tips = listOf("Identify the animal/insect if safe to do so", "Take photos if possible for medical identification", "Check for multiple bites/stings", "Watch for systemic reactions"),
                    warnings = listOf("Don't pursue or handle the animal", "Call 911 immediately for severe allergic reactions", "Don't ignore snake or spider bites"),
                    requiredTools = listOf("Phone for emergency calls", "Camera for identification if safe"),
                    stepType = StepType.ASSESSMENT,
                    isCritical = true
                ),
                GuideStep(
                    id = "bites_step_2",
                    guideId = "bites_stings_guide",
                    stepNumber = 2,
                    title = "Stinger Removal and Initial Wound Care",
                    description = "Remove stingers properly and begin wound cleaning.",
                    detailedInstructions = """
                        1. For insect stings with visible stinger:
                           • Remove stinger immediately to prevent more venom injection
                           • Use credit card, fingernail, or knife edge to scrape stinger out
                           • Scrape sideways across the stinger, don't pinch or pull
                           • Don't use tweezers - this can squeeze more venom into wound
                        2. Initial bleeding control (animal bites):
                           • Apply direct pressure with clean cloth
                           • Don't clean deep puncture wounds initially - control bleeding first
                           • Elevate wounded area if possible
                        3. For snake bites:
                           • Keep person calm and still
                           • Remove jewelry near bite before swelling starts
                           • Don't cut the wound or try to suck out venom
                           • Don't apply ice or tourniquet
                           • Keep bitten limb below heart level
                        4. Document the incident:
                           • Note time of bite/sting
                           • Description of animal/insect
                           • Location on body
                           • Initial symptoms
                    """.trimIndent(),
                    duration = "5-10 minutes depending on wound type",
                    tips = listOf("Scrape stingers, don't pinch", "Control bleeding before cleaning", "Keep snake bite victims calm and still", "Remove jewelry before swelling"),
                    warnings = listOf("Don't use tweezers on stingers", "Don't cut or suck snake bite wounds", "Don't apply ice to snake bites"),
                    requiredTools = listOf("Credit card or similar for scraping", "Clean cloths", "Pen and paper for documentation"),
                    stepType = StepType.ACTION,
                    isCritical = true
                ),
                GuideStep(
                    id = "bites_step_3",
                    guideId = "bites_stings_guide",
                    stepNumber = 3,
                    title = "Thorough Wound Cleaning and Treatment",
                    description = "Clean wounds properly and apply appropriate treatments.",
                    detailedInstructions = """
                        1. Wound cleaning for animal bites:
                           • Rinse with clean water to remove debris
                           • Wash gently with soap and water for 5 minutes minimum
                           • Pat dry with clean cloth
                           • Apply antibiotic ointment if available
                           • Cover with sterile bandage
                        2. Insect sting treatment:
                           • Wash area with soap and water
                           • Apply cold compress for 10-15 minutes
                           • Consider antihistamine for local reaction
                           • Apply topical anti-itch cream if available
                        3. Pressure point control for serious bleeding:
                           • If bleeding persists, apply pressure to arterial pressure points
                           • Maintain direct pressure on wound simultaneously
                           • Don't remove initial blood-soaked dressings
                        4. Special considerations:
                           • Human bites: Need medical attention due to bacteria in mouth
                           • Cat bites: High infection risk, seek medical care
                           • Tick bites: Remove tick properly, save for identification
                           • Marine stings: Rinse with vinegar if available, then hot water
                    """.trimIndent(),
                    duration = "10-15 minutes for proper cleaning",
                    tips = listOf("Wash animal bites for at least 5 minutes", "Use cold compress for stings", "Save ticks for medical identification", "Human and cat bites need medical attention"),
                    warnings = listOf("Don't ignore human or cat bites", "Don't use hot water on jellyfish stings without vinegar first", "Don't break tick body during removal"),
                    requiredTools = listOf("Soap and clean water", "Clean cloths", "Antibiotic ointment", "Sterile bandages", "Ice pack", "Tweezers for tick removal"),
                    stepType = StepType.ACTION
                ),
                GuideStep(
                    id = "bites_step_4",
                    guideId = "bites_stings_guide",
                    stepNumber = 4,
                    title = "Ongoing Monitoring and Follow-up Care",
                    description = "Monitor for complications and plan appropriate follow-up care.",
                    detailedInstructions = """
                        1. Monitor for allergic reactions (up to several hours):
                           • Breathing difficulties or wheezing
                           • Swelling spreading from bite/sting site
                           • Generalized rash or hives
                           • Rapid pulse, dizziness, or fainting
                           • Severe nausea or vomiting
                           • Feeling of impending doom
                        2. Watch for infection signs (24-72 hours):
                           • Increasing redness around wound
                           • Red streaking extending from wound
                           • Increasing warmth and swelling
                           • Pus or unusual discharge
                           • Fever or chills
                           • Increased pain after initial improvement
                        3. Pain and symptom management:
                           • Apply cold compress 15 minutes on, 15 minutes off
                           • Elevate affected area to reduce swelling
                           • Take over-the-counter pain medication as directed
                           • Use antihistamine for itching
                           • Keep wound clean and dry
                        4. Follow-up medical care needed for:
                           • Any animal bite that breaks skin
                           • Bites from unknown or wild animals (rabies risk)
                           • Snake or spider bites
                           • Signs of infection
                           • Lack of tetanus vaccination in last 5-10 years
                           • Any concerns about wound healing
                        5. Emergency situations requiring immediate 911 call:
                           • Severe allergic reaction (anaphylaxis)
                           • Venomous snake or spider bite
                           • Severe bleeding that won't stop
                           • Signs of systemic illness
                    """.trimIndent(),
                    duration = "Ongoing monitoring for 24-72 hours",
                    tips = listOf("Watch for delayed allergic reactions", "Monitor infection signs for 72 hours", "Keep wound elevated when possible", "Document symptoms and changes"),
                    warnings = listOf("Seek immediate care for any systemic symptoms", "Don't ignore signs of infection", "Rabies risk with wild animal bites requires immediate medical attention"),
                    requiredTools = listOf("Ice packs", "Pain medication", "Antihistamine", "Thermometer to check for fever"),
                    stepType = StepType.MONITORING,
                    isCritical = true
                )
            ),
            iconResName = "ic_bite",
            whenToCallEmergency = "Severe allergic reaction, venomous bite/sting, uncontrolled bleeding, signs of systemic illness, or bite from unknown/wild animal",
            warnings = listOf("Watch for delayed allergic reactions up to several hours", "All animal bites need medical evaluation", "Don't ignore infection signs", "Rabies risk with wild animal bites"),
            estimatedTimeMinutes = 25,
            difficulty = "Beginner"
        )
    }

    private fun createAsthmaAttackGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "asthma_attack_guide",
            title = "Asthma Attack",
            category = "Respiratory",
            severity = "HIGH",
            description = "Asthma attacks involve airway inflammation and narrowing, making breathing difficult. Quick-relief inhalers (bronchodilators) are the primary treatment. Severe attacks can be life-threatening and require immediate emergency care.",
            steps = listOf(
                GuideStep(
                    id = "asthma_step_1",
                    guideId = "asthma_attack_guide",
                    stepNumber = 1,
                    title = "Immediate Assessment and Positioning",
                    description = "Assess severity and position person for optimal breathing.",
                    detailedInstructions = """
                        1. Assess attack severity immediately:
                           • Mild: Can speak in full sentences, slight wheezing
                           • Moderate: Speaks in phrases, visible breathing effort
                           • Severe: Can only speak words, severe wheezing, blue lips
                           • Life-threatening: Cannot speak, little/no wheezing, cyanosis
                        2. Position for optimal breathing:
                           • Help person sit upright, leaning slightly forward
                           • Support with pillows or have them lean on table
                           • Loosen tight clothing around neck and chest
                           • Never lay person flat during attack
                        3. Stay calm and reassure:
                           • Speak in calm, steady voice
                           • "Help is here, we'll get through this"
                           • Your calmness helps reduce their panic
                           • Panic worsens breathing difficulties
                        4. Remove triggers if identified:
                           • Move away from allergens (pets, dust, pollen)
                           • Remove from irritants (smoke, strong odors)
                           • Ensure good air circulation
                        5. Call 911 immediately if:
                           • Person cannot speak in phrases
                           • Blue lips or fingernails
                           • Severe distress or panic
                           • No inhaler available
                    """.trimIndent(),
                    duration = "2-3 minutes assessment and positioning",
                    tips = listOf("Upright position is crucial", "Stay calm to help them stay calm", "Remove triggers if possible", "Support with pillows"),
                    warnings = listOf("Never lay person flat", "Don't leave alone during severe attack", "Call 911 for severe symptoms immediately"),
                    requiredTools = listOf("Pillows for support", "Phone for emergency if needed"),
                    stepType = StepType.ASSESSMENT,
                    isCritical = true
                ),
                GuideStep(
                    id = "asthma_step_2",
                    guideId = "asthma_attack_guide",
                    stepNumber = 2,
                    title = "Proper Inhaler Administration",
                    description = "Help person use their rescue inhaler with proper technique.",
                    detailedInstructions = """
                        1. Locate rescue inhaler (usually blue):
                           • Look for quick-relief/rescue inhaler (albuterol, salbutamol)
                           • Usually blue in color
                           • Check expiration date quickly
                           • If no inhaler available, call 911 immediately
                        2. Prepare inhaler properly:
                           • Remove cap and check for objects in mouthpiece
                           • Shake inhaler vigorously 5-10 times
                           • If using spacer, attach it to inhaler
                           • Prime inhaler if it hasn't been used recently (1-2 test puffs)
                        3. Proper inhalation technique:
                           • Have person exhale fully, away from inhaler
                           • Place lips tightly around mouthpiece (or spacer)
                           • Press down on inhaler while taking slow, deep breath
                           • Continue breathing in slowly and deeply
                           • Hold breath for 10 seconds (or as long as comfortable)
                           • Exhale slowly
                        4. If using spacer device:
                           • Attach spacer to inhaler
                           • Shake and prime as above
                           • Insert inhaler into spacer
                           • Person breathes normally through spacer mask/mouthpiece
                           • Press inhaler once and have them take 5-6 normal breaths
                        5. Multiple puffs if prescribed:
                           • Wait 30-60 seconds between puffs
                           • Shake inhaler between each puff
                           • Usually 2-4 puffs for acute attack
                    """.trimIndent(),
                    duration = "5-10 minutes for proper administration",
                    tips = listOf("Shake inhaler well before each use", "Use spacer if available", "Take slow, deep breaths", "Hold breath for 10 seconds"),
                    warnings = listOf("Don't use expired inhaler", "Don't rush the breathing technique", "Don't use someone else's inhaler without medical guidance"),
                    requiredTools = listOf("Rescue inhaler (albuterol)", "Spacer device if available"),
                    stepType = StepType.ACTION,
                    isCritical = true
                ),
                GuideStep(
                    id = "asthma_step_3",
                    guideId = "asthma_attack_guide",
                    stepNumber = 3,
                    title = "Monitor Response and Repeat Treatment",
                    description = "Monitor breathing improvement and repeat inhaler as needed.",
                    detailedInstructions = """
                        1. Monitor for improvement (5-15 minutes):
                           • Easier breathing and less wheezing
                           • Ability to speak in longer sentences
                           • Less visible breathing effort
                           • Improved color (less blue around lips)
                           • Reduced anxiety and panic
                        2. Repeat inhaler if needed:
                           • Can repeat every 15-20 minutes
                           • Usually safe to give 2-4 additional puffs
                           • Follow person's action plan if available
                           • Don't exceed 8-10 puffs in one hour without medical advice
                        3. Continue supportive care:
                           • Maintain upright position
                           • Encourage slow, controlled breathing
                           • Provide reassurance and calm presence
                           • Ensure good air circulation
                        4. Document medication given:
                           • Note time of each inhaler use
                           • Count total puffs given
                           • Record person's response
                           • This information helps medical personnel
                        5. Signs requiring immediate 911 call:
                           • No improvement after 15-20 minutes
                           • Worsening despite treatment
                           • Person becoming exhausted
                           • Confusion or drowsiness
                           • Blue lips or fingernails persisting
                    """.trimIndent(),
                    duration = "15-30 minutes monitoring and repeat treatment",
                    tips = listOf("Wait 15-20 minutes between repeat doses", "Document all medication given", "Watch for signs of improvement", "Keep person calm and upright"),
                    warnings = listOf("Don't exceed recommended inhaler doses", "Call 911 if no improvement", "Watch for exhaustion"),
                    requiredTools = listOf("Rescue inhaler", "Watch or timer", "Pen and paper for documentation"),
                    stepType = StepType.MONITORING,
                    isCritical = true
                ),
                GuideStep(
                    id = "asthma_step_4",
                    guideId = "asthma_attack_guide",
                    stepNumber = 4,
                    title = "Emergency Transport and Continued Care",
                    description = "Prepare for emergency transport if needed and provide continued support.",
                    detailedInstructions = """
                        1. Call 911 if any of these occur:
                           • Severe attack with minimal improvement
                           • Person cannot complete sentences
                           • Inhaler provides only temporary relief
                           • Person becoming tired from breathing effort
                           • Blue coloration around lips or fingernails
                           • Confusion, drowsiness, or altered mental status
                        2. Prepare for emergency transport:
                           • Gather all inhalers and medications
                           • Bring asthma action plan if available
                           • Note all medications given and times
                           • Bring insurance cards and ID
                        3. Continue care while waiting for help:
                           • Keep person sitting upright
                           • Continue inhaler treatments as prescribed
                           • Monitor vital signs and consciousness
                           • Provide emotional support and reassurance
                           • Be prepared for CPR if person becomes unconscious
                        4. Information for emergency responders:
                           • Time attack started
                           • Medications given and when
                           • Person's normal asthma medications
                           • Known triggers for this attack
                           • Person's medical history
                        5. Recovery and follow-up:
                           • Even if attack resolves, person should see doctor same day
                           • Attacks can recur hours later
                           • May need oral steroids to prevent rebound
                           • Review asthma action plan and triggers
                           • Consider medication adjustments with doctor
                    """.trimIndent(),
                    duration = "Until medical help arrives or full recovery",
                    tips = listOf("Gather all medications for transport", "Document everything for medical team", "Even resolved attacks need medical follow-up", "Stay with person until help arrives"),
                    warnings = listOf("Don't assume attack won't recur", "Seek medical care even if attack resolves", "Be prepared for CPR if consciousness lost"),
                    requiredTools = listOf("All inhalers and medications", "Asthma action plan", "Insurance cards", "Phone"),
                    stepType = StepType.MONITORING,
                    isCritical = true
                )
            ),
            iconResName = "ic_asthma",
            whenToCallEmergency = "Severe breathing difficulty, inability to speak in phrases, blue lips/fingernails, no improvement with inhaler, or person becoming exhausted",
            warnings = listOf("Never lay person flat during attack", "Don't leave alone during severe attack", "Seek medical care even if attack resolves", "Be prepared for attack to recur"),
            estimatedTimeMinutes = 30,
            difficulty = "Intermediate"
        )
    }

    private fun createDiabeticEmergenciesGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "diabetic_emergencies_guide",
            title = "Diabetic Emergencies",
            category = "Medical",
            severity = "MEDIUM",
            description = "Diabetic emergencies include hypoglycemia (low blood sugar) and hyperglycemia (high blood sugar). Hypoglycemia is more immediately life-threatening and requires rapid treatment. Recognition and proper treatment can prevent coma and death.",
            steps = listOf(
                GuideStep(
                    id = "diabetic_step_1",
                    guideId = "diabetic_emergencies_guide",
                    stepNumber = 1,
                    title = "Rapid Assessment and Recognition",
                    description = "Quickly assess consciousness level and identify type of diabetic emergency.",
                    detailedInstructions = """
                        1. Check consciousness and responsiveness:
                           • Can person respond to questions normally?
                           • Are they alert and oriented?
                           • Can they follow simple commands?
                           • Check for medical alert bracelet/necklace
                        2. Identify hypoglycemia (low blood sugar) signs:
                           • Rapid onset (minutes to hours)
                           • Sweating and clamminess
                           • Shakiness or tremors
                           • Confusion or irritability
                           • Hunger
                           • Rapid heartbeat
                           • Pale skin
                           • Anxiety or nervousness
                        3. Identify hyperglycemia (high blood sugar) signs:
                           • Gradual onset (hours to days)
                           • Excessive thirst and urination
                           • Fruity breath odor
                           • Deep, rapid breathing
                           • Dry mouth and skin
                           • Nausea and vomiting
                           • Weakness and fatigue
                        4. Immediate priority decisions:
                           • Unconscious = Call 911, don't give anything by mouth
                           • Conscious with low blood sugar signs = Give sugar immediately
                           • Conscious with high blood sugar signs = Call 911, monitor
                           • Unknown type but conscious = Give sugar (safer option)
                    """.trimIndent(),
                    duration = "2-3 minutes assessment",
                    tips = listOf("Check for medical alert jewelry", "When in doubt, treat for low blood sugar", "Hypoglycemia develops faster than hyperglycemia", "Ask about recent insulin or medication"),
                    warnings = listOf("Never give anything by mouth to unconscious person", "Don't delay treatment for low blood sugar", "Don't assume - assess carefully"),
                    requiredTools = listOf("Medical alert information if available"),
                    stepType = StepType.ASSESSMENT,
                    isCritical = true
                ),
                GuideStep(
                    id = "diabetic_step_2",
                    guideId = "diabetic_emergencies_guide",
                    stepNumber = 2,
                    title = "Hypoglycemia Treatment (Low Blood Sugar)",
                    description = "Provide immediate sugar for conscious person with low blood sugar.",
                    detailedInstructions = """
                        1. If person is conscious and showing hypoglycemia signs:
                           • Give 15-20 grams of fast-acting sugar immediately
                           • Options in order of preference:
                             - Glucose tablets (3-4 tablets)
                             - Glucose gel (1 tube)
                             - 4 ounces (1/2 cup) fruit juice
                             - 4 ounces regular (not diet) soda
                             - 1 tablespoon honey or sugar
                             - 5-6 hard candies
                        2. Administration technique:
                           • Help person sit up if possible
                           • Give sugar source slowly to prevent choking
                           • Stay with person while they consume it
                           • Encourage them to finish entire amount
                        3. If person becomes combative or confused:
                           • Don't force sugar into mouth
                           • Try honey or glucose gel rubbed on inside of cheek
                           • Call 911 if too combative to treat safely
                           • Protect yourself and the person from injury
                        4. For unconscious person:
                           • Call 911 immediately
                           • Place in recovery position
                           • Don't give anything by mouth
                           • Be prepared for seizures
                           • Monitor breathing and pulse
                        5. Special considerations:
                           • Pregnant women need immediate medical care
                           • Children may need adjusted sugar amounts
                           • Elderly may have atypical symptoms
                    """.trimIndent(),
                    duration = "5-10 minutes for treatment",
                    tips = listOf("15-20 grams fast-acting sugar", "Glucose tablets work fastest", "Stay with person during treatment", "Help them sit up if possible"),
                    warnings = listOf("Don't give anything by mouth if unconscious", "Don't use chocolate or protein bars", "Call 911 if person becomes unconscious"),
                    requiredTools = listOf("Fast-acting sugar source", "Glucose tablets/gel preferred"),
                    stepType = StepType.ACTION,
                    isCritical = true
                ),
                GuideStep(
                    id = "diabetic_step_3",
                    guideId = "diabetic_emergencies_guide",
                    stepNumber = 3,
                    title = "Monitor Response and Repeat Treatment",
                    description = "Monitor for improvement and provide additional treatment if needed.",
                    detailedInstructions = """
                        1. Wait and monitor for 15 minutes after giving sugar:
                           • Check for symptom improvement:
                             - Less confusion, more alert
                             - Sweating decreases
                             - Shakiness improves
                             - Better color and warmth
                             - More normal speech and behavior
                        2. If no improvement after 15 minutes:
                           • Give another 15-20 grams of fast-acting sugar
                           • Continue monitoring for another 15 minutes
                           • Call 911 if still no improvement
                        3. If symptoms improve:
                           • Give complex carbohydrate snack:
                             - Crackers with peanut butter
                             - Half sandwich
                             - Granola bar
                             - Fruit and protein
                           • This prevents blood sugar from dropping again
                        4. Continue monitoring for 1-2 hours:
                           • Blood sugar can drop again
                           • Watch for return of symptoms
                           • Encourage regular meals as scheduled
                           • Don't leave person alone initially
                        5. Document the episode:
                           • Time symptoms started
                           • What sugar was given and when
                           • How person responded
                           • Any medications they normally take
                           • Share information with medical team if needed
                    """.trimIndent(),
                    duration = "1-2 hours monitoring",
                    tips = listOf("Wait 15 minutes before repeating sugar", "Follow up with complex carbs", "Don't leave person alone initially", "Document everything"),
                    warnings = listOf("Blood sugar can drop again", "Don't skip follow-up snack", "Seek medical care if episode was severe"),
                    requiredTools = listOf("Additional sugar sources", "Complex carbohydrate snacks", "Watch for timing"),
                    stepType = StepType.MONITORING,
                    isCritical = true
                ),
                GuideStep(
                    id = "diabetic_step_4",
                    guideId = "diabetic_emergencies_guide",
                    stepNumber = 4,
                    title = "Emergency Transport and Follow-up Care",
                    description = "Determine need for emergency care and plan appropriate follow-up.",
                    detailedInstructions = """
                        1. Call 911 immediately for:
                           • Person remains unconscious
                           • No improvement after two sugar treatments
                           • Seizures occur
                           • Person cannot keep sugar down (vomiting)
                           • Signs of hyperglycemia (high blood sugar):
                             - Fruity breath, deep breathing
                             - Excessive thirst and urination
                             - Dry mouth and skin
                        2. Prepare for emergency transport:
                           • Gather all diabetes medications and supplies
                           • Bring blood glucose meter if available
                           • List recent meals, medications, and insulin
                           • Note any recent illness or stress
                           • Bring insurance information
                        3. Information for emergency responders:
                           • Person's normal diabetes medications
                           • Time and type of last insulin dose
                           • When they last ate
                           • Blood sugar readings if available
                           • How episode developed
                           • What treatments were given
                        4. Follow-up care planning:
                           • Even if episode resolves, contact doctor same day
                           • Review diabetes management plan
                           • Identify what caused the episode:
                             - Missed meals
                             - Too much insulin
                             - Increased activity
                             - Illness or stress
                        5. Prevention education:
                           • Regular meal timing
                           • Blood sugar monitoring
                           • Medication compliance
                           • Recognition of early warning signs
                           • When to seek medical help
                    """.trimIndent(),
                    duration = "Until medical care arranged or full recovery",
                    tips = listOf("Gather all diabetes supplies for transport", "Document what caused episode", "Contact doctor even if resolved", "Review prevention strategies"),
                    warnings = listOf("Don't ignore severe episodes", "Hyperglycemia also needs emergency care", "Episodes can recur"),
                    requiredTools = listOf("All diabetes medications", "Blood glucose meter", "Medical information", "Phone"),
                    stepType = StepType.MONITORING,
                    isCritical = true
                )
            ),
            iconResName = "ic_diabetic",
            whenToCallEmergency = "Unconscious diabetic, no improvement after two sugar treatments, seizures, vomiting, or signs of severe hyperglycemia (fruity breath, deep breathing)",
            warnings = listOf("Never give anything by mouth to unconscious person", "When in doubt with conscious person, give sugar", "Don't use chocolate for hypoglycemia treatment", "Both high and low blood sugar can be life-threatening"),
            estimatedTimeMinutes = 30,
            difficulty = "Intermediate"
        )
    }

    private fun createDrowningGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "drowning_guide",
            title = "Drowning Rescue",
            category = "Life-Threatening",
            severity = "CRITICAL",
            description = "Drowning is a leading cause of accidental death. Rescue safely without becoming a victim yourself. Focus on getting person out of water and providing rescue breathing or CPR as needed.",
            steps = listOf(
                GuideStep(
                    id = "drowning_step_1",
                    guideId = "drowning_guide",
                    stepNumber = 1,
                    title = "Rescue Safely",
                    description = "Get person out of water without endangering yourself.",
                    detailedInstructions = "If possible, rescue without entering water - throw flotation device, extend pole, or throw rope. If you must enter water, bring flotation device. Don't let victim grab you directly - they may pull you under in panic.",
                    duration = "5-10 minutes",
                    tips = listOf("Throw, don't go if possible", "Bring flotation if entering water", "Approach from behind if swimming rescue"),
                    warnings = listOf("Don't become a victim yourself", "Drowning people may panic and grab rescuer"),
                    requiredTools = listOf("Flotation device", "Rope or pole if available"),
                    stepType = StepType.SAFETY,
                    isCritical = true
                ),
                GuideStep(
                    id = "drowning_step_2",
                    guideId = "drowning_guide",
                    stepNumber = 2,
                    title = "Check Breathing and Start CPR",
                    description = "Check for breathing and start CPR if not breathing normally.",
                    detailedInstructions = "Once on shore, check for responsiveness and normal breathing. If not breathing normally, start CPR immediately - 30 chest compressions followed by 2 rescue breaths. Call emergency services.",
                    duration = "Continue until help arrives",
                    tips = listOf("Check breathing for no more than 10 seconds", "Start with chest compressions", "Don't delay for water removal"),
                    warnings = listOf("Don't spend time trying to remove water from lungs", "Start CPR immediately if not breathing"),
                    requiredTools = listOf("None required"),
                    stepType = StepType.ACTION,
                    isCritical = true
                )
            ),
            iconResName = "ic_drowning",
            whenToCallEmergency = "All drowning incidents require emergency medical evaluation",
            warnings = listOf("Don't attempt rescue beyond your abilities", "All drowning victims need medical evaluation"),
            estimatedTimeMinutes = 10,
            difficulty = "Advanced"
        )
    }

    private fun createNosebleedsGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "nosebleeds_guide",
            title = "Nosebleeds",
            category = "Minor Injuries",
            severity = "LOW",
            description = "Most nosebleeds (epistaxis) are minor and stop with proper first aid technique. They commonly occur due to dry air, nose picking, allergies, or minor trauma. Proper positioning and pressure application are key to controlling bleeding.",
            steps = listOf(
                GuideStep(
                    id = "nosebleed_step_1",
                    guideId = "nosebleeds_guide",
                    stepNumber = 1,
                    title = "Immediate Assessment and Positioning",
                    description = "Assess severity and position person correctly to control bleeding.",
                    detailedInstructions = """
                        1. Assess the situation:
                           • Check if bleeding is from one or both nostrils
                           • Ask about recent head injury or trauma
                           • Note if person takes blood thinners
                           • Assess amount of bleeding (minor vs severe)
                        2. Position person correctly:
                           • Have person sit upright in chair or on edge of bed
                           • Lean head slightly forward (not backward)
                           • This prevents blood from running down throat
                           • Keep person calm and reassured
                        3. Prepare supplies:
                           • Gather clean tissues or gauze
                           • Get bowl or container to catch blood
                           • Have ice pack ready if available
                           • Ensure good lighting to see clearly
                        4. Initial bleeding control:
                           • Gently blow nose once to clear clots (if not severe)
                           • Don't insert tissues or gauze into nostrils
                           • Don't tilt head backward
                    """.trimIndent(),
                    duration = "2-3 minutes preparation",
                    tips = listOf("Sit upright, lean slightly forward", "Stay calm and reassure person", "Don't tilt head back", "Have supplies ready"),
                    warnings = listOf("Never tilt head backward", "Don't pack tissues deep in nose", "Don't ignore severe bleeding"),
                    requiredTools = listOf("Clean tissues", "Bowl for blood", "Chair"),
                    stepType = StepType.ASSESSMENT,
                    isCritical = true
                ),
                GuideStep(
                    id = "nosebleed_step_2",
                    guideId = "nosebleeds_guide",
                    stepNumber = 2,
                    title = "Apply Direct Pressure",
                    description = "Apply firm, continuous pressure to the soft part of the nose.",
                    detailedInstructions = """
                        1. Locate correct pressure point:
                           • Find the soft, fleshy part of nose below the bony bridge
                           • This is where the nostrils can be compressed together
                           • Don't pinch the hard, bony upper part
                        2. Apply pressure correctly:
                           • Use thumb and index finger to pinch nostrils closed
                           • Apply firm, steady pressure
                           • Compress against the nasal septum (center wall)
                           • Maintain consistent pressure - don't release to check
                        3. Maintain pressure for full duration:
                           • Hold pressure for 10-15 minutes without interruption
                           • Use a timer or watch to track time
                           • Don't peek or release pressure early
                           • Have person breathe through mouth during this time
                        4. Support and comfort:
                           • Help person maintain position
                           • Provide tissues for mouth breathing
                           • Keep person calm and distracted
                           • Apply ice to bridge of nose if available
                    """.trimIndent(),
                    duration = "10-15 minutes continuous pressure",
                    tips = listOf("Pinch soft part only, not bony bridge", "Use timer to track 10-15 minutes", "Don't release pressure to check", "Breathe through mouth"),
                    warnings = listOf("Don't release pressure early", "Don't pinch bony part of nose", "Don't let person lie down"),
                    requiredTools = listOf("Timer or watch", "Tissues"),
                    stepType = StepType.ACTION,
                    isCritical = true
                ),
                GuideStep(
                    id = "nosebleed_step_3",
                    guideId = "nosebleeds_guide",
                    stepNumber = 3,
                    title = "Ice Application and Pressure Release",
                    description = "Apply ice and carefully release pressure to check results.",
                    detailedInstructions = """
                        1. Apply ice during pressure:
                           • Place ice pack wrapped in thin cloth on bridge of nose
                           • Keep ice on for 10-15 minutes maximum
                           • Don't apply ice directly to skin
                           • Continue nasal pressure while using ice
                        2. Slowly release pressure after 10-15 minutes:
                           • Gradually reduce finger pressure
                           • Don't suddenly let go
                           • Keep person leaning forward
                           • Have tissues ready to catch any blood
                        3. Check bleeding status:
                           • If bleeding has stopped: continue with prevention steps
                           • If still bleeding but slower: apply pressure for another 5-10 minutes
                           • If bleeding heavily continues: seek medical attention
                        4. If bleeding continues:
                           • Don't exceed 20 minutes total pressure time without medical help
                           • Consider calling 911 for severe bleeding
                           • Note any clots or unusual blood characteristics
                    """.trimIndent(),
                    duration = "10-15 minutes ice application",
                    tips = listOf("Wrap ice in cloth", "Release pressure gradually", "Don't exceed 20 minutes total pressure", "Have tissues ready"),
                    warnings = listOf("Don't apply ice directly to skin", "Seek help if bleeding continues after 20 minutes", "Don't ignore severe bleeding"),
                    requiredTools = listOf("Ice pack", "Thin cloth", "Additional tissues"),
                    stepType = StepType.ACTION
                ),
                GuideStep(
                    id = "nosebleed_step_4",
                    guideId = "nosebleeds_guide",
                    stepNumber = 4,
                    title = "Post-Bleeding Care and Prevention",
                    description = "Provide aftercare and prevent rebleeding.",
                    detailedInstructions = """
                        1. Immediate aftercare:
                           • Keep person upright for 30-60 minutes after bleeding stops
                           • Apply petroleum jelly gently inside nostrils if available
                           • Give person water to drink (to replace fluids)
                           • Clean up blood from face and clothing
                        2. Prevention of rebleeding:
                           • No nose blowing for 24 hours
                           • No picking or touching inside nose
                           • Avoid strenuous activity for 24 hours
                           • Sleep with head elevated on extra pillows
                           • Use humidifier or saline spray for dry air
                        3. Monitor for complications:
                           • Watch for signs of rebleeding
                           • Monitor for excessive swallowing (blood going down throat)
                           • Check for signs of significant blood loss (weakness, dizziness)
                        4. When to seek medical attention:
                           • Bleeding returns repeatedly
                           • Signs of significant blood loss
                           • Nosebleed after head injury
                           • Person takes blood thinning medications
                           • Bleeding from both nostrils simultaneously
                           • Associated symptoms (severe headache, vision changes)
                        5. Follow-up care:
                           • If frequent nosebleeds, see doctor for evaluation
                           • May need cauterization of bleeding vessels
                           • Review medications that may contribute to bleeding
                    """.trimIndent(),
                    duration = "Ongoing for 24 hours",
                    tips = listOf("No nose blowing for 24 hours", "Keep head elevated when sleeping", "Use humidifier for dry air", "Apply petroleum jelly gently"),
                    warnings = listOf("Don't ignore frequent recurring nosebleeds", "Seek medical care for nosebleeds after head injury", "Watch for signs of blood loss"),
                    requiredTools = listOf("Petroleum jelly", "Extra pillows", "Humidifier or saline spray"),
                    stepType = StepType.MONITORING
                )
            ),
            iconResName = "ic_nosebleed",
            whenToCallEmergency = "Bleeding doesn't stop after 20 minutes of pressure, heavy bleeding, nosebleed after head injury, or signs of significant blood loss",
            warnings = listOf("Never tilt head backward", "Don't blow nose for 24 hours after bleeding stops", "Seek medical care for frequent recurring nosebleeds", "Don't pack nose with tissues or gauze"),
            estimatedTimeMinutes = 25,
            difficulty = "Beginner"
        )
    }

    private fun createEyeInjuriesGuide(): FirstAidGuide {
        return FirstAidGuide(
            id = "eye_injuries_guide",
            title = "Eye Injuries",
            category = "Trauma",
            severity = "MEDIUM",
            description = "Eye injuries can cause permanent vision loss and require immediate, proper care. Different types of eye injuries require different treatments - chemical burns need immediate flushing, while penetrating injuries must never be disturbed.",
            steps = listOf(
                GuideStep(
                    id = "eye_step_1",
                    guideId = "eye_injuries_guide",
                    stepNumber = 1,
                    title = "Rapid Assessment and Injury Classification",
                    description = "Quickly identify the type of eye injury to determine proper treatment.",
                    detailedInstructions = """
                        1. Assess the injury type immediately:
                           • Chemical exposure: Acids, alkalis, household cleaners
                           • Foreign object: Dust, metal particles, insects
                           • Penetrating injury: Objects stuck in eye
                           • Blunt trauma: Punches, balls, falls
                           • Flash burns: Welding, bright lights, UV exposure
                        2. Check for emergency signs:
                           • Object protruding from eye
                           • Blood visible in eye
                           • Severe pain or inability to open eye
                           • Loss or change in vision
                           • Chemical splash or burn
                        3. Initial safety measures:
                           • Don't let person rub or touch the eye
                           • Don't apply pressure to the eye
                           • Keep person calm and still
                           • Don't remove contact lenses unless chemical exposure
                        4. Gather information quickly:
                           • What caused the injury?
                           • When did it happen?
                           • Any chemicals involved?
                           • Person's vision status before injury
                    """.trimIndent(),
                    duration = "1-2 minutes rapid assessment",
                    tips = listOf("Never let person rub eye", "Identify chemical vs mechanical injury", "Keep person calm and still", "Don't remove contact lenses except for chemicals"),
                    warnings = listOf("Never rub injured eye", "Don't delay chemical flushing", "Don't remove penetrating objects"),
                    requiredTools = listOf("Good lighting for assessment"),
                    stepType = StepType.ASSESSMENT,
                    isCritical = true
                ),
                GuideStep(
                    id = "eye_step_2",
                    guideId = "eye_injuries_guide",
                    stepNumber = 2,
                    title = "Chemical Burn Emergency Treatment",
                    description = "Immediate flushing for chemical exposures to prevent permanent damage.",
                    detailedInstructions = """
                        1. For chemical exposure (IMMEDIATE ACTION):
                           • Begin flushing immediately - every second counts
                           • Use clean, lukewarm water (room temperature preferred)
                           • Remove contact lenses if easily removable
                           • Position person with affected eye down
                        2. Proper flushing technique:
                           • Hold eye open with fingers
                           • Flush from inner corner toward outer corner
                           • Use gentle, steady stream of water
                           • Flush for minimum 15-20 minutes continuously
                           • Don't let water run into uninjured eye
                        3. Flushing positions:
                           • Over sink: affected eye lower than uninjured eye
                           • Lying down: turn head so affected eye is down
                           • Use shower if sink unavailable
                           • Eye wash station if available (workplace)
                        4. Continue flushing while:
                           • Calling 911 or arranging emergency transport
                           • Don't stop flushing to make phone calls
                           • Have someone else call if possible
                           • Bring chemical container/label to hospital
                        5. Special chemical considerations:
                           • Alkalis (drain cleaners, cement): flush for 30+ minutes
                           • Acids: flush for 15-20 minutes minimum
                           • Dry chemicals: brush away first, then flush
                           • Don't neutralize chemicals - just flush with water
                    """.trimIndent(),
                    duration = "15-30 minutes continuous flushing",
                    tips = listOf("Start flushing immediately", "Flush from inner to outer corner", "Keep affected eye lower", "Continue while arranging transport"),
                    warnings = listOf("Don't delay flushing even for phone calls", "Don't try to neutralize chemicals", "Don't stop flushing too early"),
                    requiredTools = listOf("Clean lukewarm water", "Sink or shower", "Towels"),
                    stepType = StepType.ACTION,
                    isCritical = true
                ),
                GuideStep(
                    id = "eye_step_3",
                    guideId = "eye_injuries_guide",
                    stepNumber = 3,
                    title = "Foreign Object and Penetrating Injury Care",
                    description = "Safe removal of small objects or protection of penetrating injuries.",
                    detailedInstructions = """
                        1. For small foreign objects (dust, eyelash):
                           • Don't rub the eye
                           • Try blinking several times to produce tears
                           • Gently flush with clean water or saline solution
                           • Pull upper eyelid down over lower lid briefly
                           • If object visible on lower lid, gently remove with clean cloth
                        2. For objects on upper eyelid:
                           • Have person look down
                           • Gently grasp eyelashes of upper lid
                           • Fold upper lid back over cotton swab or matchstick
                           • Remove visible object with clean, damp cloth
                           • Don't dig or scrape at embedded objects
                        3. For penetrating injuries (NEVER REMOVE):
                           • Don't remove any object stuck in eye
                           • Don't apply pressure to the eye
                           • Stabilize object with bulky padding around it
                           • Use paper cup or cone to protect protruding object
                           • Cover both eyes to prevent sympathetic movement
                        4. Bandaging technique for penetrating injuries:
                           • Place gauze pads around (not on) the object
                           • Tape cup or cone over object for protection
                           • Cover uninjured eye with gauze pad
                           • Secure all bandages with tape
                           • Keep person lying flat and still
                        5. What NOT to do:
                           • Don't remove contact lenses if object penetrating
                           • Don't flush penetrating injuries
                           • Don't apply pressure to any penetrating object
                           • Don't let person walk around with penetrating injury
                    """.trimIndent(),
                    duration = "5-10 minutes for careful removal/protection",
                    tips = listOf("Try blinking and tears first", "Only remove visible surface objects", "Stabilize penetrating objects", "Cover both eyes for penetrating injuries"),
                    warnings = listOf("Never remove penetrating objects", "Don't apply pressure to injured eye", "Don't flush penetrating injuries"),
                    requiredTools = listOf("Clean water", "Gauze pads", "Paper cup", "Tape", "Cotton swabs"),
                    stepType = StepType.ACTION,
                    isCritical = true
                ),
                GuideStep(
                    id = "eye_step_4",
                    guideId = "eye_injuries_guide",
                    stepNumber = 4,
                    title = "Monitoring and Emergency Transport",
                    description = "Monitor for complications and arrange appropriate medical care.",
                    detailedInstructions = """
                        1. Monitor vision and symptoms:
                           • Check vision in both eyes (if safe to do so)
                           • Note any changes in vision clarity
                           • Watch for increasing pain
                           • Monitor for nausea or vomiting (can indicate serious injury)
                        2. Arrange immediate emergency transport for:
                           • Any chemical exposure to eyes
                           • Penetrating objects in eye
                           • Blood visible in the eye
                           • Sudden vision loss or major changes
                           • Severe pain not relieved by minor measures
                           • Flash burns from welding or UV exposure
                        3. Comfort measures during transport:
                           • Keep person lying flat and still
                           • Don't let them rub or touch eyes
                           • Provide pain medication only as directed by medical personnel
                           • Keep both eyes covered to prevent movement
                        4. Information for medical team:
                           • Exact cause of injury
                           • Time injury occurred
                           • What chemicals were involved (bring container)
                           • First aid measures taken
                           • Person's vision before injury
                           • Current medications or medical conditions
                        5. Follow-up care instructions:
                           • Even minor eye injuries should be evaluated by doctor
                           • Watch for signs of infection:
                             - Increasing redness, pain, or discharge
                             - Vision changes developing later
                             - Sensitivity to light
                           • Don't ignore delayed symptoms
                           • Complete any prescribed antibiotic treatments
                    """.trimIndent(),
                    duration = "Until medical care obtained",
                    tips = listOf("Monitor vision changes", "Keep person still during transport", "Bring chemical containers to hospital", "Follow up even for minor injuries"),
                    warnings = listOf("Don't ignore vision changes", "Even minor injuries need medical evaluation", "Watch for delayed complications"),
                    requiredTools = listOf("Transportation", "Chemical containers for identification", "Documentation of care provided"),
                    stepType = StepType.MONITORING,
                    isCritical = true
                )
            ),
            iconResName = "ic_eye_injury",
            whenToCallEmergency = "Chemical exposure, penetrating object, blood in eye, sudden vision loss, severe pain, or flash burns",
            warnings = listOf("Never rub injured eye", "Don't remove penetrating objects", "Flush chemicals immediately and continuously", "Even minor eye injuries need medical evaluation"),
            estimatedTimeMinutes = 30,
            difficulty = "Intermediate"
        )
    }
}
