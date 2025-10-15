package com.example.firstaidapp.data.repository

import com.example.firstaidapp.R
import com.example.firstaidapp.data.models.GuideStep
import com.example.firstaidapp.data.models.StepType

object FirstAidGuidesRepository {

    fun getCPRGuide(): List<GuideStep> {
        return listOf(
            GuideStep(
                id = "cpr_step_1",
                guideId = "cpr_guide",
                stepNumber = 1,
                title = "Check for Responsiveness",
                description = "Gently tap the person's shoulders and shout 'Are you okay?'",
                detailedInstructions = "Place your hands on the person's shoulders and gently shake them. Speak loudly and clearly. Look for any signs of movement, breathing, or response to your voice.",
                iconRes = R.drawable.ic_visibility,
                imageRes = R.drawable.cpr_check_responsiveness,
                duration = "10 seconds",
                stepType = StepType.CHECK,
                isCritical = true,
                tips = listOf(
                    "Tap firmly but not aggressively",
                    "Look for eye movement or any response",
                    "Check if they're breathing normally"
                ),
                warnings = listOf("Do not shake if you suspect spinal injury")
            ),
            GuideStep(
                id = "cpr_step_2",
                guideId = "cpr_guide",
                stepNumber = 2,
                title = "Call for Emergency Help",
                description = "Call 911 immediately or ask someone else to do it",
                detailedInstructions = "If the person is unresponsive, immediately call 911. If others are around, point to someone specific and say 'You, call 911 now!' Also ask someone to find an AED if available.",
                iconRes = R.drawable.ic_phone,
                imageRes = R.drawable.emergency_call,
                stepType = StepType.CALL,
                isCritical = true,
                requiredTools = listOf("Phone", "AED (if available)"),
                tips = listOf(
                    "Be specific when asking for help",
                    "Stay on the line with 911",
                    "Give your exact location"
                )
            ),
            GuideStep(
                id = "cpr_step_3",
                guideId = "cpr_guide",
                stepNumber = 3,
                title = "Position the Person",
                description = "Place person on firm, flat surface on their back",
                detailedInstructions = "Carefully roll the person onto their back on a firm surface. Tilt their head back slightly by lifting their chin. This opens the airway.",
                iconRes = R.drawable.ic_action,
                imageRes = R.drawable.cpr_positioning,
                duration = "15 seconds",
                stepType = StepType.ACTION,
                tips = listOf(
                    "Use the log roll technique if possible",
                    "Keep head, neck, and spine aligned",
                    "Remove any visible obstructions from mouth"
                ),
                warnings = listOf(
                    "Be careful if you suspect neck injury",
                    "Don't hyperextend the neck"
                )
            ),
            GuideStep(
                id = "cpr_step_4",
                guideId = "cpr_guide",
                stepNumber = 4,
                title = "Hand Position",
                description = "Place heel of one hand on center of chest between nipples",
                detailedInstructions = "Find the center of the chest between the nipples. Place the heel of one hand there, then place your other hand on top, interlocking fingers. Keep arms straight.",
                iconRes = R.drawable.ic_action,
                imageRes = R.drawable.cpr_hand_position,
                duration = "10 seconds",
                stepType = StepType.ACTION,
                isCritical = true,
                tips = listOf(
                    "Use heel of hand, not palm or fingers",
                    "Keep fingers off the chest",
                    "Position yourself directly above hands"
                ),
                warnings = listOf(
                    "Avoid pressing on ribs or stomach",
                    "Don't let hands slip during compressions"
                )
            ),
            GuideStep(
                id = "cpr_step_5",
                guideId = "cpr_guide",
                stepNumber = 5,
                title = "Begin Chest Compressions",
                description = "Push hard and fast at least 2 inches deep, 100-120 times per minute",
                detailedInstructions = "Push straight down at least 2 inches (5 cm) deep. Allow complete chest recoil between compressions. Count out loud: '1 and 2 and 3...' Push to the beat of 'Stayin' Alive'.",
                iconRes = R.drawable.ic_action,
                imageRes = R.drawable.cpr_compressions,
                stepType = StepType.ACTION,
                isCritical = true,
                videoUrl = "https://example.com/cpr_compressions_demo",
                requiredTools = listOf("Firm surface", "Your hands"),
                tips = listOf(
                    "Use your whole body weight",
                    "Keep rhythm steady",
                    "Switch with someone every 2 minutes if possible"
                ),
                warnings = listOf(
                    "Don't stop compressions unless absolutely necessary",
                    "Expect to hear ribs crack - this is normal"
                )
            ),
            GuideStep(
                id = "cpr_step_6",
                guideId = "cpr_guide",
                stepNumber = 6,
                title = "Continue Until Help Arrives",
                description = "Keep doing 30 compressions followed by 2 rescue breaths",
                detailedInstructions = "Continue cycles of 30 chest compressions followed by 2 rescue breaths. If you haven't been trained in rescue breathing, continue with compressions only. Don't stop until emergency services arrive.",
                iconRes = R.drawable.ic_repeat,
                stepType = StepType.REPEAT,
                isCritical = true,
                tips = listOf(
                    "Hands-only CPR is still effective",
                    "Don't be afraid to push hard",
                    "Take turns if others can help"
                ),
                warnings = listOf(
                    "Don't check for pulse unless trained",
                    "Don't give up - continue until help arrives"
                )
            )
        )
    }

    fun getChokingAdultGuide(): List<GuideStep> {
        return listOf(
            GuideStep(
                id = "choking_step_1",
                guideId = "choking_guide",
                stepNumber = 1,
                title = "Assess the Situation",
                description = "Ask 'Are you choking?' Look for signs of severe airway obstruction",
                detailedInstructions = "A choking person may grab their throat, be unable to speak or cough effectively, make high-pitched sounds, or turn blue around lips and face.",
                iconRes = R.drawable.ic_visibility,
                imageRes = R.drawable.choking_assessment,
                stepType = StepType.CHECK,
                duration = "5 seconds",
                isCritical = true,
                tips = listOf(
                    "Universal choking sign is grabbing the throat",
                    "Severe choking = cannot speak or cough",
                    "Mild choking = can still speak and cough"
                ),
                warnings = listOf("If they can speak/cough, encourage them to keep coughing")
            ),
            GuideStep(
                id = "choking_step_2",
                guideId = "choking_guide",
                stepNumber = 2,
                title = "Position Behind Person",
                description = "Stand behind the person and wrap your arms around their waist",
                detailedInstructions = "For adults: stand behind and wrap arms around waist. For pregnant women or obese people: position arms around chest instead of waist.",
                iconRes = R.drawable.ic_action,
                imageRes = R.drawable.heimlich_position,
                stepType = StepType.ACTION,
                duration = "5 seconds",
                tips = listOf(
                    "Support them if they become unconscious",
                    "For wheelchair users, approach from behind chair"
                )
            ),
            GuideStep(
                id = "choking_step_3",
                guideId = "choking_guide",
                stepNumber = 3,
                title = "Make a Fist",
                description = "Place fist thumb-side against belly, just above navel",
                detailedInstructions = "Make a fist with one hand and place the thumb side against the person's belly, just above the navel but well below the breastbone.",
                iconRes = R.drawable.ic_action,
                imageRes = R.drawable.heimlich_fist_position,
                stepType = StepType.ACTION,
                isCritical = true,
                tips = listOf(
                    "Position between navel and rib cage",
                    "Thumb side should be against belly"
                ),
                warnings = listOf("Don't position over ribs or breastbone")
            ),
            GuideStep(
                id = "choking_step_4",
                guideId = "choking_guide",
                stepNumber = 4,
                title = "Perform Abdominal Thrusts",
                description = "Grab fist with other hand and thrust upward and inward forcefully",
                detailedInstructions = "Grasp your fist with your other hand and perform quick, forceful upward and inward thrusts. Each thrust should be separate and distinct, attempting to dislodge the object.",
                iconRes = R.drawable.ic_action,
                imageRes = R.drawable.heimlich_thrusts,
                stepType = StepType.ACTION,
                isCritical = true,
                videoUrl = "https://example.com/heimlich_maneuver_demo",
                tips = listOf(
                    "Use quick, forceful movements",
                    "Each thrust should be a separate attempt",
                    "Continue until object is expelled or person becomes unconscious"
                ),
                warnings = listOf(
                    "Don't use fingers to try to grab object",
                    "If person becomes unconscious, start CPR"
                )
            )
        )
    }

    fun getBurnTreatmentGuide(): List<GuideStep> {
        return listOf(
            GuideStep(
                id = "burn_step_1",
                guideId = "burn_guide",
                stepNumber = 1,
                title = "Remove from Heat Source",
                description = "Get the person away from the source of the burn immediately",
                detailedInstructions = "Remove the person from the heat source. For electrical burns, turn off power source first. For chemical burns, brush off dry chemicals before flushing with water.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                isCritical = true,
                duration = "Immediately",
                tips = listOf(
                    "Your safety first - ensure area is safe",
                    "Don't touch electrical burn victim while power is on"
                ),
                warnings = listOf(
                    "Don't put yourself in danger",
                    "For chemical burns, wear protective gear if possible"
                )
            ),
            GuideStep(
                id = "burn_step_2",
                guideId = "burn_guide",
                stepNumber = 2,
                title = "Cool the Burn",
                description = "Run cool (not cold) water over burn for 10-20 minutes",
                detailedInstructions = "Hold the burned area under cool running water or apply cool, wet towels. Don't use ice or very cold water as this can cause more damage to the tissue.",
                iconRes = R.drawable.ic_action,
                imageRes = R.drawable.burn_cooling,
                stepType = StepType.ACTION,
                duration = "10-20 minutes",
                requiredTools = listOf("Cool water", "Clean towels"),
                tips = listOf(
                    "Remove jewelry before swelling starts",
                    "Cool water helps reduce pain and swelling",
                    "Don't break blisters if they form"
                ),
                warnings = listOf(
                    "Don't use ice or very cold water",
                    "Don't apply butter, oil, or other home remedies"
                )
            ),
            GuideStep(
                id = "burn_step_3",
                guideId = "burn_guide",
                stepNumber = 3,
                title = "Assess Burn Severity",
                description = "Determine if this is a minor or major burn requiring medical attention",
                detailedInstructions = "Major burns require immediate medical attention: burns larger than 3 inches, burns on face/hands/feet/genitals, electrical or chemical burns, or burns that appear white, charred, or leathery.",
                iconRes = R.drawable.ic_visibility,
                stepType = StepType.CHECK,
                isCritical = true,
                tips = listOf(
                    "When in doubt, seek medical attention",
                    "Take photos if safe to do so for medical records"
                ),
                warnings = listOf(
                    "Don't underestimate electrical burns - they can cause internal damage",
                    "Chemical burns may continue to worsen"
                )
            )
        )
    }

    fun getHeartAttackGuide(): List<GuideStep> {
        return listOf(
            GuideStep(
                id = "heart_attack_step_1",
                guideId = "heart_attack_guide",
                stepNumber = 1,
                title = "Call Emergency Services Immediately",
                description = "Call 911 or emergency services without delay",
                detailedInstructions = "Time is critical in heart attack situations. Call emergency services immediately or have someone else call while you provide care.",
                iconRes = R.drawable.ic_phone,
                stepType = StepType.CALL,
                isCritical = true,
                duration = "Immediately",
                tips = listOf(
                    "Don't drive to hospital yourself",
                    "Emergency services can provide advanced care en route"
                ),
                warnings = listOf("Don't delay calling for help")
            ),
            GuideStep(
                id = "heart_attack_step_2",
                guideId = "heart_attack_guide",
                stepNumber = 2,
                title = "Position Comfortably",
                description = "Help person sit in comfortable, slightly reclined position",
                detailedInstructions = "Help the person sit in a comfortable position, slightly reclined. This helps reduce the workload on the heart and makes breathing easier.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                tips = listOf(
                    "Keep them calm and reassured",
                    "Sitting position is usually most comfortable"
                ),
                warnings = listOf("Don't let them exert themselves")
            ),
            GuideStep(
                id = "heart_attack_step_3",
                guideId = "heart_attack_guide",
                stepNumber = 3,
                title = "Give Aspirin if Available",
                description = "Give one adult dose (300mg) aspirin to chew if no allergies",
                detailedInstructions = "If the person can chew aspirin and has no allergies, give one adult dose (300mg) to chew. This can help reduce heart damage.",
                iconRes = R.drawable.ic_medication,
                stepType = StepType.ACTION,
                requiredTools = listOf("Aspirin 300mg"),
                tips = listOf(
                    "Chewing is better than swallowing",
                    "Check for allergies first"
                ),
                warnings = listOf(
                    "Don't give if allergic to aspirin",
                    "Don't give anything by mouth if unconscious"
                )
            ),
            GuideStep(
                id = "heart_attack_step_4",
                guideId = "heart_attack_guide",
                stepNumber = 4,
                title = "Monitor and Prepare for CPR",
                description = "Loosen tight clothing, monitor breathing, be ready for CPR",
                detailedInstructions = "Loosen any tight clothing around neck and chest. Monitor their breathing and consciousness. Be prepared to perform CPR if they become unresponsive.",
                iconRes = R.drawable.ic_visibility,
                stepType = StepType.CHECK,
                isCritical = true,
                tips = listOf(
                    "Stay with the person",
                    "Keep them calm and still"
                ),
                warnings = listOf(
                    "Don't give food or drink",
                    "Be ready to start CPR if needed"
                )
            )
        )
    }

    fun getStrokeGuide(): List<GuideStep> {
        return listOf(
            GuideStep(
                id = "stroke_step_1",
                guideId = "stroke_guide",
                stepNumber = 1,
                title = "Recognize Stroke - FAST Test",
                description = "Check for Face droop, Arm weakness, Speech difficulty",
                detailedInstructions = "FAST test: Face - ask them to smile, check for drooping. Arms - ask them to raise both arms, check for weakness. Speech - ask them to repeat a phrase, listen for slurring. Time - note when symptoms started.",
                iconRes = R.drawable.ic_visibility,
                stepType = StepType.CHECK,
                isCritical = true,
                duration = "30 seconds",
                tips = listOf(
                    "Any positive FAST sign indicates stroke",
                    "Note exact time symptoms began"
                ),
                warnings = listOf("Time is critical - don't delay")
            ),
            GuideStep(
                id = "stroke_step_2",
                guideId = "stroke_guide",
                stepNumber = 2,
                title = "Call Emergency Services",
                description = "Call 911 immediately if any FAST signs are positive",
                detailedInstructions = "If any sign of the FAST test is positive, call emergency services immediately. Time is critical for stroke treatment effectiveness.",
                iconRes = R.drawable.ic_phone,
                stepType = StepType.CALL,
                isCritical = true,
                duration = "Immediately",
                warnings = listOf("Don't drive them yourself - wait for EMS")
            ),
            GuideStep(
                id = "stroke_step_3",
                guideId = "stroke_guide",
                stepNumber = 3,
                title = "Position and Comfort",
                description = "Keep person calm, lay down with head slightly elevated",
                detailedInstructions = "Keep the person calm and still. Lay them down with head and shoulders slightly elevated. Cover with a blanket to keep warm.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                tips = listOf(
                    "Keep them comfortable and reassured",
                    "Loosen tight clothing"
                ),
                warnings = listOf(
                    "Don't give food, drink, or medication",
                    "Don't let them sleep or become inactive"
                )
            )
        )
    }

    fun getSevereBleedingGuide(): List<GuideStep> {
        return listOf(
            GuideStep(
                id = "bleeding_step_1",
                guideId = "severe_bleeding_guide",
                stepNumber = 1,
                title = "Protect Yourself",
                description = "Put on gloves if available to protect from bloodborne pathogens",
                detailedInstructions = "Before treating severe bleeding, protect yourself by wearing gloves if available. This prevents exposure to bloodborne pathogens.",
                iconRes = R.drawable.ic_safety,
                stepType = StepType.ACTION,
                duration = "10 seconds",
                requiredTools = listOf("Disposable gloves"),
                tips = listOf("Use any barrier available if no gloves"),
                warnings = listOf("Your safety is important too")
            ),
            GuideStep(
                id = "bleeding_step_2",
                guideId = "severe_bleeding_guide",
                stepNumber = 2,
                title = "Apply Direct Pressure",
                description = "Apply firm, direct pressure to wound with clean cloth",
                detailedInstructions = "Expose the wound and apply firm, direct pressure with a clean cloth or sterile gauze. Press firmly directly on the wound to stop bleeding.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                isCritical = true,
                duration = "Continuous",
                requiredTools = listOf("Clean cloth", "Sterile gauze", "Bandages"),
                tips = listOf(
                    "Don't peek under cloth to check bleeding",
                    "Add more cloth on top if blood soaks through"
                ),
                warnings = listOf(
                    "Don't remove embedded objects",
                    "Don't press on chest or eye wounds"
                )
            ),
            GuideStep(
                id = "bleeding_step_3",
                guideId = "severe_bleeding_guide",
                stepNumber = 3,
                title = "Elevate if Possible",
                description = "Elevate injured area above heart level to slow bleeding",
                detailedInstructions = "If possible and no broken bones are suspected, elevate the injured area above heart level to help slow bleeding by reducing blood flow to the area.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                tips = listOf("Only elevate if no fractures suspected"),
                warnings = listOf("Don't move if spinal injury possible")
            ),
            GuideStep(
                id = "bleeding_step_4",
                guideId = "severe_bleeding_guide",
                stepNumber = 4,
                title = "Treat for Shock",
                description = "Keep person lying down, cover to prevent shock",
                detailedInstructions = "Keep the person lying down and cover them with a blanket to maintain body temperature and prevent shock. Continue pressure on wound.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                isCritical = true,
                requiredTools = listOf("Blanket"),
                tips = listOf("Keep them calm and reassured"),
                warnings = listOf("Call EMS if bleeding won't stop after 10 minutes")
            )
        )
    }

    fun getBurnsGuide(): List<GuideStep> {
        return listOf(
            GuideStep(
                id = "burns_step_1",
                guideId = "burns_guide",
                stepNumber = 1,
                title = "Stop the Burning Process",
                description = "Remove person from heat source immediately",
                detailedInstructions = "Remove the person from the heat source immediately. For electrical burns, turn off power first. For chemical burns, brush off dry chemicals before flushing.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                isCritical = true,
                duration = "Immediately",
                tips = listOf("Your safety first - ensure area is safe"),
                warnings = listOf(
                    "Don't touch electrical victim while power is on",
                    "Wear protection for chemical burns"
                )
            ),
            GuideStep(
                id = "burns_step_2",
                guideId = "burns_guide",
                stepNumber = 2,
                title = "Remove Jewelry and Clothing",
                description = "Remove jewelry and loose clothing before swelling starts",
                detailedInstructions = "Remove any jewelry or tight clothing near the burn area before swelling begins. Don't remove clothing that is stuck to the burn.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                duration = "30 seconds",
                warnings = listOf("Don't remove clothing stuck to burn")
            ),
            GuideStep(
                id = "burns_step_3",
                guideId = "burns_guide",
                stepNumber = 3,
                title = "Cool the Burn",
                description = "Run cool (not cold) water over burn for 20 minutes",
                detailedInstructions = "Cool the burn with cool (not cold) running water for at least 20 minutes. This helps reduce pain, swelling, and tissue damage.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                duration = "20 minutes",
                requiredTools = listOf("Cool water"),
                tips = listOf(
                    "Cool water helps reduce pain and damage",
                    "Don't break blisters if they form"
                ),
                warnings = listOf(
                    "Don't use ice or very cold water",
                    "Don't apply butter, oil, or home remedies"
                )
            ),
            GuideStep(
                id = "burns_step_4",
                guideId = "burns_guide",
                stepNumber = 4,
                title = "Cover and Protect",
                description = "Cover with sterile, non-fluffy dressing",
                detailedInstructions = "For minor burns, cover loosely with sterile cling film or clean non-fluffy dressing. For severe burns, cover with clean cloth and call emergency services.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                requiredTools = listOf("Sterile dressing", "Cling film"),
                warnings = listOf(
                    "Don't use adhesive dressings on burns",
                    "Call EMS for large or deep burns"
                )
            )
        )
    }

    fun getFracturesGuide(): List<GuideStep> {
        return listOf(
            GuideStep(
                id = "fracture_step_1",
                guideId = "fractures_guide",
                stepNumber = 1,
                title = "Treat Bleeding First",
                description = "Control any bleeding before addressing the fracture",
                detailedInstructions = "If there is bleeding associated with the fracture, control it first by applying direct pressure around the wound, avoiding the broken bone area.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                isCritical = true,
                tips = listOf("Bleeding takes priority over fracture"),
                warnings = listOf("Don't press on protruding bone")
            ),
            GuideStep(
                id = "fracture_step_2",
                guideId = "fractures_guide",
                stepNumber = 2,
                title = "Keep Person Still",
                description = "Prevent unnecessary movement to avoid further injury",
                detailedInstructions = "Keep the person still and calm. Don't move them unless absolutely necessary for safety. Movement can worsen the injury.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                isCritical = true,
                tips = listOf("Reassure the person"),
                warnings = listOf("Don't move unless in immediate danger")
            ),
            GuideStep(
                id = "fracture_step_3",
                guideId = "fractures_guide",
                stepNumber = 3,
                title = "Support the Injury",
                description = "Support broken bone in position found, don't realign",
                detailedInstructions = "Support the broken bone in the position you found it. Don't try to realign or push protruding bones back in. Use padding around the injury for comfort.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                requiredTools = listOf("Padding", "Pillows", "Blankets"),
                tips = listOf("Keep bone in position found"),
                warnings = listOf("Don't try to realign bones")
            ),
            GuideStep(
                id = "fracture_step_4",
                guideId = "fractures_guide",
                stepNumber = 4,
                title = "Apply Splint if Trained",
                description = "Immobilize with splint above and below fracture",
                detailedInstructions = "If trained, apply a splint using a rigid board or rolled newspaper. Secure above and below the fracture site. Apply ice pack wrapped in cloth to reduce swelling.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                requiredTools = listOf("Rigid board", "Bandages", "Ice pack", "Cloth"),
                tips = listOf("Splint above and below fracture"),
                warnings = listOf("Don't splint if untrained or causes more pain")
            )
        )
    }

    fun getSprainsStrainsGuide(): List<GuideStep> {
        return listOf(
            GuideStep(
                id = "sprain_step_1",
                guideId = "sprains_strains_guide",
                stepNumber = 1,
                title = "Rest the Injury",
                description = "Stop using injured joint and have person rest",
                detailedInstructions = "Have the person stop using the injured joint immediately and rest. Continued use can worsen the injury and delay healing.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                duration = "Ongoing",
                tips = listOf("Complete rest is important for healing")
            ),
            GuideStep(
                id = "sprain_step_2",
                guideId = "sprains_strains_guide",
                stepNumber = 2,
                title = "Apply Ice",
                description = "Apply ice pack wrapped in cloth for up to 10 minutes",
                detailedInstructions = "Apply an ice pack wrapped in cloth to the injured area for up to 10 minutes. This helps reduce swelling and pain.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                duration = "10 minutes",
                requiredTools = listOf("Ice pack", "Cloth or towel"),
                tips = listOf("Ice helps reduce swelling and pain"),
                warnings = listOf("Don't apply ice directly to skin")
            ),
            GuideStep(
                id = "sprain_step_3",
                guideId = "sprains_strains_guide",
                stepNumber = 3,
                title = "Compress and Elevate",
                description = "Wrap with elastic bandage and elevate limb",
                detailedInstructions = "Compress the injury with an elastic bandage (not too tight) and elevate the limb above heart level if possible to reduce swelling.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                requiredTools = listOf("Elastic bandage", "Pillows for elevation"),
                tips = listOf("Elevation helps reduce swelling"),
                warnings = listOf("Don't wrap too tight - check for numbness/tingling")
            )
        )
    }

    fun getShockGuide(): List<GuideStep> {
        return listOf(
            GuideStep(
                id = "shock_step_1",
                guideId = "shock_guide",
                stepNumber = 1,
                title = "Call Emergency Services",
                description = "Call 911 immediately if shock is suspected",
                detailedInstructions = "If person shows signs of shock (pale skin, cold sweat, rapid pulse, altered consciousness), call emergency services immediately.",
                iconRes = R.drawable.ic_phone,
                stepType = StepType.CALL,
                isCritical = true,
                duration = "Immediately",
                tips = listOf("Shock is life-threatening"),
                warnings = listOf("Don't delay calling for help")
            ),
            GuideStep(
                id = "shock_step_2",
                guideId = "shock_guide",
                stepNumber = 2,
                title = "Position on Back",
                description = "Lay person flat on their back",
                detailedInstructions = "Lay the person flat on their back. If no head, neck, or spinal injury is suspected, elevate their legs about 12 inches to improve blood flow to vital organs.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                tips = listOf("Elevation helps blood flow to heart and brain"),
                warnings = listOf("Don't elevate legs if spinal injury suspected")
            ),
            GuideStep(
                id = "shock_step_3",
                guideId = "shock_guide",
                stepNumber = 3,
                title = "Treat Underlying Cause",
                description = "Control bleeding and treat the cause of shock",
                detailedInstructions = "Control any bleeding and treat the underlying cause of shock (injury, allergic reaction, etc.). Cover with blanket to maintain body temperature.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                isCritical = true,
                requiredTools = listOf("Blanket", "First aid supplies"),
                tips = listOf("Keep them warm but not overheated"),
                warnings = listOf(
                    "Don't give food or drink - they may vomit",
                    "Be ready to perform CPR if breathing stops"
                )
            )
        )
    }

    fun getHypothermiaGuide(): List<GuideStep> {
        return listOf(
            GuideStep(
                id = "hypothermia_step_1",
                guideId = "hypothermia_guide",
                stepNumber = 1,
                title = "Move to Warm Place",
                description = "Get person out of cold into warm environment",
                detailedInstructions = "Move the person out of the cold environment into a warm place immediately. Shelter them from wind and cold.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                isCritical = true,
                duration = "Immediately",
                tips = listOf("Any warm shelter is better than cold exposure")
            ),
            GuideStep(
                id = "hypothermia_step_2",
                guideId = "hypothermia_guide",
                stepNumber = 2,
                title = "Remove Wet Clothing",
                description = "Replace wet clothes with dry, warm layers",
                detailedInstructions = "Remove any wet clothing and replace with dry, warm layers or blankets. Cover the head as significant heat is lost through the head.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                requiredTools = listOf("Dry clothing", "Warm blankets"),
                tips = listOf("Cover head to prevent heat loss"),
                warnings = listOf("Be gentle - hypothermic people are fragile")
            ),
            GuideStep(
                id = "hypothermia_step_3",
                guideId = "hypothermia_guide",
                stepNumber = 3,
                title = "Gradual Rewarming",
                description = "Warm center of body first with warm compresses",
                detailedInstructions = "Warm the center of body first using warm (not hot) compresses on neck, chest, and groin. Provide warm, non-alcoholic drinks if fully conscious.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                requiredTools = listOf("Warm compresses", "Warm drinks"),
                tips = listOf("Warm drinks help if person is alert"),
                warnings = listOf(
                    "Don't rewarm too rapidly - no hot baths",
                    "Don't rub or massage extremities",
                    "No alcohol or caffeine"
                )
            )
        )
    }

    fun getHeatEmergenciesGuide(): List<GuideStep> {
        return listOf(
            GuideStep(
                id = "heat_step_1",
                guideId = "heat_emergencies_guide",
                stepNumber = 1,
                title = "Move to Cool Place",
                description = "Get person out of heat into cool, shaded area",
                detailedInstructions = "Move the person to a cool, shaded place immediately. Have them lie down and remove excess clothing to help cooling.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                isCritical = true,
                duration = "Immediately",
                tips = listOf("Any shade or air conditioning helps"),
                warnings = listOf("Don't delay - heat emergencies can be fatal")
            ),
            GuideStep(
                id = "heat_step_2",
                guideId = "heat_emergencies_guide",
                stepNumber = 2,
                title = "Cool the Person",
                description = "Apply cool water, fan, or ice packs to cool body",
                detailedInstructions = "For heat exhaustion: apply cool, wet cloths and fan. For heatstroke: use aggressive cooling with ice packs on neck, armpits, and groin, or immerse in cool water if possible.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                isCritical = true,
                requiredTools = listOf("Cool water", "Ice packs", "Fan", "Wet cloths"),
                tips = listOf(
                    "Heatstroke requires aggressive cooling",
                    "Stop cooling if person starts shivering"
                ),
                warnings = listOf("Call 911 immediately for heatstroke symptoms")
            ),
            GuideStep(
                id = "heat_step_3",
                guideId = "heat_emergencies_guide",
                stepNumber = 3,
                title = "Give Fluids if Alert",
                description = "Provide cool water or electrolyte drinks if conscious",
                detailedInstructions = "If the person is fully conscious and not vomiting, give sips of cool water or electrolyte drinks. No alcohol or caffeine.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                requiredTools = listOf("Cool water", "Electrolyte drinks"),
                tips = listOf("Small sips are better than large amounts"),
                warnings = listOf(
                    "Don't give fluids if unconscious or vomiting",
                    "No alcohol or caffeinated drinks"
                )
            )
        )
    }

    fun getSeizuresGuide(): List<GuideStep> {
        return listOf(
            GuideStep(
                id = "seizure_step_1",
                guideId = "seizures_guide",
                stepNumber = 1,
                title = "Stay Calm and Time Seizure",
                description = "Remain calm, ensure safety, and time the seizure duration",
                detailedInstructions = "Stay calm and note the time the seizure started. Most seizures last 1-3 minutes. Remove nearby hazards like sharp objects or furniture.",
                iconRes = R.drawable.ic_visibility,
                stepType = StepType.CHECK,
                isCritical = true,
                duration = "Ongoing",
                tips = listOf(
                    "Most seizures stop on their own",
                    "Timing helps medical professionals"
                ),
                warnings = listOf("Don't restrain the person")
            ),
            GuideStep(
                id = "seizure_step_2",
                guideId = "seizures_guide",
                stepNumber = 2,
                title = "Position Safely",
                description = "Lay person on their side, cushion head",
                detailedInstructions = "Gently lay the person on their side in recovery position to keep airway clear. Cushion their head with something soft like a pillow or folded jacket.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                requiredTools = listOf("Pillow", "Soft padding"),
                tips = listOf(
                    "Side position prevents choking",
                    "Loosen tight clothing around neck"
                ),
                warnings = listOf(
                    "Never put anything in their mouth",
                    "Don't hold them down"
                )
            ),
            GuideStep(
                id = "seizure_step_3",
                guideId = "seizures_guide",
                stepNumber = 3,
                title = "Monitor and Reassure",
                description = "Stay with person, monitor breathing, provide reassurance",
                detailedInstructions = "Stay with the person throughout the seizure. After it ends, remain calm and reassuring as they may be confused. Turn head to side if they vomit.",
                iconRes = R.drawable.ic_visibility,
                stepType = StepType.CHECK,
                tips = listOf(
                    "They may be confused after seizure",
                    "Speak calmly and reassuringly"
                ),
                warnings = listOf("Don't give food or drink until fully alert")
            )
        )
    }

    fun getPoisoningGuide(): List<GuideStep> {
        return listOf(
            GuideStep(
                id = "poison_step_1",
                guideId = "poisoning_guide",
                stepNumber = 1,
                title = "Remove from Source",
                description = "Get person away from poison source immediately",
                detailedInstructions = "For ingested poisons: remove any remaining substance from mouth. For inhaled poisons: get person into fresh air immediately. Avoid contaminating yourself.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                isCritical = true,
                duration = "Immediately",
                tips = listOf(
                    "Your safety first - don't become a victim",
                    "Use gloves or mask if dealing with chemicals"
                ),
                warnings = listOf("Don't induce vomiting unless instructed")
            ),
            GuideStep(
                id = "poison_step_2",
                guideId = "poisoning_guide",
                stepNumber = 2,
                title = "Call Poison Control",
                description = "Call Poison Control Center or 911 immediately",
                detailedInstructions = "Call Poison Control Center (1-800-222-1222 in US) or emergency services immediately. Have the poison container available to read ingredients to the specialist.",
                iconRes = R.drawable.ic_phone,
                stepType = StepType.CALL,
                isCritical = true,
                duration = "Immediately",
                requiredTools = listOf("Phone", "Poison container/label"),
                tips = listOf(
                    "Have poison container ready for information",
                    "Follow specialist's exact instructions"
                ),
                warnings = listOf("Don't give antidotes unless instructed")
            ),
            GuideStep(
                id = "poison_step_3",
                guideId = "poisoning_guide",
                stepNumber = 3,
                title = "Monitor and Support",
                description = "Monitor breathing and consciousness, be ready for CPR",
                detailedInstructions = "Monitor the person's breathing and consciousness. If they become unconscious or stop breathing, be prepared to perform CPR. Position them to prevent choking if they vomit.",
                iconRes = R.drawable.ic_visibility,
                stepType = StepType.CHECK,
                isCritical = true,
                tips = listOf("Recovery position helps if they vomit"),
                warnings = listOf(
                    "Don't give food or drink unless told",
                    "Be ready to start CPR if needed"
                )
            )
        )
    }

    fun getBitesStingsGuide(): List<GuideStep> {
        return listOf(
            GuideStep(
                id = "bites_step_1",
                guideId = "bites_stings_guide",
                stepNumber = 1,
                title = "Remove Stinger if Present",
                description = "For bee stings, scrape out stinger - don't pinch",
                detailedInstructions = "For insect stings, remove the stinger by scraping it out with a credit card or fingernail. Don't pinch or squeeze as this can inject more venom.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                duration = "30 seconds",
                requiredTools = listOf("Credit card", "Fingernail"),
                tips = listOf("Scraping is better than pinching"),
                warnings = listOf("Don't squeeze the stinger")
            ),
            GuideStep(
                id = "bites_step_2",
                guideId = "bites_stings_guide",
                stepNumber = 2,
                title = "Clean and Cool",
                description = "Wash area and apply cold pack to reduce swelling",
                detailedInstructions = "Wash the bite or sting area with soap and water. Apply a cold pack wrapped in cloth to reduce swelling and pain. For venomous bites, keep the person still.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                requiredTools = listOf("Soap", "Water", "Cold pack", "Cloth"),
                tips = listOf(
                    "Cold helps reduce swelling",
                    "Keep venomous bite below heart level"
                ),
                warnings = listOf(
                    "Don't apply ice directly to venomous bites",
                    "Don't cut wound or suck out venom"
                )
            ),
            GuideStep(
                id = "bites_step_3",
                guideId = "bites_stings_guide",
                stepNumber = 3,
                title = "Monitor for Allergic Reaction",
                description = "Watch for signs of allergic reaction or systemic symptoms",
                detailedInstructions = "Monitor for signs of allergic reaction: difficulty breathing, swelling beyond bite area, hives, or nausea. For snake bites, watch for systemic symptoms.",
                iconRes = R.drawable.ic_visibility,
                stepType = StepType.CHECK,
                isCritical = true,
                tips = listOf("Allergic reactions can develop quickly"),
                warnings = listOf("Call 911 for any systemic symptoms or snake bites")
            )
        )
    }

    fun getAllergicReactionsGuide(): List<GuideStep> {
        return listOf(
            GuideStep(
                id = "allergy_step_1",
                guideId = "allergic_reactions_guide",
                stepNumber = 1,
                title = "Recognize Anaphylaxis",
                description = "Look for severe allergic reaction signs: breathing difficulty, swelling",
                detailedInstructions = "Signs of anaphylaxis include difficulty breathing, swelling of face/tongue/throat, widespread hives, rapid pulse, dizziness, or loss of consciousness.",
                iconRes = R.drawable.ic_visibility,
                stepType = StepType.CHECK,
                isCritical = true,
                duration = "30 seconds",
                tips = listOf(
                    "Anaphylaxis can develop rapidly",
                    "Multiple body systems are affected"
                ),
                warnings = listOf("Anaphylaxis is life-threatening")
            ),
            GuideStep(
                id = "allergy_step_2",
                guideId = "allergic_reactions_guide",
                stepNumber = 2,
                title = "Use Epinephrine Auto-Injector",
                description = "Inject EpiPen immediately into outer thigh",
                detailedInstructions = "If person has an epinephrine auto-injector (EpiPen), inject immediately into outer thigh. Hold firmly for 3 seconds, then massage the area.",
                iconRes = R.drawable.ic_medication,
                stepType = StepType.ACTION,
                isCritical = true,
                duration = "Immediately",
                requiredTools = listOf("EpiPen or epinephrine auto-injector"),
                tips = listOf(
                    "Can inject through clothing",
                    "Hold firmly for 3 seconds"
                ),
                warnings = listOf("Don't wait - inject immediately")
            ),
            GuideStep(
                id = "allergy_step_3",
                guideId = "allergic_reactions_guide",
                stepNumber = 3,
                title = "Call 911 and Position",
                description = "Call emergency services and position person properly",
                detailedInstructions = "Call 911 immediately. If breathing is difficult, keep person upright. If no breathing problems, lay down with legs elevated. Be ready for second injection after 5-10 minutes.",
                iconRes = R.drawable.ic_phone,
                stepType = StepType.CALL,
                isCritical = true,
                tips = listOf(
                    "Always call 911 even after using EpiPen",
                    "Second injection may be needed"
                ),
                warnings = listOf("Be ready to perform CPR if they collapse")
            )
        )
    }

    fun getAsthmaAttackGuide(): List<GuideStep> {
        return listOf(
            GuideStep(
                id = "asthma_step_1",
                guideId = "asthma_attack_guide",
                stepNumber = 1,
                title = "Keep Person Upright and Calm",
                description = "Help person sit upright and stay calm",
                detailedInstructions = "Help the person sit upright in a comfortable position. Stay calm yourself and reassure them. Panic can worsen breathing difficulties.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                tips = listOf(
                    "Sitting upright helps breathing",
                    "Your calmness helps them stay calm"
                ),
                warnings = listOf("Don't lay them down")
            ),
            GuideStep(
                id = "asthma_step_2",
                guideId = "asthma_attack_guide",
                stepNumber = 2,
                title = "Assist with Inhaler",
                description = "Help them use quick-relief inhaler properly",
                detailedInstructions = "If they have a quick-relief inhaler, help them use it: shake inhaler, attach spacer if available, have them exhale fully, then inhale slowly while pressing inhaler. Give 2 puffs, 1 minute apart.",
                iconRes = R.drawable.ic_medication,
                stepType = StepType.ACTION,
                requiredTools = listOf("Quick-relief inhaler", "Spacer if available"),
                tips = listOf(
                    "Shake inhaler before use",
                    "Slow, deep inhalation is important"
                ),
                warnings = listOf("Don't exceed recommended doses")
            ),
            GuideStep(
                id = "asthma_step_3",
                guideId = "asthma_attack_guide",
                stepNumber = 3,
                title = "Monitor and Repeat if Needed",
                description = "Monitor breathing and give additional doses if directed",
                detailedInstructions = "Monitor their breathing and response. If no improvement after first dose, give additional doses as directed on inhaler (usually up to 3 total doses).",
                iconRes = R.drawable.ic_visibility,
                stepType = StepType.CHECK,
                tips = listOf("Keep track of doses given"),
                warnings = listOf("Call 911 if no improvement after 1-2 doses")
            )
        )
    }

    fun getDiabeticEmergenciesGuide(): List<GuideStep> {
        return listOf(
            GuideStep(
                id = "diabetic_step_1",
                guideId = "diabetic_emergencies_guide",
                stepNumber = 1,
                title = "Assess Consciousness Level",
                description = "Check if person is conscious and able to swallow",
                detailedInstructions = "Determine if the person is conscious and alert enough to swallow safely. If unconscious or unable to swallow, place in recovery position and call 911.",
                iconRes = R.drawable.ic_visibility,
                stepType = StepType.CHECK,
                isCritical = true,
                duration = "30 seconds",
                tips = listOf("When in doubt, treat for low blood sugar"),
                warnings = listOf("Never give anything by mouth to unconscious person")
            ),
            GuideStep(
                id = "diabetic_step_2",
                guideId = "diabetic_emergencies_guide",
                stepNumber = 2,
                title = "Give Quick-Acting Sugar",
                description = "Provide 15-20g of quick-acting carbohydrate if conscious",
                detailedInstructions = "If conscious and able to swallow, give 15-20 grams of quick-acting carbohydrate: 4 oz juice or soda, glucose tablets/gel, or candy.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                requiredTools = listOf("Juice", "Glucose tablets", "Candy", "Regular soda"),
                tips = listOf(
                    "Glucose tablets work fastest",
                    "Regular soda works well too"
                ),
                warnings = listOf("Don't give diet drinks - need real sugar")
            ),
            GuideStep(
                id = "diabetic_step_3",
                guideId = "diabetic_emergencies_guide",
                stepNumber = 3,
                title = "Recheck and Repeat",
                description = "Wait 10-15 minutes, recheck, repeat if needed",
                detailedInstructions = "Wait 10-15 minutes and recheck their condition. If no improvement, repeat the sugar dose. If they have glucagon and someone is trained, assist with injection.",
                iconRes = R.drawable.ic_visibility,
                stepType = StepType.CHECK,
                duration = "10-15 minutes",
                requiredTools = listOf("Glucagon kit if available"),
                tips = listOf("Improvement should occur within 15 minutes"),
                warnings = listOf("Call 911 if no improvement or if unconscious")
            )
        )
    }

    fun getDrowningGuide(): List<GuideStep> {
        return listOf(
            GuideStep(
                id = "drowning_step_1",
                guideId = "drowning_guide",
                stepNumber = 1,
                title = "Ensure Scene Safety",
                description = "Only attempt rescue if safe - don't become another victim",
                detailedInstructions = "Assess the water conditions and your swimming ability. Only enter water if you are a strong swimmer and it's safe. Use reaching aids, throwing aids, or call for professional help if needed.",
                iconRes = R.drawable.ic_safety,
                stepType = StepType.CHECK,
                isCritical = true,
                duration = "30 seconds",
                tips = listOf("Reach or throw, don't go unless trained"),
                warnings = listOf("Don't put yourself at risk - you can't help if you become a victim")
            ),
            GuideStep(
                id = "drowning_step_2",
                guideId = "drowning_guide",
                stepNumber = 2,
                title = "Check Responsiveness and Breathing",
                description = "Once out of water, immediately check if person is responsive and breathing",
                detailedInstructions = "Once the person is safely out of water, immediately check for responsiveness and normal breathing. Look, listen, and feel for breathing for no more than 10 seconds.",
                iconRes = R.drawable.ic_visibility,
                stepType = StepType.CHECK,
                isCritical = true,
                duration = "10 seconds",
                tips = listOf("Water may come out of mouth - this is normal"),
                warnings = listOf("Don't waste time trying to drain water from lungs")
            ),
            GuideStep(
                id = "drowning_step_3",
                guideId = "drowning_guide",
                stepNumber = 3,
                title = "Begin CPR if Not Breathing",
                description = "Start with 5 rescue breaths, then 30 compressions",
                detailedInstructions = "If not breathing, begin CPR starting with 5 initial rescue breaths (head tilt, pinch nose), then proceed with 30 chest compressions followed by 2 breaths. Continue until person breathes or EMS arrives.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                isCritical = true,
                tips = listOf(
                    "Drowning victims need oxygen first",
                    "Be ready to clear water from airway"
                ),
                warnings = listOf("Even if they recover, seek medical evaluation")
            )
        )
    }

    fun getNosebleedsGuide(): List<GuideStep> {
        return listOf(
            GuideStep(
                id = "nosebleed_step_1",
                guideId = "nosebleeds_guide",
                stepNumber = 1,
                title = "Sit and Lean Forward",
                description = "Have person sit down and lean forward to prevent swallowing blood",
                detailedInstructions = "Have the person sit down and lean slightly forward. Keep head above heart level. This prevents blood from going down the throat.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                tips = listOf("Leaning forward prevents swallowing blood"),
                warnings = listOf("Don't tilt head back or lie down")
            ),
            GuideStep(
                id = "nosebleed_step_2",
                guideId = "nosebleeds_guide",
                stepNumber = 2,
                title = "Pinch Soft Part of Nose",
                description = "Pinch soft part below bony bridge firmly for 10-15 minutes",
                detailedInstructions = "Ask them to pinch the soft part of nose (just below bony bridge) firmly with thumb and index finger for 10-15 minutes without letting go. Breathe through mouth.",
                iconRes = R.drawable.ic_action,
                stepType = StepType.ACTION,
                duration = "10-15 minutes",
                tips = listOf(
                    "Don't peek - maintain pressure for full time",
                    "Breathe through mouth while pinching"
                ),
                warnings = listOf("Don't pack tissue deep into nose")
            ),
            GuideStep(
                id = "nosebleed_step_3",
                guideId = "nosebleeds_guide",
                stepNumber = 3,
                title = "Check and Repeat if Needed",
                description = "After 15 minutes, check bleeding - repeat if necessary",
                detailedInstructions = "After 10-15 minutes, release pressure and check if bleeding has stopped. If still bleeding, pinch again for another 5-10 minutes.",
                iconRes = R.drawable.ic_visibility,
                stepType = StepType.CHECK,
                duration = "5-10 minutes",
                tips = listOf("Most nosebleeds stop within 20 minutes"),
                warnings = listOf("Seek help if bleeding is very heavy or won't stop after 30 minutes")
            )
        )
    }
}
