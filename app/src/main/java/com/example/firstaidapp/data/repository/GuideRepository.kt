package com.example.firstaidapp.data.repository

import androidx.lifecycle.LiveData
import com.example.firstaidapp.data.database.ContactDao
import com.example.firstaidapp.data.database.GuideDao
import com.example.firstaidapp.data.database.SearchDao
import com.example.firstaidapp.data.models.ContactType
import com.example.firstaidapp.data.models.EmergencyContact
import com.example.firstaidapp.data.models.FirstAidGuide
import com.example.firstaidapp.data.models.SearchHistory
import kotlinx.coroutines.flow.Flow

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

    suspend fun searchGuidesList(query: String): List<FirstAidGuide> {
        return guideDao.searchGuidesList(query)
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
    val allContacts: Flow<List<EmergencyContact>> = contactDao.getAllContacts()

    fun getContactsByState(state: String): Flow<List<EmergencyContact>> {
        return contactDao.getContactsByState(state)
    }

    fun getContactsByStateWithNational(state: String): Flow<List<EmergencyContact>> {
        return contactDao.getContactsByStateWithNational(state)
    }

    fun getContactsByType(type: ContactType): Flow<List<EmergencyContact>> {
        return contactDao.getContactsByType(type)
    }

    fun searchContacts(query: String): Flow<List<EmergencyContact>> {
        return contactDao.searchContacts(query)
    }

    suspend fun getContactsCount(): Int {
        return contactDao.getContactsCount()
    }

    suspend fun getAvailableStates(): List<String> {
        return contactDao.getAvailableStates()
    }

    suspend fun insertContact(contact: EmergencyContact): Long {
        return contactDao.insertContact(contact)
    }

    suspend fun insertContacts(contacts: List<EmergencyContact>) {
        contactDao.insertContacts(contacts)
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
