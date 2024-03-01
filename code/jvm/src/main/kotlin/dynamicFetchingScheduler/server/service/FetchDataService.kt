package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.repository.TransactionManager
import java.net.URL
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

/**
 * Service for fetching data from a provider
 */
@Service
class FetchDataService(
	private val transactionManager: TransactionManager,
	private val restTemplate: RestTemplate // For HTTP requests TODO: FIND A WAY TO FETCH HTTP REQUESTS
) {
	/**
	 * Fetches the data from the provider and saves it to the database.
	 *
	 * @param providerURL The URL of the provider to fetch from
	 */
	fun fetchAndSave(providerURL: URL) {
		transactionManager.run {
			val provider = it.providerRepository.findByUrl(providerURL) //TODO: FIX
			if (provider != null && provider.isActive) {
				val response = restTemplate.getForObject(provider.url.toString(), String::class.java)
				if (response != null) {
					//it.rawDataRepository.saveRawData(RawData(providerURL, LocalDateTime.now(), response))
					//it.providerRepository.updateLastFetch(providerURL, LocalDateTime.now())
				}
			}
		}
	}
}
