package dynamicFetchingScheduler.server.repository.jdbi.mappers

import dynamicFetchingScheduler.server.domain.Provider
import dynamicFetchingScheduler.server.domain.ProviderWithData
import dynamicFetchingScheduler.server.domain.RawData
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
		val providerId = rs.getInt("id")
		val provider = Provider(
			id = providerId,
			name = rs.getString("name"),
			url = URL(rs.getString("url")),
			frequency = Duration.ofSeconds(rs.getLong("frequency")),
			isActive = rs.getBoolean("is_active"),
			lastFetch = rs.getTimestamp("last_fetched")?.toLocalDateTime()
		)
		val rawData = mutableListOf<RawData>()
		do {
			val rawDataTimestamp = rs.getTimestamp("fetch_time")?.toLocalDateTime() ?: break
			val data = rs.getString("data")
			rawData.add(RawData(providerId, rawDataTimestamp, data))
		} while (rs.next())
		return ProviderWithData(provider, rawData)
	}
}