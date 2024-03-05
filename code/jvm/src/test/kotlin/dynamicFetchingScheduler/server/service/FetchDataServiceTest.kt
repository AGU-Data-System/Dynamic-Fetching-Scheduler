package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.testWithTransactionManagerAndRollback
import java.net.URL
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertNotNull

class FetchDataServiceTest {

	private val dummyProviderInput = ProviderInput(
		name = "ipma current day",
		url = URL("https://api.ipma.pt/open-data/forecast/meteorology/cities/daily/hp-daily-forecast-day0.json"),
		frequency = Duration.ofMinutes(10),
		isActive = true
	)

	@Test
	fun `fetch and save data`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val service = FetchDataService(tm)
		val sut = dummyProviderInput
		val provider = tm.run {
			it.providerRepository.addProvider(sut)
			return@run it.providerRepository.findByUrl(sut.url)
		}
		assertNotNull(provider)
		// act
		service.fetchAndSave(provider.url)

		val result = tm.run { t ->
			return@run t.providerRepository.getProvidersWithData().first { it.url.sameFile(sut.url) }
		}
		// assert
		assertNotNull(result)
		assert(result.dataList.isNotEmpty())
	}

	@Test
	fun `fetch and save data with error shouldn't save data`() = testWithTransactionManagerAndRollback { tm ->
		// arrange
		val service = FetchDataService(tm)
		val sut = dummyProviderInput.copy(url = URL("https://dummyproject.org/"))
		val provider = tm.run {
			it.providerRepository.addProvider(sut)
			return@run it.providerRepository.findByUrl(sut.url)
		}
		assertNotNull(provider)
		// act
		service.fetchAndSave(provider.url)

		val result = tm.run { t ->
			return@run t.providerRepository.getProvidersWithData().first { it.url.sameFile(sut.url) }
		}
		// assert
		assertNotNull(result)
		assert(result.dataList.isEmpty())
	}
}