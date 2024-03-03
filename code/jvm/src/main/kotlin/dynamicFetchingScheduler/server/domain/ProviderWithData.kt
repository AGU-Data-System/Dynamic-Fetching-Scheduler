package dynamicFetchingScheduler.server.domain

import java.net.URL
import java.time.Duration
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

/**
 * Represents the provider and its data
 *
 * @property id The id of the provider
 * @property name The name of the provider
 * @property url The URL of the provider
 * @property dataList The data fetched from the provider
 * @see ProviderRawData
 */
data class ProviderWithData(
    val id: Int,
    val name: String,
    val url: URL,
    val frequency: Duration,
    val isActive: Boolean,
    val lastFetch: LocalDateTime?,
    val dataList: List<ProviderRawData>
) {
    constructor(provider: Provider, dataList: List<RawData>) : this(
        id = provider.id,
        name = provider.name,
        url = provider.url,
        frequency = provider.frequency,
        isActive = provider.isActive,
        lastFetch = provider.lastFetch,
        dataList = dataList.map { ProviderRawData(it) }
    )
}