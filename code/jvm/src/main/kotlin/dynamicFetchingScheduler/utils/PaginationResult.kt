package dynamicFetchingScheduler.utils

/**
 * Result of a paginated query
 *
 * @property items The items of the current page
 * @property totalItems The total number of items
 * @property currentPage The current page
 * @property totalPages The total number of pages
 */
data class PaginationResult<T>(
	val items: List<T>,
	val totalItems: Int,
	val currentPage: Int,
	val totalPages: Int
)