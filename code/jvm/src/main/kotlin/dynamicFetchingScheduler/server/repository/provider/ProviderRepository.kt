package dynamicFetchingScheduler.server.repository.provider

import dynamicFetchingScheduler.server.domain.Provider
import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.domain.ProviderWithData
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
    fun deleteProvider(url: URL): Int //TODO: check

    /**
     * Get a provider from the database.
     *
     * @param url The URL of the provider to get
     * @return The provider
     */
    fun findByUrl(url: URL): Provider?

    /**
     * Get all providers from the database.
     *
     * @return The list of providers
     */
    fun getProvidersWithData(): List<ProviderWithData>

    /**
     * Get the data of a provider from the database.
     *
     * @param id The URL of the provider to get
     * @return The provider
     */
    fun getProviderData(id: Int): List<RawData>
}