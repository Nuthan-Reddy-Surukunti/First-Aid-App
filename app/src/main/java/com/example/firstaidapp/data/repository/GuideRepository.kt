package com.example.firstaidapp.data.repository

import androidx.lifecycle.LiveData
import com.example.firstaidapp.data.database.ContactDao
import com.example.firstaidapp.data.database.GuideDao
import com.example.firstaidapp.data.database.SearchDao
import com.example.firstaidapp.data.models.EmergencyContact
import com.example.firstaidapp.data.models.FirstAidGuide
import com.example.firstaidapp.data.models.SearchHistory

class GuideRepository(
    private val guideDao: GuideDao,
    private val contactDao: ContactDao,
    private val searchDao: SearchDao
) {
    // Guide operations
    val allGuides: LiveData<List<FirstAidGuide>> = guideDao.getAllGuides()
    val allCategories: LiveData<List<String>> = guideDao.getAllCategories()
    val favoriteGuides: LiveData<List<FirstAidGuide>> = guideDao.getFavoriteGuides()

    fun getGuideById(guideId: String): LiveData<FirstAidGuide?> {
        return guideDao.getGuideById(guideId)
    }

    fun getGuidesByCategory(category: String): LiveData<List<FirstAidGuide>> {
        return guideDao.getGuidesByCategory(category)
    }

    fun searchGuides(query: String): LiveData<List<FirstAidGuide>> {
        return guideDao.searchGuides(query)
    }

    suspend fun insertGuides(guides: List<FirstAidGuide>) {
        guideDao.insertAll(guides)
    }

    suspend fun updateGuide(guide: FirstAidGuide) {
        guideDao.updateGuide(guide)
    }

    suspend fun toggleFavorite(guideId: String, isFavorite: Boolean) {
        guideDao.updateFavoriteStatus(guideId, isFavorite)
    }

    suspend fun updateLastAccessed(guideId: String) {
        guideDao.updateLastAccessed(guideId, System.currentTimeMillis())
    }

    // Contact operations
    val allContacts: LiveData<List<EmergencyContact>> = contactDao.getAllContacts()
    val defaultContacts: LiveData<List<EmergencyContact>> = contactDao.getDefaultContacts()

    suspend fun insertContact(contact: EmergencyContact) {
        contactDao.insertContact(contact)
    }

    suspend fun insertContacts(contacts: List<EmergencyContact>) {
        contactDao.insertAll(contacts)
    }

    suspend fun updateContact(contact: EmergencyContact) {
        contactDao.updateContact(contact)
    }

    suspend fun deleteContact(contact: EmergencyContact) {
        contactDao.deleteContact(contact)
    }

    // Search history operations
    val recentSearches: LiveData<List<SearchHistory>> = searchDao.getRecentSearches()
    val recentSearchQueries: LiveData<List<String>> = searchDao.getRecentSearchQueries()

    suspend fun saveSearch(query: String, resultCount: Int) {
        searchDao.insertSearch(SearchHistory(query = query, resultCount = resultCount))
    }

    suspend fun clearSearchHistory() {
        searchDao.clearSearchHistory()
    }

    suspend fun deleteOldSearches(daysToKeep: Int = 30) {
        val cutoffTime = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L)
        searchDao.deleteOldSearches(cutoffTime)
    }
}
