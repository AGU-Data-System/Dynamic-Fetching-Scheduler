package dynamicFetchingScheduler.server.http.controller.models

import dynamicFetchingScheduler.server.domain.ProviderWithData

/**
 * Output model for a provider with its data to be sent by the API.
 *
 * @property id The ID of the provider
 * @property name The name of the provider
 * @property url The URL of the provider
 * @property frequency The frequency of the provider
 * @property isActive Whether the provider is active
 * @property lastFetch The last time the provider was fetched
 * @property dataList The data of the provider
 */
data class ProviderWithDataOutputModel(
	val id: Int,
	val name: String,
	val url: String,
	val frequency: String,
	val isActive: Boolean,
	val lastFetch: String?,
	val dataList: List<ProviderRawDataOutputModel>
) {
	constructor(provider: ProviderWithData) : this(
		id = provider.id,
		name = provider.name,
		url = provider.url.toString(),
		frequency = provider.frequency.toString(),
		isActive = provider.isActive,
		lastFetch = provider.lastFetch?.toString(),
		dataList = provider.dataList.map { ProviderRawDataOutputModel(it) }
	)
}
