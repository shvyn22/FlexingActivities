package shvyn22.flexingactivities.domain.favorites.use_case

import app.cash.turbine.test
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import shvyn22.flexingactivities.domain.favorites.model.testFavorite
import shvyn22.flexingactivities.domain.favorites.repository.FakeFavoritesRepository

@OptIn(ExperimentalCoroutinesApi::class)
class GetFavoritesUseCaseTest {

    private lateinit var repository: FakeFavoritesRepository
    private lateinit var useCase: GetFavoritesUseCase

    @Before
    fun setup() {
        repository = FakeFavoritesRepository()
        useCase = GetFavoritesUseCase(repository)
    }

    @Test
    fun `emits empty list initially`() = runTest {
        useCase().test {
            assertEquals(emptyList<Any>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits seeded favorites`() = runTest {
        repository.save(testFavorite)

        useCase().test {
            assertEquals(listOf(testFavorite), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits updates when favorites change`() = runTest {
        useCase().test {
            assertEquals(emptyList<Any>(), awaitItem())
            repository.save(testFavorite)
            assertEquals(listOf(testFavorite), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
