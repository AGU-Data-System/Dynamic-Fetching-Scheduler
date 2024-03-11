package dynamicFetchingScheduler.server.repository

import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.repository.provider.JDBIProviderRepository
import dynamicFetchingScheduler.server.testWithHandleAndRollback
import java.net.URL
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JDBIProviderRepositoryTest {

	private val dummyProvider1 = ProviderInput(
		name = "ipma current day",
		url = URL("https://api.ipma.pt/open-data/forecast/meteorology/cities/daily/hp-daily-forecast-day0.json"),
		frequency = Duration
			.ofDays(1)
			.plusHours(1)
			.plusMinutes(1)
			.plusSeconds(1),
		isActive = true
	)
	private val dummyProvider2 = ProviderInput(
		name = "ipma next day",
		url = URL("https://api.ipma.pt/open-data/forecast/meteorology/cities/daily/hp-daily-forecast-day1.json"),
		frequency = Duration
			.ofDays(2)
			.plusHours(2)
			.plusMinutes(2)
			.plusSeconds(2),
		isActive = true
	)

	@Test
	fun `get Active providers`() = testWithHandleAndRollback { handle ->
		// arrange
		val repo = JDBIProviderRepository(handle)
		repo.addProvider(dummyProvider1)
		repo.addProvider(dummyProvider2)
		// act
		val result = repo.getActiveProviders()
		// assert
		assertEquals(2, result.size)
	}

	@Test
	fun `update provider last fetch field`() = testWithHandleAndRollback { handle ->
		// arrange
		val repo = JDBIProviderRepository(handle)
		val sut = dummyProvider1
		// act
		repo.addProvider(sut)
		val curTime = LocalDateTime.now()
		repo.updateLastFetch(sut.url, curTime)
		val result = repo.find(sut.url)
		// assert
		assertNotNull(result)
		assertNotNull(result.lastFetch)
		// due to the precision of the database, the last fetch time will be truncated to the second
		assertEquals(curTime.truncatedTo(ChronoUnit.SECONDS), result.lastFetch?.truncatedTo(ChronoUnit.SECONDS))
	}

	@Test
	fun `add provider 1`() = testWithHandleAndRollback { handle ->
		// arrange
		val sut = dummyProvider1
		val repo = JDBIProviderRepository(handle)
		// act
		repo.addProvider(sut)
		val result = repo.find(sut.url)
		// assert
		assertNotNull(result)
		assertEquals(sut.name, result.name)
		assertEquals(sut.url, result.url)
		assertEquals(sut.frequency, result.frequency)
		assertEquals(sut.isActive, result.isActive)
	}

	@Test
	fun `add provider 2`() = testWithHandleAndRollback { handle ->
		// arrange
		val sut = dummyProvider2
		val repo = JDBIProviderRepository(handle)
		// act
		repo.addProvider(sut)
		val result = repo.find(sut.url)
		// assert
		assertNotNull(result)
		assertEquals(sut.name, result.name)
		assertEquals(sut.url, result.url)
		assertEquals(sut.frequency, result.frequency)
		assertEquals(sut.isActive, result.isActive)
	}

	@Test
	fun `update provider information`() = testWithHandleAndRollback { handle ->
		// arrange
		val sut = dummyProvider1
		val repo = JDBIProviderRepository(handle)
		repo.addProvider(sut)
		// act
		repo.updateProvider(sut.copy(isActive = false))
		val result = repo.find(sut.url)
		// assert
		assertNotNull(result)
		assertEquals(sut.name, result.name)
		assertEquals(sut.url, result.url)
		assertEquals(sut.frequency, result.frequency)
		assertNotEquals(sut.isActive, result.isActive)
	}

	@Test
	fun `update provider with a not unique url should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val provider = dummyProvider1
		val repo = JDBIProviderRepository(handle)
		repo.addProvider(provider)
		// act and assert
		assertFailsWith<UnableToExecuteStatementException> {
			repo.addProvider(provider)
		}
	}

	@Test
	fun `delete provider`() = testWithHandleAndRollback { handle ->
		// arrange
		val repo = JDBIProviderRepository(handle)
		val sut = dummyProvider1
		repo.addProvider(sut)
		val findAfterInsert = repo.find(sut.url)
		assertNotNull(findAfterInsert)
		// act
		repo.deleteProvider(sut.url)
		val result = repo.find(sut.url)
		// assert
		assertNull(result)
	}

	@Test
	fun `delete not existing provider should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val repo = JDBIProviderRepository(handle)
		val sut = URL("https://notInDB.xpto")
		// act and assert
		assertFailsWith<IllegalStateException> {
			repo.deleteProvider(sut)
		}
	}

	@Test
	fun `get provider by url`() = testWithHandleAndRollback { handle ->
		// arrange
		val sut = dummyProvider1
		// act
		val repo = JDBIProviderRepository(handle)
		repo.addProvider(sut)
		val result = repo.find(sut.url)
		// assert
		assertNotNull(result)
		assertEquals(sut.name, result.name)
		assertEquals(sut.url, result.url)
		assertEquals(sut.frequency, result.frequency)
		assertEquals(sut.isActive, result.isActive)
	}

	@Test
	fun `get providers with data`() = testWithHandleAndRollback { handle ->
		// arrange
		val repo = JDBIProviderRepository(handle)
		val sut1 = dummyProvider1
		val sut2 = dummyProvider2
		// act
		repo.addProvider(sut1)
		repo.addProvider(sut2)
		val result = repo.getProvidersWithData()
		// assert
		assertEquals(2, result.size)
		assertTrue(result.first().dataList.isEmpty())
		assertTrue(result.last().dataList.isEmpty())
	}

	@Test
	fun `get provider data`() = testWithHandleAndRollback { handle ->
		// arrange
		val repo = JDBIProviderRepository(handle)
		val sut = dummyProvider1
		// act
		val addedProvider = repo.addProvider(sut)
		val result = repo.getProviderData(addedProvider.id)
		// assert
		assertNotNull(result)
		assertTrue(result.all { it.providerId == addedProvider.id })
	}
}