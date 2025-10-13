package com.example.firstaidapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.firstaidapp.data.models.FirstAidGuide

@Dao
interface GuideDao {
    @Query("SELECT * FROM first_aid_guides ORDER BY category, title")
    fun getAllGuides(): LiveData<List<FirstAidGuide>>

    @Query("SELECT * FROM first_aid_guides WHERE id = :guideId")
    fun getGuideById(guideId: String): LiveData<FirstAidGuide?>

    @Query("SELECT * FROM first_aid_guides WHERE category = :category ORDER BY title")
    fun getGuidesByCategory(category: String): LiveData<List<FirstAidGuide>>

    @Query("SELECT * FROM first_aid_guides WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchGuides(query: String): LiveData<List<FirstAidGuide>>

    @Query("SELECT * FROM first_aid_guides WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    suspend fun searchGuidesList(query: String): List<FirstAidGuide>

    @Query("SELECT * FROM first_aid_guides WHERE isFavorite = 1 ORDER BY lastAccessedTimestamp DESC")
    fun getFavoriteGuides(): LiveData<List<FirstAidGuide>>

    @Query("SELECT DISTINCT category FROM first_aid_guides ORDER BY category")
    fun getAllCategories(): LiveData<List<String>>

    @Query("SELECT COUNT(*) FROM first_aid_guides")
    suspend fun getGuidesCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuide(guide: FirstAidGuide)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(guides: List<FirstAidGuide>)

    @Update
    suspend fun updateGuide(guide: FirstAidGuide)

    @Query("UPDATE first_aid_guides SET isFavorite = :isFavorite WHERE id = :guideId")
    suspend fun updateFavoriteStatus(guideId: String, isFavorite: Boolean)

    @Query("UPDATE first_aid_guides SET lastAccessedTimestamp = :timestamp WHERE id = :guideId")
    suspend fun updateLastAccessed(guideId: String, timestamp: Long)

    @Delete
    suspend fun deleteGuide(guide: FirstAidGuide)

    @Query("DELETE FROM first_aid_guides")
    suspend fun deleteAllGuides()
}
