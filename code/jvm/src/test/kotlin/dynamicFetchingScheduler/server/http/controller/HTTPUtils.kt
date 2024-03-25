package dynamicFetchingScheduler.server.http.controller

import dynamicFetchingScheduler.server.http.controller.ProviderModels.SendModel
import java.time.LocalDateTime
import kotlin.test.assertTrue
import org.springframework.test.web.reactive.server.WebTestClient

/**
 * This object is used to make HTTP requests to the server
 */
object HTTPUtils {
	/**
	 * This method is used to add a provider
	 *
	 * @param client the Client that allows to communicate with the server
	 * @param sendModel the provider to add
	 *
	 * @return the response body of the request
	 */
	fun addProvider(client: WebTestClient, sendModel: SendModel) =
		client.post().uri("/provider")
			.bodyValue(
				sendModel
			)
			.exchange()
			.expectStatus().is2xxSuccessful
			.expectHeader().value("Location") {
				assertTrue(it.startsWith("/api/provider/"))
			}
			.expectBody()
			.returnResult()
			.responseBody!!

	/**
	 * This method is used to update a provider
	 *
	 * @param client the Client that allows to communicate with the server
	 * @param sendModel the provider to update
	 *
	 * @return the response body of the request
	 */
	fun updateProvider(client: WebTestClient, providerId: Int, sendModel: SendModel) =
		client.post().uri("/provider/$providerId")
			.bodyValue(
				sendModel
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
	 * @param providerId the ID of the provider to delete
	 *
	 * @return the response body of the request
	 */
	fun deleteProvider(client: WebTestClient, providerId: Int) =
		client.delete().uri("/provider/$providerId")
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
	fun getAllProviders(client: WebTestClient) =
		client.get().uri("/providers")
			.exchange()
			.expectStatus().is2xxSuccessful
			.expectBody()
			.returnResult()
			.responseBody!!

	/**
	 * This method is used to get a provider with its data
	 *
	 * @param client the Client that allows to communicate with the server
	 * @param providerId the ID of the provider to get
	 * @param beginDate the date to get the data from
	 */
	fun getProviderData(client: WebTestClient, providerId: Int, beginDate: LocalDateTime) =
		client.get().uri("/provider/$providerId?beginDate=$beginDate")
			.exchange()
			.expectStatus().is2xxSuccessful
			.expectBody()
			.returnResult()
			.responseBody!!
}