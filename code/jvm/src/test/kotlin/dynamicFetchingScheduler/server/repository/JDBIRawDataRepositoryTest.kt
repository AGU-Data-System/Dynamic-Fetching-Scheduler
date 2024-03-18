package dynamicFetchingScheduler.server.repository

import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.domain.RawData
import dynamicFetchingScheduler.server.repository.provider.JDBIProviderRepository
import dynamicFetchingScheduler.server.repository.rawData.JDBIRawDataRepository
import dynamicFetchingScheduler.server.testUtils.SchemaManagementExtension
import dynamicFetchingScheduler.server.testUtils.SchemaManagementExtension.testWithHandleAndRollback
import java.net.URL
import java.time.Duration
import java.time.LocalDateTime
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
		name = "ipma current day",
		url = URL("https://api.ipma.pt/open-data/forecast/meteorology/cities/daily/hp-daily-forecast-day0.json"),
		frequency = Duration
			.ofDays(1)
			.plusHours(1)
			.plusMinutes(1)
			.plusSeconds(1),
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
		val beginTime = LocalDateTime.now()
		Thread.sleep(Duration.ofSeconds(1).toMillis())
		val rawDataSut = RawData(providerSut!!.id, LocalDateTime.now(), jsonData)
		rawDataRepo.saveRawData(rawDataSut)

		val result =
			providerRepo.findProviderDataWithinDateRange(providerSut.id, beginTime, LocalDateTime.now(), 0, 1000)

		// assert
		assertEquals(1, result.size)
		assertEquals(jsonData, result[0].data)
		assertEquals(providerSut.id, result[0].providerId)
		assertTrue { result[0].fetchTime.isBefore(LocalDateTime.now()) }
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
		val rawDataSut = RawData(providerSut!!.id, LocalDateTime.now(), stringData)

		// assert
		assertFailsWith<UnableToExecuteStatementException> {
			rawDataRepo.saveRawData(rawDataSut)
		}
	}
}