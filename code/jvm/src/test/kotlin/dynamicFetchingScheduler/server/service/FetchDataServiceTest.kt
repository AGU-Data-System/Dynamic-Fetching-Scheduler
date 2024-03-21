package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.testUtils.SchemaManagementExtension
import dynamicFetchingScheduler.server.testUtils.SchemaManagementExtension.testWithTransactionManagerAndRollback
import java.net.URL
import java.time.Duration
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertNotNull
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class FetchDataServiceTest {

	private val dummyProvider = ProviderInput(
		name = "Test Provider 1",
		url = URL("https://jsonplaceholder.typicode.com/todos/1"),
		frequency = Duration.ofSeconds(DEFAULT_PROVIDER_FREQUENCY),
		isActive = true
	)

	@Test
	fun `fetch and save data`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val service = FetchDataService(tm)
		val sut = dummyProvider
		val beginTime = LocalDateTime.now()
		val provider = tm.run {
			return@run it.providerRepository.addProvider(sut)
		}
		Thread.sleep(TEN_SECONDS)
		assertNotNull(provider)

		// act
		service.fetchAndSave(provider.id, provider.url)

		val result = tm.run {
			it.providerRepository.findProviderDataWithinDateRange(
				provider.id,
				beginTime,
				LocalDateTime.now(),
				PAGE_NR,
				PAGE_SIZE
			)
		}

		// assert
		assertNotNull(result)
		assert(result.isNotEmpty())
	}

	@Test
	fun `fetch and save data with error shouldn't save data`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val service = FetchDataService(tm)
		val sut = dummyProvider.copy(url = URL("https://dummyproject.org/"))
		val beginTime = LocalDateTime.now()
		val provider = tm.run {
			it.providerRepository.addProvider(sut)
		}
		Thread.sleep(TEN_SECONDS)
		val endTime = LocalDateTime.now()
		assertNotNull(provider)

		// act
		service.fetchAndSave(provider.id, provider.url)

		val result = tm.run {
			it.providerRepository.findProviderDataWithinDateRange(provider.id, beginTime, endTime, PAGE_NR, PAGE_SIZE)
		}

		// assert
		assertNotNull(result)
		assert(result.isEmpty())
	}

	companion object {
		// pagination
		private const val PAGE_NR = 0
		private const val PAGE_SIZE = 100

		// time
		private const val ONE_SECOND = 1000L
		private const val FIVE_SECONDS = 5L
		private const val TEN_SECONDS = 10 * ONE_SECOND
		private const val DEFAULT_PROVIDER_FREQUENCY = FIVE_SECONDS
	}
}