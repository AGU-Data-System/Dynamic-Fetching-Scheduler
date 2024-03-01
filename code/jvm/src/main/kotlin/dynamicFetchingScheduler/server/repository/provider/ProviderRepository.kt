package dynamicFetchingScheduler.server.repository.provider

import dynamicFetchingScheduler.server.domain.Provider
import java.time.LocalDateTime

/**
 * Repository for the Provider database operations.
 */
interface ProviderRepository {

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
     * @return The id of the added provider
     */
    fun addProvider(provider: Provider): Int

    /**
     * Update a provider in the database.
     *
     * @param provider The provider to update
     */
    fun updateProvider(provider: Provider)

    /**
     * Delete a provider from the database.
     *
     * @param providerId The id of the provider to delete
     */
    fun deleteProvider(providerId: Int)
}