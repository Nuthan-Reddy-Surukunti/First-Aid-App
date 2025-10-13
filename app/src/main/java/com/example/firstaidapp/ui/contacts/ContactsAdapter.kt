package com.example.firstaidapp.ui.contacts

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firstaidapp.R
import com.example.firstaidapp.data.models.EmergencyContact
import com.example.firstaidapp.databinding.ItemContactBinding

class ContactsAdapter(
    private val onCallClick: (EmergencyContact) -> Unit
) : ListAdapter<EmergencyContact, ContactsAdapter.ContactViewHolder>(ContactDiffCallback()) {

    private val animationHandler = Handler(Looper.getMainLooper())
    private var isInitialLoad = true
    private val animatedPositions = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        // Reset view state before binding
        holder.resetViewState()
        holder.bind(getItem(position), position)
    }

    override fun onViewAttachedToWindow(holder: ContactViewHolder) {
        super.onViewAttachedToWindow(holder)
        val position = holder.adapterPosition
        if (isInitialLoad && position != RecyclerView.NO_POSITION && !animatedPositions.contains(position)) {
            animatedPositions.add(position)
            holder.playEntranceAnimation()
        }
    }

    override fun submitList(list: List<EmergencyContact>?) {
        // Clear animated positions when new list is submitted
        animatedPositions.clear()
        super.submitList(list)
        isInitialLoad = true
        animationHandler.postDelayed({
            isInitialLoad = false
        }, (list?.size?.times(120L) ?: 0L) + 1000) // Dynamic delay based on list size
    }

    inner class ContactViewHolder(
        private val binding: ItemContactBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var emergencyPulseAnimator: AnimatorSet? = null
        private var entranceAnimator: AnimatorSet? = null
        private var isPressed = false
        private var currentContact: EmergencyContact? = null

        fun resetViewState() {
            // Cancel any running animations
            emergencyPulseAnimator?.cancel()
            entranceAnimator?.cancel()

            // Reset view properties to default state
            binding.root.apply {
                alpha = 1f
                translationX = 0f
                translationY = 0f
                scaleX = 1f
                scaleY = 1f
                rotation = 0f
                background = null
            }

            binding.btnCall.apply {
                scaleX = 1f
                scaleY = 1f
                rotation = 0f
                alpha = 1f
            }

            isPressed = false
        }

        fun bind(contact: EmergencyContact, position: Int) {
            currentContact = contact

            binding.tvContactName.text = contact.name
            binding.tvContactNumber.text = contact.phoneNumber
            binding.tvContactType.text = contact.type.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }

            // Setup enhanced call button with sophisticated animations
            setupCallButton(contact)

            // Set contact type styling and special effects
            setupContactTypeEffects(contact)

            // Add hover effect for better interactivity
            setupHoverEffect()
        }

        fun playEntranceAnimation() {
            // Ensure view is in correct initial state
            binding.root.alpha = 0f
            binding.root.translationX = 300f
            binding.root.scaleX = 0.3f
            binding.root.scaleY = 0.3f
            binding.root.rotation = 5f

            val position = adapterPosition
            if (position == RecyclerView.NO_POSITION) return

            val delay = (position * 120L) // Staggered cascade effect

            // Create entrance animation set
            entranceAnimator = AnimatorSet().apply {
                val slideIn = ObjectAnimator.ofFloat(binding.root, "translationX", 300f, 0f).apply {
                    duration = 600
                    interpolator = OvershootInterpolator(1.2f)
                }

                val scaleXAnimator = ObjectAnimator.ofFloat(binding.root, "scaleX", 0.3f, 1.1f, 1.0f).apply {
                    duration = 700
                }

                val scaleYAnimator = ObjectAnimator.ofFloat(binding.root, "scaleY", 0.3f, 1.1f, 1.0f).apply {
                    duration = 700
                }

                val rotateAnimator = ObjectAnimator.ofFloat(binding.root, "rotation", 5f, 0f).apply {
                    duration = 500
                    interpolator = DecelerateInterpolator()
                }

                val fadeIn = ObjectAnimator.ofFloat(binding.root, "alpha", 0f, 1f).apply {
                    duration = 400
                }

                playTogether(slideIn, scaleXAnimator, scaleYAnimator, rotateAnimator, fadeIn)
                startDelay = delay

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        // Start emergency pulse if applicable after entrance completes
                        currentContact?.let { contact ->
                            if (contact.type.name in listOf("EMERGENCY_SERVICE", "POISON_CONTROL")) {
                                startEmergencyPulse()
                            }
                        }
                    }
                })
            }

            entranceAnimator?.start()
        }

        private fun setupCallButton(contact: EmergencyContact) {
            binding.btnCall.setOnClickListener { view ->
                if (isPressed) return@setOnClickListener
                isPressed = true

                // Enhanced press animation
                val pressAnimator = createButtonPressAnimation()
                pressAnimator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        // Create release animation with bounce back
                        val releaseAnimator = createButtonReleaseAnimation()
                        releaseAnimator.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                isPressed = false
                                // Trigger the actual call
                                onCallClick(contact)
                            }
                        })
                        releaseAnimator.start()
                    }
                })
                pressAnimator.start()

                // Add ripple effect to the entire card
                createCardRippleEffect()
            }
        }

        private fun createButtonPressAnimation(): AnimatorSet {
            val animatorSet = AnimatorSet()

            val scaleX = ObjectAnimator.ofFloat(binding.btnCall, "scaleX", 1f, 0.92f)
            val scaleY = ObjectAnimator.ofFloat(binding.btnCall, "scaleY", 1f, 0.92f)
            val rotation = ObjectAnimator.ofFloat(binding.btnCall, "rotation", 0f, 2f)
            val alpha = ObjectAnimator.ofFloat(binding.btnCall, "alpha", 1f, 0.7f)

            animatorSet.playTogether(scaleX, scaleY, rotation, alpha)
            animatorSet.duration = 100
            animatorSet.interpolator = DecelerateInterpolator()

            return animatorSet
        }

        private fun createButtonReleaseAnimation(): AnimatorSet {
            val animatorSet = AnimatorSet()

            // Overshoot effect
            val scaleX1 = ObjectAnimator.ofFloat(binding.btnCall, "scaleX", 0.92f, 1.05f)
            val scaleY1 = ObjectAnimator.ofFloat(binding.btnCall, "scaleY", 0.92f, 1.05f)
            val rotation1 = ObjectAnimator.ofFloat(binding.btnCall, "rotation", 2f, 0f)
            val alpha1 = ObjectAnimator.ofFloat(binding.btnCall, "alpha", 0.7f, 1f)

            val firstPhase = AnimatorSet().apply {
                playTogether(scaleX1, scaleY1, rotation1, alpha1)
                duration = 150
                interpolator = OvershootInterpolator(1.5f)
            }

            // Settle back to normal
            val scaleX2 = ObjectAnimator.ofFloat(binding.btnCall, "scaleX", 1.05f, 1f)
            val scaleY2 = ObjectAnimator.ofFloat(binding.btnCall, "scaleY", 1.05f, 1f)

            val secondPhase = AnimatorSet().apply {
                playTogether(scaleX2, scaleY2)
                duration = 150
                interpolator = DecelerateInterpolator()
            }

            animatorSet.playSequentially(firstPhase, secondPhase)
            return animatorSet
        }

        private fun createCardRippleEffect() {
            // Subtle wave effect across the card
            val waveAnimator = ObjectAnimator.ofFloat(binding.root, "scaleX", 1f, 1.02f, 1f).apply {
                duration = 300
                interpolator = DecelerateInterpolator()
            }

            val waveAnimatorY = ObjectAnimator.ofFloat(binding.root, "scaleY", 1f, 1.02f, 1f).apply {
                duration = 300
                interpolator = DecelerateInterpolator()
            }

            val colorAnimator = ValueAnimator.ofArgb(
                ContextCompat.getColor(binding.root.context, android.R.color.transparent),
                ContextCompat.getColor(binding.root.context, R.color.primary_light),
                ContextCompat.getColor(binding.root.context, android.R.color.transparent)
            ).apply {
                duration = 400
                addUpdateListener { animator ->
                    val color = animator.animatedValue as Int
                    binding.root.background = ColorDrawable(color)
                }
            }

            AnimatorSet().apply {
                playTogether(waveAnimator, waveAnimatorY, colorAnimator)
                start()
            }
        }

        private fun setupContactTypeEffects(contact: EmergencyContact) {
            // Cancel previous pulse animation
            emergencyPulseAnimator?.cancel()

            when (contact.type.name) {
                "EMERGENCY_SERVICE", "POISON_CONTROL" -> {
                    binding.tvContactType.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.emergency_red)
                    )
                    // Don't start pulse immediately - wait for entrance animation if running
                    if (!isInitialLoad) {
                        startEmergencyPulse()
                    }
                }
                "MEDICAL" -> {
                    binding.tvContactType.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.severity_critical)
                    )
                }
                else -> {
                    binding.tvContactType.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.text_secondary)
                    )
                }
            }
        }

        private fun startEmergencyPulse() {
            emergencyPulseAnimator?.cancel()

            emergencyPulseAnimator = AnimatorSet().apply {
                val pulseScale = ObjectAnimator.ofFloat(binding.root, "scaleX", 1f, 1.02f).apply {
                    duration = 1500
                    repeatCount = ValueAnimator.INFINITE
                    repeatMode = ValueAnimator.REVERSE
                    interpolator = DecelerateInterpolator()
                }

                val pulseScaleY = ObjectAnimator.ofFloat(binding.root, "scaleY", 1f, 1.02f).apply {
                    duration = 1500
                    repeatCount = ValueAnimator.INFINITE
                    repeatMode = ValueAnimator.REVERSE
                    interpolator = DecelerateInterpolator()
                }

                val pulseAlpha = ObjectAnimator.ofFloat(binding.root, "alpha", 1f, 0.85f).apply {
                    duration = 1500
                    repeatCount = ValueAnimator.INFINITE
                    repeatMode = ValueAnimator.REVERSE
                    interpolator = DecelerateInterpolator()
                }

                playTogether(pulseScale, pulseScaleY, pulseAlpha)
                start()
            }
        }

        private fun setupHoverEffect() {
            // Remove any existing touch listener first
            binding.root.setOnTouchListener(null)

            // Add subtle elevation changes on touch that don't conflict with click
            binding.root.setOnTouchListener { view, event ->
                // Only handle if not during button press
                if (!isPressed) {
                    when (event.action) {
                        android.view.MotionEvent.ACTION_DOWN -> {
                            view.animate()
                                .scaleX(0.98f)
                                .scaleY(0.98f)
                                .setDuration(100)
                                .start()
                        }
                        android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                            view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(200)
                                .setInterpolator(OvershootInterpolator())
                                .start()
                        }
                    }
                }
                false // Don't consume the touch event
            }
        }

        fun cleanup() {
            emergencyPulseAnimator?.cancel()
            entranceAnimator?.cancel()
            emergencyPulseAnimator = null
            entranceAnimator = null
        }
    }

    override fun onViewDetachedFromWindow(holder: ContactViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.cleanup()
    }

    class ContactDiffCallback : DiffUtil.ItemCallback<EmergencyContact>() {
        override fun areItemsTheSame(oldItem: EmergencyContact, newItem: EmergencyContact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EmergencyContact, newItem: EmergencyContact): Boolean {
            return oldItem == newItem
        }
    }
}
