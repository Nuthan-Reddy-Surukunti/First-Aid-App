package com.example.firstaidapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.firstaidapp.data.database.GuideDao
import com.example.firstaidapp.data.models.FirstAidGuide
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class GuideRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var guideDao: GuideDao

    private lateinit var repository: GuideRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = GuideRepository(guideDao)
    }

    @Test
    fun `getAllGuides returns data from dao`() = runTest {
        // Given
        val expectedGuides = listOf(
            FirstAidGuide(
                id = 1,
                title = "CPR",
                category = "Emergency",
                shortDescription = "Cardiopulmonary Resuscitation",
                difficulty = "Hard",
                estimatedTimeMinutes = 10,
                keywords = "CPR, heart, breathing",
                isFavorite = false
            )
        )
        val liveData = MutableLiveData(expectedGuides)
        `when`(guideDao.getAllGuides()).thenReturn(liveData)

        // When
        val result = repository.getAllGuides()

        // Then
        verify(guideDao).getAllGuides()
        assertEquals(expectedGuides, result.value)
    }

    @Test
    fun `searchGuides returns filtered results`() = runTest {
        // Given
        val query = "CPR"
        val expectedResults = listOf(
            FirstAidGuide(
                id = 1,
                title = "CPR",
                category = "Emergency",
                shortDescription = "Cardiopulmonary Resuscitation",
                difficulty = "Hard",
                estimatedTimeMinutes = 10,
                keywords = "CPR, heart, breathing",
                isFavorite = false
            )
        )
        `when`(guideDao.searchGuides(query)).thenReturn(expectedResults)

        // When
        val result = repository.searchGuides(query)

        // Then
        verify(guideDao).searchGuides(query)
        assertEquals(expectedResults, result)
    }

    @Test
    fun `toggleFavorite updates guide favorite status`() = runTest {
        // Given
        val guideId = 1
        val guide = FirstAidGuide(
            id = guideId,
            title = "CPR",
            category = "Emergency",
            shortDescription = "Cardiopulmonary Resuscitation",
            difficulty = "Hard",
            estimatedTimeMinutes = 10,
            keywords = "CPR, heart, breathing",
            isFavorite = false
        )
        `when`(guideDao.getGuideById(guideId)).thenReturn(guide)

        // When
        repository.toggleFavorite(guideId)

        // Then
        verify(guideDao).getGuideById(guideId)
        verify(guideDao).update(guide.copy(isFavorite = true))
    }
}
