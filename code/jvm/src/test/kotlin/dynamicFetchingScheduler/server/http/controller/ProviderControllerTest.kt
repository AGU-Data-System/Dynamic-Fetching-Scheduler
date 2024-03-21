package dynamicFetchingScheduler.server.http.controller

import com.google.gson.Gson
import dynamicFetchingScheduler.server.http.controller.models.inputModels.ProviderInputModel
import dynamicFetchingScheduler.server.http.controller.models.outputModels.ProviderOutputModel
import dynamicFetchingScheduler.server.testUtils.SchemaManagementExtension
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SchemaManagementExtension::class)
class ProviderControllerTest {

	private val dummyProviderInput = ProviderInputModel(
		name = "ipma current day",
		url = "https://api.ipma.pt/open-data/forecast/meteorology/cities/daily/hp-daily-forecast-day0.json",
		frequency = Duration.ofSeconds(TEN_SECONDS).toString(),
		isActive = true
	)

	// One of the very few places where we use property injection
	@LocalServerPort
	var port: Int = 8080

	@Test
	fun `add provider`() {
		val client = WebTestClient
			.bindToServer()
			.baseUrl("http://localhost:$port/api")
			.responseTimeout(Duration.ofHours(1))
			.build()

		val sut = addProvider(client, dummyProviderInput)
		val result = getAllProviders(client)

		assert(result.isNotEmpty())
		assert(result.contains(sut))
	}

	@Test
	fun `update provider`() {
		val client = WebTestClient
			.bindToServer()
			.baseUrl("http://localhost:$port/api")
			.responseTimeout(Duration.ofHours(1))
			.build()

		val providerInput = ProviderInputModel(
			name = "Test Update",
			url = "https://api.ipma.pt/open-data/forecast/meteorology/cities/daily/hp-daily-forecast-day2.json",
			frequency = Duration.ofSeconds(TEN_SECONDS).toString(),
			isActive = true
		)

		addProvider(client, providerInput)

		val providers = getAllProviders(client)
		val resultList1 = GSON.fromJson(providers, ProviderList::class.java)

		assert(resultList1.providers.isNotEmpty())

		val provider = resultList1.providers.first { it.name == providerInput.name && it.url == providerInput.url}

		val updatedProviderModel = ProviderInputModel(
			name = "Updated name",
			url = "https://api.ipma.pt/open-data/forecast/meteorology/cities/daily/hp-daily-forecast-day1.json",
			frequency = Duration.ofSeconds(TEN_SECONDS/2).toString(),
			isActive = false
		)

		updateProvider(client, provider.id, updatedProviderModel)

		val resultList2 = GSON.fromJson(getAllProviders(client), ProviderList::class.java)

		val updatedProvider = resultList2.providers.first { it.id == provider.id }

		assert(resultList2.providers.contains(updatedProvider))
		assertFalse(resultList2.providers.contains(provider))
		assertNotEquals(resultList1, resultList2)
	}

	@Test
	fun `delete provider`() {
		val client = WebTestClient
			.bindToServer()
			.baseUrl("http://localhost:$port/api")
			.responseTimeout(Duration.ofHours(1))
			.build()

		addProvider(client, dummyProviderInput)
		addProvider(client, dummyProviderInput.copy(name = "Test Delete"))

		val resultList1 = GSON.fromJson(getAllProviders(client), ProviderList::class.java)

		assert(resultList1.providers.isNotEmpty())

		val providerToDelete = resultList1.providers.find { it.url == dummyProviderInput.url && it.name == "Test Delete" }
		assertNotNull(providerToDelete)

		runBlocking {
			// Wait for the provider to be fetched
			delay(TEN_SECONDS/2)
		}

		deleteProvider(client, providerToDelete.id)

		val resultList2 = GSON.fromJson(getAllProviders(client), ProviderList::class.java)

		assert(resultList2.providers.size == resultList1.providers.size - 1)
		assertFalse(resultList2.providers.contains(providerToDelete))
	}

	@Test
	fun `get all providers with their data`() {
		// arrange
		val client = WebTestClient
			.bindToServer()
			.baseUrl("http://localhost:$port/api")
			.responseTimeout(Duration.ofHours(1))
			.build()

		// act
		// assert
	}

	@Test
	fun `get provider's info and data by url`() {
		// arrange
		val client = WebTestClient
			.bindToServer()
			.baseUrl("http://localhost:$port/api")
			.responseTimeout(Duration.ofHours(1))
			.build()

		// act
		// assert
	}

	companion object {
		private const val TEN_SECONDS = 10*1000L
		private val GSON = Gson()

		private data class ProviderList(
			val providers: List<ProviderOutputModel>,
			val size: Int
		)
	}

	/**
	 * This method is used to add a provider
	 *
	 * @param client the Client that allows to communicate with the server
	 * @param providerInput the provider to add
	 *
	 * @return the response body of the request
	 */
	private fun addProvider(client: WebTestClient, providerInput: ProviderInputModel) =
		client.post().uri("/provider")
			.bodyValue(
				providerInput
			)
			.exchange()
			.expectStatus().is2xxSuccessful
			.expectBody()
			.returnResult()
			.responseBody!!
			.toString(Charsets.UTF_8)

	/**
	 * This method is used to update a provider
	 *
	 * @param client the Client that allows to communicate with the server
	 * @param providerInput the provider to update
	 *
	 * @return the response body of the request
	 */
	private fun updateProvider(client: WebTestClient, providerId: Int, providerInput: ProviderInputModel) =
		client.post().uri("/provider/$providerId")
			.bodyValue(
				providerInput
			)
			.exchange()
			.expectStatus().is2xxSuccessful
			.expectBody()
			.returnResult()
			.responseBody!!
			.toString(Charsets.UTF_8)

	/**
	 * This method is used to delete a provider
	 *
	 * @param client the Client that allows to communicate with the server
	 * @param providerId the ID of the provider to delete
	 *
	 * @return the response body of the request
	 */
	private fun deleteProvider(client: WebTestClient, providerId: Int) =
		client.delete().uri("/provider/$providerId")
			.exchange()
			.expectStatus().is2xxSuccessful
			.expectBody()
			.returnResult()
			.responseBody!!
			.toString(Charsets.UTF_8)

	/**
	 * This method is used to get all providers
	 *
	 * @param client the Client that allows to communicate with the server
	 *
	 * @return the response body of the request
	 */
	private fun getAllProviders(client: WebTestClient) =
		client.get().uri("/providers")
			.exchange()
			.expectStatus().is2xxSuccessful
			.expectBody()
			.returnResult()
			.responseBody!!
			.toString(Charsets.UTF_8)
}
