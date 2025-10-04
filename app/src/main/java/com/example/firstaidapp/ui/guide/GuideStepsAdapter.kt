package com.example.firstaidapp.ui.guide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firstaidapp.data.models.GuideStep
import com.example.firstaidapp.databinding.ItemGuideStepBinding

class GuideStepsAdapter : ListAdapter<GuideStep, GuideStepsAdapter.StepViewHolder>(StepDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val binding = ItemGuideStepBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StepViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StepViewHolder(
        private val binding: ItemGuideStepBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(step: GuideStep) {
            binding.tvStepNumber.text = step.stepNumber.toString()
            binding.tvStepTitle.text = step.title
            binding.tvStepDescription.text = step.description

            if (step.duration != null) {
                binding.tvDuration.text = step.duration
                binding.tvDuration.visibility = View.VISIBLE
            } else {
                binding.tvDuration.visibility = View.GONE
            }
        }
    }

    class StepDiffCallback : DiffUtil.ItemCallback<GuideStep>() {
        override fun areItemsTheSame(oldItem: GuideStep, newItem: GuideStep): Boolean {
            return oldItem.stepNumber == newItem.stepNumber
        }

        override fun areContentsTheSame(oldItem: GuideStep, newItem: GuideStep): Boolean {
            return oldItem == newItem
        }
    }
}
