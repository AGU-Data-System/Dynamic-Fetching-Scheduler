package dynamicFetchingScheduler.server.http.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity

fun ResponseEntity.BodyBuilder.addPaginationHeaders(totalItems: Int, totalPages: Int, currentPage: Int, pageSize: Int) : ResponseEntity.BodyBuilder {
    val headersToAdd = HttpHeaders().apply {
        add("X-Total-Count", totalItems.toString())
        add("X-Total-Pages", totalPages.toString())
        add("X-Current-Page", currentPage.toString())
        add("X-Page-Size", pageSize.toString())
        if (currentPage > 0) {
            add("X-Previous-Page", (currentPage - 1).toString())
        }
        if (currentPage < totalPages - 1) {
            add("X-Next-Page", (currentPage + 1).toString())
        }
    }
    return this.headers(headersToAdd)
}