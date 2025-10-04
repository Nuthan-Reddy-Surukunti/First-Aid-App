package com.example.firstaidapp.ui.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.firstaidapp.data.database.GuideDao
import com.example.firstaidapp.data.database.SearchDao
import com.example.firstaidapp.data.models.FirstAidGuide
import com.example.firstaidapp.data.models.SearchHistory
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
class SearchViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var guideDao: GuideDao

    @Mock
    private lateinit var searchDao: SearchDao

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        val mockSearchHistory = MutableLiveData<List<SearchHistory>>()
        `when`(searchDao.getRecentSearches()).thenReturn(mockSearchHistory)
        viewModel = SearchViewModel(guideDao, searchDao)
    }

    @Test
    fun `searchGuides updates search results`() = runTest {
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
        viewModel.searchGuides(query)

        // Then
        verify(guideDao).searchGuides(query)
        assertEquals(expectedResults, viewModel.searchResults.value)
    }

    @Test
    fun `saveSearchQuery stores search in database`() = runTest {
        // Given
        val query = "burns"

        // When
        viewModel.saveSearchQuery(query)

        // Then
        verify(searchDao).insertSearch(any(SearchHistory::class.java))
    }

    @Test
    fun `getAllGuides loads all guides`() = runTest {
        // Given
        val allGuides = listOf(
            FirstAidGuide(1, "CPR", "Emergency", "CPR Guide", "Hard", 10, "CPR", false),
            FirstAidGuide(2, "Burns", "Injury", "Burn Treatment", "Medium", 5, "burns", false)
        )
        `when`(guideDao.searchGuides("")).thenReturn(allGuides)

        // When
        viewModel.getAllGuides()

        // Then
        verify(guideDao).searchGuides("")
        assertEquals(allGuides, viewModel.searchResults.value)
    }
}
