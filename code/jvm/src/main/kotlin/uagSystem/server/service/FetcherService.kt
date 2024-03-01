package uagSystem.server.service

import java.net.URL
import org.springframework.stereotype.Service
import uagSystem.server.domain.Provider
import uagSystem.server.repository.TransactionManager
import uagSystem.server.service.errors.AddOrUpdateProviderError
import uagSystem.server.service.errors.DeleteProviderError
import uagSystem.utils.failure
import uagSystem.utils.success

/**
 * A service for the sonar gas data
 */
@Service
class FetcherService(private val transactionManager: TransactionManager) {

	/**
	 * Adds or updates a provider.
	 * @param provider The provider to be added or updated.
	 * @return The result of the operation, Either a [AddOrUpdateProviderError] or the provider.
	 * @see AddOrUpdateProviderResult
	 */
	fun addOrUpdateProvider(provider: Provider): AddOrUpdateProviderResult {
		return transactionManager.run {
			val oldProvider = it.providerRepository.findByUrl(provider.url)
			if (oldProvider != null) {
				it.providerRepository.update(provider)
			} else {
				it.providerRepository.add(provider)
			}
			val result = it.providerRepository.findByUrl(provider.url)

			return@run if (result != null) {
				success(result)
			} else {
				failure(AddOrUpdateProviderError.UnknownError)
			}
		}
	}

	/**
	 * Deletes a provider by its name.
	 * @param url The url of the provider to be deleted.
	 * @return The result of the operation, Either a [DeleteProviderError] or the unit.
	 * @see DeleteProviderResult
	 */
	fun deleteProvider(url: URL): DeleteProviderResult {
		return transactionManager.run {
			val oldProvider = it.providerRepository.findByUrl(url)
			if (oldProvider != null) {
				it.providerRepository.delete(url)
			} else {
				failure(DeleteProviderError.ProviderNotFound)
			}
			val result = it.providerRepository.findByUrl(url)

			return@run if (result == null) {
				success(Unit)
			} else {
				failure(DeleteProviderError.UnknownError)
			}
		}
	}
}