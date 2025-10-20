package com.example.firstaidapp.data.repository

import com.example.firstaidapp.data.models.ContactType
import com.example.firstaidapp.data.models.EmergencyContact

object EmergencyContactsData {
    
    fun getAllEmergencyContacts(): List<EmergencyContact> {
        return listOf(
            // National Services
            EmergencyContact(
                name = "Integrated Emergency Helpline (ERSS)",
                phoneNumber = "112",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Pan-India single emergency number for police, fire and medical services",
                state = "National",
                isDefault = true
            ),
            EmergencyContact(
                name = "Police (Emergency Helpline)",
                phoneNumber = "100",
                type = ContactType.POLICE,
                relationship = "Emergency",
                notes = "National emergency police helpline (calls to 100 are now integrated into 112 ERSS)",
                state = "National",
                isDefault = true
            ),
            EmergencyContact(
                name = "Fire Brigade",
                phoneNumber = "101",
                type = ContactType.FIRE_DEPARTMENT,
                relationship = "Emergency",
                notes = "National fire brigade emergency number (also covered by 112 ERSS)",
                state = "National",
                isDefault = true
            ),
            EmergencyContact(
                name = "Ambulance Services",
                phoneNumber = "108",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "National emergency ambulance number (free emergency ambulance service)",
                state = "National",
                isDefault = true
            ),
            EmergencyContact(
                name = "Women (Domestic Abuse Helpline)",
                phoneNumber = "181",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "24/7 national helpline for women in distress (domestic violence)",
                state = "National",
                isDefault = true
            ),
            EmergencyContact(
                name = "Women (General Helpline)",
                phoneNumber = "1091",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Women-in-distress helpline (immediate police assistance)",
                state = "National",
                isDefault = true
            ),
            EmergencyContact(
                name = "Disaster Management Control Room",
                phoneNumber = "1070",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "National Disaster Management emergency helpline (NDMA Control Room)",
                state = "National",
                isDefault = true
            ),
            EmergencyContact(
                name = "Mental Health Helpline (Tele-MANAS)",
                phoneNumber = "14416",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "National 24x7 toll-free mental health counselling helpline",
                state = "National",
                isDefault = true
            ),
            EmergencyContact(
                name = "Child Helpline",
                phoneNumber = "1098",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "National helpline for children in need of care and protection",
                state = "National",
                isDefault = true
            ),
            
            // Andhra Pradesh
            EmergencyContact(
                name = "Ambulance (Emergency Medical Transport)",
                phoneNumber = "108",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "State ambulance helpline for medical emergencies (free emergency ambulance)",
                state = "Andhra Pradesh",
                isDefault = true
            ),
            EmergencyContact(
                name = "Women Helpline (Emergency)",
                phoneNumber = "1091",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "State women-in-distress helpline (assistance and police referral)",
                state = "Andhra Pradesh",
                isDefault = true
            ),
            EmergencyContact(
                name = "State Emergency Control Room",
                phoneNumber = "1800-425-3077",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "State government control room for emergency coordination",
                state = "Andhra Pradesh",
                isDefault = true
            ),
            
            // Karnataka
            EmergencyContact(
                name = "Ambulance (Medical Emergency)",
                phoneNumber = "108",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Karnataka state emergency ambulance helpline",
                state = "Karnataka",
                isDefault = true
            ),
            EmergencyContact(
                name = "Women Helpline",
                phoneNumber = "1091",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Women's emergency helpline in Karnataka",
                state = "Karnataka",
                isDefault = true
            ),
            EmergencyContact(
                name = "State Disaster Control Room",
                phoneNumber = "1077",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Karnataka state disaster management control room helpline",
                state = "Karnataka",
                isDefault = true
            ),
            
            // Kerala
            EmergencyContact(
                name = "Ambulance (Medical Emergency)",
                phoneNumber = "102",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Kerala state emergency ambulance helpline",
                state = "Kerala",
                isDefault = true
            ),
            EmergencyContact(
                name = "Women Helpline",
                phoneNumber = "1091",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Women's emergency helpline in Kerala",
                state = "Kerala",
                isDefault = true
            ),
            
            // Telangana
            EmergencyContact(
                name = "Ambulance (Emergency Medical Transport)",
                phoneNumber = "102",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Telangana state emergency ambulance helpline",
                state = "Telangana",
                isDefault = true
            ),
            EmergencyContact(
                name = "Women Helpline (Sexual Harassment)",
                phoneNumber = "1091",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Women's helpline in Telangana (24/7 emergency support)",
                state = "Telangana",
                isDefault = true
            ),
            EmergencyContact(
                name = "State Disaster Control Room",
                phoneNumber = "1077",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "State disaster management helpline for Telangana",
                state = "Telangana",
                isDefault = true
            ),
            
            // Tripura
            EmergencyContact(
                name = "Ambulance (Medical Emergency)",
                phoneNumber = "102",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Tripura state emergency ambulance helpline",
                state = "Tripura",
                isDefault = true
            ),
            EmergencyContact(
                name = "Women Helpline",
                phoneNumber = "1091",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Women's emergency helpline in Tripura",
                state = "Tripura",
                isDefault = true
            ),
            EmergencyContact(
                name = "State Disaster Control Room",
                phoneNumber = "1077",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Tripura state disaster management helpline",
                state = "Tripura",
                isDefault = true
            ),
            
            // National Capital Territory of Delhi
            EmergencyContact(
                name = "Ambulance (Medical Emergency)",
                phoneNumber = "102",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Delhi emergency ambulance helpline",
                state = "Delhi",
                isDefault = true
            ),
            EmergencyContact(
                name = "Women Helpline",
                phoneNumber = "1091",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Delhi women's emergency helpline",
                state = "Delhi",
                isDefault = true
            ),
            EmergencyContact(
                name = "State Disaster Helpline",
                phoneNumber = "1077",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Delhi disaster management control room helpline",
                state = "Delhi",
                isDefault = true
            ),
            
            // Chandigarh
            EmergencyContact(
                name = "Ambulance (Emergency Medical Transport)",
                phoneNumber = "102",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Chandigarh emergency ambulance helpline",
                state = "Chandigarh",
                isDefault = true
            ),
            EmergencyContact(
                name = "Women & Child Helpline",
                phoneNumber = "1091",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Chandigarh helpline for women and children in distress",
                state = "Chandigarh",
                isDefault = true
            ),
            
            // Ladakh
            EmergencyContact(
                name = "Ambulance (Medical Helpline)",
                phoneNumber = "102",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Ladakh UT emergency medical helpline",
                state = "Ladakh",
                isDefault = true
            ),
            EmergencyContact(
                name = "Women Helpline",
                phoneNumber = "1091",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Ladakh women's emergency helpline",
                state = "Ladakh",
                isDefault = true
            ),
            
            // Lakshadweep
            EmergencyContact(
                name = "Ambulance (Emergency Medical Transport)",
                phoneNumber = "102",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Lakshadweep emergency ambulance numbers (108 also accessible)",
                state = "Lakshadweep",
                isDefault = true
            ),
            EmergencyContact(
                name = "Women Helpline",
                phoneNumber = "1091",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Lakshadweep women's emergency helpline",
                state = "Lakshadweep",
                isDefault = true
            ),
            
            // Andaman and Nicobar Islands
            EmergencyContact(
                name = "Ambulance (Island Emergency Transport)",
                phoneNumber = "102",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Island emergency ambulance helpline (Andaman & Nicobar)",
                state = "Andaman and Nicobar Islands",
                isDefault = true
            ),
            EmergencyContact(
                name = "Women Helpline",
                phoneNumber = "1091",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Andaman & Nicobar women's emergency helpline",
                state = "Andaman and Nicobar Islands",
                isDefault = true
            ),
            EmergencyContact(
                name = "Disaster Management Control Room",
                phoneNumber = "1070",
                type = ContactType.EMERGENCY_SERVICE,
                relationship = "Emergency",
                notes = "Andaman & Nicobar disaster management control helpline",
                state = "Andaman and Nicobar Islands",
                isDefault = true
            )
        )
    }
    
    fun getStatesList(): List<String> {
        return listOf(
            "Andhra Pradesh",
            "Karnataka",
            "Kerala",
            "Telangana",
            "Tripura",
            "Delhi",
            "Chandigarh",
            "Ladakh",
            "Lakshadweep",
            "Andaman and Nicobar Islands"
        ).sorted()
    }
}
