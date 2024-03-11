package dynamicFetchingScheduler.server.http.controller.models.outputModels

import dynamicFetchingScheduler.server.domain.Provider

/**
 * Output model for a list of providers to be sent by the API.
 *
 * @property providers The list of providers
 * @property size The size of the list
 */
data class ProviderListOutputModel(
	val providers: List<ProviderOutputModel>,
	val size :Int = providers.size
) {

	constructor(providers: List<Provider>) : this(
		providers = providers.map { ProviderOutputModel(it) },
		size = providers.size
	)
}