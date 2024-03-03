package dynamicFetchingScheduler.server.http.controller

import dynamicFetchingScheduler.server.http.URIs
import dynamicFetchingScheduler.server.http.controller.models.ProviderInputModel
import dynamicFetchingScheduler.server.http.controller.models.ProviderOutputModel
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
				ResponseEntity.ok().body(ProviderOutputModel(result.value.first))
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
				ResponseEntity.ok().body("")
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
				ResponseEntity.ok().body("")
			}

			is Failure -> {
				logger.error("Failed to delete provider with url: {}, and error {}", url, result.value)
				ResponseEntity.badRequest().body(result.value.toString())
			}
		}
	}

//	@GetMapping(URIs.PROVIDER)
//	fun provider(): ResponseEntity<*> {
//		val result = providerService.getProvider()
//		return ResponseEntity.ok().body(ProviderOutputModel(result))
//	}
//
//	/**
//	 * Gets all providers.
//	 */
//	@GetMapping(URIs.PROVIDERS)
//	fun providers(): ResponseEntity<*> {
//		val result = providerService.getProviders()
//		return ResponseEntity.ok().body(ProviderListOutputModel(result))
//	}
	//GET LAST X DATA FROM PROVIDER (in case of omition of x, return all data)
	//GET LAST X DATA FROM ALL PROVIDERS (in case of omition of x, return all data)

}