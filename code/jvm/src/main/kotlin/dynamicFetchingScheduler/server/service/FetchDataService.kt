package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.RawData
import dynamicFetchingScheduler.server.repository.TransactionManager
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDateTime
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

/**
 * Service for fetching data from a provider
 */
@Service
class FetchDataService(
	private val transactionManager: TransactionManager
) {
	/**
	 * Fetches the data from the provider and saves it to the database.
	 *
	 * @param providerId The id of the provider to fetch from
	 * @param providerURL The URL of the provider to fetch from
	 */
	fun fetchAndSave(providerId: Int, providerURL: URL) {
		logger.info("Fetching data from provider: {}", providerURL)
		val response = fetch(providerURL.toString())
		logger.info("Fetched data from provider with status code: {}", response.statusCode)
		if (response.statusCode != HttpStatus.OK.value()) return
		transactionManager.run {
			val provider = it.providerRepository.find(providerId) ?: return@run
			val curTime = LocalDateTime.now()
			it.rawDataRepository.saveRawData(RawData(provider.id, curTime, response.body))
			it.providerRepository.updateLastFetch(providerId, curTime)
			logger.info("Saved data from provider on Database with url: {} and id: {}", providerURL, providerId)
		}
	}

	/**
	 * Makes a request to the given URL.
	 *
	 * @param url The URL to make the request to
	 * @return The response body
	 */
	private fun fetch(url: String): Response {
		val client = HttpClient.newHttpClient()
		val request = HttpRequest.newBuilder()
			.uri(URI.create(url))
			.GET()
			.build()
		try {
			val response = client.send(request, HttpResponse.BodyHandlers.ofString())
			return Response(response.statusCode(), response.body())
		} catch (e: Exception) {
			logger.error("Error fetching data from provider with url: {} and exception:", url, e)
			return Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.message.toString())
		}
	}

	/**
	 * Represents a response from a request.
	 *
	 * @property statusCode The status code of the response
	 * @property body The body of the response
	 */
	private data class Response(val statusCode: Int, val body: String)

	companion object {
		private val logger = LoggerFactory.getLogger(FetchDataService::class.java)
	}
}

