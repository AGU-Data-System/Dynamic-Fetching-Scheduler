package dynamicFetchingScheduler.server.repository.jdbi.mappers

import dynamicFetchingScheduler.server.domain.RawData
import java.sql.ResultSet
import java.time.ZonedDateTime
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

/**
 * Maps a row of the result set to a [RawData]
 * @see RowMapper
 * @see RawData
 */
class RawDataMapper : RowMapper<RawData> {

	/**
	 * Maps a row of the result set to a [RawData]
	 *
	 * @param rs The result set to map
	 * @param ctx The statement context
	 * @return The [RawData] from the result set
	 */
	override fun map(rs: ResultSet?, ctx: StatementContext?): RawData {
		return RawData(
			providerId = rs!!.getInt("provider_id"),
			fetchTime = rs.getTimestamp("fetch_time").let { ZonedDateTime.of(it.toLocalDateTime(), ZonedDateTime.now().zone) },
			data = rs.getString("data")
		)
	}
}