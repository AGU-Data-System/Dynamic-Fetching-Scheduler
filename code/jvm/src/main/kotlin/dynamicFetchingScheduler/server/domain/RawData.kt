package dynamicFetchingScheduler.server.domain

import java.net.URL
import java.time.LocalDateTime
import org.json.JSONObject

/**
 * Represents the raw data fetched from a provider.
 *
 * @property providerUrl The URL of the provider
 * @property fetchTime The time at which the data was fetched
 * @property data The raw data fetched from the provider
 */
data class RawData(
	val providerUrl: URL,
	val fetchTime: LocalDateTime,
	val data: JSONObject
)
