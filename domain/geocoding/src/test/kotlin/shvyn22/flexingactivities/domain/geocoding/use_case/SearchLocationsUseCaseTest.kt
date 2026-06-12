package shvyn22.flexingactivities.domain.geocoding.use_case

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import shvyn22.flexingactivities.domain.core.resource.Resource
import shvyn22.flexingactivities.domain.core.resource.ResourceError
import shvyn22.flexingactivities.domain.geocoding.model.GeoLocation
import shvyn22.flexingactivities.domain.geocoding.repository.FakeGeocodingRepository

@OptIn(ExperimentalCoroutinesApi::class)
class SearchLocationsUseCaseTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeGeocodingRepository
    private lateinit var useCase: SearchLocationsUseCase

    @Before
    fun setup() {
        repository = FakeGeocodingRepository()
        useCase = SearchLocationsUseCase(testDispatcher, repository)
    }

    @Test
    fun `returns results when repository has matching locations`() = runTest {
        repository.register(berlinGeoLocation)

        val result = useCase("Berlin")

        assertTrue(result is Resource.Success)
        assertEquals(listOf(berlinGeoLocation), (result as Resource.Success).data)
    }

    @Test
    fun `returns NotFound when no locations match query`() = runTest {
        repository.register(berlinGeoLocation)

        val result = useCase("xyz")

        assertTrue(result is Resource.Error)
        assertEquals(ResourceError.NotFound, (result as Resource.Error).error)
    }

    @Test
    fun `returns NoNetwork when network fails`() = runTest {
        repository.shouldFailNetwork = true

        val result = useCase("Berlin")

        assertTrue(result is Resource.Error)
        assertEquals(ResourceError.NoNetwork, (result as Resource.Error).error)
    }

    @Test
    fun `returns all results matching the query`() = runTest {
        repository.register(berlinGeoLocation, viennaGeoLocation)

        // "i" is contained in both "Berlin" (berlIn) and "Vienna" (vIenna)
        val result = useCase("i")

        assertTrue(result is Resource.Success)
        assertEquals(2, (result as Resource.Success).data.size)
    }

    companion object {
        private val berlinGeoLocation = GeoLocation(
            id = 1L,
            name = "Berlin",
            country = "Germany",
            countryCode = "DE",
            latitude = 52.52,
            longitude = 13.405,
            admin1 = "Brandenburg",
            timezone = "Europe/Berlin",
        )

        private val viennaGeoLocation = GeoLocation(
            id = 2L,
            name = "Vienna",
            country = "Austria",
            countryCode = "AT",
            latitude = 48.2082,
            longitude = 16.3738,
            admin1 = null,
            timezone = "Europe/Vienna",
        )
    }
}
