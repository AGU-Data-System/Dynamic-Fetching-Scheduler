package dynamicFetchingScheduler.server.domain

import java.time.LocalDateTime

/**
 * Represents the data fetched from a provider without the provider's information
 *
 * @property fetchTime The time at which the data was fetched
 * @property data The raw data fetched from the provider
 * @see RawData
 * @see ProviderWithData
 */
data class ProviderRawData(
	val fetchTime: LocalDateTime,
	val data: String
) {
	constructor(rawData: RawData) : this(
		fetchTime = rawData.fetchTime,
		data = rawData.data
	)
}
