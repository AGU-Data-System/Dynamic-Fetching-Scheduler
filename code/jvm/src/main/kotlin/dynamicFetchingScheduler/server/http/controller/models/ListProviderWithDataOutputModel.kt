package dynamicFetchingScheduler.server.http.controller.models

import dynamicFetchingScheduler.server.domain.ProviderWithData

/**
 * Output model for a list of providers with their data to be sent by the API.
 *
 * @property providers The list of providers with their data
 */
data class ListProviderWithDataOutputModel(
	val providers: List<ProviderWithDataOutputModel>
) {
	constructor(providers: List<ProviderWithData>) : this(
		providers = providers.map { ProviderWithDataOutputModel(it) }
	)
}