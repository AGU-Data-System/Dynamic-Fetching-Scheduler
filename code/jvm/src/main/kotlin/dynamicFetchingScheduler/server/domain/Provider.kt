package dynamicFetchingScheduler.server.domain

import java.net.URL
import java.time.Duration
import java.time.LocalDateTime

/**
 * Represents a provider and its configuration.
 *
 * @property id The id of the provider
 * @property name The name of the provider
 * @property url The URL of the provider
 * @property frequency The frequency at which the provider's data should be polled
 * @property isActive Whether the provider is active or not
 * @property lastFetch The last time the provider's data was fetched
 */
data class Provider(
    val name: String,
    val url: URL,
    val frequency: Duration,
    val isActive: Boolean,
    val lastFetch: LocalDateTime? = null
)