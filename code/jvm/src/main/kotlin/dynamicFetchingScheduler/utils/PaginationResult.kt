package dynamicFetchingScheduler.utils

data class PaginationResult<T>(
    val items: List<T>,
    val totalItems: Int,
    val currentPage: Int,
    val totalPages: Int
)