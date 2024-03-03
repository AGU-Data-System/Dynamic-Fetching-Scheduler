package dynamicFetchingScheduler.server.domain

data class ProviderOutputData (
    val id: Int,
    val name: String,
    val url: String,
    val timestamp: String,
    val data: String
)