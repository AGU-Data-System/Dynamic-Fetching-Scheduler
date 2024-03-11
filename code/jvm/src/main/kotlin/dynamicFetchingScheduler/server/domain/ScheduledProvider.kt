package dynamicFetchingScheduler.server.domain

/**
 * A provider with its scheduling status.
 *
 * @property provider The provider
 * @property isScheduled Whether the provider is scheduled
 */
data class ScheduledProvider(
	val provider: Provider,
	val isScheduled: Boolean
)