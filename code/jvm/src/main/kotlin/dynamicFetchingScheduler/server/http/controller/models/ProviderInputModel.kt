package dynamicFetchingScheduler.server.http.controller.models

import dynamicFetchingScheduler.server.domain.Provider
import java.net.URL
import java.time.Duration

/**
 * Input model for a provider
 *
 * @property name The name of the provider
 * @property url The URL of the provider
 * @property frequency The frequency at which the provider's data should be polled, in [ISO-8601](https://en.wikipedia.org/wiki/ISO_8601#Durations) format
 * @property isActive Whether the provider is active or not
 */
data class ProviderInputModel(
    val name: String,
    val url: String,
    val frequency: String,
    val isActive: Boolean
) {
    /**
     * Converts the input model to a provider
     *
     * @return The provider
     */
    fun toProvider() = Provider(
        name = name,
        url = URL(url),
        frequency = Duration.parse(frequency),
        isActive = isActive
    )
}