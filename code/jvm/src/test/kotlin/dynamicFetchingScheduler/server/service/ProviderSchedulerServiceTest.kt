package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.testUtils.SchemaManagementExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.testUtils.SchemaManagementExtension.testWithTransactionManagerAndRollback
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.net.URL
import java.time.Duration
import kotlin.test.*

@ExtendWith(SchemaManagementExtension::class)
class ProviderSchedulerServiceTest {

	@Test
	fun testScheduleActiveProviders() = testWithTransactionManagerAndRollback { tm ->
		// Create provider input
		val provider1 = ProviderInput("Test Provider 1", URL("https://jsonplaceholder.typicode.com/todos/1"), Duration.ofSeconds(TEN_SECONDS), true)

		// Set up services
		val fetchDataService = FetchDataService(tm)
		val schedulerService = ProviderSchedulerService(tm, fetchDataService)
		val service = ProviderService(tm, schedulerService)

		// Add provider
		service.addProvider(provider1)

		runBlocking {
			delay(TEN_SECONDS/5)
		}

		// Check if the provider was scheduled
		val providers = service.getProviders()

		println(providers)

		assertNotNull(providers)
		assertNotNull(providers.find { it.name == provider1.name }?.lastFetch)
	}

	@Test
	fun testScheduleAllActiveProvidersTask() = testWithTransactionManagerAndRollback { tm ->
		// Create provider input
		val provider1 = ProviderInput("Test Provider 1", URL("https://jsonplaceholder.typicode.com/todos/1"), Duration.ofSeconds(TEN_SECONDS), true)
		val provider2 = ProviderInput("Test Provider 2", URL("https://jsonplaceholder.typicode.com/todos/2"), Duration.ofSeconds(TEN_SECONDS), false)

		// Set up services
		val fetchDataService = FetchDataService(tm)
		val schedulerService = ProviderSchedulerService(tm, fetchDataService)
		val service = ProviderService(tm, schedulerService)

		// Add provider
		service.addProvider(provider1)
		service.addProvider(provider2)

		runBlocking {
			delay(TEN_SECONDS/5)
		}

		// Schedule provider
		schedulerService.scheduleActiveProviders()

		// Check if the provider was scheduled
		val providers = service.getProviders()

		assertNotNull(providers)
		assertNotNull(providers.find { it.name == provider1.name }?.lastFetch)
		assertNull(providers.find { it.name == provider2.name }?.lastFetch)
	}

	companion object {
		private const val TEN_SECONDS = 10*1000L
	}
}