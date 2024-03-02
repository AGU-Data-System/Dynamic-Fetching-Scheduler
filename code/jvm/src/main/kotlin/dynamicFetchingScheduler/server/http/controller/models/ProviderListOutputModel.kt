package dynamicFetchingScheduler.server.http.controller.models

import dynamicFetchingScheduler.server.domain.Provider

/**
 * Output model for a list of providers to be returned by the API.
 *
 * @property providers The list of providers
 */
data class ProviderListOutputModel(
	val providers: List<ProviderOutputModel>
) {
	constructor(providers: List<Provider>) : this(
		providers.map { ProviderOutputModel(it) }
	)
}
