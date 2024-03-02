package dynamicFetchingScheduler.server.http.controller

import kotlin.test.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProviderInputControllerTest {

	// One of the very few places where we use property injection
	@LocalServerPort
	var port: Int = 8080

	@Test
	fun `add provider`() {
		// arrange
		// act
		// assert
	}
}