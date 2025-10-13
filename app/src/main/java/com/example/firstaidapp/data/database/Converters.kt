package com.example.firstaidapp.data.database

import androidx.room.TypeConverter
import com.example.firstaidapp.data.models.ContactType
import com.example.firstaidapp.data.models.GuideStep
import com.example.firstaidapp.data.models.StepType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromContactType(value: ContactType): String {
        return value.name
    }

    @TypeConverter
    fun toContactType(value: String): ContactType {
        return ContactType.valueOf(value)
    }

    @TypeConverter
    fun fromGuideStepList(value: List<GuideStep>?): String? {
        return if (value == null) null else gson.toJson(value)
    }

    @TypeConverter
    fun toGuideStepList(value: String?): List<GuideStep>? {
        return if (value == null) null else {
            val listType = object : TypeToken<List<GuideStep>>() {}.type
            gson.fromJson(value, listType)
        }
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return if (value == null) null else gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return if (value == null) null else {
            val listType = object : TypeToken<List<String>>() {}.type
            gson.fromJson(value, listType)
        }
    }

    @TypeConverter
    fun fromStepType(stepType: StepType): String {
        return stepType.name
    }

    @TypeConverter
    fun toStepType(stepType: String): StepType {
        return try {
            StepType.valueOf(stepType)
        } catch (e: IllegalArgumentException) {
            StepType.ACTION // Default fallback
        }
    }
}
