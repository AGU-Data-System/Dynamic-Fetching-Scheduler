package dynamicFetchingScheduler.server.repository.rawData

import dynamicFetchingScheduler.server.domain.RawData

interface RawDataRepository {
    fun saveRawData(rawData: RawData)
}