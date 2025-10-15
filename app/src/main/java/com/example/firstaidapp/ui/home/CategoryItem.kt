package com.example.firstaidapp.ui.home

import com.example.firstaidapp.data.models.FirstAidGuide

/**
 * Sealed class representing different types of items in the categorized list
 */
sealed class CategoryItem {
    data class CategoryHeader(
        val title: String,
        val icon: String,
        val description: String,
        val isExpanded: Boolean = false,
        val guideCount: Int = 0
    ) : CategoryItem()

    data class GuideItem(
        val guide: FirstAidGuide
    ) : CategoryItem()
}
