package dynamicFetchingScheduler.server.domain

import java.time.ZonedDateTime

/**
 * Represents the raw data fetched from a provider.
 *
 * @property providerId The id of the provider
 * @property fetchTime The time at which the data was fetched
 * @property data The raw data fetched from the provider
 */
data class RawData(
	val providerId: Int,
	val fetchTime: ZonedDateTime,
	val data: String
)
