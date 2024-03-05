package dynamicFetchingScheduler.server.http.controller

import kotlin.test.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProviderControllerTest {

	// One of the very few places where we use property injection
	@LocalServerPort
	var port: Int = 8080

	@Test
	fun `add provider`() {
		// arrange
		// act
		// assert
	}

	@Test
	fun `update provider`() {
		// arrange
		// act
		// assert
	}

	@Test
	fun `delete provider`() {
		// arrange
		// act
		// assert
	}

	@Test
	fun `get all providers with their data`() {
		// arrange
		// act
		// assert
	}

	@Test
	fun `get provider's info and data by url`() {
		// arrange
		// act
		// assert
	}
}