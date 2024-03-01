package dynamicFetchingScheduler.server.repository.provider

import dynamicFetchingScheduler.server.domain.Provider
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
	 * @param providerId The id of the provider to update
	 * @param lastFetch The time to update the field to
	 */
	fun updateLastFetch(providerId: Int, lastFetch: LocalDateTime)

	/**
	 * Add a provider to the database.
	 *
	 * @param provider The provider to add
	 */
	fun addProvider(provider: Provider)

	/**
	 * Update a provider in the database.
	 *
	 * @param provider The provider to update
	 */
	fun updateProvider(provider: Provider)

	/**
	 * Delete a provider from the database.
	 *
	 * @param url The URL of the provider to delete
	 */
	fun deleteProvider(url: URL)

	/**
	 * Get a provider from the database.
	 *
	 * @param url The URL of the provider to get
	 * @return The provider
	 */
	fun findByUrl(url: URL): Provider?
}