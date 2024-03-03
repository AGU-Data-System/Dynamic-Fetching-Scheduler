package dynamicFetchingScheduler.server.service.errors

/**
 * Error for deleting a provider
 */
sealed class DeleteProviderError {
    data object ProviderNotFound : DeleteProviderError()
}