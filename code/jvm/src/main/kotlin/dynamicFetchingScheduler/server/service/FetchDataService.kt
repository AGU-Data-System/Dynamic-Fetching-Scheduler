package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.RawData
import dynamicFetchingScheduler.server.repository.TransactionManager
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDateTime

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
     * @param providerURL The URL of the provider to fetch from
     */
    fun fetchAndSave(providerURL: URL) {
        logger.info("Fetching data from provider: {}", providerURL)
        val response = fetch(providerURL.toString())
        logger.info("Fetched data from provider with status code: {}", response.first)
        if (response.first != HttpStatus.OK.value()) return // TODO: ERROR With status code
        transactionManager.run {
            val provider = it.providerRepository.findByUrl(providerURL) ?: return@run
            it.rawDataRepository.saveRawData(RawData(provider.id, LocalDateTime.now(), response.second))
            it.providerRepository.updateLastFetch(providerURL, LocalDateTime.now())
            logger.info("Saved data from provider on Database with url: {} and name: {}", providerURL, provider.name)
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
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.statusCode() to response.body()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(FetchDataService::class.java)
    }
}

typealias Response = Pair<Int, String>
