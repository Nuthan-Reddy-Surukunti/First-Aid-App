package com.example.firstaidapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Helper class for managing YouTube video links for first aid guides
 * Provides curated educational videos for each emergency procedure
 */
object YouTubeVideoHelper {

    /**
     * Curated YouTube video links for each first aid guide
     * Videos are from verified medical/safety organizations
     */
    private val guideVideoLinks = mapOf(
        "cpr" to "https://www.youtube.com/watch?v=y3k4M3BSnRs", // Red Cross CPR Tutorial
        "choking" to "https://www.youtube.com/watch?v=7CgtIgSyAiU", // Red Cross Choking Tutorial
        "bleeding" to "https://www.youtube.com/watch?v=Vq9bL1xkf5M", // Severe Bleeding Control
        "burns" to "https://www.youtube.com/watch?v=p8NxqYLm8Ms", // Burn Treatment Guide
        "fractures" to "https://www.youtube.com/watch?v=IEh_k7XC7RA", // Fracture First Aid
        "poisoning" to "https://www.youtube.com/watch?v=jS_A9kfRgmQ", // Poisoning Emergency
        "shock" to "https://www.youtube.com/watch?v=0L26M4r2p6A", // Treating Shock
        "heart_attack" to "https://www.youtube.com/watch?v=gDwt7dD3awc", // Heart Attack Response
        "stroke" to "https://www.youtube.com/watch?v=S4z1hRCz8hk", // FAST Stroke Recognition
        "allergic_reactions" to "https://www.youtube.com/watch?v=QeC9CWwv8kI" // Allergic Reaction/Anaphylaxis
    )

    /**
     * Get YouTube video link for a specific guide
     */
    fun getVideoLinkForGuide(guideId: String): String? {
        return guideVideoLinks[guideId.lowercase().replace(" ", "_")]
    }

    /**
     * Open YouTube video in the YouTube app or browser
     */
    fun openVideo(context: Context, videoUrl: String?) {
        if (videoUrl.isNullOrEmpty()) {
            return
        }

        try {
            // Try to open in YouTube app first
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.google.android.youtube")

            context.startActivity(intent)
        } catch (e: Exception) {
            // If YouTube app is not installed, open in browser
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (browserException: Exception) {
                browserException.printStackTrace()
            }
        }
    }

    /**
     * Check if video link is available for a guide
     */
    fun hasVideoLink(guideId: String): Boolean {
        return getVideoLinkForGuide(guideId) != null
    }

    /**
     * Get all guide IDs that have video links
     */
    fun getGuidesWithVideos(): List<String> {
        return guideVideoLinks.keys.toList()
    }

    /**
     * Get video title for display
     */
    fun getVideoTitle(guideId: String): String {
        return when (guideId.lowercase().replace(" ", "_")) {
            "cpr" -> "CPR Tutorial by Red Cross"
            "choking" -> "Choking Emergency Response"
            "bleeding" -> "Severe Bleeding Control"
            "burns" -> "Burn Treatment Guide"
            "fractures" -> "Fracture First Aid"
            "poisoning" -> "Poisoning Emergency Response"
            "shock" -> "Treating Medical Shock"
            "heart_attack" -> "Heart Attack Response"
            "stroke" -> "FAST Stroke Recognition"
            "allergic_reactions" -> "Allergic Reaction & Anaphylaxis"
            else -> "Watch Tutorial Video"
        }
    }
}

