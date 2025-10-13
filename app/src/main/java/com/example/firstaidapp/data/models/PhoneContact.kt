package com.example.firstaidapp.data.models

data class PhoneContact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val isSelected: Boolean = false
) {
    fun getInitial(): String {
        return if (name.isNotEmpty()) {
            name.first().uppercaseChar().toString()
        } else {
            "?"
        }
    }
}
