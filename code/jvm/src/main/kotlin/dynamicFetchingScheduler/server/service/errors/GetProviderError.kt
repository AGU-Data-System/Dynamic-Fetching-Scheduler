package dynamicFetchingScheduler.server.service.errors

/**
 * Error for getting a provider
 */
sealed class GetProviderError {
	data object ProviderNotFound : GetProviderError()
}