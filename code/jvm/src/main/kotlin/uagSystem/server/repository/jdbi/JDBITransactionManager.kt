package uagSystem.server.repository.jdbi

import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component
import uagSystem.server.repository.Transaction
import uagSystem.server.repository.TransactionManager

/**
 * A JDBI implementation of [TransactionManager]
 * @see TransactionManager
 */
@Component
class JDBITransactionManager(private val jdbi: Jdbi) : TransactionManager {

	/**
	 * Runs a transaction and returns the result
	 *
	 * @param block the block to be run in a transaction
	 *
	 * @return The result of the transaction
	 */
	override fun <R> run(block: (Transaction) -> R): R =
		jdbi.inTransaction<R, Exception> { handle ->
			val transaction = JDBITransaction(handle)
			block(transaction)
		}
}
