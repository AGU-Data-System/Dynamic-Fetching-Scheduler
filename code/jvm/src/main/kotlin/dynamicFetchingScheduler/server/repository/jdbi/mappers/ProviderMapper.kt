package dynamicFetchingScheduler.server.repository.jdbi.mappers

import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.repository.parsePostgresIntervalToDuration
import java.net.URL
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

/**
 * Maps a row of the result set to a [ProviderInput]
 * @see RowMapper
 * @see ProviderInput
 */
class ProviderMapper : RowMapper<ProviderInput> {
	/**
	 * Maps a row of the result set to a [ProviderInput]
	 * @param rs The result set to map
	 * @param ctx The statement context
	 * @return The [ProviderInput] from the result set
	 */
	override fun map(rs: ResultSet, ctx: StatementContext?): ProviderInput {
		return ProviderInput(
			id = rs.getInt("id"),
			name = rs.getString("name"),
			url = URL(rs.getString("url")),
			frequency = parsePostgresIntervalToDuration(rs.getString("frequency")),
			isActive = rs.getBoolean("is_active")
		)
	}
}
