package uagSystem.server

/**
 * Environment variables
 */
object Environment {

	/**
	 * Get the database url from the environment variables
	 */
	fun getDbUrl() = System.getenv(KEY_DB_URL) ?: throw Exception("Missing env var $KEY_DB_URL")

	private const val KEY_DB_URL = "DB_URL"
}
