package uagSystem.server.repository

/**
 * A transaction Manager for the repositories
 */
interface TransactionManager {

	/**
	 * Runs a transaction and returns the result
	 */
	fun <R> run(block: (Transaction) -> R): R
}
