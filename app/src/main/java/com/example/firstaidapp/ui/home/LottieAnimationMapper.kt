package com.example.firstaidapp.ui.home

/**
 * Maps first aid guide titles to their corresponding Lottie animation files
 * This provides animated visual demonstrations for better understanding
 */
object LottieAnimationMapper {

    private val animationMap = mapOf(
        // Life-Threatening Emergencies - Critical animations
        "CPR" to "lottie/cpr_animation.json",
        "Choking" to "lottie/choking_animation.json",
        "Heart Attack" to "lottie/heart_attack_animation.json",
        "Stroke" to "lottie/stroke_animation.json",
        "Drowning" to "lottie/drowning_animation.json",

        // Trauma & Injuries - Medical procedure animations
        "Severe Bleeding" to "lottie/bleeding_animation.json",
        "Burns" to "lottie/burns_animation.json",
        "Fractures" to "lottie/fracture_animation.json",
        "Sprains and Strains" to "lottie/sprain_animation.json",
        "Eye Injuries" to "lottie/eye_injury_animation.json",
        "Nosebleeds" to "lottie/nosebleed_animation.json",

        // Medical Conditions - Health emergency animations
        "Allergic Reactions" to "lottie/allergic_reaction_animation.json",
        "Asthma Attack" to "lottie/asthma_animation.json",
        "Diabetic Emergencies" to "lottie/diabetes_animation.json",
        "Seizures" to "lottie/seizure_animation.json",
        "Poisoning" to "lottie/poisoning_animation.json",
        "Shock" to "lottie/shock_animation.json",

        // Environmental Emergencies - Environmental hazard animations
        "Hypothermia" to "lottie/hypothermia_animation.json",
        "Heat Exhaustion" to "lottie/heat_exhaustion_animation.json",
        "Bites and Stings" to "lottie/bites_stings_animation.json"
    )

    // Category header animations for visual appeal
    private val categoryAnimationMap = mapOf(
        "Life-Threatening Emergencies" to "lottie/emergency_pulse_animation.json",
        "Trauma & Injuries" to "lottie/bandage_animation.json",
        "Medical Conditions" to "lottie/medical_cross_animation.json",
        "Environmental Emergencies" to "lottie/environment_animation.json"
    )

    /**
     * Get the appropriate Lottie animation for a first aid guide
     * Falls back to a generic medical animation if specific animation not found
     */
    fun getAnimationForGuide(guideTitle: String): String? {
        // Try exact match first
        animationMap[guideTitle]?.let { return it }

        // Try partial matching for guides with complex names
        animationMap.forEach { (key, animation) ->
            if (guideTitle.contains(key, ignoreCase = true)) {
                return animation
            }
        }

        // Default fallback animation
        return "lottie/generic_medical_animation.json"
    }

    /**
     * Get the appropriate Lottie animation for category headers
     */
    fun getAnimationForCategory(categoryTitle: String): String? {
        return categoryAnimationMap[categoryTitle] ?: "lottie/generic_medical_animation.json"
    }

    /**
     * Check if animation file exists (for fallback purposes)
     */
    fun hasAnimation(animationPath: String?): Boolean {
        return !animationPath.isNullOrBlank()
    }
}
