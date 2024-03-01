package dynamicFetchingScheduler.server.service.errors

/**
 * Error for adding or updating a provider
 */
sealed class AddProviderError {
	data object ProviderAlreadyExists : AddProviderError()
	data object UnknownError : AddProviderError()
}