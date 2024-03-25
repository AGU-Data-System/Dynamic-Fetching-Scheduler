package dynamicFetchingScheduler.server.http.controller

import dynamicFetchingScheduler.server.http.controller.HTTPUtils.addProvider
import dynamicFetchingScheduler.server.http.controller.HTTPUtils.deleteProvider
import dynamicFetchingScheduler.server.http.controller.HTTPUtils.getAllProviders
import dynamicFetchingScheduler.server.http.controller.HTTPUtils.getProviderData
import dynamicFetchingScheduler.server.http.controller.HTTPUtils.updateProvider
import dynamicFetchingScheduler.server.http.controller.ProviderModels.toProviderResponse
import dynamicFetchingScheduler.server.http.controller.ProviderModels.toProviderResponseList
import dynamicFetchingScheduler.server.http.controller.ProviderModels.toProviderWithDataResponse
import dynamicFetchingScheduler.server.testUtils.SchemaManagementExtension
import java.time.Duration
import java.time.LocalDateTime
import kotlin.test.Test
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
		val result = addProvider(client, sut).toProviderResponse().takeLastFetch()
		val allProviders = getAllProviders(client).toProviderResponseList().takeLastFetch()

		// assert
		assert(allProviders.providers.isNotEmpty())
		assert(allProviders.providers.contains(result))

		cleanTest(result.id)
	}

	@Test
	fun `update provider`() {
		// arrange
		val client = testClient()
		val sut = dummyProvider

		val provider = addProvider(client, sut).toProviderResponse()

		val resultList1 = getAllProviders(client).toProviderResponseList()

		// act
		val updatedProviderModel = dummyProvider.copy(name = "Test Update")
		updateProvider(client, provider.id, updatedProviderModel)

		val resultList2 = getAllProviders(client).toProviderResponseList()
		val updatedProvider = resultList2.providers.first { it.id == provider.id }

		// assert
		assert(resultList2.providers.contains(updatedProvider))
		assertFalse(resultList2.providers.contains(provider))
		assertNotEquals(resultList1, resultList2)

		cleanTest(updatedProvider.id)
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
		addProvider(client, sut1).toProviderResponse()
		addProvider(client, sut2).toProviderResponse()

		// act
		val result = getAllProviders(client).toProviderResponseList()

		// assert
		assert(result.providers.size == 2)
		assert(result.providers.any { it.name == sut1.name })
		assert(result.providers.any { it.name == sut2.name })

		cleanTest(*result.providers.map { it.id }.toIntArray())
	}

	@Test
	fun `get provider's info and data by url`() {
		// arrange
		val client = testClient()
		val sut = dummyProvider
		val provider = addProvider(client, sut).toProviderResponse()
		// act

		val curTime = LocalDateTime.now()
		runBlocking {
			delay(THIRTY_SECONDS)
		}
		val result = getProviderData(client, provider.id, curTime).toProviderWithDataResponse()

		// assert
		assert(result.id == provider.id)
		assert(result.dataList.size in 1..3)

		cleanTest(provider.id)
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
