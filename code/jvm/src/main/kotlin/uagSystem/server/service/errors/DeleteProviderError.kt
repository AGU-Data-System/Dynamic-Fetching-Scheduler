package uagSystem.server.service.errors

/**
 * Error for deleting a provider
 */
sealed class DeleteProviderError {
	data object ProviderNotFound : DeleteProviderError()

	data object UnknownError : DeleteProviderError()
}