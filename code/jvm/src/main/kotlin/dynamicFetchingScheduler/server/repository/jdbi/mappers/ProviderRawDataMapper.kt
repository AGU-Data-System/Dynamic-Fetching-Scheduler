package dynamicFetchingScheduler.server.repository.jdbi.mappers

import dynamicFetchingScheduler.server.domain.ProviderRawData
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class ProviderRawDataMapper : RowMapper<ProviderRawData> {
    override fun map(rs: ResultSet, ctx: StatementContext): ProviderRawData {
        return ProviderRawData(
            fetchTime = rs.getTimestamp("fetch_time").toLocalDateTime(),
            data = rs.getString("data")
        )
    }
}