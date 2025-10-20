package com.example.firstaidapp.ui.home

/**
 * Maps first aid guide titles to their corresponding demonstration photos
 * This provides visual step-by-step guides for better understanding
 */
object PhotoMapper {

    private val guidePhotoMap = mapOf(
        // Life-Threatening Emergencies - Critical procedure photos
        "CPR (Cardiopulmonary Resuscitation)" to "photos/cpr_demonstration.png",
        "Choking Emergency" to "photos/choking_demonstration.jpg",
        "Heart Attack Emergency" to "photos/heart_attack_care.jpg",
        "Stroke Emergency (FAST)" to "photos/stroke_assessment.jpg",
        "Drowning" to "photos/drowning_rescue.jpg",

        // Trauma & Injuries - Medical procedure photos
        "Severe Bleeding Control" to "photos/bleeding_control.jpg",
        "Burns Treatment" to "photos/burns_treatment.jpg",
        "Broken Bones & Fractures" to "photos/fracture_care.jpg",
        "Sprains and Strains" to "photos/sprain_care.jpg",
        "Eye Injuries" to "photos/eye_injury_care.jpg",
        "Nosebleeds" to "photos/nosebleed_care.jpg",

        // Medical Conditions - Emergency response photos
        "Allergic Reactions" to "photos/allergic_reaction.jpg",
        "Asthma Attack" to "photos/asthma_care.jpg",
        "Diabetic Emergencies" to "photos/diabetic_emergency.jpg",
        "Seizures" to "photos/seizure_care.jpg",
        "Poisoning Emergency" to "photos/poisoning_care.jpg",
        "Shock" to "photos/shock_treatment.jpg",

        // Environmental Emergencies - Hazard response photos
        "Hypothermia" to "photos/hypothermia_care.jpg",
        "Heat Exhaustion and Heatstroke" to "photos/heat_exhaustion.jpg",
        "Bites and Stings" to "photos/bites_stings.jpg"
    )

    /**
     * Get the photo path for a given guide title
     */
    fun getPhotoForGuide(guideTitle: String): String? {
        return guidePhotoMap[guideTitle]
    }

    /**
     * Check if a photo exists for the given guide
     */
    fun hasPhoto(photoPath: String?): Boolean {
        return photoPath != null && guidePhotoMap.containsValue(photoPath)
    }

    /**
     * Get all available photo paths
     */
    fun getAllPhotoPaths(): List<String> {
        return guidePhotoMap.values.toList()
    }

    /**
     * Get photo name from guide title (for fallback purposes)
     */
    fun getPhotoName(guideTitle: String): String {
        return guideTitle.lowercase()
            .replace(" ", "_")
            .replace("(", "")
            .replace(")", "")
            .replace("&", "and")
            .replace("-", "_") + ".jpg"
    }
}
