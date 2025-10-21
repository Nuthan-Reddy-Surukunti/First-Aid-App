package com.example.firstaidapp.ui.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firstaidapp.data.models.PhoneContact

class PhoneContactsAdapter(
    private val onSelectionChanged: (List<PhoneContact>) -> Unit
) : ListAdapter<PhoneContact, PhoneContactsAdapter.PhoneContactViewHolder>(PhoneContactDiffCallback()) {

    private val selectedContacts = mutableSetOf<String>() // Store selected contact IDs
    private var allContacts = listOf<PhoneContact>() // Store all contacts for retrieval

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_multiple_choice, parent, false)
        return PhoneContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhoneContactViewHolder, position: Int) {
        val contact = getItem(position)
        holder.bind(contact, selectedContacts.contains(contact.id)) { isSelected ->
            if (isSelected) {
                selectedContacts.add(contact.id)
            } else {
                selectedContacts.remove(contact.id)
            }
            notifySelectionChanged()
        }
    }

    override fun submitList(list: List<PhoneContact>?) {
        if (list != null && list.size > currentList.size) {
            // Store all contacts when full list is submitted
            allContacts = list
        }
        super.submitList(list)
    }

    private fun notifySelectionChanged() {
        val selected = allContacts.filter { selectedContacts.contains(it.id) }
        onSelectionChanged(selected)
    }

    fun getSelectedContacts(): List<PhoneContact> {
        return allContacts.filter { selectedContacts.contains(it.id) }
    }

    fun clearSelection() {
        selectedContacts.clear()
        notifyDataSetChanged()
    }

    class PhoneContactViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val checkedTextView: CheckedTextView = itemView.findViewById(android.R.id.text1)

        fun bind(contact: PhoneContact, isSelected: Boolean, onToggle: (Boolean) -> Unit) {
            checkedTextView.text = "${contact.name}\n${contact.phoneNumber}"
            checkedTextView.isChecked = isSelected

            // Update background for selected items
            itemView.setBackgroundColor(
                if (isSelected) 0x1A4CAF50 else 0x00000000
            )

            itemView.setOnClickListener {
                val newState = !checkedTextView.isChecked
                checkedTextView.isChecked = newState
                onToggle(newState)
            }
        }
    }

    private class PhoneContactDiffCallback : DiffUtil.ItemCallback<PhoneContact>() {
        override fun areItemsTheSame(oldItem: PhoneContact, newItem: PhoneContact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PhoneContact, newItem: PhoneContact): Boolean {
            return oldItem == newItem
        }
    }
}

