package dynamicFetchingScheduler.server.http.controller

import dynamicFetchingScheduler.server.http.controller.HTTPUtils.addProvider
import dynamicFetchingScheduler.server.http.controller.HTTPUtils.addProviderFail
import dynamicFetchingScheduler.server.http.controller.HTTPUtils.deleteProvider
import dynamicFetchingScheduler.server.http.controller.HTTPUtils.getAllProviders
import dynamicFetchingScheduler.server.http.controller.HTTPUtils.getProviderData
import dynamicFetchingScheduler.server.http.controller.HTTPUtils.updateProvider
import dynamicFetchingScheduler.server.http.controller.HTTPUtils.updateProviderFail
import dynamicFetchingScheduler.server.http.controller.ProviderModels.toProviderId
import dynamicFetchingScheduler.server.http.controller.ProviderModels.toProviderResponseList
import dynamicFetchingScheduler.server.http.controller.ProviderModels.toProviderWithDataResponse
import dynamicFetchingScheduler.server.testUtils.SchemaManagementExtension
import java.time.Duration
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SchemaManagementExtension::class)
class ProviderControllerTest {

	private val dummyProvider = ProviderModels.SendModel(
		name = "Test Provider 1",
		url = "https://jsonplaceholder.typicode.com/todos/1",
		frequency = Duration.ofSeconds(DEFAULT_PROVIDER_FREQUENCY).toString(),
		isActive = true
	)

	// One of the very few places where we use property injection
	@LocalServerPort
	var port: Int = 8080

	/**
	 * This method is used to create a test client
	 *
	 * @return the test client
	 */
	private fun testClient() = WebTestClient
		.bindToServer()
		.baseUrl("http://localhost:$port/api")
		.responseTimeout(Duration.ofHours(1))
		.build()

	@Test
	fun `add provider`() {
		// arrange
		val client = testClient()
		val sut = dummyProvider

		// act
		val result = addProvider(client, sut).toProviderId()
		val allProviders = getAllProviders(client).toProviderResponseList()

		// assert
		assert(allProviders.providers.isNotEmpty())
		assert(allProviders.providers.map { it.id }.contains(result))

		cleanTest(result)
	}

	@Test
	fun `add provider should fail with invalid URL`() {
		// arrange
		val client = testClient()
		val sut = dummyProvider.copy(url = "Error URL")

		// act & assert
		addProviderFail(client, sut)

		cleanTest()
	}

	@Test
	fun `add provider should fail with invalid frequency`() {
		// arrange
		val client = testClient()
		val sut = dummyProvider.copy(frequency = "Error Frequency")

		// act & assert
		addProviderFail(client, sut)

		cleanTest()
	}

	@Test
	fun `update provider`() {
		// arrange
		val client = testClient()
		val sut = dummyProvider

		val provider = addProvider(client, sut).toProviderId()

		val resultList1 = getAllProviders(client).toProviderResponseList()

		// act
		val updatedProviderModel = dummyProvider.copy(name = "Test Update")
		updateProvider(client, provider, updatedProviderModel)

		val resultList2 = getAllProviders(client).toProviderResponseList()
		val updatedProvider = resultList2.providers.first { it.id == provider }

		// assert
		assert(resultList2.providers.contains(updatedProvider))
		assertNotEquals(resultList1, resultList2)

		cleanTest(provider)
	}

	@Test
	fun `update provider should fail`() {
		// arrange
		val client = testClient()
		val sut = dummyProvider

		val provider = addProvider(client, sut).toProviderId()

		val resultList1 = getAllProviders(client).toProviderResponseList()

		// act
		val updatedProviderModel =
			dummyProvider.copy(name = "Test Update", url = "Error URL", frequency = "Error Frequency")
		updateProviderFail(client, provider, updatedProviderModel)

		val resultList2 = getAllProviders(client).toProviderResponseList()
		val updatedProvider = resultList2.providers.find { it.name == "Test Update" }

		// assert
		assertNull(updatedProvider)
		assert(resultList2.providers.map { it.id }.contains(provider))
		assertEquals(resultList1, resultList2)

		cleanTest(provider)
	}

	@Test
	fun `delete provider`() {
		// arrange
		val client = testClient()
		val sut = dummyProvider

		addProvider(client, sut)
		addProvider(client, sut.copy(name = "Test Delete"))

		val resultList1 = getAllProviders(client).toProviderResponseList()

		assert(resultList1.providers.isNotEmpty())

		val providerToDelete = resultList1.providers.find { it.url == sut.url && it.name == "Test Delete" }
		assertNotNull(providerToDelete)
		requireNotNull(providerToDelete)

		runBlocking {
			// Wait for the provider to be fetched
			delay(TEN_SECONDS / 2)
		}

		// act
		deleteProvider(client, providerToDelete.id)

		val resultList2 = getAllProviders(client).toProviderResponseList()

		// assert
		assert(resultList2.providers.size == resultList1.providers.size - 1)
		assertFalse(resultList2.providers.contains(providerToDelete))

		cleanTest(*resultList2.providers.map { it.id }.toIntArray())
	}

	@Test
	fun `get all providers`() {
		// arrange
		val client = testClient()
		val sut1 = dummyProvider
		val sut2 = dummyProvider.copy(name = "Test Provider 2")
		addProvider(client, sut1)
		addProvider(client, sut2)

		// act
		val result = getAllProviders(client).toProviderResponseList()

		// assert
		assert(result.providers.size == 2)
		assert(result.providers.any { it.name == sut1.name })
		assert(result.providers.any { it.name == sut2.name })

		cleanTest(*result.providers.map { it.id }.toIntArray())
	}

	@Test
	fun `get all providers shouldn't return invalid providers`() {
		// arrange
		val client = testClient()
		val sut1 = dummyProvider.copy(url = "Error URL")
		val sut2 = dummyProvider.copy(frequency = "Error Frequency")
		val sut3 = dummyProvider.copy(url = "Error URL", frequency = "Error Frequency")
		addProviderFail(client, sut1)
		addProviderFail(client, sut2)
		addProviderFail(client, sut3)

		// act
		val result = getAllProviders(client).toProviderResponseList()

		// assert
		assert(result.providers.filterNot { it.name == sut1.name || it.name == sut2.name || it.name == sut3.name }
			.isEmpty())
	}

	@Test
	fun `get provider's info and data by url`() {
		// arrange
		val client = testClient()
		val sut = dummyProvider
		val provider = addProvider(client, sut).toProviderId()
		// act

		val curTime = LocalDateTime.now()
		runBlocking {
			delay(THIRTY_SECONDS)
		}
		val result = getProviderData(client, provider, curTime).toProviderWithDataResponse()

		// assert
		assert(result.id == provider)
		assert(result.dataList.size in 1..3)

		cleanTest(provider)
	}

	/**
	 * This method is used to clean up the test because it is not wrapped in a transaction
	 *
	 * @param ids The ids of the providers to clean up
	 */
	private fun cleanTest(vararg ids: Int) {
		ids.forEach { id ->
			deleteProvider(testClient(), id)
		}
	}

	companion object {
		private const val ONE_SECOND = 1000L
		private const val TEN_SECONDS = 10 * ONE_SECOND
		private const val THIRTY_SECONDS = 30 * ONE_SECOND
		private const val DEFAULT_PROVIDER_FREQUENCY = 10L
	}
}
