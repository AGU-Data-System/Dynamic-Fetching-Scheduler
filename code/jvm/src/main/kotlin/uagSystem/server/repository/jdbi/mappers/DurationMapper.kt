package uagSystem.server.repository.jdbi.mappers

import java.sql.ResultSet
import java.time.Duration
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import uagSystem.server.repository.parsePostgresIntervalToDuration

/**
 * Maps a column of the result set to a [Duration]
 * @see ColumnMapper
 * @see Duration
 */
class DurationMapper : ColumnMapper<Duration> {

	/**
	 * Maps a row of the result set to a [Duration]
	 * @param rs The result set to map
	 */
	override fun map(rs: ResultSet, columnNumber: Int, ctx: StatementContext?): Duration {
		val interval = rs.getString(columnNumber)
		return parsePostgresIntervalToDuration(interval)
	}
}