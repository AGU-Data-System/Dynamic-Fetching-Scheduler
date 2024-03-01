package uagSystem.server.repository.jdbi.mappers

import java.net.URL
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import uagSystem.server.domain.Provider
import uagSystem.server.repository.parsePostgresIntervalToDuration

/**
 * Maps a row of the result set to a [Provider]
 * @see RowMapper
 * @see Provider
 */
class ProviderMapper : RowMapper<Provider> {
	/**
	 * Maps a row of the result set to a [Provider]
	 * @param rs The result set to map
	 * @param ctx The statement context
	 * @return The [Provider] from the result set
	 */
	override fun map(rs: ResultSet, ctx: StatementContext?): Provider {
		return Provider(
			name = rs.getString("name"),
			url = URL(rs.getString("url")),
			frequency = parsePostgresIntervalToDuration(rs.getString("frequency")),
			isActive = rs.getBoolean("is_active")
		)
	}
}
