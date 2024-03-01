package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.Provider
import dynamicFetchingScheduler.server.repository.TransactionManager
import dynamicFetchingScheduler.server.service.errors.AddProviderError
import dynamicFetchingScheduler.server.service.errors.DeleteProviderError
import dynamicFetchingScheduler.server.service.errors.UpdateProviderError
import dynamicFetchingScheduler.utils.failure
import dynamicFetchingScheduler.utils.success
import java.net.URL
import org.springframework.stereotype.Service

@Service
class ProviderService(
	private val transactionManager: TransactionManager,
	//private val schedulerService: ProviderSchedulerService
) {
	/**
	 * Adds a provider.
	 *
	 * @param provider The provider to be added
	 *
	 * @return The result of adding the provider
	 */
	fun addProvider(provider: Provider): AddProviderResult {
		return transactionManager.run {
			val existingProvider = it.providerRepository.findByUrl(provider.url)
			if (existingProvider != null) {
				return@run failure(AddProviderError.ProviderAlreadyExists)
			}

			it.providerRepository.addProvider(provider)

			val result =
				it.providerRepository.findByUrl(provider.url) ?: return@run failure(AddProviderError.UnknownError)
			return@run success(result)

			//schedulerService.scheduleProviderTask(provider) // TODO: Right place?
		}

	}

	/**
	 * Updates a provider.
	 *
	 * @param provider The provider to be updated
	 */
	fun updateProvider(provider: Provider): UpdateProviderResult {
		return transactionManager.run {
			it.providerRepository.findByUrl(provider.url) ?: return@run failure(UpdateProviderError.ProviderNotFound)

			it.providerRepository.updateProvider(provider)

			val result =
				it.providerRepository.findByUrl(provider.url) ?: return@run failure(UpdateProviderError.UnknownError)
			return@run success(result)

			//schedulerService.stopProviderTask(provider.url) // TODO: Right place?
			//schedulerService.scheduleProviderTask(provider) // TODO: Right place?

		}
	}

	/**
	 * Deletes a provider.
	 *
	 * @param url The URL of the provider to be deleted
	 */
	fun deleteProvider(url: URL): DeleteProviderResult {
		return transactionManager.run {
			it.providerRepository.findByUrl(url) ?: return@run failure(DeleteProviderError.ProviderNotFound)

			it.providerRepository.deleteProvider(url)

			it.providerRepository.findByUrl(url) ?: return@run success(Unit)

			return@run failure(DeleteProviderError.UnknownError)
		}
	}
}