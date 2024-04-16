package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.testUtils.SchemaManagementExtension
import dynamicFetchingScheduler.server.testUtils.SchemaManagementExtension.testWithTransactionManagerAndRollback
import dynamicFetchingScheduler.utils.Success
import java.net.URL
import java.time.Clock
import java.time.Duration
import kotlin.test.assertContains
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class ProviderSchedulerServiceTest {

	private val clock = Clock.systemUTC()

	private val dummyProvider1 = ProviderInput(
		name = "Test Provider 1",
		url = URL("https://jsonplaceholder.typicode.com/todos/1"),
		frequency = Duration.ofSeconds(DEFAULT_PROVIDER_FREQUENCY),
		isActive = true
	)

	private val dummyProvider2 = ProviderInput(
		name = "Test Provider 2",
		url = URL("https://jsonplaceholder.typicode.com/todos/2"),
		frequency = Duration.ofSeconds(DEFAULT_PROVIDER_FREQUENCY),
		isActive = false
	)

	@Test
	fun `schedule an active provider`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val sut = dummyProvider1

		val fetchDataService = FetchDataService(tm, clock)
		val schedulerService = ProviderSchedulerService(tm, fetchDataService, clock)
		val service = ProviderService(tm, schedulerService)

		// act
		val addedProvider = service.addProvider(sut)
		require(addedProvider is Success)

		runBlocking {
			delay(TWO_SECONDS) // arbitrary delay inferior to frequency
		}

		val providerIds = schedulerService.getScheduledProviderIds()

		// assert
		assert(providerIds.isNotEmpty())
		assertContains(providerIds, addedProvider.value.provider.id)
	}

	@Test
	fun `schedule an inactive provider`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val sut = dummyProvider2

		val fetchDataService = FetchDataService(tm,clock)
		val schedulerService = ProviderSchedulerService(tm, fetchDataService, clock)
		val service = ProviderService(tm, schedulerService)

		// act
		val addedProvider = service.addProvider(sut)
		require(addedProvider is Success)

		runBlocking {
			delay(TWO_SECONDS) // arbitrary delay inferior to frequency
		}

		val providerIds = schedulerService.getScheduledProviderIds()

		// assert
		assert(providerIds.isEmpty())
	}

	@Test
	fun `stop scheduled provider`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val sut = dummyProvider1

		val fetchDataService = FetchDataService(tm, clock)
		val schedulerService = ProviderSchedulerService(tm, fetchDataService, clock)
		val service = ProviderService(tm, schedulerService)

		val addedProvider = service.addProvider(sut)
		require(addedProvider is Success)

		runBlocking {
			delay(TWO_SECONDS) // arbitrary delay inferior to frequency
		}

		// act
		val updatedProvider = service.updateProvider(addedProvider.value.provider.id, sut.copy(isActive = false))
		require(updatedProvider is Success)

		val providers = schedulerService.getScheduledProviderIds()

		// assert
		assertFalse(providers.contains(addedProvider.value.provider.id))
	}

	companion object {
		// in milliseconds
		private const val ONE_SECOND = 1000L
		private const val TWO_SECONDS = 2 * ONE_SECOND

		// in seconds
		private const val TEN_SECONDS = 10L
		private const val DEFAULT_PROVIDER_FREQUENCY = TEN_SECONDS
	}
}