package dynamicFetchingScheduler.server.testUtils.jdbiUtils

import dynamicFetchingScheduler.server.repository.Transaction
import dynamicFetchingScheduler.server.repository.TransactionManager
import dynamicFetchingScheduler.server.repository.configureWithAppRequirements
import dynamicFetchingScheduler.server.repository.jdbi.JDBITransaction
import java.nio.file.Files
import java.nio.file.Paths
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.postgresql.ds.PGSimpleDataSource

object SchemaManagementExtension : BeforeAllCallback, AfterAllCallback {

	private const val SCHEMA_NAME = "test_schema"
	private const val CRETE_TABLES_SQL_PATH = "src/sql/create-tables.sql"

	/**
	 * Creates and configures a [Jdbi] instance with the app requirements.
	 */
	private fun jdbiTest() =
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
		jdbiTest().useTransaction<Exception> { handle ->
			handle.execute("SET Schema '$SCHEMA_NAME'") // To ensure that the schema is set
			block(handle)
			handle.rollback()
		}

	/**
	 * Executes the given block with a [TransactionManager] that never commits.
	 * @param block The block to execute
	 * @return The result of the block
	 */
	fun testWithTransactionManagerAndRollback(block: (TransactionManager) -> Unit) =
		jdbiTest().useTransaction<Exception> { handle ->
			handle.execute("SET Schema '$SCHEMA_NAME'") // To ensure that the schema is set
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


	override fun beforeAll(context: ExtensionContext) {
		jdbiTest().useHandle<Exception> { handle ->
			handle.execute("CREATE SCHEMA IF NOT EXISTS $SCHEMA_NAME")
			handle.execute("SET Schema '$SCHEMA_NAME'")

			executeSqlFromFile(handle)
		}
	}

	override fun afterAll(context: ExtensionContext) {
		jdbiTest().useHandle<Exception> { handle ->
			handle.execute("DROP SCHEMA IF EXISTS $SCHEMA_NAME CASCADE")
		}
	}

	private fun executeSqlFromFile(handle: Handle) {
		val path = Paths.get(CRETE_TABLES_SQL_PATH)
		val sql = Files.readString(path)
		// Assuming your SQL statements are separated by semicolons,
		// Adjust this logic if your SQL script has a different format
		sql.split(";").forEach { statement ->
			val trimmedStatement = statement.trim()
			if (trimmedStatement.isNotEmpty()) {
				handle.execute(trimmedStatement)
			}
		}
	}
}