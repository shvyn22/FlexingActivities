package shvyn22.flexingactivities.domain.core.resource

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ExecuteUseCaseTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Test
    fun `IOException maps to NoNetwork`() = runTest {
        val result = executeUseCase<Unit>(dispatcher) {
            throw IOException("network failure")
        }

        assertTrue(result is Resource.Error)
        assertEquals(ResourceError.NoNetwork, (result as Resource.Error).error)
    }

    @Test
    fun `ResponseException with 404 maps to NotFound`() = runTest {
        val exception = createResponseException(HttpStatusCode.NotFound)

        val result = executeUseCase<Unit>(dispatcher) {
            throw exception
        }

        assertTrue(result is Resource.Error)
        assertEquals(ResourceError.NotFound, (result as Resource.Error).error)
    }

    @Test
    fun `ResponseException with 500 maps to Server`() = runTest {
        val exception = createResponseException(HttpStatusCode.InternalServerError)

        val result = executeUseCase<Unit>(dispatcher) {
            throw exception
        }

        assertTrue(result is Resource.Error)
        assertEquals(ResourceError.Server, (result as Resource.Error).error)
    }

    @Test
    fun `ResponseException with 503 maps to Server`() = runTest {
        val exception = createResponseException(HttpStatusCode.ServiceUnavailable)

        val result = executeUseCase<Unit>(dispatcher) {
            throw exception
        }

        assertTrue(result is Resource.Error)
        assertEquals(ResourceError.Server, (result as Resource.Error).error)
    }

    @Test
    fun `ResponseException with 401 maps to Unknown`() = runTest {
        val exception = createResponseException(HttpStatusCode.Unauthorized)

        val result = executeUseCase<Unit>(dispatcher) {
            throw exception
        }

        assertTrue(result is Resource.Error)
        assertEquals(ResourceError.Unknown, (result as Resource.Error).error)
    }

    @Test
    fun `unexpected exception maps to Custom`() = runTest {
        val message = "something went wrong"

        val result = executeUseCase<Unit>(dispatcher) {
            throw RuntimeException(message)
        }

        assertTrue(result is Resource.Error)
        val error = (result as Resource.Error).error
        assertTrue(error is ResourceError.Custom)
        assertEquals(message, (error as ResourceError.Custom).message)
    }

    @Test
    fun `successful action returns Success`() = runTest {
        val expected = 42

        val result = executeUseCase(dispatcher) {
            Resource.Success(expected)
        }

        assertTrue(result is Resource.Success)
        assertEquals(expected, (result as Resource.Success).data)
    }

    private suspend fun createResponseException(statusCode: HttpStatusCode): ResponseException {
        val engine = MockEngine { respond("", statusCode) }
        val client = HttpClient(engine) { expectSuccess = false }
        val response = client.get("https://test.example")
        client.close()
        return ResponseException(response, "")
    }
}
