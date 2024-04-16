package dynamicFetchingScheduler.server.repository.provider

import dynamicFetchingScheduler.server.domain.Provider
import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.domain.RawData
import java.time.ZonedDateTime
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
			handle.createQuery("SELECT id, name, url, extract(epoch from frequency) as frequency, is_active, last_fetched FROM provider WHERE is_active = true")
				.mapTo<Provider>()
				.list()

		logger.info("Fetched active providers: {}", providers)

		return providers
	}

	/**
	 * Updates the provider's lastFetch field
	 *
	 * @param id The id of the provider to update
	 * @param lastFetched The time to update the field to
	 */
	override fun updateLastFetch(id: Int, lastFetched: ZonedDateTime) {

		logger.info("Updating last fetch for provider: {}", id)

		handle.createUpdate("UPDATE provider SET last_fetched = :lastFetched WHERE id = :id")
			.bind("id", id)
			.bind("lastFetched", lastFetched)
			.execute()

		logger.info("Updated last fetch for provider: {}", id)
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
	 * @param id The ID of the provider to update.
	 * @param provider The provider to update.
	 */
	override fun updateProvider(id: Int, provider: ProviderInput): Provider {

		val oldProvider = handle.createQuery(
			"""
            SELECT id, name, url, extract(epoch from frequency) as frequency, is_active, last_fetched 
            FROM provider 
            WHERE id = :id
            """.trimIndent()
		)
			.bind("id", id)
			.mapTo<Provider>()
			.one()

		logger.info("Updating provider: {}", oldProvider)

		handle.createUpdate(
			"""
			UPDATE provider SET name = :name, 
			frequency = :frequency, 
			is_active = :isActive,
			url = :url
			WHERE id = :id
			""".trimIndent()
		)
			.bind("id", id)
			.bind("name", provider.name)
			.bind("url", provider.url.toString())
			.bind("frequency", provider.frequency)
			.bind("isActive", provider.isActive)
			.execute()

		val newProvider = Provider(id, provider, oldProvider.lastFetch)

		logger.info("Updated provider: {}", newProvider)

		return newProvider
	}

	/**
	 * Delete a provider from the database.
	 *
	 * @param id The url of the provider to delete.
	 */
	override fun deleteProvider(id: Int) {

		logger.info("Deleting provider data from provider with id: {}", id)

		handle.createUpdate("DELETE FROM raw_data WHERE provider_id = :id")
			.bind("id", id)
			.execute()

		logger.info("Deleting provider with id: {}", id)

		handle.createUpdate("DELETE FROM provider WHERE id = :id")
			.bind("id", id)
			.execute()

		logger.info("Provider deleted and all its data")
	}

	/**
	 * Get a provider from the database.
	 *
	 * @param id The URL of the provider to get
	 * @return The provider
	 */
	override fun find(id: Int): Provider? {

		logger.info("Fetching provider with id: {}", id)

		val provider = handle.createQuery(
			"""
            SELECT id, name, url, extract(epoch from frequency) as frequency, is_active, last_fetched 
            FROM provider 
            WHERE id = :id
            """.trimIndent()
		)
			.bind("id", id)
			.mapTo<Provider>()
			.firstOrNull()

		logger.info("Fetched provider by URL: {}", provider)

		return provider
	}

	/**
	 * Get all providers from the database, paginated.
	 *
	 * @return The list of providers
	 */
	override fun allProviders(): List<Provider> {
		logger.info("Fetching all providers")

		val providers = handle.createQuery("""
			SELECT id, name, url, extract(epoch from frequency) as frequency, is_active, last_fetched
			FROM provider
			ORDER BY id
    	""".trimIndent())
			.mapTo<Provider>()
			.list()

		logger.info("Fetched all providers, number of providers: {}", providers.size)

		return providers
	}

	/**
	 * Get data of a provider from the database, paginated.
	 *
	 * @param providerId The ID of the provider to get data for
	 * @param beginDate The beginning date of the data
	 * @param endDate The end date of the data
	 * @param page The page number to get
	 * @param size The size of each page
	 */
	override fun findProviderDataWithinDateRange(providerId: Int, beginDate: ZonedDateTime, endDate: ZonedDateTime, page: Int, size: Int): List<RawData> {
		val offset = page * size

		val dataQuery = """
        SELECT provider_id, fetch_time, data
        FROM raw_data
        WHERE provider_id = :providerId AND fetch_time BETWEEN :beginDate AND :endDate
        ORDER BY fetch_time
		LIMIT :size OFFSET :offset
    """.trimIndent()

		return handle.createQuery(dataQuery)
			.bind("providerId", providerId)
			.bind("beginDate", beginDate.toLocalDateTime())
			.bind("endDate", endDate.toLocalDateTime())
			.bind("size", size)
			.bind("offset", offset)
			.mapTo<RawData>()
			.list()
	}


	/**
	 * Count the total number of providers in the database.
	 *
	 * @return The total number of providers
	 */
	override fun countTotalProviders(): Int {

		logger.info("Counting total providers")

		val nrOfProviders = handle.createQuery("SELECT COUNT(*) FROM provider")
			.mapTo<Int>()
			.one()

		logger.info("Total providers: {}", nrOfProviders)

		return nrOfProviders
	}

	/**
	 * Count the total number of data for a provider in the database within a date range.
	 *
	 * @param providerId The ID of the provider to get
	 * @param beginDate The beginning date of the data
	 * @param endDate The end date of the data
	 *
	 * @return The total number of data
	 */
	override fun countTotalProviderDataWithinDateRange(
		providerId: Int,
		beginDate: ZonedDateTime,
		endDate: ZonedDateTime
	): Int {
		val countQuery = """
			SELECT COUNT(*)
			FROM raw_data
			WHERE provider_id = :providerId AND fetch_time BETWEEN :beginDate AND :endDate
		""".trimIndent()

		return handle.createQuery(countQuery)
			.bind("providerId", providerId)
			.bind("beginDate", beginDate)
			.bind("endDate", endDate)
			.mapTo<Int>()
			.one()
	}

	companion object {
		private val logger = LoggerFactory.getLogger(JDBIProviderRepository::class.java)
	}
}
