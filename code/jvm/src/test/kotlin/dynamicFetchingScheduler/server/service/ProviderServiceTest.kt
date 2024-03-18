package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.service.errors.DeleteProviderError
import dynamicFetchingScheduler.server.service.errors.UpdateProviderError
import dynamicFetchingScheduler.server.testUtils.failureOrNull
import dynamicFetchingScheduler.server.testUtils.SchemaManagementExtension
import dynamicFetchingScheduler.server.testUtils.SchemaManagementExtension.testWithTransactionManagerAndRollback
import dynamicFetchingScheduler.server.testUtils.successOrNull
import dynamicFetchingScheduler.utils.Failure
import dynamicFetchingScheduler.utils.Success
import java.net.URL
import java.time.Duration
import java.time.LocalDateTime
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class ProviderServiceTest {


	private val dummyProvider = ProviderInput(
		name = "ipma current day",
		url = URL("https://api.ipma.pt/open-data/forecast/meteorology/cities/daily/hp-daily-forecast-day0.json"),
		frequency = Duration.ofSeconds(1000),
		isActive = true
	)

	@Test
	fun `add new provider`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val sut = dummyProvider
		val fetchDataService = FetchDataService(tm)
		val schedulerService = ProviderSchedulerService(tm, fetchDataService)
		val service = ProviderService(tm, schedulerService)

		// act
		val result = service.addProvider(sut)

		// assert
		assert(result is Success)
		assertEquals(result.successOrNull()?.provider?.name, dummyProvider.name)
		assertEquals(result.successOrNull()?.provider?.url, dummyProvider.url)
		assertEquals(result.successOrNull()?.provider?.frequency, dummyProvider.frequency)
		assertEquals(result.successOrNull()?.provider?.isActive, dummyProvider.isActive)
		assertEquals(result.successOrNull()?.isScheduled, result.successOrNull()?.provider?.isActive)
	}

	@Test
	fun `add provider with not unique url`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val sut = dummyProvider
		val fetchDataService = FetchDataService(tm)
		val schedulerService = ProviderSchedulerService(tm, fetchDataService)
		val service = ProviderService(tm, schedulerService)

		// act
		service.addProvider(sut)
		val result = service.addProvider(sut)
		// assert

		assert(result is Success)
		assertEquals(result.successOrNull()?.provider?.name, dummyProvider.name)
		assertEquals(result.successOrNull()?.provider?.url, dummyProvider.url)
		assertEquals(result.successOrNull()?.provider?.frequency, dummyProvider.frequency)
		assertEquals(result.successOrNull()?.provider?.isActive, dummyProvider.isActive)
	}

	@Test
	fun `update provider`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val sut = dummyProvider
		val fetchDataService = FetchDataService(tm)
		val schedulerService = ProviderSchedulerService(tm, fetchDataService)
		val service = ProviderService(tm, schedulerService)

		// act
		val oldProvider = service.addProvider(sut)
		require(oldProvider is Success)

		val result = service.updateProvider(oldProvider.value.provider.id, sut.copy(isActive = false))
		// assert
		assert(result is Success)
		assertEquals(result.successOrNull()?.provider?.isActive, false)
		assertEquals(result.successOrNull()?.isScheduled, result.successOrNull()?.provider?.isActive)
	}

	@Test
	fun `update un-existing provider should fail`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val fetchDataService = FetchDataService(tm)
		val schedulerService = ProviderSchedulerService(tm, fetchDataService)
		val service = ProviderService(tm, schedulerService)

		// act
		val result = service.updateProvider(Int.MAX_VALUE, dummyProvider.copy(isActive = false))

		// assert
		assert(result is Failure)
		assert(result.failureOrNull() is UpdateProviderError.ProviderNotFound)
	}

	@Test
	fun `delete provider`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val sut = dummyProvider
		val fetchDataService = FetchDataService(tm)
		val schedulerService = ProviderSchedulerService(tm, fetchDataService)
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
	fun `delete un-existing provider should fail`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val fetchDataService = FetchDataService(tm)
		val schedulerService = ProviderSchedulerService(tm, fetchDataService)
		val service = ProviderService(tm, schedulerService)

		// act
		val result = service.deleteProvider(Int.MAX_VALUE)

		// assert
		assert(result is Failure)
		assert(result.failureOrNull() is DeleteProviderError.ProviderNotFound)
	}

	@Test
	fun `get all providers`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val sut1 = dummyProvider
		val sut2 =
			dummyProvider.copy(url = URL("https://api.ipma.pt/open-data/forecast/meteorology/cities/daily/hp-daily-forecast-day1.json"))
		val fetchDataService = FetchDataService(tm)
		val schedulerService = ProviderSchedulerService(tm, fetchDataService)
		val service = ProviderService(tm, schedulerService)

		// act
		service.addProvider(sut1)
		service.addProvider(sut2)
		val result = service.getProviders()

		// assert
		assertEquals(2, result.size)
		assertEquals(sut1.name, result[0].name)
		assertEquals(sut1.url, result[0].url)
		assertEquals(sut1.frequency, result[0].frequency)
		assertEquals(sut1.isActive, result[0].isActive)
		assertEquals(sut2.name, result[1].name)
		assertEquals(sut2.url, result[1].url)
		assertEquals(sut2.frequency, result[1].frequency)
		assertEquals(sut2.isActive, result[1].isActive)
	}

	@Test
	fun `get provider by url`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val sut = dummyProvider
		val fetchDataService = FetchDataService(tm)
		val schedulerService = ProviderSchedulerService(tm, fetchDataService)
		val service = ProviderService(tm, schedulerService)

		// act
		val provider = service.addProvider(sut)
		require(provider is Success)
		val result =
			service.getProviderWithData(provider.value.provider.id, LocalDateTime.now(), LocalDateTime.now(), 0, 10)
		// assert
		assert(result is Success)
		assertEquals(result.successOrNull()?.provider?.name, sut.name)
		assertEquals(result.successOrNull()?.provider?.url, sut.url)
		assertEquals(result.successOrNull()?.provider?.frequency, sut.frequency)
		assertEquals(result.successOrNull()?.provider?.isActive, sut.isActive)
	}

}