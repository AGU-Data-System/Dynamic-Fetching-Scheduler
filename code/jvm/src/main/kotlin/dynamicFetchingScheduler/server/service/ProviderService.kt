package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.PaginatedProviderWithData
import dynamicFetchingScheduler.server.domain.Provider
import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.domain.ScheduledProvider
import dynamicFetchingScheduler.server.repository.TransactionManager
import dynamicFetchingScheduler.server.service.errors.GetProviderError
import dynamicFetchingScheduler.server.service.errors.UpdateProviderError
import dynamicFetchingScheduler.utils.PaginationResult
import dynamicFetchingScheduler.utils.failure
import dynamicFetchingScheduler.utils.success
import java.time.ZonedDateTime
import kotlin.math.ceil
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
			logger.info("Adding Provider to repository")
			return@run it.providerRepository.addProvider(providerInput)
		}

		return if (provider.isActive) {
			logger.info("New provider is Active: {}", provider)
			logger.info("Adding Provider to scheduler")
			schedulerService.scheduleProviderTask(provider)
			logger.info("Provider added successfully to scheduler")
			success(ScheduledProvider(provider, SCHEDULED))

		} else success(ScheduledProvider(provider, !SCHEDULED))
	}

	/**
	 * Updates a provider.
	 *
	 * @param providerId The id of the provider to be updated
	 * @param provider The provider to be updated
	 *
	 * @return The result of updating the provider
	 */
	fun updateProvider(providerId: Int, provider: ProviderInput): UpdateProviderResult {
		val updatedProvider = transactionManager.run {
			it.providerRepository.find(providerId) ?: return@run null

			logger.info("Updating Provider with url: {}", provider.url)
			return@run it.providerRepository.updateProvider(providerId, provider)

		} ?: return failure(UpdateProviderError.ProviderNotFound)

		return if (updatedProvider.isActive) {
			logger.info("Updated provider is Active: {}", provider)
			schedulerService.stopProviderTask(updatedProvider.id)
			schedulerService.scheduleProviderTask(updatedProvider)
			success(ScheduledProvider(updatedProvider, SCHEDULED))

		} else {
			logger.info("Updated provider is Inactive: {}", provider)
			schedulerService.stopProviderTask(updatedProvider.id)
			success(ScheduledProvider(updatedProvider, !SCHEDULED))
		}
	}

	/**
	 * Deletes a provider.
	 *
	 * @param id The id of the provider to be deleted
	 *
	 * @return The result of deleting the provider
	 */
	fun deleteProvider(id: Int): DeleteProviderResult {
		transactionManager.run {
			it.providerRepository.deleteProvider(id)
		}
		logger.info("Stopping scheduling of provider with id: {}", id)
		schedulerService.stopProviderTask(id)

		return success(Unit)
	}

	/**
	 * Gets all providers.
	 *
	 * @return A result containing the list of providers.
	 */
	fun getProviders(): List<Provider> {
		return transactionManager.run {
			logger.info("Fetching all providers")
			val providers = it.providerRepository.allProviders()
			logger.info("Fetched all providers")
			providers
		}
	}

	/**
	 * Gets a provider and its data.
	 *
	 * @param id The id of the provider to get
	 * @param beginDate The beginning date of the data
	 * @param endDate The end date of the data
	 * @param page The page number to get
	 * @param size The size of each page
	 *
	 * @return The provider and its data
	 */
	fun getProviderWithData(
		id: Int,
		beginDate: ZonedDateTime,
		endDate: ZonedDateTime,
		page: Int,
		size: Int
	): GetProviderResult {
		return transactionManager.run {
			val provider = it.providerRepository.find(id) ?: return@run failure(GetProviderError.ProviderNotFound)
			logger.info("Provider found with id: {}", id)

			val dataList = it.providerRepository.findProviderDataWithinDateRange(provider.id, beginDate, endDate, page, size)
			logger.info("Fetched data for provider with id: {} between: {} and: {}", id, beginDate, endDate)

			val totalItems = it.providerRepository.countTotalProviderDataWithinDateRange(provider.id, beginDate, endDate)
			logger.info("Total items for provider with id: {} between: {} and: {} is: {}", id, beginDate, endDate, totalItems)

			val totalPages = ceil(totalItems.toDouble() / size).toInt()
			logger.info("Total pages for provider with id: {} between: {} and: {} is: {}", id, beginDate, endDate, totalPages)

			val pageResult = PaginationResult(dataList, totalItems, page, totalPages)
			success(PaginatedProviderWithData(provider, pageResult))
		}
	}

	companion object {
		private val logger = LoggerFactory.getLogger(ProviderService::class.java)
		private const val SCHEDULED = true
	}
}