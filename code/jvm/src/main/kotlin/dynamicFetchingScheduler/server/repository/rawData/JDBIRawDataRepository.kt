package dynamicFetchingScheduler.server.repository.rawData

import org.jdbi.v3.core.Handle
import dynamicFetchingScheduler.server.domain.RawData

class JDBIRawDataRepository(private val handle: Handle) : RawDataRepository {
    override fun saveRawData(rawData: RawData) {
        TODO("Not yet implemented")
    }
}