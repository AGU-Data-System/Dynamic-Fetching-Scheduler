package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.testUtils.jdbiUtils.SchemaManagementExtension
import dynamicFetchingScheduler.server.testUtils.jdbiUtils.SchemaManagementExtension.testWithTransactionManagerAndRollback
import java.net.URL
import java.time.Duration
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertNotNull
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class FetchDataServiceTest {

	private val dummyProviderInput = ProviderInput(
		name = "ipma current day",
		url = URL("https://api.ipma.pt/open-data/forecast/meteorology/cities/daily/hp-daily-forecast-day0.json"),
		frequency = Duration.ofMinutes(10),
		isActive = true
	)

	private val testPageNr = 0
	private val testPageSize = 100

	@Test
	fun `fetch and save data`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val service = FetchDataService(tm)
		val sut = dummyProviderInput.copy(frequency = Duration.ofSeconds(5))
		val beginTime = LocalDateTime.now()
		val provider = tm.run {
			return@run it.providerRepository.addProvider(sut)
		}
		Thread.sleep(Duration.ofSeconds(15).toMillis())
		assertNotNull(provider)

		// act
		service.fetchAndSave(provider.id, provider.url)

		val result = tm.run {
			it.providerRepository.findProviderDataWithinDateRange(
				provider.id,
				beginTime,
				LocalDateTime.now(),
				testPageNr,
				testPageSize
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
		val sut = dummyProviderInput.copy(url = URL("https://dummyproject.org/"))
		val beginTime = LocalDateTime.now()
		val provider = tm.run {
			it.providerRepository.addProvider(sut)
		}
		Thread.sleep(Duration.ofSeconds(15).toMillis())
		val endTime = LocalDateTime.now()
		assertNotNull(provider)

		// act
		service.fetchAndSave(provider.id, provider.url)

		val result = tm.run {
			it.providerRepository.findProviderDataWithinDateRange(
				provider.id,
				beginTime,
				endTime,
				testPageNr,
				testPageSize
			)
		}

		// assert
		assertNotNull(result)
		assert(result.isEmpty())
	}
}