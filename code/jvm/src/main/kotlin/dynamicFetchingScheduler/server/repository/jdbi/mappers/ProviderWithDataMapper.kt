package dynamicFetchingScheduler.server.repository.jdbi.mappers

import dynamicFetchingScheduler.server.domain.ProviderWithData
import java.net.URL
import java.sql.ResultSet
import java.time.Duration
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

/**
 * Maps a row of the result set to a [ProviderWithData]
 * @see RowMapper
 * @see ProviderWithData
 */
class ProviderWithDataMapper : RowMapper<ProviderWithData> {

	/**
	 * Maps a row of the result set to a [ProviderWithData]
	 * @param rs The result set to map
	 * @param ctx The statement context
	 * @return The [ProviderWithData] from the result set
	 */
	override fun map(rs: ResultSet, ctx: StatementContext?): ProviderWithData {
		return ProviderWithData(
			id = rs.getInt("id"),
			name = rs.getString("name"),
			url = URL(rs.getString("url")),
			frequency = Duration.ofSeconds(rs.getLong("frequency")),
			isActive = rs.getBoolean("is_active"),
			lastFetch = rs.getTimestamp("last_fetched")?.toLocalDateTime(),
			dataList = emptyList() // TODO: Implement this
		)
	}
}