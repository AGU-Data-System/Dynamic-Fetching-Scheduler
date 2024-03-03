package dynamicFetchingScheduler.server.repository.rawData

import dynamicFetchingScheduler.server.domain.Provider
import dynamicFetchingScheduler.server.domain.RawData
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.LoggerFactory

/**
 * A JDBI implementation of [RawDataRepository]
 * @see RawDataRepository
 * @see Handle
 */
class JDBIRawDataRepository(private val handle: Handle) : RawDataRepository {

	/**
	 * Save raw data to the database
	 *
	 * @param rawData The raw data to save
	 */
	override fun saveRawData(rawData: RawData) {
		logger.info("Fetching provider for url: {}", rawData.providerUrl)

		val provider = handle.createQuery("SELECT id, name, url, extract(epoch from frequency) as frequency, is_active, last_fetched FROM provider WHERE url = :url")
			.bind("url", rawData.providerUrl.toString())
			.mapTo<Provider>()
			.one()

		logger.info("Saving raw data for provider: {}", provider.name)

		handle.createUpdate("INSERT INTO raw_data (provider_id, fetch_time, data) VALUES (:providerId, :timestamp, cast(:data as jsonb))")
			.bind("providerId", provider.id)
			.bind("data", rawData.data)
			.bind("timestamp", rawData.fetchTime)
			.execute()

		logger.info("Raw data saved for provider: {}", provider.name)
	}

	companion object {
		private val logger = LoggerFactory.getLogger(JDBIRawDataRepository::class.java)
	}
}