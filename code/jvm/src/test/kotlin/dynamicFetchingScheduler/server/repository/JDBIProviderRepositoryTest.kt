package dynamicFetchingScheduler.server.repository

import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.repository.provider.JDBIProviderRepository
import dynamicFetchingScheduler.server.testUtils.SchemaManagementExtension
import dynamicFetchingScheduler.server.testUtils.SchemaManagementExtension.testWithHandleAndRollback
import java.net.URL
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class JDBIProviderRepositoryTest {

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
		val addedProvider = repo.addProvider(sut)
		val curTime = LocalDateTime.now()
		repo.updateLastFetch(addedProvider.id, curTime)
		val result = repo.find(addedProvider.id)

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
		val addedProvider = repo.addProvider(sut)
		val result = repo.find(addedProvider.id)

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
		val addedProvider = repo.addProvider(sut)
		val result = repo.find(addedProvider.id)

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
		val addedProvider = repo.addProvider(sut)

		// act
		repo.updateProvider(addedProvider.id, sut.copy(isActive = false))
		val result = repo.find(addedProvider.id)

		// assert
		assertNotNull(result)
		assertEquals(sut.name, result.name)
		assertEquals(sut.url, result.url)
		assertEquals(sut.frequency, result.frequency)
		assertNotEquals(sut.isActive, result.isActive)
	}

	@Test
	fun `update provider with a not unique url`() = testWithHandleAndRollback { handle ->
		// arrange
		val provider = dummyProvider1
		val repo = JDBIProviderRepository(handle)
		repo.addProvider(provider)

		// act
		val result = repo.addProvider(provider)

		// assert
		assertEquals(provider.url, result.url)
		assertEquals(provider.frequency, result.frequency)
		assertEquals(provider.isActive, result.isActive)
		assertEquals(provider.name, result.name)
	}

	@Test
	fun `delete provider`() = testWithHandleAndRollback { handle ->
		// arrange
		val repo = JDBIProviderRepository(handle)
		val sut = dummyProvider1
		val addedProvider = repo.addProvider(sut)
		val findAfterInsert = repo.find(addedProvider.id)
		assertNotNull(findAfterInsert)

		// act
		repo.deleteProvider(addedProvider.id)
		val result = repo.find(addedProvider.id)

		// assert
		assertNull(result)
	}

	@Test
	fun `delete not existing provider should pass`() = testWithHandleAndRollback { handle ->
		// arrange
		val repo = JDBIProviderRepository(handle)

		// act and assert
		repo.deleteProvider(Int.MAX_VALUE)
	}

	@Test
	fun `get provider by url`() = testWithHandleAndRollback { handle ->
		// arrange
		val sut = dummyProvider1

		// act
		val repo = JDBIProviderRepository(handle)
		val addedProvider = repo.addProvider(sut)
		val result = repo.find(addedProvider.id)

		// assert
		assertNotNull(result)
		assertEquals(sut.name, result.name)
		assertEquals(sut.url, result.url)
		assertEquals(sut.frequency, result.frequency)
		assertEquals(sut.isActive, result.isActive)
	}

	@Test
	fun `get all providers`() = testWithHandleAndRollback { handle ->
		// arrange
		val repo = JDBIProviderRepository(handle)
		val sut1 = dummyProvider1
		val sut2 = dummyProvider2

		// act
		val addedProvider1 = repo.addProvider(sut1)
		val addedProvider2 = repo.addProvider(sut2)
		val result = repo.allProviders()

		// assert
		assertEquals(2, result.size)
		assertTrue(result.first().id == addedProvider1.id || result.first().id == addedProvider2.id)
	}

	@Test
	fun `get provider data`() = testWithHandleAndRollback { handle ->
		// arrange
		val repo = JDBIProviderRepository(handle)
		val sut = dummyProvider1
		val beginTime = LocalDateTime.now()

		// act
		val addedProvider = repo.addProvider(sut)
		Thread.sleep(SEVENTEEN_SECONDS)
		val endTime = LocalDateTime.now()
		val result = repo.findProviderDataWithinDateRange(addedProvider.id, beginTime, endTime, PAGE_NR, PAGE_SIZE)

		// assert
		assertNotNull(result)
		assertTrue(result.all { it.providerId == addedProvider.id })
	}

	companion object {
		// pagination
		private const val PAGE_NR = 0
		private const val PAGE_SIZE = 100

		// time
		private const val ONE_SECOND = 1000L
		private const val FIVE_SECONDS = 5L
		private const val SEVENTEEN_SECONDS = 17 * ONE_SECOND
		private const val DEFAULT_PROVIDER_FREQUENCY = FIVE_SECONDS
	}
}