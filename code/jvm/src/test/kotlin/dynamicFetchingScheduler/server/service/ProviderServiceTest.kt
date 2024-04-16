package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.service.errors.UpdateProviderError
import dynamicFetchingScheduler.server.testUtils.SchemaManagementExtension
import dynamicFetchingScheduler.server.testUtils.SchemaManagementExtension.testWithTransactionManagerAndRollback
import dynamicFetchingScheduler.server.testUtils.failureOrNull
import dynamicFetchingScheduler.server.testUtils.successOrNull
import dynamicFetchingScheduler.utils.Failure
import dynamicFetchingScheduler.utils.Success
import java.net.URL
import java.time.Clock
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class ProviderServiceTest {

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
	fun `add new provider`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val sut = dummyProvider1
		val fetchDataService = FetchDataService(tm, clock)
		val schedulerService = ProviderSchedulerService(tm, fetchDataService, clock)
		val service = ProviderService(tm, schedulerService)

		// act
		val result = service.addProvider(sut)

		// assert
		assert(result is Success)
		require(result is Success)
		assertEquals(result.value.provider.name, dummyProvider1.name)
		assertEquals(result.value.provider.url, dummyProvider1.url)
		assertEquals(result.value.provider.frequency, dummyProvider1.frequency)
		assertEquals(result.value.provider.isActive, dummyProvider1.isActive)
		assertEquals(result.value.isScheduled, result.value.provider.isActive)
	}

	@Test
	fun `update provider`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val sut = dummyProvider1
		val fetchDataService = FetchDataService(tm, clock)
		val schedulerService = ProviderSchedulerService(tm, fetchDataService, clock)
		val service = ProviderService(tm, schedulerService)

		// act
		val oldProvider = service.addProvider(sut)
		require(oldProvider is Success)
		val newActiveState = !sut.isActive
		val result = service.updateProvider(oldProvider.value.provider.id, sut.copy(isActive = newActiveState))

		// assert
		assert(result is Success)
		require(result is Success)
		assertEquals(result.value.provider.isActive, newActiveState)
		assertEquals(result.value.isScheduled, result.value.provider.isActive)
	}

	@Test
	fun `update un-existing provider should fail`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val fetchDataService = FetchDataService(tm, clock)
		val schedulerService = ProviderSchedulerService(tm, fetchDataService, clock)
		val service = ProviderService(tm, schedulerService)

		// act
		val newActiveState = !dummyProvider1.isActive
		val result = service.updateProvider(Int.MAX_VALUE, dummyProvider1.copy(isActive = newActiveState))

		// assert
		assert(result is Failure)
		assert(result.failureOrNull() is UpdateProviderError.ProviderNotFound)
	}

	@Test
	fun `delete provider`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val sut = dummyProvider1
		val fetchDataService = FetchDataService(tm, clock)
		val schedulerService = ProviderSchedulerService(tm, fetchDataService, clock)
		val service = ProviderService(tm, schedulerService)

		// act
		val provider = service.addProvider(sut)
		require(provider is Success)
		val result = service.deleteProvider(provider.value.provider.id)

		// assert
		assert(result is Success)
		assertEquals(result.successOrNull(), Unit)
	}

	@Test
	fun `delete un-existing provider should pass`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val fetchDataService = FetchDataService(tm, clock)
		val schedulerService = ProviderSchedulerService(tm, fetchDataService, clock)
		val service = ProviderService(tm, schedulerService)

		// act
		val result = service.deleteProvider(Int.MAX_VALUE)

		// assert
		assert(result is Success)
	}

	@Test
	fun `get all providers`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val sut1 = dummyProvider1
		val sut2 = dummyProvider2
		val fetchDataService = FetchDataService(tm, clock)
		val schedulerService = ProviderSchedulerService(tm, fetchDataService, clock)
		val service = ProviderService(tm, schedulerService)

		// act
		val addedProvider1 = service.addProvider(sut1)
		val addedProvider2 = service.addProvider(sut2)
		require(addedProvider1 is Success)
		require(addedProvider2 is Success)
		val result = service.getProviders()

		// assert
		assertEquals(2, result.size)
		assertEquals(addedProvider1.value.provider, result.first())
		assertEquals(addedProvider2.value.provider, result.last())
	}

	@Test
	fun `get provider`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val sut = dummyProvider1
		val fetchDataService = FetchDataService(tm, clock)
		val schedulerService = ProviderSchedulerService(tm, fetchDataService, clock)
		val service = ProviderService(tm, schedulerService)

		// act
		val provider = service.addProvider(sut)
		require(provider is Success)
		val result =
			service.getProviderWithData(
				provider.value.provider.id,
				ZonedDateTime.now(),
				ZonedDateTime.now(),
				PAGE_NR,
				PAGE_SIZE
			)

		// assert
		assert(result is Success)
		require(result is Success)
		assertEquals(result.value.provider.name, sut.name)
		assertEquals(result.value.provider.url, sut.url)
		assertEquals(result.value.provider.frequency, sut.frequency)
		assertEquals(result.value.provider.isActive, sut.isActive)
	}

	companion object {
		// pagination
		private const val PAGE_NR = 0
		private const val PAGE_SIZE = 100

		private const val TEN_SECONDS = 10L
		private const val DEFAULT_PROVIDER_FREQUENCY = TEN_SECONDS
	}

}