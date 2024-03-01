package uagSystem.server.repository

import uagSystem.server.repository.provider.ProviderRepository
import uagSystem.server.repository.rawData.RawDataRepository

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
