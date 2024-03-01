package dynamicFetchingScheduler.server.repository

import dynamicFetchingScheduler.server.repository.provider.ProviderRepository
import dynamicFetchingScheduler.server.repository.rawData.RawDataRepository

/**
 * A transaction for the repositories
 */
interface Transaction {

	val providerRepository: ProviderRepository

	val rawDataRepository: RawDataRepository

	/**
	 * Rolls back the transaction
	 */
	fun rollback()
}
