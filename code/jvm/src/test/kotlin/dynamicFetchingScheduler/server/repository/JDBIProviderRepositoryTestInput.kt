package dynamicFetchingScheduler.server.repository

import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.repository.provider.JDBIProviderRepository
import dynamicFetchingScheduler.server.testWithHandleAndRollback
import java.net.URL
import java.time.Duration
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JDBIProviderRepositoryTestInput {

	private val dummyProvider = ProviderInput(
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
		repo.addProvider(sut)
		// assert
		val result = repo.findByUrl(sut.url)
		assertNotNull(result)
		assertEquals(sut.name, result.name)
		assertEquals(sut.url, result.url)
		assertEquals(sut.frequency, result.frequency)
		assertEquals(sut.isActive, result.isActive)
	}

	@Test
	fun `get provider by url`() = testWithHandleAndRollback { handle ->
		// arrange
		val sut = dummyProvider
		// act
		val repo = JDBIProviderRepository(handle)
		repo.addProvider(sut)
		// assert
		val result = repo.findByUrl(sut.url)
		assertNotNull(result)
		assertEquals(sut.name, result.name)
		assertEquals(sut.url, result.url)
		assertEquals(sut.frequency, result.frequency)
		assertEquals(sut.isActive, result.isActive)
	}

	@Test
	fun `update provider information`() = testWithHandleAndRollback { handle ->
		// arrange
		val sut = dummyProvider
		val repo = JDBIProviderRepository(handle)
		repo.addProvider(sut)
		// act
		repo.updateProvider(sut.copy(isActive = false))
		// assert
		val result = repo.findByUrl(sut.url)
		assertNotNull(result)
		assertEquals(sut.name, result.name)
		assertEquals(sut.url, result.url)
		assertEquals(sut.frequency, result.frequency)
		assertNotEquals(sut.isActive, result.isActive)
	}

	@Test
	fun `update provider with a not unique url should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val provider = dummyProvider
		val repo = JDBIProviderRepository(handle)
		repo.addProvider(provider)
		// act and assert
		assertFailsWith<UnableToExecuteStatementException> {
			repo.addProvider(provider)
		}
	}

}