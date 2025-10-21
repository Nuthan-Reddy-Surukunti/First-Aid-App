package com.example.firstaidapp.data.repository

import com.example.firstaidapp.data.models.ContactType
import com.example.firstaidapp.data.models.EmergencyContact

/**
 * Provides data for emergency contacts and related utilities.
 */
object EmergencyContactsData {
    /**
     * Returns a list of Indian states for manual state selection.
     */
    fun getStatesList(): List<String> = listOf(
        "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa",
        "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala",
        "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland",
        "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
        "Uttar Pradesh", "Uttarakhand", "West Bengal",
        // Union Territories
        "Andaman and Nicobar Islands", "Chandigarh", "Dadra and Nagar Haveli and Daman and Diu",
        "Delhi", "Jammu and Kashmir", "Ladakh", "Lakshadweep", "Puducherry"
    )

    /**
     * Returns a list of default emergency contacts (national-level for India).
     */
    fun getAllEmergencyContacts(): List<EmergencyContact> {
        return listOf(
            EmergencyContact(
                name = "Police Emergency",
                phoneNumber = "100",
                type = ContactType.POLICE,
                state = "National",
                isDefault = true
            ),
            EmergencyContact(
                name = "Fire Brigade",
                phoneNumber = "101",
                type = ContactType.FIRE_DEPARTMENT,
                state = "National",
                isDefault = true
            ),
            EmergencyContact(
                name = "Ambulance",
                phoneNumber = "102",
                type = ContactType.EMERGENCY_SERVICE,
                state = "National",
                isDefault = true
            ),
            EmergencyContact(
                name = "Emergency Helpline",
                phoneNumber = "112",
                type = ContactType.EMERGENCY_SERVICE,
                state = "National",
                isDefault = true
            ),
            EmergencyContact(
                name = "Women Helpline",
                phoneNumber = "1091",
                type = ContactType.EMERGENCY_SERVICE,
                state = "National",
                isDefault = true
            ),
            EmergencyContact(
                name = "Child Helpline",
                phoneNumber = "1098",
                type = ContactType.EMERGENCY_SERVICE,
                state = "National",
                isDefault = true
            ),
            EmergencyContact(
                name = "Tourist Helpline",
                phoneNumber = "1363",
                type = ContactType.OTHER,
                state = "National",
                isDefault = true
            ),
            EmergencyContact(
                name = "Railway Enquiry",
                phoneNumber = "139",
                type = ContactType.OTHER,
                state = "National",
                isDefault = true
            ),
            EmergencyContact(
                name = "Poison Control",
                phoneNumber = "1066",
                type = ContactType.POISON_CONTROL,
                state = "National",
                isDefault = true
            )
        )
    }

    /**
     * Returns a list of emergency contacts for a given Indian state.
     */
    fun getContactsForState(state: String): List<EmergencyContact> {
        return when (state) {
            "Andhra Pradesh" -> listOf(
                EmergencyContact(
                    name = "Andhra Pradesh Police",
                    phoneNumber = "112",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "AP Ambulance Service",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Arunachal Pradesh" -> listOf(
                EmergencyContact(
                    name = "Arunachal Pradesh Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Arunachal Pradesh Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Assam" -> listOf(
                EmergencyContact(
                    name = "Assam Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Assam Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Bihar" -> listOf(
                EmergencyContact(
                    name = "Bihar Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Bihar Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Chhattisgarh" -> listOf(
                EmergencyContact(
                    name = "Chhattisgarh Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Chhattisgarh Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Goa" -> listOf(
                EmergencyContact(
                    name = "Goa Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Goa Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Gujarat" -> listOf(
                EmergencyContact(
                    name = "Gujarat Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Gujarat Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Haryana" -> listOf(
                EmergencyContact(
                    name = "Haryana Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Haryana Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Himachal Pradesh" -> listOf(
                EmergencyContact(
                    name = "Himachal Pradesh Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Himachal Pradesh Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Jharkhand" -> listOf(
                EmergencyContact(
                    name = "Jharkhand Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Jharkhand Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Karnataka" -> listOf(
                EmergencyContact(
                    name = "Karnataka Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Karnataka Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Kerala" -> listOf(
                EmergencyContact(
                    name = "Kerala Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Kerala Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Madhya Pradesh" -> listOf(
                EmergencyContact(
                    name = "Madhya Pradesh Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "MP Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Maharashtra" -> listOf(
                EmergencyContact(
                    name = "Maharashtra Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Maharashtra Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Manipur" -> listOf(
                EmergencyContact(
                    name = "Manipur Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Manipur Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Meghalaya" -> listOf(
                EmergencyContact(
                    name = "Meghalaya Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Meghalaya Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Mizoram" -> listOf(
                EmergencyContact(
                    name = "Mizoram Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Mizoram Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Nagaland" -> listOf(
                EmergencyContact(
                    name = "Nagaland Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Nagaland Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Odisha" -> listOf(
                EmergencyContact(
                    name = "Odisha Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Odisha Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Punjab" -> listOf(
                EmergencyContact(
                    name = "Punjab Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Punjab Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Rajasthan" -> listOf(
                EmergencyContact(
                    name = "Rajasthan Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Rajasthan Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Sikkim" -> listOf(
                EmergencyContact(
                    name = "Sikkim Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Sikkim Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Tamil Nadu" -> listOf(
                EmergencyContact(
                    name = "Tamil Nadu Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Tamil Nadu Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Telangana" -> listOf(
                EmergencyContact(
                    name = "Telangana Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Telangana Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Tripura" -> listOf(
                EmergencyContact(
                    name = "Tripura Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Tripura Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Uttar Pradesh" -> listOf(
                EmergencyContact(
                    name = "Uttar Pradesh Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "UP-112 Emergency Services",
                    phoneNumber = "112",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "UP Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Uttarakhand" -> listOf(
                EmergencyContact(
                    name = "Uttarakhand Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Uttarakhand Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "West Bengal" -> listOf(
                EmergencyContact(
                    name = "West Bengal Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "West Bengal Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            // Union Territories
            "Andaman and Nicobar Islands" -> listOf(
                EmergencyContact(
                    name = "A&N Islands Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "A&N Islands Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Chandigarh" -> listOf(
                EmergencyContact(
                    name = "Chandigarh Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Chandigarh Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Dadra and Nagar Haveli and Daman and Diu" -> listOf(
                EmergencyContact(
                    name = "DNH & DD Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "DNH & DD Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Delhi" -> listOf(
                EmergencyContact(
                    name = "Delhi Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Delhi Fire Service",
                    phoneNumber = "101",
                    type = ContactType.FIRE_DEPARTMENT,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Delhi Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Jammu and Kashmir" -> listOf(
                EmergencyContact(
                    name = "J&K Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "J&K Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Ladakh" -> listOf(
                EmergencyContact(
                    name = "Ladakh Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Ladakh Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Lakshadweep" -> listOf(
                EmergencyContact(
                    name = "Lakshadweep Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Lakshadweep Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            "Puducherry" -> listOf(
                EmergencyContact(
                    name = "Puducherry Police",
                    phoneNumber = "100",
                    type = ContactType.POLICE,
                    state = state,
                    isDefault = false
                ),
                EmergencyContact(
                    name = "Puducherry Ambulance",
                    phoneNumber = "108",
                    type = ContactType.EMERGENCY_SERVICE,
                    state = state,
                    isDefault = false
                )
            )
            else -> emptyList()
        }
    }

    /**
     * Returns all emergency contacts including national and all state-specific contacts.
     */
    fun getAllEmergencyContactsWithStates(): List<EmergencyContact> {
        val allContacts = mutableListOf<EmergencyContact>()

        // Add national contacts
        allContacts.addAll(getAllEmergencyContacts())

        // Add state-specific contacts for all states
        getStatesList().forEach { state ->
            allContacts.addAll(getContactsForState(state))
        }

        return allContacts
    }

}
