package dynamicFetchingScheduler.server.http.controller

import dynamicFetchingScheduler.server.http.controller.models.ProviderInputModel
import java.time.Duration
import kotlin.test.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProviderControllerTest {

	private val dummyProviderInput = ProviderInputModel(
		name = "ipma current day",
		url = "https://api.ipma.pt/open-data/forecast/meteorology/cities/daily/hp-daily-forecast-day0.json",
		frequency = "P0Y0M0DT1H1M1S",
		isActive = true
	)

	// One of the very few places where we use property injection
	@LocalServerPort
	var port: Int = 8080

	@Test
	fun `add provider`() {
		// arrange
		val client = WebTestClient
			.bindToServer()
			.baseUrl("http://localhost:$port/api")
			.responseTimeout(Duration.ofHours(1))
			.build()
		val sut = addProvider(client, dummyProviderInput)

		// act
		addProvider(client, dummyProviderInput)
		val result = getProvider(client, dummyProviderInput.url).also { println(it) }

		// assert
		assert(result == sut)
	}

	@Test
	fun `update provider`() {
		// arrange
		val client = WebTestClient
			.bindToServer()
			.baseUrl("https://localhost:$port/api")
			.responseTimeout(Duration.ofHours(1))
			.build()

		// act
		// assert
	}

	@Test
	fun `delete provider`() {
		// arrange
		val client = WebTestClient
			.bindToServer()
			.baseUrl("https://localhost:$port/api")
			.responseTimeout(Duration.ofHours(1))
			.build()

		// act
		// assert
	}

	@Test
	fun `get all providers with their data`() {
		// arrange
		val client = WebTestClient
			.bindToServer()
			.baseUrl("https://localhost:$port/api")
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
			.baseUrl("https://localhost:$port/api")
			.responseTimeout(Duration.ofHours(1))
			.build()

		// act
		// assert
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

	/**
	 * This method is used to update a provider
	 *
	 * @param client the Client that allows to communicate with the server
	 * @param providerInput the provider to update
	 *
	 * @return the response body of the request
	 */
	private fun updateProvider(client: WebTestClient, providerInput: ProviderInputModel) =
		client.post().uri("/provider/update")
			.bodyValue(
				providerInput
			)
			.exchange()
			.expectStatus().is2xxSuccessful
			.expectBody()
			.returnResult()
			.responseBody!!

	/**
	 * This method is used to delete a provider
	 *
	 * @param client the Client that allows to communicate with the server
	 * @param providerURL the URL of the provider to delete
	 *
	 * @return the response body of the request
	 */
	private fun deleteProvider(client: WebTestClient, providerURL: String) =
		client.post().uri("/provider")
			.bodyValue(
				"url" to providerURL
			)
			.exchange()
			.expectStatus().is2xxSuccessful
			.expectBody()
			.returnResult()
			.responseBody!!

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

	/**
	 * This method is used to get a provider
	 *
	 * @param client the Client that allows to communicate with the server
	 * @param providerURL the URL of the provider to get
	 *
	 * @return the response body of the request
	 */
	private fun getProvider(client: WebTestClient, providerURL: String) =
		client.get().uri("/provider?url=$providerURL")
			.exchange()
			.expectStatus().is2xxSuccessful
			.expectBody()
			.returnResult()
			.responseBody!!
}