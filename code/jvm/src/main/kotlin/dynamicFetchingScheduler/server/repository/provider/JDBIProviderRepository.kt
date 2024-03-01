package dynamicFetchingScheduler.server.repository.provider

import dynamicFetchingScheduler.server.domain.Provider
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.time.LocalDateTime

/**
 * A JDBI implementation of the [ProviderRepository].
 * @property handle The JDBI handle to perform the database operations.
 * @see ProviderRepository
 */
class JDBIProviderRepository(private val handle: Handle) : ProviderRepository {

    /**
     * Add a provider to the database.
     *
     * @param provider The provider to add.
     */
    override fun addProvider(provider: Provider): Int {
        return handle.createUpdate("INSERT INTO provider (name, url, frequency, last_fetched, is_active) VALUES (:name, :url, :frequency, :lastFetch, :isActive)")
            .bind("name", provider.name)
            .bind("url", provider.url.toString())
            .bind("frequency", provider.frequency)
            .bind("lastFetch", provider.lastFetch)
            .bind("isActive", provider.isActive)
            .executeAndReturnGeneratedKeys("id")
            .mapTo<Int>()
            .one()
    }

    /**
     * Update a provider in the database.
     *
     * @param provider The provider to update.
     */
    override fun updateProvider(provider: Provider) {
        handle.createUpdate("UPDATE provider SET url = :url, frequency = :frequency, is_active = :isActive, last_fetch = :lastFetch WHERE name = :name") //todo: this is wrong.
            .bind("name", provider.name)
            .bind("url", provider.url.toString())
            .bind("frequency", provider.frequency)
            .bind("isActive", provider.isActive)
            .bind("lastFetch", provider.lastFetch)
            .execute()
    }

    override fun getActiveProviders(): List<Provider> {
        return handle.createQuery("SELECT * FROM provider WHERE is_active = true")
            .mapTo<Provider>()
            .list()
    }

    override fun updateLastFetch(providerId: Int, lastFetch: LocalDateTime) {
        handle.createUpdate("UPDATE provider SET last_fetch = :lastFetch WHERE id = :id")
            .bind("id", providerId)
            .bind("lastFetch", lastFetch)
            .execute()
    }

    /**
     * Delete a provider from the database.
     *
     * @param url The url of the provider to delete.
     */
    override fun deleteProvider(providerId: Int) {
        handle.createUpdate("DELETE FROM provider WHERE url = :url")
            .bind("url", url.toString())
            .execute()
    }
}
