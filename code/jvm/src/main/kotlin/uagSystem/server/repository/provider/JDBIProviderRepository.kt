package uagSystem.server.repository.provider

import java.net.URL
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import uagSystem.server.domain.Provider

/**
 * A JDBI implementation of the [ProviderRepository].
 * @property handle The JDBI handle to perform the database operations.
 * @see ProviderRepository
 */
class JDBIProviderRepository(private val handle: Handle) : ProviderRepository {

	/**
	 * Find a provider by its url.
	 *
	 * @param url The url of the provider.
	 *
	 * @return The provider with the given url, or null if it does not exist.
	 */
	override fun findByUrl(url: URL): Provider? {
		return handle.createQuery("SELECT * FROM provider WHERE url = :url")
			.bind("url", url.toString())
			.mapTo<Provider>()
			.firstOrNull()
	}

	/**
	 * Add a provider to the database.
	 *
	 * @param provider The provider to add.
	 */
	override fun add(provider: Provider) {
		handle.createUpdate("INSERT INTO provider (name, url, frequency, last_fetched, is_active) VALUES (:name, :url, :frequency, :lastFetch, :isActive)")
			.bind("name", provider.name)
			.bind("url", provider.url.toString())
			.bind("frequency", provider.frequency)
			.bind("lastFetch", provider.lastFetch)
			.bind("isActive", provider.isActive)
			.execute()
	}

	/**
	 * Update a provider in the database.
	 *
	 * @param provider The provider to update.
	 */
	override fun update(provider: Provider) {
		handle.createUpdate("UPDATE provider SET url = :url, frequency = :frequency, is_active = :isActive, last_fetch = :lastFetch WHERE name = :name") //todo: this is wrong.
			.bind("name", provider.name)
			.bind("url", provider.url.toString())
			.bind("frequency", provider.frequency)
			.bind("isActive", provider.isActive)
			.bind("lastFetch", provider.lastFetch)
			.execute()
	}

	/**
	 * Delete a provider from the database.
	 *
	 * @param url The url of the provider to delete.
	 */
	override fun delete(url: URL) {
		handle.createUpdate("DELETE FROM provider WHERE url = :url")
			.bind("url", url.toString())
			.execute()
	}
}
