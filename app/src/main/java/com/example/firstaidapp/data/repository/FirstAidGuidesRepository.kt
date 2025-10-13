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
}
