package com.example.firstaidapp.data.repository

import com.example.firstaidapp.R
import com.example.firstaidapp.data.models.FirstAidGuide

object FirstAidGuidesData {

    fun getAllFirstAidGuides(): List<FirstAidGuide> {
        return listOf(
            // CPR Guide
            FirstAidGuide(
                id = "cpr_guide",
                title = "CPR - Adult",
                category = "CPR",
                severity = "CRITICAL",
                description = "Cardiopulmonary resuscitation for unresponsive adults not breathing normally. CPR combines chest compressions with rescue breathing to maintain circulation and oxygenation.",
                steps = FirstAidGuidesRepository.getCPRGuide(),
                iconResName = "ic_heart",
                whenToCallEmergency = "Call 911 immediately if person is unresponsive and not breathing normally. Don't delay CPR to call - have someone else call while you provide care.",
                warnings = listOf(
                    "Don't perform CPR on conscious persons",
                    "Expect ribs to crack during compressions - this is normal",
                    "Don't stop compressions unless absolutely necessary",
                    "Don't check for pulse unless trained"
                ),
                estimatedTimeMinutes = 30,
                difficulty = "Intermediate"
            ),

            // Choking Guide
            FirstAidGuide(
                id = "choking_guide",
                title = "Choking - Adult",
                category = "Airway",
                severity = "CRITICAL",
                description = "Emergency treatment for severe airway obstruction in conscious adults. Uses back blows and abdominal thrusts (Heimlich maneuver) to dislodge foreign objects.",
                steps = FirstAidGuidesRepository.getChokingAdultGuide(),
                iconResName = "ic_airway",
                whenToCallEmergency = "Call 911 if choking is not relieved quickly, person becomes unconscious, or shows severe distress (blue lips, extreme panic). If alone and choking severely, call EMS immediately.",
                warnings = listOf(
                    "Don't perform abdominal thrusts on pregnant women or small children",
                    "Don't do blind finger sweeps",
                    "If person becomes unconscious, start CPR",
                    "Don't use fingers to grab visible objects - may push deeper"
                ),
                estimatedTimeMinutes = 5,
                difficulty = "Beginner"
            ),

            // Heart Attack Guide
            FirstAidGuide(
                id = "heart_attack_guide",
                title = "Heart Attack",
                category = "Cardiac",
                severity = "CRITICAL",
                description = "Emergency care for suspected myocardial infarction. Focuses on rapid emergency response, comfort positioning, and medication assistance while monitoring for cardiac arrest.",
                steps = FirstAidGuidesRepository.getHeartAttackGuide(),
                iconResName = "ic_heart_attack",
                whenToCallEmergency = "Always call 911 for chest pain lasting more than a few minutes, especially if radiating to arm/jaw, or accompanied by shortness of breath, nausea, or lightheadedness. If person loses consciousness, start CPR.",
                warnings = listOf(
                    "Don't drive to hospital yourself - wait for EMS",
                    "Don't give food or drink in case surgery is needed",
                    "Check allergies before giving aspirin",
                    "Don't give anything by mouth if unconscious"
                ),
                estimatedTimeMinutes = 15,
                difficulty = "Beginner"
            ),

            // Stroke Guide
            FirstAidGuide(
                id = "stroke_guide",
                title = "Stroke Recognition & Care",
                category = "Neurological",
                severity = "CRITICAL",
                description = "Recognition and emergency care for stroke using FAST assessment. Time is critical for stroke treatment effectiveness - rapid identification and emergency response save brain tissue.",
                steps = FirstAidGuidesRepository.getStrokeGuide(),
                iconResName = "ic_brain",
                whenToCallEmergency = "Call 911 immediately at first suspicion of stroke. If person shows face droop, arm weakness, or slurred speech, call EMS at once. Time is critical in stroke treatment.",
                warnings = listOf(
                    "Don't drive them to hospital yourself - wait for EMS",
                    "Don't give food, drink, or medication",
                    "Don't let them sleep or become inactive",
                    "Note exact time symptoms began"
                ),
                estimatedTimeMinutes = 10,
                difficulty = "Beginner"
            ),

            // Severe Bleeding Guide
            FirstAidGuide(
                id = "severe_bleeding_guide",
                title = "Severe Bleeding Control",
                category = "Trauma",
                severity = "CRITICAL",
                description = "Emergency control of severe bleeding from traumatic injuries. Uses direct pressure, elevation, and shock prevention to control hemorrhage and maintain circulation.",
                steps = FirstAidGuidesRepository.getSevereBleedingGuide(),
                iconResName = "ic_bleeding",
                whenToCallEmergency = "Call 911 immediately if bleeding is large, spurting (arterial), or won't stop with pressure. Also call for head, neck, chest, or abdominal bleeding, or any signs of shock.",
                warnings = listOf(
                    "Don't remove embedded objects - apply pressure around them",
                    "Don't press on open chest wounds or eye injuries",
                    "Don't use tourniquet unless life-threatening and trained",
                    "Watch for signs of shock"
                ),
                estimatedTimeMinutes = 20,
                difficulty = "Intermediate"
            ),

            // Burns Guide
            FirstAidGuide(
                id = "burns_guide",
                title = "Burns Treatment",
                category = "Trauma",
                severity = "HIGH",
                description = "Treatment for thermal, chemical, and electrical burns. Focuses on stopping the burning process, cooling the burn, and assessing severity for appropriate medical care.",
                steps = FirstAidGuidesRepository.getBurnsGuide(),
                iconResName = "ic_fire",
                whenToCallEmergency = "Call 911 for 3rd-degree burns, burns larger than person's hand, burns on face/hands/feet/genitals, chemical/electrical burns, or if person shows shock.",
                warnings = listOf(
                    "Don't apply ice, butter, or home remedies",
                    "Don't break blisters",
                    "Don't remove clothing stuck to burn",
                    "Don't underestimate electrical burns - internal damage possible"
                ),
                estimatedTimeMinutes = 25,
                difficulty = "Intermediate"
            ),

            // Fractures Guide
            FirstAidGuide(
                id = "fractures_guide",
                title = "Fractures (Broken Bones)",
                category = "Trauma",
                severity = "HIGH",
                description = "Emergency care for suspected bone fractures. Focuses on bleeding control, immobilization, and preventing further injury while awaiting medical care.",
                steps = FirstAidGuidesRepository.getFracturesGuide(),
                iconResName = "ic_bone",
                whenToCallEmergency = "Call EMS for: bone protruding through skin, severe deformity, heavy bleeding, numbness/tingling below injury, severe pain, or suspected spinal injury.",
                warnings = listOf(
                    "Don't try to realign bones",
                    "Don't press on protruding bones",
                    "Don't move person unless necessary for safety",
                    "Treat bleeding first, then immobilize"
                ),
                estimatedTimeMinutes = 15,
                difficulty = "Intermediate"
            ),

            // Sprains and Strains Guide
            FirstAidGuide(
                id = "sprains_strains_guide",
                title = "Sprains and Strains",
                category = "Musculoskeletal",
                severity = "MEDIUM",
                description = "Treatment for joint sprains and muscle strains using R.I.C.E. method (Rest, Ice, Compression, Elevation) to reduce pain and swelling.",
                steps = FirstAidGuidesRepository.getSprainsStrainsGuide(),
                iconResName = "ic_joint",
                whenToCallEmergency = "Usually managed at home, but call if you suspect fracture, person cannot move limb, severe deformity, or pain/swelling worsens significantly.",
                warnings = listOf(
                    "Don't apply heat in first 48 hours",
                    "Don't wrap too tight - check circulation",
                    "If in doubt, treat as possible fracture"
                ),
                estimatedTimeMinutes = 10,
                difficulty = "Beginner"
            ),

            // Shock Guide
            FirstAidGuide(
                id = "shock_guide",
                title = "Shock Treatment",
                category = "Circulation",
                severity = "CRITICAL",
                description = "Treatment for circulatory shock from blood loss, anaphylaxis, or other causes. Focuses on maintaining circulation and treating underlying causes.",
                steps = FirstAidGuidesRepository.getShockGuide(),
                iconResName = "ic_shock",
                whenToCallEmergency = "Call 911 immediately if shock is suspected. Any cause of shock (severe bleeding, severe allergic reaction, etc.) warrants emergency response.",
                warnings = listOf(
                    "Don't give food or drink - they may vomit",
                    "Don't elevate legs if spinal injury suspected",
                    "Be ready to perform CPR if breathing stops",
                    "Shock is life-threatening"
                ),
                estimatedTimeMinutes = 20,
                difficulty = "Intermediate"
            ),

            // Hypothermia Guide
            FirstAidGuide(
                id = "hypothermia_guide",
                title = "Hypothermia",
                category = "Environmental",
                severity = "HIGH",
                description = "Treatment for dangerously low body temperature. Focuses on gentle rewarming and preventing further heat loss while avoiding rapid temperature changes.",
                steps = FirstAidGuidesRepository.getHypothermiaGuide(),
                iconResName = "ic_cold",
                whenToCallEmergency = "Call 911 if core temperature very low, person is drowsy/confused/not shivering, or any doubt about severity. Severe hypothermia requires immediate medical care.",
                warnings = listOf(
                    "Don't rewarm too rapidly - no hot baths",
                    "Don't rub or massage extremities",
                    "No alcohol or caffeine",
                    "Be gentle - hypothermic people are fragile"
                ),
                estimatedTimeMinutes = 30,
                difficulty = "Intermediate"
            ),

            // Heat Emergencies Guide
            FirstAidGuide(
                id = "heat_emergencies_guide",
                title = "Heat Exhaustion & Heatstroke",
                category = "Environmental",
                severity = "HIGH",
                description = "Treatment for heat-related illnesses from heat exhaustion to life-threatening heatstroke. Focuses on rapid cooling and fluid replacement.",
                steps = FirstAidGuidesRepository.getHeatEmergenciesGuide(),
                iconResName = "ic_heat",
                whenToCallEmergency = "Heatstroke: Call 911 immediately. Heat exhaustion: Call if person faints, has seizure, or doesn't improve with rest and cooling.",
                warnings = listOf(
                    "Heatstroke can be deadly - don't ignore symptoms",
                    "Don't give fluids if unconscious or vomiting",
                    "No alcohol or caffeinated drinks",
                    "Stop cooling if person starts shivering"
                ),
                estimatedTimeMinutes = 20,
                difficulty = "Beginner"
            ),

            // Seizures Guide
            FirstAidGuide(
                id = "seizures_guide",
                title = "Seizures",
                category = "Neurological",
                severity = "HIGH",
                description = "Emergency care for seizures focusing on safety, positioning, and monitoring. Most seizures stop on their own but require careful observation and support.",
                steps = FirstAidGuidesRepository.getSeizuresGuide(),
                iconResName = "ic_brain_wave",
                whenToCallEmergency = "Call 911 if seizure lasts >5 minutes, multiple seizures occur, first seizure, doesn't regain consciousness, breathing problems, or injury during seizure.",
                warnings = listOf(
                    "Never put anything in their mouth",
                    "Don't restrain the person",
                    "Don't give food/drink until fully alert",
                    "Most seizures stop on their own"
                ),
                estimatedTimeMinutes = 15,
                difficulty = "Beginner"
            ),

            // Poisoning Guide
            FirstAidGuide(
                id = "poisoning_guide",
                title = "Poisoning",
                category = "Toxicological",
                severity = "CRITICAL",
                description = "Emergency treatment for ingested or inhaled poisons. Focuses on removing from source, calling poison control, and supportive care.",
                steps = FirstAidGuidesRepository.getPoisoningGuide(),
                iconResName = "ic_poison",
                whenToCallEmergency = "Call 911 if unconscious, trouble breathing, convulsing, or dangerous substance involved. Always call Poison Control (1-800-222-1222) for guidance.",
                warnings = listOf(
                    "Don't induce vomiting unless instructed",
                    "Don't give antidotes unless told by professional",
                    "Don't contaminate yourself",
                    "Have poison container ready for information"
                ),
                estimatedTimeMinutes = 20,
                difficulty = "Intermediate"
            ),

            // Bites and Stings Guide
            FirstAidGuide(
                id = "bites_stings_guide",
                title = "Bites and Stings",
                category = "Envenomation",
                severity = "MEDIUM",
                description = "Treatment for insect stings, snake bites, and other venomous bites. Focuses on venom removal, wound care, and monitoring for systemic reactions.",
                steps = FirstAidGuidesRepository.getBitesStingsGuide(),
                iconResName = "ic_bug",
                whenToCallEmergency = "Call 911 for any snake bite, systemic symptoms (difficulty breathing, swelling beyond bite, nausea), or signs of allergic reaction.",
                warnings = listOf(
                    "Don't suck out venom or cut wound",
                    "Don't apply ice to venomous bites",
                    "Don't apply tourniquet",
                    "Keep bitten limb immobilized"
                ),
                estimatedTimeMinutes = 15,
                difficulty = "Beginner"
            ),

            // Allergic Reactions Guide
            FirstAidGuide(
                id = "allergic_reactions_guide",
                title = "Allergic Reactions (Anaphylaxis)",
                category = "Immunological",
                severity = "CRITICAL",
                description = "Emergency treatment for severe allergic reactions and anaphylaxis. Focuses on rapid epinephrine administration and emergency response.",
                steps = FirstAidGuidesRepository.getAllergicReactionsGuide(),
                iconResName = "ic_allergy",
                whenToCallEmergency = "Always call 911 immediately for anaphylaxis, even after using EpiPen. Anaphylaxis can progress rapidly and be fatal without treatment.",
                warnings = listOf(
                    "Don't wait - inject epinephrine immediately",
                    "Always call 911 even after using EpiPen",
                    "Be ready for second injection after 5-10 minutes",
                    "Be ready to perform CPR if they collapse"
                ),
                estimatedTimeMinutes = 10,
                difficulty = "Intermediate"
            ),

            // Asthma Attack Guide
            FirstAidGuide(
                id = "asthma_attack_guide",
                title = "Asthma Attack",
                category = "Respiratory",
                severity = "HIGH",
                description = "Emergency care for asthma attacks focusing on bronchodilator use, positioning, and monitoring. Severe attacks can be life-threatening.",
                steps = FirstAidGuidesRepository.getAsthmaAttackGuide(),
                iconResName = "ic_lungs",
                whenToCallEmergency = "Call 911 if no improvement after 1-2 inhaler doses, person very breathless, too short of breath to speak, bluish lips, or first severe attack.",
                warnings = listOf(
                    "Don't lay them down - keep upright",
                    "Don't exceed recommended inhaler doses",
                    "Don't panic the person - stay calm"
                ),
                estimatedTimeMinutes = 15,
                difficulty = "Beginner"
            ),

            // Diabetic Emergencies Guide
            FirstAidGuide(
                id = "diabetic_emergencies_guide",
                title = "Diabetic Emergencies",
                category = "Metabolic",
                severity = "HIGH",
                description = "Treatment for diabetic emergencies including hypoglycemia (low blood sugar) and hyperglycemia. When in doubt, treat for low blood sugar.",
                steps = FirstAidGuidesRepository.getDiabeticEmergenciesGuide(),
                iconResName = "ic_glucose",
                whenToCallEmergency = "Call 911 if unconscious, having seizure, cannot swallow, no improvement after sugar treatment, or suspected severe hyperglycemia with vomiting/dehydration.",
                warnings = listOf(
                    "Never give anything by mouth to unconscious person",
                    "When in doubt, treat for low blood sugar",
                    "Don't give diet drinks - need real sugar"
                ),
                estimatedTimeMinutes = 15,
                difficulty = "Beginner"
            ),

            // Drowning Guide
            FirstAidGuide(
                id = "drowning_guide",
                title = "Drowning",
                category = "Respiratory",
                severity = "CRITICAL",
                description = "Emergency response for drowning incidents. Focuses on safe rescue, immediate CPR, and post-rescue care. Even recovered victims need medical evaluation.",
                steps = FirstAidGuidesRepository.getDrowningGuide(),
                iconResName = "ic_water",
                whenToCallEmergency = "Always call 911 for any drowning incident, even if person seems to recover. Secondary drowning and hypothermia can occur later.",
                warnings = listOf(
                    "Don't put yourself at risk during rescue",
                    "Reach or throw, don't go unless trained",
                    "Even if they recover, seek medical evaluation",
                    "Be ready to clear water from airway"
                ),
                estimatedTimeMinutes = 20,
                difficulty = "Advanced"
            ),

            // Nosebleeds Guide
            FirstAidGuide(
                id = "nosebleeds_guide",
                title = "Nosebleeds",
                category = "Minor Trauma",
                severity = "LOW",
                description = "Treatment for nosebleeds using direct pressure and proper positioning. Most nosebleeds stop with proper first aid within 20 minutes.",
                steps = FirstAidGuidesRepository.getNosebleedsGuide(),
                iconResName = "ic_nose",
                whenToCallEmergency = "Seek help if bleeding very heavy, lasts >30 minutes despite pressure, accompanied by facial injury/head trauma, or person feels faint.",
                warnings = listOf(
                    "Don't tilt head back or lie down",
                    "Don't pack tissue deep into nose",
                    "Don't peek during pressure - maintain full time"
                ),
                estimatedTimeMinutes = 20,
                difficulty = "Beginner"
            )
        )
    }
}
