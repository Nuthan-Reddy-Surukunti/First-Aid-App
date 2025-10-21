package com.example.firstaidapp.utils

import com.example.firstaidapp.R

/**
 * Maps guide steps to their corresponding illustration images.
 * Since we have one main image per guide (not per step), this returns the same
 * guide image for all steps within that guide.
 *
 * To add images:
 * 1. Place image files in res/drawable/ folder
 * 2. Name them according to the pattern: guide_[name].png
 *    Example: guide_cpr.png, guide_choking.png, etc.
 * 3. Update the mappings below
 */
object StepImageMapper {

    /**
     * Returns the drawable resource ID for a guide's main image.
     * The same image is shown for all steps within the guide.
     * @param guideId The ID of the guide (e.g., "cpr_guide")
     * @param stepNumber The step number (not used, kept for compatibility)
     * @return Drawable resource ID or null if no image is available
     */
    fun getStepImage(guideId: String, stepNumber: Int): Int? {
        return getGuideImage(guideId)
    }

    /**
     * Returns the drawable resource ID for a guide's main demonstration image.
     * @param guideId The ID of the guide
     * @return Drawable resource ID or null if no image is available
     */
    fun getGuideImage(guideId: String): Int? {
        return when (guideId) {
            "cpr_guide" -> R.drawable.guide_cpr
            "choking_guide" -> R.drawable.guide_choking
            "heat_exhaustion_guide" -> R.drawable.guide_heat_exhaustion
            // Add more guide images here as you provide them
            // "bleeding_guide" -> R.drawable.guide_bleeding
            // "burns_guide" -> R.drawable.guide_burns
            // "fractures_guide" -> R.drawable.guide_fractures
            // "poisoning_guide" -> R.drawable.guide_poisoning
            // "shock_guide" -> R.drawable.guide_shock
            // "heart_attack_guide" -> R.drawable.guide_heart_attack
            // "stroke_guide" -> R.drawable.guide_stroke
            // "allergic_reaction_guide" -> R.drawable.guide_allergic_reaction
            // "sprains_strains_guide" -> R.drawable.guide_sprains_strains
            // "hypothermia_guide" -> R.drawable.guide_hypothermia
            // "seizures_guide" -> R.drawable.guide_seizures
            // "bites_stings_guide" -> R.drawable.guide_bites_stings
            // "asthma_attack_guide" -> R.drawable.guide_asthma_attack
            // "diabetic_emergencies_guide" -> R.drawable.guide_diabetic_emergencies
            // "drowning_guide" -> R.drawable.guide_drowning
            // "nosebleeds_guide" -> R.drawable.guide_nosebleeds
            // "eye_injuries_guide" -> R.drawable.guide_eye_injuries
            else -> null
        }
    }

    /**
     * Returns a placeholder image if no specific guide image is available.
     */
    fun getPlaceholderImage(): Int {
        return R.drawable.ic_first_aid_placeholder
    }
}

