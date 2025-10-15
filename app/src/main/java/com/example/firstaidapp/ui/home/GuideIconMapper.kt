package com.example.firstaidapp.ui.home

import com.example.firstaidapp.R

/**
 * Maps first aid guide titles to their corresponding drawable icons
 * This makes guides more accessible for rural users who may not know medical terminology
 */
object GuideIconMapper {

    private val iconMap = mapOf(
        // Life-Threatening Emergencies
        "CPR" to R.drawable.ic_cpr,
        "Choking" to R.drawable.ic_choking,
        "Heart Attack" to R.drawable.ic_heart_attack,
        "Stroke" to R.drawable.ic_stroke,
        "Drowning" to R.drawable.ic_drowning,

        // Trauma & Injuries
        "Severe Bleeding" to R.drawable.ic_bleeding,
        "Burns" to R.drawable.ic_burns,
        "Fractures" to R.drawable.ic_fracture,
        "Sprains and Strains" to R.drawable.ic_sprain,
        "Eye Injuries" to R.drawable.ic_eye_injury,
        "Nosebleeds" to R.drawable.ic_nosebleed,

        // Medical Conditions
        "Allergic Reactions" to R.drawable.ic_allergic_reaction,
        "Asthma Attack" to R.drawable.ic_asthma,
        "Diabetic Emergencies" to R.drawable.ic_diabetes,
        "Seizures" to R.drawable.ic_seizure,
        "Poisoning" to R.drawable.ic_poisoning,
        "Shock" to R.drawable.ic_shock,

        // Environmental Emergencies
        "Hypothermia" to R.drawable.ic_hypothermia,
        "Heat Exhaustion" to R.drawable.ic_heat_exhaustion,
        "Bites and Stings" to R.drawable.ic_bites_stings
    )

    /**
     * Get the appropriate icon for a first aid guide
     * Falls back to a generic medical icon if specific icon not found
     */
    fun getIconForGuide(guideTitle: String): Int {
        // Try exact match first
        iconMap[guideTitle]?.let { return it }

        // Try partial matching for guides with complex names
        iconMap.forEach { (key, icon) ->
            if (guideTitle.contains(key, ignoreCase = true)) {
                return icon
            }
        }

        // Default fallback icon
        return R.drawable.ic_medical_default
    }
}
