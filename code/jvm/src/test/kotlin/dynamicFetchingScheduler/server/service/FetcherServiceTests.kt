package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.testWithTransactionManagerAndRollback
import org.junit.jupiter.api.Test
import java.net.URL
import java.time.Duration

class FetcherServiceTests {

    @Test
    fun `add new provider`() {
        // arrange
        val dummy = ProviderInput(
            name = "dummy",
            url = URL("http://dummy.com"),
            frequency = Duration.ofMillis(1000),
            isActive = true
        )
        // act
        testWithTransactionManagerAndRollback {
//			val service = ProviderService(it)
//			val sut = service.addProvider(dummy)
//			// assert
//			assert(sut is Success)
        }
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
    fun `update provider with not unique url should fail`() {
        // arrange
        // act
        // assert
    }


}