package dynamicFetchingScheduler.server.repository.provider

import dynamicFetchingScheduler.server.domain.Provider
import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.domain.ProviderWithData
import dynamicFetchingScheduler.server.domain.RawData
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

		val providers =
			handle.createQuery("SELECT id, name, id, name, url, extract(epoch from frequency) as frequency, is_active, last_fetched FROM provider WHERE is_active = true")
				.mapTo<Provider>()
				.list()

		logger.info("Fetched active providers: {}", providers)

		return providers
	}

	/**
	 * Updates the provider's lastFetch field
	 *
	 * @param providerURL The URL of the provider to update
	 * @param lastFetched The time to update the field to
	 */
	override fun updateLastFetch(providerURL: URL, lastFetched: LocalDateTime) {

		logger.info("Updating last fetch for provider: {}", providerURL)

		handle.createUpdate("UPDATE provider SET last_fetched = :lastFetched WHERE url = :url")
			.bind("url", providerURL.toString())
			.bind("lastFetched", lastFetched)
			.execute()

		logger.info("Updated last fetch for provider: {}", providerURL)
	}

	/**
	 * Add a provider to the database.
	 *
	 * @param provider The provider to add.
	 */
	override fun addProvider(provider: ProviderInput): Provider {

		logger.info("Adding provider: {}", provider)

		val providerId =
			handle.createUpdate("INSERT INTO provider (name, url, frequency, is_active) VALUES (:name, :url, :frequency, :isActive)")
				.bind("name", provider.name)
				.bind("url", provider.url.toString())
				.bind("frequency", provider.frequency)
				.bind("isActive", provider.isActive)
				.executeAndReturnGeneratedKeys()
				.mapTo<Int>()
				.one()

		val newProvider = Provider(providerId, provider)

		logger.info("Added provider: {}", newProvider)

		return newProvider
	}

	/**
	 * Update a provider in the database.
	 *
	 * @param provider The provider to update.
	 */
	override fun updateProvider(provider: ProviderInput): Provider {

		val oldProvider = handle.createQuery(
			"""
            SELECT id, name, id, name, url, extract(epoch from frequency) as frequency, is_active, last_fetched 
            FROM provider 
            WHERE url = :url
            """.trimIndent()
		)
			.bind("url", provider.url.toString())
			.mapTo<Provider>()
			.one()

		logger.info("Updating provider: {}", oldProvider)

		val providerId = handle.createUpdate(
			"""
			UPDATE provider SET name = :name, 
			frequency = :frequency, 
			is_active = :isActive
			WHERE url = :url
			""".trimIndent()
		)
			.bind("name", provider.name)
			.bind("url", provider.url.toString())
			.bind("frequency", provider.frequency)
			.bind("isActive", provider.isActive)
			.executeAndReturnGeneratedKeys()
			.mapTo<Int>()
			.one()

		val newProvider = Provider(providerId, provider)
		logger.info("Updated provider: {}", newProvider)

		return newProvider
	}

	/**
	 * Delete a provider from the database.
	 *
	 * @param url The url of the provider to delete.
	 */
	override fun deleteProvider(url: URL): Int {

		logger.info("Deleting provider: {}", url)

		val providerId = handle.createUpdate("DELETE FROM provider WHERE url = :url")
			.bind("url", url.toString())
			.executeAndReturnGeneratedKeys("id")
			.mapTo<Int>()
			.one()

		logger.info("Provider deleted")
		return providerId
	}

	/**
	 * Get a provider from the database.
	 *
	 * @param url The URL of the provider to get
	 * @return The provider
	 */
	override fun findByUrl(url: URL): Provider? {

		logger.info("Fetching provider by URL: {}", url)

		val provider = handle.createQuery(
			"""
            SELECT id, name, id, name, url, extract(epoch from frequency) as frequency, is_active, last_fetched 
            FROM provider 
            WHERE url = :url
            """.trimIndent()
		)
			.bind("url", url.toString())
			.mapTo<Provider>()
			.firstOrNull()

		logger.info("Fetched provider by URL: {}", provider)

		return provider
	}

	/**
	 * Get all providers from the database.
	 *
	 * @return The list of all providers
	 */
	override fun getProvidersWithData(): List<ProviderWithData> {

		logger.info("Fetching providers from database")

		val providers = handle.createQuery(
			"""
            SELECT p.id, p.name, p.url, extract(epoch from p.frequency) as frequency, p.is_active, p.last_fetched, 
            r.fetch_time, r.data
            FROM provider p
            LEFT JOIN raw_data r ON p.id = r.provider_id 
            ORDER BY p.id, r.fetch_time
            """.trimIndent()
		)
			.mapTo<ProviderWithData>()
			.list()

		logger.info("Fetched {} providers from database", providers.size)

		return providers
	}

	/**
	 * Get the data of a provider from the database.
	 *
	 * @param id The URL of the provider to get
	 * @return The provider
	 */
	override fun getProviderData(id: Int): List<RawData> {
		logger.info("Fetching data for provider: {}", id)

		return handle.createQuery("SELECT * FROM raw_data WHERE provider_id = :id")
			.bind("id", id)
			.mapTo<RawData>()
			.list()
	}

	companion object {
		private val logger = LoggerFactory.getLogger(JDBIProviderRepository::class.java)
	}
}
