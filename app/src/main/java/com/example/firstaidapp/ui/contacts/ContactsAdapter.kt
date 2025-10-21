package com.example.firstaidapp.ui.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firstaidapp.data.models.EmergencyContact
import com.example.firstaidapp.databinding.ItemContactBinding

class ContactsAdapter(
    private val onCallClick: (EmergencyContact) -> Unit,
    private val onContactClick: (EmergencyContact) -> Unit,
    private val onEditClick: (EmergencyContact) -> Unit,
    private val onDeleteClick: (EmergencyContact) -> Unit
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
        holder.bind(getItem(position))
    }

    inner class ContactViewHolder(
        private val binding: ItemContactBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: EmergencyContact) {
            // Bind contact data to views
            binding.tvContactName.text = contact.name
            binding.tvContactNumber.text = contact.phoneNumber
            binding.tvContactType.text = contact.type.name.replace("_", " ")

            // Call button click
            binding.btnCall.setOnClickListener {
                onCallClick(contact)
            }

            // Contact card click - opens details
            binding.root.setOnClickListener {
                onContactClick(contact)
            }

            // Long press - shows context menu
            binding.root.setOnLongClickListener {
                showContextMenu(contact)
                true
            }
        }

        private fun showContextMenu(contact: EmergencyContact) {
            val popupMenu = android.widget.PopupMenu(binding.root.context, binding.root)
            popupMenu.menu.add(0, 1, 0, "View Details")
            popupMenu.menu.add(0, 2, 1, "Edit Contact")
            popupMenu.menu.add(0, 3, 2, "Delete Contact")

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> {
                        onContactClick(contact)
                        true
                    }
                    2 -> {
                        onEditClick(contact)
                        true
                    }
                    3 -> {
                        onDeleteClick(contact)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
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

