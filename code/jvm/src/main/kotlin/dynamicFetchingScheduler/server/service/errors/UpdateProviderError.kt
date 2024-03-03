package dynamicFetchingScheduler.server.service.errors

/**
 * Error for updating a provider
 */
sealed class UpdateProviderError {
    data object ProviderNotFound : UpdateProviderError()
}