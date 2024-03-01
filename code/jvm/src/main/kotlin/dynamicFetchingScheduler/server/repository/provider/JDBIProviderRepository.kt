package dynamicFetchingScheduler.server.repository.provider

import dynamicFetchingScheduler.server.domain.Provider
import java.net.URL
import java.time.LocalDateTime
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.LoggerFactory

/**
 * A JDBI implementation of the [ProviderRepository].
 * @property handle The JDBI handle to perform the database operations.
 * @see ProviderRepository
 */
class JDBIProviderRepository(private val handle: Handle) : ProviderRepository {

	/**
	 * Get all active providers from the database.
	 *
	 * @return The list of active providers
	 */
	override fun getActiveProviders(): List<Provider> {

		logger.info("Fetching active providers")

		val providers = handle.createQuery("SELECT * FROM provider WHERE is_active = true")
			.mapTo<Provider>()
			.list()

		logger.info("Fetched active providers: {}", providers)

		return providers
	}

	/**
	 * Updates the provider's lastFetch field
	 *
	 * @param providerId The id of the provider to update
	 * @param lastFetch The time to update the field to
	 */
	override fun updateLastFetch(providerId: Int, lastFetch: LocalDateTime) {

		logger.info("Updating last fetch for provider: {}", providerId)

		handle.createUpdate("UPDATE provider SET last_fetch = :lastFetch WHERE id = :id")
			.bind("id", providerId)
			.bind("lastFetch", lastFetch)
			.execute()

		logger.info("Updated last fetch for provider: {}", providerId)
	}

	/**
	 * Add a provider to the database.
	 *
	 * @param provider The provider to add.
	 */
	override fun addProvider(provider: Provider) {

		logger.info("Adding provider: {}", provider)

		handle.createUpdate("INSERT INTO provider (name, url, frequency, last_fetched, is_active) VALUES (:name, :url, :frequency, :lastFetch, :isActive)")
			.bind("name", provider.name)
			.bind("url", provider.url.toString())
			.bind("frequency", provider.frequency)
			.bind("lastFetch", provider.lastFetch)
			.bind("isActive", provider.isActive)
			.execute()

		logger.info("Added provider: {}", provider.name)
	}

	/**
	 * Update a provider in the database.
	 *
	 * @param provider The provider to update.
	 */
	override fun updateProvider(provider: Provider) {

		val oldProvider = handle.createQuery("SELECT * FROM provider WHERE url = :url")
			.bind("url", provider.url.toString())
			.mapTo<Provider>()
			.one()

		logger.info("Updating provider: {}", oldProvider)

		handle.createUpdate(
			"""
			UPDATE provider SET name = :name, 
			frequency = :frequency, 
			is_active = :isActive, 
			last_fetched = :lastFetch 
			WHERE url = :url
			""".trimIndent()
		)
			.bind("name", provider.name)
			.bind("url", provider.url.toString())
			.bind("frequency", provider.frequency)
			.bind("isActive", provider.isActive)
			.bind("lastFetch", provider.lastFetch)
			.execute()

		logger.info("Updated provider: {}", provider)
	}

	/**
	 * Delete a provider from the database.
	 *
	 * @param url The url of the provider to delete.
	 */
	override fun deleteProvider(url: URL) {

		logger.info("Deleting provider: {}", url)

		handle.createUpdate("DELETE FROM provider WHERE url = :url")
			.bind("url", url.toString())
			.execute()

		logger.info("Provider deleted")
	}

	/**
	 * Get a provider from the database.
	 *
	 * @param url The URL of the provider to get
	 * @return The provider
	 */
	override fun findByUrl(url: URL): Provider? {

		logger.info("Fetching provider by URL: {}", url)

		val provider = handle.createQuery("SELECT * FROM provider WHERE url = :url")
			.bind("url", url.toString())
			.mapTo<Provider>()
			.firstOrNull()

		logger.info("Fetched provider by URL: {}", provider)

		return provider
	}

	companion object {
		private val logger = LoggerFactory.getLogger(JDBIProviderRepository::class.java)
	}
}
