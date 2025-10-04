package com.example.firstaidapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "first_aid_guides")
data class FirstAidGuide(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val description: String,
    val iconResName: String,
    val severity: String, // LOW, MEDIUM, HIGH, CRITICAL
    val steps: List<GuideStep>,
    val warnings: List<String>,
    val whenToCallEmergency: String,
    val isFavorite: Boolean = false,
    val lastAccessedTimestamp: Long = 0
)

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromGuideStepList(value: List<GuideStep>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toGuideStepList(value: String): List<GuideStep> {
        val listType = object : TypeToken<List<GuideStep>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
}
