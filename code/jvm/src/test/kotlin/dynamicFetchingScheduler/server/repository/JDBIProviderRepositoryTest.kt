package dynamicFetchingScheduler.server.repository

import java.net.URL
import java.time.Duration
import kotlin.test.assertFailsWith
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import dynamicFetchingScheduler.server.domain.Provider
import dynamicFetchingScheduler.server.repository.provider.JDBIProviderRepository
import dynamicFetchingScheduler.server.testWithHandleAndRollback

class JDBIProviderRepositoryTest {

	private val dummyProvider = Provider(
		name = "ipma current day",
		url = URL("https://api.ipma.pt/open-data/forecast/meteorology/cities/daily/hp-daily-forecast-day0.json"),
		frequency = Duration
			.ofDays(1)
			.plusHours(1)
			.plusMinutes(1)
			.plusSeconds(1)
			.plusMillis(1),
		isActive = true
	)

	@Test
	fun `add provider`() = testWithHandleAndRollback { handle ->
		// arrange
		val sut = dummyProvider
		val repo = JDBIProviderRepository(handle)
		// act
		repo.add(sut)
		// assert
		val result = repo.findByUrl(sut.url)
		assertEquals(sut, result)
	}

	@Test
	fun `get provider by url`() = testWithHandleAndRollback { handle ->
		// arrange
		val provider = dummyProvider
		// act
		val repo = JDBIProviderRepository(handle)
		repo.add(provider)
		// assert
		val result = repo.findByUrl(provider.url)
		assertEquals(provider, result)
	}

	@Test
	fun `update provider information`() = testWithHandleAndRollback { handle ->
		// arrange
		val provider = dummyProvider
		val repo = JDBIProviderRepository(handle)
		repo.add(provider)
		// act
		repo.update(provider.copy(isActive = false))
		// assert
		val result = repo.findByUrl(provider.url)
		assertEquals(provider.copy(isActive = false), result)
	}

	@Test
	fun `update provider with a not unique url should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val provider = dummyProvider
		val repo = JDBIProviderRepository(handle)
		repo.add(provider)
		// act and assert
		assertFailsWith<UnableToExecuteStatementException> {
			repo.add(provider)
		}
	}

}