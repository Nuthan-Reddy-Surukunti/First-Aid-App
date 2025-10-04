package com.example.firstaidapp.utils

import android.content.Context
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat

/**
 * Accessibility utilities to improve app usability for users with disabilities
 * Implements WCAG 2.1 guidelines for mobile accessibility
 */
class AccessibilityHelper private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: AccessibilityHelper? = null

        fun getInstance(): AccessibilityHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AccessibilityHelper().also { INSTANCE = it }
            }
        }

        // Minimum touch target size (48dp)
        const val MIN_TOUCH_TARGET_SIZE_DP = 48

        // High contrast color ratios
        const val NORMAL_CONTRAST_RATIO = 4.5f
        const val LARGE_TEXT_CONTRAST_RATIO = 3.0f
    }

    /**
     * Sets up accessibility for emergency action buttons
     */
    fun setupEmergencyButtonAccessibility(button: View, emergencyType: String) {
        ViewCompat.setAccessibilityDelegate(button, object : androidx.core.view.AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View,
                info: AccessibilityNodeInfoCompat
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info.contentDescription = "Emergency action: $emergencyType. Double tap to call for help."
                info.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK)
                info.isClickable = true
            }
        })

        // Set important for accessibility
        ViewCompat.setImportantForAccessibility(button, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
    }

    /**
     * Sets up accessibility for guide step navigation
     */
    fun setupStepNavigationAccessibility(
        view: View,
        stepNumber: Int,
        totalSteps: Int,
        stepTitle: String
    ) {
        val contentDescription = "Step $stepNumber of $totalSteps: $stepTitle"
        ViewCompat.setAccessibilityDelegate(view, object : androidx.core.view.AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View,
                info: AccessibilityNodeInfoCompat
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info.contentDescription = contentDescription
                info.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK)
            }
        })
    }

    /**
     * Announces important updates to screen readers
     */
    fun announceForAccessibility(context: Context, message: String) {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        if (accessibilityManager.isEnabled) {
            val event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT)
            event.text.add(message)
            accessibilityManager.sendAccessibilityEvent(event)
        }
    }

    /**
     * Checks if accessibility services are enabled
     */
    fun isAccessibilityEnabled(context: Context): Boolean {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        return accessibilityManager.isEnabled
    }

    /**
     * Sets up live region for dynamic content updates
     */
    fun setupLiveRegion(view: View, mode: Int = ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE) {
        ViewCompat.setAccessibilityLiveRegion(view, mode)
    }

    /**
     * Configures heading accessibility for section titles
     */
    fun setupHeadingAccessibility(view: View, headingText: String) {
        ViewCompat.setAccessibilityDelegate(view, object : androidx.core.view.AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View,
                info: AccessibilityNodeInfoCompat
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info.isHeading = true
                info.contentDescription = "Heading: $headingText"
            }
        })
    }
}
