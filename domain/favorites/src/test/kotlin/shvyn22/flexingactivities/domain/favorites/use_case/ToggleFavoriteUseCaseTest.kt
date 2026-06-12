package shvyn22.flexingactivities.domain.favorites.use_case

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import shvyn22.flexingactivities.domain.core.model.Activity
import shvyn22.flexingactivities.domain.core.resource.Resource
import shvyn22.flexingactivities.domain.favorites.model.testFavorite
import shvyn22.flexingactivities.domain.favorites.repository.FakeFavoritesRepository

@OptIn(ExperimentalCoroutinesApi::class)
class ToggleFavoriteUseCaseTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeFavoritesRepository
    private lateinit var useCase: ToggleFavoriteUseCase

    private val scores = mapOf(
        Activity.SKIING to 10,
        Activity.SURFING to 50,
        Activity.OUTDOOR_SIGHTSEEING to 75,
        Activity.INDOOR_SIGHTSEEING to 20,
    )

    @Before
    fun setup() {
        repository = FakeFavoritesRepository()
        useCase = ToggleFavoriteUseCase(testDispatcher, repository)
    }

    @Test
    fun `adds new location and returns Success(true) when not a favorite`() = runTest {
        val result = useCase(
            latitude = 52.52,
            longitude = 13.405,
            locationName = "Berlin",
            scores = scores,
        )

        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)
        val saved = repository.getByCoordinates(52.52, 13.405)
        assertTrue(saved != null)
        assertEquals("Berlin", saved?.name)
    }

    @Test
    fun `removes existing location and returns Success(false) when already a favorite`() = runTest {
        repository.save(testFavorite)

        val result = useCase(
            latitude = testFavorite.latitude,
            longitude = testFavorite.longitude,
            locationName = testFavorite.name,
            scores = scores,
        )

        assertTrue(result is Resource.Success)
        assertEquals(false, (result as Resource.Success).data)
        assertNull(repository.getByCoordinates(testFavorite.latitude, testFavorite.longitude))
    }

    @Test
    fun `saved location has correct coordinates and name`() = runTest {
        useCase(
            latitude = 48.2082,
            longitude = 16.3738,
            locationName = "Vienna",
            scores = scores,
        )

        val saved = repository.getByCoordinates(48.2082, 16.3738)
        assertEquals("Vienna", saved?.name)
        assertEquals(48.2082, saved?.latitude)
        assertEquals(16.3738, saved?.longitude)
        assertEquals(scores, saved?.scores)
    }
}
