package dynamicFetchingScheduler.server.repository.rawData

import dynamicFetchingScheduler.server.domain.RawData
import org.jdbi.v3.core.Handle
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

		handle.createUpdate("INSERT INTO raw_data (provider_id, fetch_time, data) VALUES (:providerId, :timestamp, cast(:data as jsonb))")
			.bind("providerId", rawData.providerId)
			.bind("data", rawData.data)
			.bind("timestamp", rawData.fetchTime)
			.execute()

		logger.info("Raw data saved for provider: {}", rawData.providerId)
	}

	companion object {
		private val logger = LoggerFactory.getLogger(JDBIRawDataRepository::class.java)
	}
}