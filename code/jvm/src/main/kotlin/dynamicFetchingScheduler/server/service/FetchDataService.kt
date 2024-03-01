package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.RawData
import dynamicFetchingScheduler.server.repository.TransactionManager
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime

@Service
class FetchDataService(
    private val transactionManager: TransactionManager,
    private val restTemplate: RestTemplate // For HTTP requests TODO: FIND A WAY TO FETCH HTTP REQUESTS
) {
    fun fetchAndSave(providerId: Int) {
        transactionManager.run {
            val provider = it.providerRepository.findById(providerId) //TODO: FIX
            if (provider != null && provider.isActive) {
                val response = restTemplate.getForObject(provider.url, String::class.java)
                if (response != null) {
                    it.rawDataRepository.saveRawData(RawData(providerId, LocalDateTime.now(), response))
                    it.providerRepository.updateLastFetch(providerId, LocalDateTime.now())
                }
            }
        }
    }
}
