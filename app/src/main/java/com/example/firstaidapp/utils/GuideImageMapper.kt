package com.example.firstaidapp.utils

import com.example.firstaidapp.R
import com.example.firstaidapp.data.models.GuideStep
import com.example.firstaidapp.data.models.StepType

/**
 * Utility class for mapping guide steps and categories to appropriate images
 */
object GuideImageMapper {

    /**
     * Get the appropriate image resource for a guide step
     */
    fun getStepImage(step: GuideStep, guideName: String? = null): Int {
        // First check if step has specific image
        step.imageRes?.let { return it }

        // Then check by guide name/category
        guideName?.let { name ->
            getImageByGuideName(name, step)?.let { return it }
        }

        // Check by step title keywords
        getImageByStepTitle(step.title)?.let { return it }

        // Check by step type
        return getImageByStepType(step.stepType)
    }

    /**
     * Get fallback image for a step when no specific image is provided
     */
    fun getFallbackImageForStep(step: GuideStep, guideName: String? = null): Int {
        return getStepImage(step, guideName)
    }

    /**
     * Get image by guide name/category
     */
    private fun getImageByGuideName(guideName: String, step: GuideStep): Int? {
        return when {
            guideName.contains("CPR", ignoreCase = true) -> {
                when {
                    step.title.contains("check", ignoreCase = true) ||
                    step.title.contains("responsive", ignoreCase = true) -> R.drawable.cpr_check_responsiveness

                    step.title.contains("position", ignoreCase = true) ||
                    step.title.contains("hand", ignoreCase = true) -> R.drawable.cpr_hand_position

                    step.title.contains("compression", ignoreCase = true) ||
                    step.title.contains("chest", ignoreCase = true) -> R.drawable.cpr_compressions

                    step.title.contains("airway", ignoreCase = true) ||
                    step.title.contains("tilt", ignoreCase = true) -> R.drawable.cpr_positioning

                    else -> R.drawable.cpr_compression
                }
            }

            guideName.contains("Choking", ignoreCase = true) -> {
                when {
                    step.title.contains("assess", ignoreCase = true) ||
                    step.title.contains("check", ignoreCase = true) -> R.drawable.choking_assessment

                    step.title.contains("heimlich", ignoreCase = true) ||
                    step.title.contains("abdominal", ignoreCase = true) -> R.drawable.heimlich_thrusts

                    step.title.contains("position", ignoreCase = true) ||
                    step.title.contains("fist", ignoreCase = true) -> R.drawable.heimlich_fist_position

                    else -> R.drawable.heimlich_position
                }
            }

            guideName.contains("Burns", ignoreCase = true) -> {
                when {
                    step.title.contains("cool", ignoreCase = true) ||
                    step.title.contains("water", ignoreCase = true) -> R.drawable.burns_cool_water

                    else -> R.drawable.burn_cooling
                }
            }

            guideName.contains("Bleeding", ignoreCase = true) ||
            guideName.contains("Cuts", ignoreCase = true) -> {
                R.drawable.cuts_pressure
            }

            else -> null
        }
    }

    /**
     * Get image by step title keywords
     */
    private fun getImageByStepTitle(title: String): Int? {
        return when {
            title.contains("emergency", ignoreCase = true) ||
            title.contains("call 911", ignoreCase = true) ||
            title.contains("call emergency", ignoreCase = true) -> R.drawable.emergency_call

            title.contains("CPR", ignoreCase = true) ||
            title.contains("compression", ignoreCase = true) -> R.drawable.cpr_compressions

            title.contains("choking", ignoreCase = true) ||
            title.contains("heimlich", ignoreCase = true) -> R.drawable.choking_assessment

            title.contains("position", ignoreCase = true) ||
            title.contains("hand placement", ignoreCase = true) -> R.drawable.cpr_hand_position

            title.contains("burn", ignoreCase = true) ||
            title.contains("cooling", ignoreCase = true) -> R.drawable.burn_cooling

            title.contains("cut", ignoreCase = true) ||
            title.contains("bleeding", ignoreCase = true) ||
            title.contains("pressure", ignoreCase = true) -> R.drawable.cuts_pressure

            title.contains("check", ignoreCase = true) ||
            title.contains("assess", ignoreCase = true) -> R.drawable.cpr_check_responsiveness

            else -> null
        }
    }

    /**
     * Get image by step type
     */
    private fun getImageByStepType(stepType: StepType): Int {
        return when (stepType) {
            StepType.CHECK, StepType.ASSESSMENT -> R.drawable.cpr_check_responsiveness
            StepType.POSITIONING -> R.drawable.cpr_positioning
            StepType.ACTION -> R.drawable.cpr_compression
            StepType.EMERGENCY_CALL, StepType.CALL -> R.drawable.emergency_call
            StepType.MONITORING -> R.drawable.cpr_check_responsiveness
            StepType.SAFETY -> R.drawable.ic_warning
            StepType.FOLLOW_UP -> R.drawable.cpr_compression
            StepType.REPEAT -> R.drawable.cpr_compressions
        }
    }

    /**
     * Get image file path for a specific step (returns asset path for photos)
     */
    fun getImageForStep(guideName: String, stepNumber: Int, stepTitle: String): String? {
        val guideKey = guideName.lowercase().replace(" ", "_")

        return when {
            guideName.contains("CPR", ignoreCase = true) -> {
                // Use the main CPR demonstration photo for all steps since specific step images don't exist
                "photos/cpr_demonstration.png"
            }

            guideName.contains("Choking", ignoreCase = true) -> {
                // Use the choking photo for choking-related steps
                "photos/Choking.png"
            }

            guideName.contains("Burns", ignoreCase = true) -> {
                when (stepNumber) {
                    1 -> "images/burns_safety.jpg"
                    2 -> "images/burns_cool_water.jpg"
                    3 -> "images/burns_covering.jpg"
                    else -> "images/burns_general.jpg"
                }
            }

            guideName.contains("Bleeding", ignoreCase = true) ||
            guideName.contains("Cuts", ignoreCase = true) -> {
                when (stepNumber) {
                    1 -> "images/bleeding_assessment.jpg"
                    2 -> "images/bleeding_pressure.jpg"
                    3 -> "images/bleeding_bandaging.jpg"
                    else -> "images/bleeding_general.jpg"
                }
            }

            else -> null
        }
    }

    /**
     * Get category icon for guide cards
     */
    fun getCategoryIcon(category: String): Int {
        return when (category.lowercase()) {
            "cpr" -> R.drawable.ic_cpr
            "choking" -> R.drawable.ic_choking
            "burns" -> R.drawable.ic_burns
            "bleeding", "cuts" -> R.drawable.ic_bleeding
            "fractures" -> R.drawable.ic_fractures
            "allergic_reactions", "allergic reaction" -> R.drawable.ic_allergic_reaction
            "poisoning" -> R.drawable.ic_poisoning
            "shock" -> R.drawable.ic_shock
            "seizures", "seizure" -> R.drawable.ic_seizure
            "heart attack" -> R.drawable.ic_heart_attack
            "stroke" -> R.drawable.ic_stroke
            "drowning" -> R.drawable.ic_drowning
            "diabetes" -> R.drawable.ic_diabetes
            "asthma" -> R.drawable.ic_asthma
            "eye injury" -> R.drawable.ic_eye_injury
            "nosebleed" -> R.drawable.ic_nosebleed
            "sprain" -> R.drawable.ic_sprain
            "heat exhaustion" -> R.drawable.ic_heat_exhaustion
            "hypothermia" -> R.drawable.ic_hypothermia
            "bites & stings" -> R.drawable.ic_bites_stings
            "snake bite" -> R.drawable.ic_snake_bite
            else -> R.drawable.ic_medical_default
        }
    }

    /**
     * Get background image for guide detail headers
     */
    fun getGuideHeaderBackground(guideName: String): Int {
        return when {
            guideName.contains("CPR", ignoreCase = true) -> R.drawable.gradient_emergency_hero
            guideName.contains("Choking", ignoreCase = true) -> R.drawable.gradient_guide_header
            guideName.contains("Emergency", ignoreCase = true) -> R.drawable.gradient_emergency_hero
            else -> R.drawable.gradient_card_background
        }
    }
}
