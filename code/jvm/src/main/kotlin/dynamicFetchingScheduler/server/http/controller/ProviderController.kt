package dynamicFetchingScheduler.server.http.controller

import dynamicFetchingScheduler.server.http.URIs
import dynamicFetchingScheduler.server.http.controller.models.ProviderInputModel
import dynamicFetchingScheduler.server.service.ProviderSchedulerService
import dynamicFetchingScheduler.server.service.ProviderService
import dynamicFetchingScheduler.utils.Failure
import dynamicFetchingScheduler.utils.Success
import java.net.URL
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for fetching data from the providers.
 *
 * @property schedulerService The service for fetching data from the providers, injected by Spring
 */
@RestController
class ProviderController(
	private val schedulerService: ProviderSchedulerService,
	private val providerService: ProviderService
) {
	companion object {
		private val logger = LoggerFactory.getLogger(ProviderController::class.java)
	}

	/**
	 * Adds or updates a provider.
	 *
	 * @param provider The provider to be added or updated
	 */
	@PostMapping(URIs.PROVIDER)
	fun addProvider(@RequestBody provider: ProviderInputModel): ResponseEntity<*> {
		val newProvider = provider.toProvider()
		return when (val result = providerService.addProvider(newProvider)) {
			is Success -> {
				logger.info("Provider added successfully")
				ResponseEntity.ok().body(result.value.toString())
			}

			is Failure -> {
				logger.error("Failed to add provider: {}, with error {}", provider, result.value)
				ResponseEntity.badRequest().body(result.value.toString())
			}
		}

		// schedulerService.scheduleProviderTask(provider.toProvider())
	}

	/**
	 * Updates a provider.
	 *
	 * @param provider The provider to be updated
	 */
	@PostMapping(URIs.UPDATE_PROVIDER) // TODO change this
	fun updateProvider(@RequestBody provider: ProviderInputModel): ResponseEntity<*> {
		//schedulerService.stopProviderTask(id)
		//schedulerService.scheduleProviderTask(provider.toProvider())
		val newProvider = provider.toProvider()
		return when (val result = providerService.updateProvider(newProvider)) {
			is Success -> {
				logger.info("Provider updated successfully")
				ResponseEntity.ok().body("")
			}

			is Failure -> {
				logger.error("Failed to update provider: {}, with error {}", provider, result.value)
				ResponseEntity.badRequest().body(result.value.toString())
			}
		}
		// return ResponseEntity.ok("Provider updated successfully") // TODO: Adjust the response message
	}

	/**
	 * Deletes a provider.
	 *
	 * @param url The URL of the provider to delete
	 */
	@DeleteMapping(URIs.PROVIDER)
	fun deleteProvider(@RequestBody url: String): ResponseEntity<String> {
		//val providerId = // TODO: Extract providerId from the URL or adjust the method parameter
		//schedulerService.stopProviderTask(providerId)
		val providerURL = URL(url)
		return when (val result = providerService.deleteProvider(providerURL)) {
			is Success -> {
				logger.info("Provider deleted successfully")
				ResponseEntity.ok().body("")
			}

			is Failure -> {
				logger.error("Failed to delete provider with url: {}, and error {}", url, result.value)
				ResponseEntity.badRequest().body(result.value.toString())
			}
		}
	}

	//TODO: Maneira de dar get aos dados de um ou todos os providers, por exemplo com uma data, ou o ultimo fetch
}