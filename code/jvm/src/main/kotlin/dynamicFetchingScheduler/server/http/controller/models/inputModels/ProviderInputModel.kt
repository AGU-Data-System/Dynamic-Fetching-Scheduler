package dynamicFetchingScheduler.server.http.controller.models.inputModels

import dynamicFetchingScheduler.server.domain.ProviderInput
import java.net.URL
import java.time.Duration

/**
 * Input model for a provider
 *
 * @property name The name of the provider
 * @property url The URL of the provider
 * @property frequency The frequency at which the provider's data should be polled,
 * in [ISO-8601](https://en.wikipedia.org/wiki/ISO_8601#Durations) format
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
	fun toProviderInput() = ProviderInput(
		name = name,
		url = URL(url),
		frequency = Duration.parse(frequency),
		isActive = isActive
	)

	/**
	 * Checks if the URL is valid
	 *
	 * @return Whether the URL is valid
	 */
	fun urlIsValid() = try {
		URL(url)
		true
	} catch (e: Exception) {
		false
	}

	/**
	 * Checks if the frequency is valid
	 *
	 * @return Whether the frequency is valid
	 */
	fun frequencyIsValid() = try {
		Duration.parse(frequency)
		true
	} catch (e: Exception) {
		false
	}
}