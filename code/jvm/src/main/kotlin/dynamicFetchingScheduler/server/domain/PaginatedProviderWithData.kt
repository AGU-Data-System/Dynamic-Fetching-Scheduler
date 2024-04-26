package dynamicFetchingScheduler.server.domain

import dynamicFetchingScheduler.utils.PaginationResult

/**
 * A provider with its paginated data.
 *
 * @property provider The provider
 * @property data The paginated data
 */
data class PaginatedProviderWithData(
	val provider: Provider,
	val data: PaginationResult<RawData>
)