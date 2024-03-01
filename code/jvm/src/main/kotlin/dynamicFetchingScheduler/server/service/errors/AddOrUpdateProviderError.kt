package dynamicFetchingScheduler.server.service.errors

/**
 * Error for adding or updating a provider
 */
sealed class AddOrUpdateProviderError {
    data object UnknownError : AddOrUpdateProviderError()
}