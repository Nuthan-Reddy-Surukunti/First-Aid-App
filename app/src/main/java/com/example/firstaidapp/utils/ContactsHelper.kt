package com.example.firstaidapp.utils

import android.content.Context
import android.provider.ContactsContract
import com.example.firstaidapp.data.models.PhoneContact

object ContactsHelper {

    /**
     * Retrieves all contacts from the device's contact list
     * Requires READ_CONTACTS permission
     */
    fun getPhoneContacts(context: Context): List<PhoneContact> {
        val contacts = mutableListOf<PhoneContact>()
        val seenIds = mutableSetOf<String>()

        try {
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            )

            cursor?.use {
                val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                while (it.moveToNext()) {
                    val id = it.getString(idIndex) ?: continue
                    val name = it.getString(nameIndex) ?: "Unknown"
                    val number = it.getString(numberIndex) ?: continue

                    // Avoid duplicates - only add each contact once (first phone number)
                    if (!seenIds.contains(id)) {
                        seenIds.add(id)
                        contacts.add(
                            PhoneContact(
                                id = id,
                                name = name,
                                phoneNumber = normalizePhoneNumber(number)
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return contacts
    }

    /**
     * Normalize phone number by removing spaces, dashes, and parentheses
     */
    private fun normalizePhoneNumber(number: String): String {
        return number.replace(Regex("[\\s\\-()]"), "")
    }

    /**
     * Search contacts by name or phone number
     */
    fun searchContacts(contacts: List<PhoneContact>, query: String): List<PhoneContact> {
        if (query.isBlank()) return contacts

        val lowerQuery = query.lowercase()
        return contacts.filter { contact ->
            contact.name.lowercase().contains(lowerQuery) ||
            contact.phoneNumber.contains(query)
        }
    }
}

