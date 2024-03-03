package dynamicFetchingScheduler.server.domain

import java.net.URL
import java.time.Duration

/**
 * Represents the input needed to create a provider
 *
 * @property name The name of the provider
 * @property url The URL of the provider
 * @property frequency The frequency at which the provider's data should be polled
 * @property isActive Whether the provider is active or not
 */
data class ProviderInput(
    val name: String,
    val url: URL,
    val frequency: Duration,
    val isActive: Boolean
)