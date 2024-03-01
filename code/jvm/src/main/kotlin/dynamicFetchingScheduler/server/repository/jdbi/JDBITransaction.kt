package dynamicFetchingScheduler.server.repository.jdbi

import dynamicFetchingScheduler.server.repository.Transaction
import dynamicFetchingScheduler.server.repository.provider.JDBIProviderRepository
import dynamicFetchingScheduler.server.repository.provider.ProviderRepository
import dynamicFetchingScheduler.server.repository.rawData.JDBIRawDataRepository
import dynamicFetchingScheduler.server.repository.rawData.RawDataRepository
import org.jdbi.v3.core.Handle

/**
 * A JDBI implementation of [Transaction]
 * @see Transaction
 * @see Handle
 */
class JDBITransaction(private val handle: Handle) : Transaction {

	override val providerRepository: ProviderRepository = JDBIProviderRepository(handle)
	override val rawDataRepository: RawDataRepository = JDBIRawDataRepository(handle)

	/**
	 * Rolls back the transaction
	 */
	override fun rollback() {
		handle.rollback()
	}
}