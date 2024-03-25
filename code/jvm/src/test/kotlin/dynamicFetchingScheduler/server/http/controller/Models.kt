package dynamicFetchingScheduler.server.http.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

/**
 * Models for the provider controller.
 */
object ProviderModels {

	/**
	 * Model to send when creating a provider.
	 *
	 * @property name The name of the provider
	 * @property url The URL of the provider
	 * @property frequency The frequency at which the provider's data should be polled,
	 * in [ISO-8601](https://en.wikipedia.org/wiki/ISO_8601#Durations) format
	 * @property isActive Whether the provider is active or not
	 */
	data class SendModel(
		val name: String,
		val url: String,
		val frequency: String,
		val isActive: Boolean
	)

	/**
	 * Model received when creating, updating or deleting a provider.
	 *
	 * @property id The ID of the provider
	 * @property name The name of the provider
	 * @property url The URL of the provider
	 * @property frequency The frequency at which the provider's data should be polled,
	 * in [ISO-8601](https://en.wikipedia.org/wiki/ISO_8601#Durations) format
	 * @property isActive Whether the provider is active or not
	 * @property lastFetch The last time the provider was fetched
	 */
	data class ProviderResponse(
		val id: Int,
		val name: String,
		val url: String,
		val frequency: String,
		val isActive: Boolean,
		val lastFetch: String?
	) {
		/**
		 * Converts the response model to a provider without the last fetch time.
		 *
		 * @return The provider without the last fetch time
		 * @see ProviderWOLastFetch
		 * @see ProviderResponse
		 */
		data class ProviderWOLastFetch(
			val id: Int,
			val name: String,
			val url: String,
			val frequency: String,
			val isActive: Boolean
		)

		/**
		 * Converts the response model to a provider without the last fetch time.
		 *
		 * @return The provider without the last fetch time
		 * @see ProviderWOLastFetch
		 * @see ProviderResponse
		 */
		fun takeLastFetch() = ProviderWOLastFetch(id, name, url, frequency, isActive)

	}

	/**
	 * Converts a byte array to a provider response.
	 *
	 * @return The provider response
	 */
	fun ByteArray.toProviderResponse(): ProviderResponse {
		val objectMapper = jacksonObjectMapper()
		val jsonNode = objectMapper.readTree(this)
		return ProviderResponse(
			id = jsonNode.get("id").asInt(),
			name = jsonNode.get("name").asText(),
			url = jsonNode.get("url").asText(),
			frequency = jsonNode.get("frequency").asText(),
			isActive = jsonNode.get("isActive").asBoolean(),
			lastFetch = jsonNode.get("lastFetch").asTextOrNull()
		)
	}

	/**
	 * Model received when getting a list of providers.
	 *
	 * @property providers The list of providers
	 * @property size The size of the list
	 */
	data class ProviderResponseList(
		val providers: List<ProviderResponse>,
		val size: Int = providers.size
	) {

		/**
		 * Model List of providers without the last fetch time.
		 */
		data class ProviderListWOLastFetch(
			val providers: List<ProviderResponse.ProviderWOLastFetch>,
			val size: Int = providers.size
		)

		/**
		 * Converts the response model to a list of providers without the last fetch time.
		 *
		 * @return The list of providers without the last fetch time
		 * @see ProviderResponse.ProviderWOLastFetch
		 * @see ProviderResponseList
		 */
		fun takeLastFetch() = ProviderListWOLastFetch(providers.map { it.takeLastFetch() })

	}

	/**
	 * Converts a byte array to a provider list response.
	 *
	 * @return The provider list response
	 */
	fun ByteArray.toProviderResponseList(): ProviderResponseList {
		val objectMapper = jacksonObjectMapper()
		val jsonNode = objectMapper.readTree(this)
		val providers = jsonNode.get("providers").map { provider ->
			ProviderResponse(
				id = provider.get("id").asInt(),
				name = provider.get("name").asText(),
				url = provider.get("url").asText(),
				frequency = provider.get("frequency").asText(),
				isActive = provider.get("isActive").asBoolean(),
				lastFetch = provider.get("lastFetch").asTextOrNull()
			)
		}
		return ProviderResponseList(providers)

	}

	/**
	 * Model of the received provider's data.
	 *
	 * @property fetchTime The time the data was fetched
	 * @property data The data fetched
	 */
	data class ProviderDataResponse(
		val fetchTime: String,
		val data: String
	)

	/**
	 * Converts a JsonNode to a provider data response.
	 *
	 * @return The provider data response
	 */
	private fun JsonNode.toProviderDataResponse(): ProviderDataResponse {
		return ProviderDataResponse(
			fetchTime = this.get("fetchTime").asText(),
			data = this.get("data").asText()
		)
	}

	/**
	 * Model received when getting a provider with its data.
	 *
	 * @property id The ID of the provider
	 * @property name The name of the provider
	 * @property url The URL of the provider
	 * @property frequency The frequency at which the provider's data should be polled,
	 * in [ISO-8601](https://en.wikipedia.org/wiki/ISO_8601#Durations) format
	 * @property isActive Whether the provider is active or not
	 * @property lastFetch The last time the provider was fetched
	 * @property dataList The data of the provider
	 */
	data class ProviderWithDataResponse(
		val id: Int,
		val name: String,
		val url: String,
		val frequency: String,
		val isActive: Boolean,
		val lastFetch: String?,
		val dataList: List<ProviderDataResponse>
	)

	/**
	 * Converts a byte array to a provider with data response.
	 *
	 * @return The provider with data response
	 */
	fun ByteArray.toProviderWithDataResponse(): ProviderWithDataResponse {
		val objectMapper = jacksonObjectMapper()
		val jsonNode = objectMapper.readTree(this)

		return ProviderWithDataResponse(
			id = jsonNode.get("id").asInt(),
			name = jsonNode.get("name").asText(),
			url = jsonNode.get("url").asText(),
			frequency = jsonNode.get("frequency").asText(),
			isActive = jsonNode.get("isActive").asBoolean(),
			lastFetch = jsonNode.get("lastFetch").asTextOrNull(),
			dataList = jsonNode.get("dataList").map { data ->
				data.toProviderDataResponse()
			}
		)
	}

	/**
	 * Util function to get a text value from a JsonNode or null if it is "null".
	 */
	private fun JsonNode.asTextOrNull(): String? {
		return if (this.asText() == "null") null else this.asText()
	}
}