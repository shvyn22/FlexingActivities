package shvyn22.flexingactivities.domain.core.resource

import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException

sealed interface Resource<T> {
    data class Success<T>(val data: T) : Resource<T>
    data class Error<T>(val error: ResourceError) : Resource<T>
}

suspend fun <T> executeUseCase(
    dispatcher: CoroutineDispatcher,
    action: suspend () -> Resource<T>,
): Resource<T> {
    return withContext(dispatcher) {
        try {
            action()
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error(ResourceError.NoNetwork)
        } catch (e: ResponseException) {
            Resource.Error(e.toResourceError())
        } catch (e: Exception) {
            Resource.Error(ResourceError.Custom(e.message ?: ""))
        }
    }
}

fun ResponseException.toResourceError(): ResourceError {
    return when (response.status.value) {
        404 -> ResourceError.NotFound
        in 500..599 -> ResourceError.Server
        else -> ResourceError.Unknown
    }
}