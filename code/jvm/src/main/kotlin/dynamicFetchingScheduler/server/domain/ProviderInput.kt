package dynamicFetchingScheduler.server.domain

import java.net.URL
import java.time.Duration

/**
 *
 */
data class ProviderInput(
	val name: String,
	val url: URL,
	val frequency: Duration,
	val isActive: Boolean
)