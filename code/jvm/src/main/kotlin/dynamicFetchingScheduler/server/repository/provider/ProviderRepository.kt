package dynamicFetchingScheduler.server.repository.provider

import dynamicFetchingScheduler.server.domain.Provider
import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.domain.RawData
import java.net.URL
import java.time.LocalDateTime

/**
 * Repository for the Provider operations.
 */
interface ProviderRepository {

	/**
	 * Get all active providers from the database.
	 *
	 * @return The list of active providers
	 */
	fun getActiveProviders(): List<Provider>

	/**
	 * Updates the provider's lastFetch field
	 *
	 * @param providerURL The URL of the provider to update
	 * @param lastFetched The time to update the field to
	 */
	fun updateLastFetch(providerURL: URL, lastFetched: LocalDateTime)

	/**
	 * Add a provider to the database.
	 *
	 * @param provider The provider to add
	 */
	fun addProvider(provider: ProviderInput): Provider

	/**
	 * Update a provider in the database.
	 *
	 * @param provider The provider to update
	 */
	fun updateProvider(provider: ProviderInput): Provider

	/**
	 * Delete a provider from the database.
	 *
	 * @param url The URL of the provider to delete
	 */
	fun deleteProvider(url: URL): Int

	/**
	 * Get a provider from the database.
	 *
	 * @param url The URL of the provider to get
	 * @return The provider
	 */
	fun findByUrl(url: URL): Provider?

	/**
	 * Count the total number of providers in the database.
	 *
	 * @return The total number of providers
	 */
	fun countTotalProviders(): Int

	/**
	 * Count the total number of data for a provider in the database within a date range.
	 *
	 * @param providerId The ID of the provider to get
	 * @param beginDate The beginning date of the data
	 * @param endDate The end date of the data
	 *
	 * @return The total number of data
	 */
	fun countTotalProviderDataWithinDateRange(providerId: Int, beginDate: LocalDateTime, endDate: LocalDateTime): Int

	/**
	 * Get all providers from the database, paginated.
	 *
	 * @param page The page number to get
	 * @param size The size of each page
	 *
	 * @return The list of providers, and the pagination information
	 */
	fun findPaginatedProviders(page: Int, size: Int): List<Provider>

	/**
	 * Get the data of a provider from the database, paginated within a date range.
	 *
	 * @param providerId The ID of the provider to get
	 * @param beginDate The beginning date of the data
	 * @param endDate The end date of the data
	 * @param page The page number to get
	 * @param size The size of each page
	 *
	 * @return The provider
	 */
	fun findProviderDataWithinDateRange(
		providerId: Int,
		beginDate: LocalDateTime,
		endDate: LocalDateTime,
		page: Int,
		size: Int
	): List<RawData>
}