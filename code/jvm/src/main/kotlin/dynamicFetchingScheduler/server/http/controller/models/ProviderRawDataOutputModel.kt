package dynamicFetchingScheduler.server.http.controller.models

import dynamicFetchingScheduler.server.domain.ProviderRawData

/**
 * Output model for the raw data of a provider to be sent by the API.
 *
 * @property fetchTime The time the data was fetched
 * @property data The raw data
 */
data class ProviderRawDataOutputModel(
	val fetchTime: String,
	val data: String
) {
	constructor(providerRawData: ProviderRawData) : this(
		fetchTime = providerRawData.fetchTime.toString(),
		data = providerRawData.data
	)
}