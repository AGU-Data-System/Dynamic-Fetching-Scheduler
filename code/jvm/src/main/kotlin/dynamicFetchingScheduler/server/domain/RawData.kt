package dynamicFetchingScheduler.server.domain

import org.json.JSONObject
import java.time.LocalDateTime

data class RawData(
    val providerId: Int,
    val fetchTime: LocalDateTime,
    val data: JSONObject
)
