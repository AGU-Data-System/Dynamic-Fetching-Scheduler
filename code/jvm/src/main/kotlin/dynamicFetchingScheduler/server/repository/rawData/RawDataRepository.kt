package dynamicFetchingScheduler.server.repository.rawData

import dynamicFetchingScheduler.server.domain.RawData

/**
 * Repository for raw data from providers
 */
interface RawDataRepository {

	/**
	 * Save raw data to the database
	 *
	 * @param rawData The raw data to save
	 */
	fun saveRawData(rawData: RawData)
}