package dynamicFetchingScheduler.server.http.controller

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HomeTest {

	// One of the very few places where we use property injection
	@LocalServerPort
	var port: Int = 8080

	@Test
	fun `get home page`() {
		val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

		client.get().uri("/api/")
			.exchange()
			.expectStatus().isOk
			.expectBody()
	}
}