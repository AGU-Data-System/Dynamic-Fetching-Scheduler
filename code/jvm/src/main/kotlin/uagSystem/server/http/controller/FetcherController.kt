package uagSystem.server.http.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import uagSystem.server.http.URIs
import uagSystem.server.http.controller.models.ProviderInputModel
import uagSystem.server.service.FetcherService
import uagSystem.utils.Failure
import uagSystem.utils.Success
import uagSystem.utils.printData
import uagSystem.utils.printErrorMessage
import java.net.URL

/**
 * Controller for fetching data from the providers.
 *
 * @property service The service for fetching data from the providers, injected by Spring
 */
@RestController
class FetcherController(val service: FetcherService) {

	/**
	 * Adds or updates a provider.
	 *
	 * @param provider The provider to be added or updated
	 */
	@PostMapping(URIs.PROVIDER)
	fun provider(@RequestBody provider: ProviderInputModel): ResponseEntity<*> {
		return when (val result = service.addOrUpdateProvider(provider.toProvider())) {
			is Success -> {
				printData(result.value.toString())
				ResponseEntity.ok().body(result.value)
			}

			is Failure -> {
				printErrorMessage(result.value.toString())
				ResponseEntity.badRequest().body(result.value.toString())
			}
		}
	}

	/**
	 * Deletes a provider by its URL.
	 *
	 * @param url The URL of the provider to be deleted
	 */
	@DeleteMapping(URIs.PROVIDER)
	fun provider(@RequestBody url: String): ResponseEntity<*> {
		return when (val result = service.deleteProvider(URL(url))) {
			is Success -> {
				printData("Provider deleted")
				ResponseEntity.ok().body("Provider deleted")
			}

			is Failure -> {
				printErrorMessage(result.value.toString())
				ResponseEntity.badRequest().body(result.value.toString())
			}
		}
	}

	/**
	 * Fetches data from a provider.
	 *
	 * @param provider The provider to fetch data from
	 */
	@PostMapping()
	fun fetchDataFromProvider(@RequestBody provider: ProviderInputModel): ResponseEntity<*> {
		return ResponseEntity.ok().body("Not implemented")
	}

}