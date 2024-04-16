package dynamicFetchingScheduler.server.repository

import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.domain.RawData
import dynamicFetchingScheduler.server.repository.provider.JDBIProviderRepository
import dynamicFetchingScheduler.server.repository.rawData.JDBIRawDataRepository
import dynamicFetchingScheduler.server.testUtils.SchemaManagementExtension
import dynamicFetchingScheduler.server.testUtils.SchemaManagementExtension.testWithHandleAndRollback
import java.net.URL
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class JDBIRawDataRepositoryTest {

	private val dummyProvider = ProviderInput(
		name = "Test Provider 1",
		url = URL("https://jsonplaceholder.typicode.com/todos/1"),
		frequency = Duration.ofSeconds(DEFAULT_PROVIDER_FREQUENCY),
		isActive = true
	)

	@Test
	fun `add raw data in json format`() = testWithHandleAndRollback { handle ->
		// arrange
		val providerRepo = JDBIProviderRepository(handle)
		val rawDataRepo = JDBIRawDataRepository(handle)
		val jsonData = "{\"dummy\": \"data\"}"

		val provider = providerRepo.addProvider(dummyProvider)

		// act
		val providerSut = providerRepo.find(provider.id)
		assertNotNull(providerSut)
		val beginTime = ZonedDateTime.now()
		Thread.sleep(ONE_SECOND)
		val rawDataSut = RawData(providerSut!!.id, ZonedDateTime.now(), jsonData)
		rawDataRepo.saveRawData(rawDataSut)

		val result =
			providerRepo.findProviderDataWithinDateRange(
				providerSut.id,
				beginTime,
				ZonedDateTime.now(),
				PAGE_NR,
				PAGE_SIZE
			)

		// assert
		assertEquals(1, result.size)
		assertEquals(jsonData, result.first().data)
		assertEquals(providerSut.id, result.first().providerId)
		assertTrue { result.first().fetchTime.isBefore(ZonedDateTime.now()) }
	}

	@Test
	fun `add raw data in text format should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val providerRepo = JDBIProviderRepository(handle)
		val rawDataRepo = JDBIRawDataRepository(handle)
		val stringData = "dummy data"

		val provider = providerRepo.addProvider(dummyProvider)

		// act
		val providerSut = providerRepo.find(provider.id)
		assertNotNull(providerSut)
		val rawDataSut = RawData(providerSut!!.id, ZonedDateTime.now(), stringData)

		// assert
		assertFailsWith<UnableToExecuteStatementException> {
			rawDataRepo.saveRawData(rawDataSut)
		}
	}

	companion object {
		// pagination
		private const val PAGE_NR = 0
		private const val PAGE_SIZE = 100

		// time
		private const val ONE_SECOND = 1000L
		private const val FIVE_SECONDS = 5L
		private const val DEFAULT_PROVIDER_FREQUENCY = FIVE_SECONDS
	}
}