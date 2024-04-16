package dynamicFetchingScheduler.server.repository.jdbi.mappers

import dynamicFetchingScheduler.server.domain.ProviderRawData
import java.sql.ResultSet
import java.time.ZonedDateTime
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

/**
 * Maps a row of the result set to a [ProviderRawData]
 * @see RowMapper
 * @see ProviderRawData
 */
class ProviderRawDataMapper : RowMapper<ProviderRawData> {

	/**
	 * Maps a row of the result set to a [ProviderRawData]
	 * @param rs The result set to map
	 * @param ctx The statement context
	 * @return The [ProviderRawData] from the result set
	 */
	override fun map(rs: ResultSet, ctx: StatementContext): ProviderRawData {
		return ProviderRawData(
			fetchTime = rs.getTimestamp("fetch_time").let { ZonedDateTime.of(it.toLocalDateTime(), ZonedDateTime.now().zone) },
			data = rs.getString("data")
		)
	}
}