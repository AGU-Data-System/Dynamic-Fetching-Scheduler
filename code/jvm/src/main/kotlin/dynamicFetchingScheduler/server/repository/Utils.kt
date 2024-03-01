package dynamicFetchingScheduler.server.repository

import java.time.Duration
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import dynamicFetchingScheduler.server.repository.jdbi.mappers.DurationMapper
import dynamicFetchingScheduler.server.repository.jdbi.mappers.ProviderMapper

/**
 * Configures the JDBI instance with the requirements of the application
 * @return the configured JDBI instance
 */
fun Jdbi.configureWithAppRequirements(): Jdbi {
	installPlugin(KotlinPlugin())
	installPlugin(PostgresPlugin())

	registerColumnMapper(DurationMapper())

	registerRowMapper(ProviderMapper())

	return this
}

/**
 * Parses a Postgres interval string to a [Duration]
 * @param interval The interval string to parse
 * @return The [Duration] parsed from the interval string
 */
fun parsePostgresIntervalToDuration(interval: String): Duration {
	val pattern = """(\d+)\s+days?\s+(\d{2}):(\d{2}):(\d{2})(?:\.(\d{1,3}))?""".toRegex()

	val matchResult = pattern.find(interval) ?: return Duration.ZERO

	val (days, hours, minutes, seconds, millis) = matchResult.destructured

	return Duration
		.ofDays(days.toLongOrZero())
		.plusHours(hours.toLongOrZero())
		.plusMinutes(minutes.toLong())
		.plusSeconds(seconds.toLong())
		.plusMillis(millis.toLongOrZero())
}

private fun String.toLongOrZero() = toLongOrNull() ?: 0