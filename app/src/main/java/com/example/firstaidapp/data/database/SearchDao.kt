package com.example.firstaidapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.firstaidapp.data.models.SearchHistory

@Dao
interface SearchDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 10")
    fun getRecentSearches(): LiveData<List<SearchHistory>>

    @Query("SELECT DISTINCT query FROM search_history ORDER BY timestamp DESC LIMIT 5")
    fun getRecentSearchQueries(): LiveData<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: SearchHistory)

    @Query("DELETE FROM search_history WHERE timestamp < :cutoffTime")
    suspend fun deleteOldSearches(cutoffTime: Long)

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()
}
