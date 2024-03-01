package uagSystem.server.repository.jdbi

import org.jdbi.v3.core.Handle
import uagSystem.server.repository.Transaction
import uagSystem.server.repository.provider.JDBIProviderRepository
import uagSystem.server.repository.provider.ProviderRepository
import uagSystem.server.repository.rawData.JDBIRawDataRepository
import uagSystem.server.repository.rawData.RawDataRepository

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