package com.example.firstaidapp.ui.home

import com.example.firstaidapp.R
import com.example.firstaidapp.data.models.FirstAidGuide

data class GuideCategory(
    val title: String,
    val icon: String, // Emoji icon
    val description: String,
    val guides: List<FirstAidGuide> = emptyList()
)

object GuideCategories {

    val categories = listOf(
        GuideCategory(
            title = "Life-Threatening Emergencies",
            icon = "🚨",
            description = "Critical situations requiring immediate action",
            guides = emptyList()
        ),
        GuideCategory(
            title = "Trauma & Injuries",
            icon = "🩹",
            description = "Physical injuries and wound care",
            guides = emptyList()
        ),
        GuideCategory(
            title = "Medical Conditions",
            icon = "💊",
            description = "Medical emergencies and health conditions",
            guides = emptyList()
        ),
        GuideCategory(
            title = "Environmental Emergencies",
            icon = "🌡️",
            description = "Weather and environmental hazards",
            guides = emptyList()
        )
    )

    private val categoryMappings = mapOf(
        "Life-Threatening Emergencies" to listOf("CPR", "Choking", "Heart Attack", "Stroke", "Drowning"),
        "Trauma & Injuries" to listOf("Severe Bleeding", "Burns", "Fractures", "Sprains and Strains", "Eye Injuries", "Nosebleeds"),
        "Medical Conditions" to listOf("Allergic Reactions", "Asthma Attack", "Diabetic Emergencies", "Seizures", "Poisoning", "Shock"),
        "Environmental Emergencies" to listOf("Hypothermia", "Heat Exhaustion", "Bites and Stings")
    )

    fun getCategorizedGuides(guides: List<FirstAidGuide>): List<GuideCategory> {
        val categoryMap = mutableMapOf<String, MutableList<FirstAidGuide>>().apply {
            categories.forEach { put(it.title, mutableListOf()) }
        }

        guides.forEach { guide ->
            categoryMappings.forEach { (categoryName, guideTitles) ->
                if (guideTitles.any { guide.title.contains(it, ignoreCase = true) }) {
                    categoryMap[categoryName]?.add(guide)
                }
            }
        }

        return categories.map {
            it.copy(guides = categoryMap[it.title] ?: emptyList())
        }.filter { it.guides.isNotEmpty() }
    }
}
