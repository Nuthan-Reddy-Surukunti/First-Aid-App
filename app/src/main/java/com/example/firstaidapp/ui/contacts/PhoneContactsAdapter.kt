package com.example.firstaidapp.ui.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firstaidapp.data.models.PhoneContact
import com.example.firstaidapp.databinding.ItemPhoneContactBinding

class PhoneContactsAdapter(
    private val onContactToggled: (PhoneContact, Boolean) -> Unit
) : ListAdapter<PhoneContact, PhoneContactsAdapter.ContactViewHolder>(ContactDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemPhoneContactBinding.inflate(
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
        private val binding: ItemPhoneContactBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: PhoneContact) {
            binding.apply {
                tvContactName.text = contact.name
                tvContactPhone.text = contact.phoneNumber
                tvContactInitial.text = contact.getInitial()
                cbSelectContact.isChecked = contact.isSelected

                // Handle checkbox changes
                cbSelectContact.setOnCheckedChangeListener { _, isChecked ->
                    onContactToggled(contact, isChecked)
                }

                // Handle item click
                root.setOnClickListener {
                    cbSelectContact.isChecked = !cbSelectContact.isChecked
                }
            }
        }
    }

    class ContactDiffCallback : DiffUtil.ItemCallback<PhoneContact>() {
        override fun areItemsTheSame(oldItem: PhoneContact, newItem: PhoneContact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PhoneContact, newItem: PhoneContact): Boolean {
            return oldItem == newItem
        }
    }
}
