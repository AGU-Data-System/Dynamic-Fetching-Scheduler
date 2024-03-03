package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.domain.ProviderWithData
import dynamicFetchingScheduler.server.repository.TransactionManager
import dynamicFetchingScheduler.server.service.errors.AddProviderError
import dynamicFetchingScheduler.server.service.errors.DeleteProviderError
import dynamicFetchingScheduler.server.service.errors.GetProviderError
import dynamicFetchingScheduler.server.service.errors.UpdateProviderError
import dynamicFetchingScheduler.utils.failure
import dynamicFetchingScheduler.utils.success
import java.net.URL
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProviderService(
    private val transactionManager: TransactionManager,
    private val schedulerService: ProviderSchedulerService
) {
    /**
     * Adds a provider.
     *
     * @param providerInput The provider to be added
     *
     * @return The result of adding the provider
     */
    fun addProvider(providerInput: ProviderInput): AddProviderResult {
        val provider = transactionManager.run {
            if (it.providerRepository.findByUrl(providerInput.url) != null) return@run null

            logger.info("Adding Provider to repository")
            return@run it.providerRepository.addProvider(providerInput)

        } ?: return failure(AddProviderError.ProviderAlreadyExists)

        return if (provider.isActive) {
            logger.info("New provider is Active: {}", provider)
            logger.info("Adding Provider to scheduler")
            schedulerService.scheduleProviderTask(provider)
            logger.info("Provider added successfully to scheduler")
            success(ProviderSuccess(provider, SCHEDULED))

        } else success(ProviderSuccess(provider, !SCHEDULED))
    }

    /**
     * Updates a provider.
     *
     * @param provider The provider to be updated
     */
    fun updateProvider(provider: ProviderInput): UpdateProviderResult {
        val updatedProvider = transactionManager.run {
            it.providerRepository.findByUrl(provider.url) ?: return@run null

            logger.info("Updating Provider with url: {}", provider.url)
            return@run it.providerRepository.updateProvider(provider)

        } ?: return failure(UpdateProviderError.ProviderNotFound)

        return if (updatedProvider.isActive) {
            logger.info("Updated provider is Active: {}", provider)
            schedulerService.stopProviderTask(updatedProvider.id)
            schedulerService.scheduleProviderTask(updatedProvider)
            success(ProviderSuccess(updatedProvider, SCHEDULED))

        } else {
            logger.info("Updated provider is Inactive: {}", provider)
            schedulerService.stopProviderTask(updatedProvider.id)
            success(ProviderSuccess(updatedProvider, !SCHEDULED))
        }
    }

    /**
     * Deletes a provider.
     *
     * @param url The URL of the provider to be deleted
     */
    fun deleteProvider(url: URL): DeleteProviderResult {
        val deletedProvider = transactionManager.run {
            val deletedProvider = it.providerRepository.findByUrl(url) ?: return@run null
            it.providerRepository.deleteProvider(url)
            deletedProvider
        } ?: return failure(DeleteProviderError.ProviderNotFound)
        if (deletedProvider.isActive) {
            logger.info("Stopping scheduling of provider: {}", deletedProvider)
            schedulerService.stopProviderTask(deletedProvider.id)
        }

        return success(Unit)
    }

    /**
     * Gets all providers and their data.
     *
     * @return The list of providers and their data
     */
    fun getAllProviders(): List<ProviderWithData> {
        return transactionManager.run {
            it.providerRepository.getProvidersWithData()
        }
    }

    /**
     * Gets a provider and its data.
     *
     * @param url The URL of the provider to get
     * @return The provider and its data
     */
    fun getProvider(url: URL): GetProviderResult {
        return transactionManager.run {
            val provider = it.providerRepository.findByUrl(url) ?: return@run failure(GetProviderError.ProviderNotFound)
            val dataList = it.providerRepository.getProviderData(provider.id)
            success(ProviderWithData(provider, dataList))
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ProviderService::class.java)
        private const val SCHEDULED = true
    }
}