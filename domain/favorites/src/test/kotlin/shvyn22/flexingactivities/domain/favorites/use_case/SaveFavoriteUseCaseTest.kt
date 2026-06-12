package shvyn22.flexingactivities.domain.favorites.use_case

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import shvyn22.flexingactivities.domain.core.resource.Resource
import shvyn22.flexingactivities.domain.core.resource.ResourceError
import shvyn22.flexingactivities.domain.favorites.model.testFavorite
import shvyn22.flexingactivities.domain.favorites.model.testFavorite2
import shvyn22.flexingactivities.domain.favorites.repository.FakeFavoritesRepository

@OptIn(ExperimentalCoroutinesApi::class)
class SaveFavoriteUseCaseTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeFavoritesRepository
    private lateinit var useCase: SaveFavoriteUseCase

    @Before
    fun setup() {
        repository = FakeFavoritesRepository()
        useCase = SaveFavoriteUseCase(testDispatcher, repository)
    }

    @Test
    fun `returns Success and saves location`() = runTest {
        val result = useCase(testFavorite)

        assertTrue(result is Resource.Success)
        assertEquals(testFavorite.name, repository.getById(1L)?.name)
    }

    @Test
    fun `saves multiple locations independently`() = runTest {
        useCase(testFavorite)
        useCase(testFavorite2)

        assertEquals(testFavorite.name, repository.getById(1L)?.name)
        assertEquals(testFavorite2.name, repository.getById(2L)?.name)
    }

    @Test
    fun `returns NoNetwork when save throws IOException`() = runTest {
        repository.shouldThrow = true

        val result = useCase(testFavorite)

        assertTrue(result is Resource.Error)
    }
}
