package dynamicFetchingScheduler.server.repository.rawData

import dynamicFetchingScheduler.server.domain.RawData
import org.jdbi.v3.core.Handle

class JDBIRawDataRepository(private val handle: Handle) : RawDataRepository {
    override fun saveRawData(rawData: RawData) {
        TODO("Not yet implemented")
    }
}