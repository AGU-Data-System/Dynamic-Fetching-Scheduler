package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.http.controller.models.ProviderInputModel
import dynamicFetchingScheduler.server.repository.TransactionManager
import org.springframework.stereotype.Service

@Service
class ProviderService(
    private val transactionManager: TransactionManager,
    private val schedulerService: ProviderSchedulerService
) {
    fun addOrUpdateProvider(providerInputModel: ProviderInputModel){
        //TODO: FAZER
    }

    fun deleteProvider(url: String){
        //TODO: FAZER
    }
}