package dynamicFetchingScheduler.server

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import dynamicFetchingScheduler.server.repository.Transaction
import dynamicFetchingScheduler.server.repository.TransactionManager
import dynamicFetchingScheduler.server.repository.configureWithAppRequirements
import dynamicFetchingScheduler.server.repository.jdbi.JDBITransaction
import dynamicFetchingScheduler.utils.Either
import dynamicFetchingScheduler.utils.Failure
import dynamicFetchingScheduler.utils.Success

private val jdbi =
	Jdbi.create(
		PGSimpleDataSource().apply {
			setURL(System.getenv("DB_URL"))
		}
	).configureWithAppRequirements()

/**
 * Creates and configures a [Jdbi] instance with the app requirements.
 */
fun jbdiTest() =
	Jdbi.create(
		PGSimpleDataSource().apply {
			setURL(System.getenv("DB_URL"))
		}
	).configureWithAppRequirements()

/**
 * Executes the given block with a [Handle] and rolls back the transaction.
 * @param block The block to execute
 * @return The result of the block
 */
fun testWithHandleAndRollback(block: (Handle) -> Unit) =
	jdbi.useTransaction<Exception> { handle ->
		block(handle)
		handle.rollback()
	}

/**
 * Executes the given block with a [TransactionManager] that never commits.
 * @param block The block to execute
 * @return The result of the block
 */
fun testWithTransactionManagerAndRollback(block: (TransactionManager) -> Unit) =
	jdbi.useTransaction<Exception> { handle ->
		val transaction = JDBITransaction(handle)

		// a test TransactionManager that never commits
		val transactionManager = object : TransactionManager {
			override fun <R> run(block: (Transaction) -> R): R {
				return block(transaction)
				// n.b. no commit happens
			}
		}
		block(transactionManager)

		// finally, we roll back everything
		handle.rollback()
	}

/**
 * Accesses if the given [Either] is a [Success]
 * and returns its value if not returns null
 * @receiver The [Either] to access
 * @return The value of the [Success] or null
 */
fun <L, R> Either<L, R>.successOrNull(): R? =
	when (this) {
		is Success -> this.value
		else -> null
	}

/**
 * Accesses if the given [Either] is a [Failure]
 * and returns its value if not returns null
 * @receiver The [Either] to access
 * @return The value of the [Failure] or null
 */
fun <L, R> Either<L, R>.failureOrNull(): L? =
	when (this) {
		is Failure -> this.value
		else -> null
	}