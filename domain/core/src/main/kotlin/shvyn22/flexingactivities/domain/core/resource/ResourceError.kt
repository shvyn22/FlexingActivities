package shvyn22.flexingactivities.domain.core.resource

sealed interface ResourceError {
    data object NoNetwork : ResourceError
    data object NotFound : ResourceError
    data object Server : ResourceError
    data object Unknown : ResourceError
    data class Custom(val message: String) : ResourceError
}