package shvyn22.flexingactivities.domain.favorites.use_case

import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import shvyn22.flexingactivities.domain.core.resource.Resource
import shvyn22.flexingactivities.domain.core.resource.ResourceError
import shvyn22.flexingactivities.domain.favorites.model.testFavorite
import shvyn22.flexingactivities.domain.favorites.repository.FakeFavoritesRepository

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteFavoriteUseCaseTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeFavoritesRepository
    private lateinit var useCase: DeleteFavoriteUseCase

    @Before
    fun setup() {
        repository = FakeFavoritesRepository()
        useCase = DeleteFavoriteUseCase(testDispatcher, repository)
    }

    @Test
    fun `returns Success and removes location`() = runTest {
        repository.save(testFavorite)

        val result = useCase(testFavorite.id)

        assertTrue(result is Resource.Success)
        assertNull(repository.getById(testFavorite.id))
    }

    @Test
    fun `deleting non-existent id returns Success without side effects`() = runTest {
        repository.save(testFavorite)

        val result = useCase(999L)

        assertTrue(result is Resource.Success)
        // original still present
        assertTrue(repository.getById(testFavorite.id) != null)
    }

    @Test
    fun `returns NoNetwork when delete throws IOException`() = runTest {
        repository.save(testFavorite)
        repository.shouldThrow = true

        val result = useCase(testFavorite.id)

        assertTrue(result is Resource.Error)
    }
}
