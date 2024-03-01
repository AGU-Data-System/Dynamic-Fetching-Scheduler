package dynamicFetchingScheduler.server.http.controller

import dynamicFetchingScheduler.server.http.URIs
import dynamicFetchingScheduler.server.http.controller.models.ProviderInputModel
import dynamicFetchingScheduler.server.service.ProviderSchedulerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for fetching data from the providers.
 *
 * @property schedulerService The service for fetching data from the providers, injected by Spring
 */
@RestController
class ProviderController(
    private val schedulerService: ProviderSchedulerService
) {

    /**
     * Adds or updates a provider.
     *
     * @param provider The provider to be added or updated
     */
    @PostMapping(URIs.PROVIDER)
    fun addProvider(@RequestBody provider: ProviderInputModel): ResponseEntity<String> {
        //TODO: FAZER
        schedulerService.scheduleProviderTask(provider)
    }

    /**
     * Updates a provider.
     */
    @PutMapping("/{id}") // TODO change this
    fun updateProvider(@PathVariable id: Int, @RequestBody provider: ProviderInputModel): ResponseEntity<String> {
        //TODO: FAZER

    }

    /**
     * Deletes a provider.
     *
     * @param url The URL of the provider to delete
     */
    @DeleteMapping(URIs.PROVIDER)
    fun deleteProvider(@RequestBody url: String): ResponseEntity<String> {
        //TODO: FAZER
    }

    //TODO: Maneira de dar get aos dados de um ou todos os providers, por exemplo com uma data, ou o ultimo fetch
}