package dynamicFetchingScheduler.server.repository.jdbi.mappers

import dynamicFetchingScheduler.server.domain.ProviderWithData
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class ProviderWithDataMapper : RowMapper<ProviderWithData> {

    override fun map(rs: ResultSet, ctx: StatementContext?): ProviderWithData {
        return ProviderWithData(
            id =,
            name =,
            url =,
            frequency =,
            isActive =,
            lastFetch =,
            dataList =
        )
    }
}