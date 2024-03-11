package dynamicFetchingScheduler.server.http.controller

import dynamicFetchingScheduler.server.domain.ProviderWithData
import dynamicFetchingScheduler.server.http.URIs
import dynamicFetchingScheduler.server.http.controller.models.GetProviderOutputModel
import dynamicFetchingScheduler.server.http.controller.models.ProviderInputModel
import dynamicFetchingScheduler.server.http.controller.models.ProviderWithDataOutputModel
import dynamicFetchingScheduler.server.http.controller.models.toOutputModel
import dynamicFetchingScheduler.server.service.ProviderService
import dynamicFetchingScheduler.utils.Failure
import dynamicFetchingScheduler.utils.Success
import java.net.URL
import java.time.LocalDateTime
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for fetching data from the providers.
 *
 * @property providerService The service for manipulating providers, injected by Spring
 */
@RestController
class ProviderController(
	private val providerService: ProviderService
) {
	companion object {
		private val logger = LoggerFactory.getLogger(ProviderController::class.java)
	}

	/**
	 * Adds or updates a provider.
	 *
	 * @param providerInputModel The provider to be added or updated
	 */
	@PostMapping(URIs.PROVIDER)
	fun add(@RequestBody providerInputModel: ProviderInputModel): ResponseEntity<*> {
		val providerInput = providerInputModel.toProviderInput()

		return when (val result = providerService.addProvider(providerInput)) {
			is Success -> {
				logger.info("Provider added successfully")
				logger.info(
					"The added provider is: {} and he is currently {}",
					result.value.first,
					if (result.value.second) "active" else "inactive"
				)
				ResponseEntity.ok().body(GetProviderOutputModel(result.value.first)) //TODO: Should be 201, with location header
			}

			is Failure -> {
				logger.error("Failed to add provider: {}, with error {}", providerInputModel, result.value)
				ResponseEntity.badRequest().body(result.value.toString())
			}
		}
	}

	/**
	 * Updates a provider.
	 *
	 * @param provider The provider to be updated
	 */
	@PostMapping(URIs.UPDATE_PROVIDER) // TODO change this
	fun update(@RequestBody provider: ProviderInputModel): ResponseEntity<*> {
		val newProvider = provider.toProviderInput()

		return when (val result = providerService.updateProvider(newProvider)) {
			is Success -> {
				logger.info("Provider updated successfully")
				logger.info("The updated provider is: {} and is {}", result.value.first, result.value.second)
				ResponseEntity.ok().body(GetProviderOutputModel(result.value.first))
			}

			is Failure -> {
				logger.error("Failed to update provider: {}, with error {}", provider, result.value)
				ResponseEntity.badRequest().body(result.value.toString())
			}
		}
	}

	/**
	 * Deletes a provider.
	 *
	 * @param url The URL of the provider to delete
	 */
	@DeleteMapping(URIs.PROVIDER)
	fun delete(@RequestBody url: String): ResponseEntity<String> {
		val providerURL = URL(url)
		return when (val result = providerService.deleteProvider(providerURL)) {
			is Success -> {
				logger.info("Provider deleted successfully")
				ResponseEntity.noContent().build()
			}

			is Failure -> {
				logger.error("Failed to delete provider with url: {}, and error {}", url, result.value)
				ResponseEntity.badRequest().body(result.value.toString())
			}
		}
	}

	/**
	 * Gets all providers and their data.
	 *
	 * @param beginDate The beginning date of the data
	 * @param endDate The end date of the data
	 * @param page The page number to get, default is 0
	 * @param size The size of each page, default is 10
	 */
	@GetMapping(URIs.PROVIDERS)
	fun getProvidersWithData(
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) beginDate: LocalDateTime,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: Optional<LocalDateTime>,
		@RequestParam(defaultValue = "0") page: Int,
		@RequestParam(defaultValue = "10") size: Int
	): ResponseEntity<*> {
		logger.info("Fetching all providers")

		val result = providerService.getProvidersWithData(beginDate, endDate.orElse(LocalDateTime.now()), page, size)
		logger.info("Providers fetched successfully")
		return ResponseEntity
			.ok()
			.addPaginationHeaders(result.totalItems, result.totalPages, result.currentPage, result.items.size)
			.body(result.items.toOutputModel())
	}

	/**
	 * Gets a provider and its data by URL.
	 *
	 * @param url The URL of the provider to get
	 */
	@GetMapping(URIs.PROVIDER)
	fun getProviderWithData(
		@RequestBody url: String,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) beginDate: LocalDateTime,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: Optional<LocalDateTime>,
		@RequestParam(defaultValue = "0") page: Int,
		@RequestParam(defaultValue = "10") size: Int
	): ResponseEntity<*> {
		//val providerURL = URL(url)

		val providerURL = URL(url)
		return when (val result = providerService.getProvider(url, beginDate, endDate.orElse(LocalDateTime.now()), page, size)) {
			is Success -> {
				logger.info("Provider fetched successfully")
				val provider = result.value.first
				val pagedData = result.value.second
				val providerWithData = ProviderWithData(provider, pagedData.items) //TODO: Probably not correct because it's domain and should be in service layer
				ResponseEntity
					.ok()
					.addPaginationHeaders(pagedData.totalItems, pagedData.totalPages, pagedData.currentPage, pagedData.items.size)
					.body(ProviderWithDataOutputModel(providerWithData))
			}

			is Failure -> {
				logger.error("Failed to fetch provider with url: {}, and error {}", url, result.value)
				ResponseEntity.badRequest().body(result.value.toString())
			}
		}
	}
}

