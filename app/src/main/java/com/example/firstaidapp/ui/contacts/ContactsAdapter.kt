package com.example.firstaidapp.ui.contacts

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class ContactViewHolder(
        private val binding: ItemContactBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var currentContact: EmergencyContact? = null

        fun resetViewState() {
            // Reset view properties to default state
            binding.root.background = ColorDrawable(ContextCompat.getColor(binding.root.context, R.color.white))
        }

        fun bind(contact: EmergencyContact, position: Int) {
            currentContact = contact
            // Bind contact data to views
            binding.tvContactName.text = contact.name
            binding.tvContactNumber.text = contact.phoneNumber
            binding.btnCall.setOnClickListener {
                onCallClick(contact)
            }
        }
    }
}

class ContactDiffCallback : DiffUtil.ItemCallback<EmergencyContact>() {
    override fun areItemsTheSame(oldItem: EmergencyContact, newItem: EmergencyContact): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: EmergencyContact, newItem: EmergencyContact): Boolean {
        return oldItem == newItem
    }
}
