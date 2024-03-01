package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.Provider
import dynamicFetchingScheduler.server.testWithTransactionManagerAndRollback
import dynamicFetchingScheduler.utils.Success
import org.junit.jupiter.api.Test
import java.net.URL
import java.time.Duration

class FetcherServiceTests {

    @Test
    fun `add new provider`() {
        // arrange
        val dummy = Provider(
            name = "dummy",
            url = URL("http://dummy.com"),
            frequency = Duration.ofMillis(1000),
            isActive = true,
            lastFetch = null
        )
        // act
        testWithTransactionManagerAndRollback {
            val service = FetcherService(it)
            val sut = service.addOrUpdateProvider(dummy)
            // assert
            assert(sut is Success)
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