package dynamicFetchingScheduler.server.http.controller.models

import dynamicFetchingScheduler.server.domain.ProviderRawData
import dynamicFetchingScheduler.server.domain.ProviderWithData

data class ProviderRawDataOutputModel(
    val fetchTime: String,
    val data: String
) {
    constructor(providerRawData: ProviderRawData) : this(
        fetchTime = providerRawData.fetchTime.toString(),
        data = providerRawData.data
    )
}

data class ProviderWithDataOutputModel(
    val id: Int,
    val name: String,
    val url: String,
    val frequency: String,
    val isActive: Boolean,
    val lastFetch: String?,
    val dataList: List<ProviderRawDataOutputModel>
) {
    constructor(provider: ProviderWithData) : this(
        id = provider.id,
        name = provider.name,
        url = provider.url.toString(),
        frequency = provider.frequency.toString(),
        isActive = provider.isActive,
        lastFetch = provider.lastFetch?.toString(),
        dataList = provider.dataList.map { ProviderRawDataOutputModel(it) }
    )
}

