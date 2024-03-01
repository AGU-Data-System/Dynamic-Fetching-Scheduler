package uagSystem.server.domain

import java.net.URL
import java.time.Duration

/**
 * Represents a provider and its configuration.
 *
 * @property name The name of the provider
 * @property url The URL of the provider
 * @property frequency The frequency at which the provider's data should be polled
 * @property isActive Whether the provider is active or not
 * @property lastFetch The time since the last request to the provider was made
 */
data class Provider(
	val name: String,
	val url: URL,
	val frequency: Duration,
	val isActive: Boolean,
	val lastFetch: Duration? = null
)