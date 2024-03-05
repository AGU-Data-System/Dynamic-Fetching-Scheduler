package dynamicFetchingScheduler.server.repository

import dynamicFetchingScheduler.server.repository.jdbi.mappers.ProviderMapper
import dynamicFetchingScheduler.server.repository.jdbi.mappers.ProviderRawDataMapper
import dynamicFetchingScheduler.server.repository.jdbi.mappers.ProviderWithDataMapper
import dynamicFetchingScheduler.server.repository.jdbi.mappers.RawDataMapper
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin

/**
 * Configures the JDBI instance with the requirements of the application
 * @return the configured JDBI instance
 */
fun Jdbi.configureWithAppRequirements(): Jdbi {
	installPlugin(KotlinPlugin())
	installPlugin(PostgresPlugin())

	registerRowMapper(ProviderMapper())
	registerRowMapper(RawDataMapper())
	registerRowMapper(ProviderRawDataMapper())
	registerRowMapper(ProviderWithDataMapper())

	return this
}
