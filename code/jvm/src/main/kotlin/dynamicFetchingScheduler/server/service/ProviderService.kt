package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.repository.TransactionManager
import dynamicFetchingScheduler.server.service.errors.AddProviderError
import dynamicFetchingScheduler.server.service.errors.DeleteProviderError
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
			 it.providerRepository.findByUrl(providerInput.url) ?: return@run null

			 logger.info("Adding Provider to repository")
			 return@run it.providerRepository.addProvider(providerInput)

		} ?: return failure(AddProviderError.ProviderAlreadyExists)

		return if(provider.isActive){
			logger.info("New provider is Active: {}", provider)
			logger.info("Adding Provider to Scheduling")
			schedulerService.scheduleProviderTask(provider)
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

		}?: return failure(UpdateProviderError.ProviderNotFound)

		return if(updatedProvider.isActive){
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
		val providerId = transactionManager.run {
			it.providerRepository.findByUrl(url) ?: return@run failure(DeleteProviderError.ProviderNotFound)

			return@run it.providerRepository.deleteProvider(url)
		}
		return if (providerId is Int) {
			logger.info("Deleting Schedule provider: {}", providerId)
			schedulerService.stopProviderTask(providerId)
			success(Unit)
		} else failure(DeleteProviderError.ProviderNotFound)
	}

//	/**
//	 * Get a provider by URL.
//	 *
//	 * @param url The URL of the provider to get
//	 * @return The provider
//	 */
//	fun getProvider(url: URL): Provider? {
//		return transactionManager.run {
//			it.providerRepository.findByUrl(url)
//		}
//	}
//
//	/**
//	 * Get all providers.
//	 *
//	 * @return The list of all providers
//	 */
//	fun getProviders(): List<Provider> {
//		return transactionManager.run {
//			it.providerRepository.getAllProviders()
//		}
//	}

	companion object {
		private val logger = LoggerFactory.getLogger(ProviderService::class.java)
		private const val SCHEDULED = true
	}
}