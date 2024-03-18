package dynamicFetchingScheduler.server.http.controller

import dynamicFetchingScheduler.server.domain.ProviderWithData
import dynamicFetchingScheduler.server.http.URIs
import dynamicFetchingScheduler.server.http.controller.models.inputModels.ProviderInputModel
import dynamicFetchingScheduler.server.http.controller.models.outputModels.ProviderListOutputModel
import dynamicFetchingScheduler.server.http.controller.models.outputModels.ProviderOutputModel
import dynamicFetchingScheduler.server.http.controller.models.outputModels.ProviderWithDataOutputModel
import dynamicFetchingScheduler.server.service.ProviderService
import dynamicFetchingScheduler.utils.Failure
import dynamicFetchingScheduler.utils.Success
import java.time.LocalDateTime
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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
					result.value.provider,
					if (result.value.isScheduled) "active" else "inactive"
				)
				ResponseEntity.created(URIs.provider(result.value.provider.id)).body(ProviderOutputModel(result.value.provider))
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
	 * @param id The id of the provider to update
	 * @param provider The provider to be updated
	 */
	@PostMapping(URIs.UPDATE_PROVIDER)
	fun update(@PathVariable id: Int, @RequestBody provider: ProviderInputModel): ResponseEntity<*> {
		val newProvider = provider.toProviderInput()

		return when (val result = providerService.updateProvider(id, newProvider)) {
			is Success -> {
				logger.info("Provider updated successfully")
				logger.info("The updated provider is: {} and is {}", result.value.provider, result.value.isScheduled)
				ResponseEntity.ok().body(ProviderOutputModel(result.value.provider))
			}

			is Failure -> {
				logger.error("Failed to update provider: {}, with id: {} and error: {}", provider, id, result.value)
				ResponseEntity.badRequest().body(result.value.toString())
			}
		}
	}

	/**
	 * Deletes a provider.
	 *
	 * @param id The id of the provider to delete
	 */
	@DeleteMapping(URIs.UPDATE_PROVIDER)
	fun delete(@PathVariable id: Int): ResponseEntity<String> {
		return when (val result = providerService.deleteProvider(id)) {
			is Success -> {
				logger.info("Provider deleted successfully")
				ResponseEntity.ok().body("Provider with id: $id deleted successfully")
			}

			is Failure -> {
				logger.error("Failed to delete provider with id: {}, and error {}", id, result.value)
				ResponseEntity.badRequest().body(result.value.toString())
			}
		}
	}

	/**
	 * Gets all providers.
	 */
	@GetMapping(URIs.PROVIDERS)
	fun allProviders(): ResponseEntity<*> {
		logger.info("Fetching all providers")

		val result = providerService.getProviders()
		logger.info("Providers fetched successfully")
		return ResponseEntity.ok().body(ProviderListOutputModel(result))
	}

	/**
	 * Gets a provider and its data by URL.
	 *
	 * @param id The id of the provider to get
	 */
	@GetMapping(URIs.UPDATE_PROVIDER)
	fun providerWithData(
		@PathVariable id: Int,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) beginDate: LocalDateTime,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: Optional<LocalDateTime>,
		@RequestParam(defaultValue = "0") page: Int,
		@RequestParam(defaultValue = "10") size: Int
	): ResponseEntity<*> {
		return when (val result = providerService.getProviderWithData(id, beginDate, endDate.orElse(LocalDateTime.now()), page, size)) {
			is Success -> {
				logger.info("Provider fetched successfully")
				val provider = result.value.provider
				val pagedData = result.value.data
				val providerWithData = ProviderWithData(provider, pagedData.items) //TODO: Probably not correct because it's domain and should be in service layer
				ResponseEntity
					.ok()
					.addPaginationHeaders(pagedData.totalItems, pagedData.totalPages, pagedData.currentPage, pagedData.items.size)
					.body(ProviderWithDataOutputModel(providerWithData))
			}

			is Failure -> {
				logger.error("Failed to fetch provider with id: {}, and error {}", id, result.value)
				ResponseEntity.badRequest().body(result.value.toString())
			}
		}
	}
}

