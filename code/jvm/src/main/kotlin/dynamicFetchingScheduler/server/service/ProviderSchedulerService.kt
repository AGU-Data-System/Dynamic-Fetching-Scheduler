package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.Provider
import dynamicFetchingScheduler.server.repository.TransactionManager
import jakarta.annotation.PostConstruct
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import org.springframework.stereotype.Service

/**
 * Service for scheduling provider periodic fetching.
 */
@Service
class ProviderSchedulerService(
    private val transactionManager: TransactionManager,
    private val fetchDataService: FetchDataService
) {
    private val providerPoolSize = 10
    private val scheduledTasks: MutableMap<Int, ScheduledFuture<*>> = ConcurrentHashMap()
    private val scheduler: ScheduledExecutorService =
        Executors.newScheduledThreadPool(providerPoolSize)

    @PostConstruct
    fun initialize() {
        scheduleActiveProviders()
    }

    /**
     * Schedule the active providers to be fetched periodically.
     */
    fun scheduleActiveProviders() {
        transactionManager.run {
            val activeProviders = it.providerRepository.getActiveProviders()
            activeProviders.forEach { provider ->
                if (provider.isActive) {
                    scheduleProviderTask(provider)
                }
            }
        }
    }

    /**
     * Schedule a provider to be fetched periodically.
     *
     * @param provider The provider to schedule
     */
    fun scheduleProviderTask(provider: Provider) {
        val delay = calculateInitialDelay(provider.lastFetch, provider.frequency)
        val future: ScheduledFuture<*> = scheduler.scheduleAtFixedRate({
            fetchDataService.fetchAndSave(provider.url)
        }, delay, provider.frequency.toMillis(), TimeUnit.MILLISECONDS)
        scheduledTasks[provider.id] = future
    }

    /**
     * Stop a provider from being fetched periodically if it changes to inactive.
     *
     * @param providerId The URL of the provider to stop fetching
     */
    fun stopProviderTask(providerId: Int) {
        val future = scheduledTasks[providerId]
        future?.cancel(false)
        scheduledTasks.remove(providerId)
    }

    /**
     * Calculate the initial delay for a provider to be fetched.
     *
     * @param lastFetched The last time the provider was fetched
     * @param frequency The frequency to fetch the provider
     * @return The initial delay
     */
    private fun calculateInitialDelay(lastFetched: LocalDateTime?, frequency: Duration): Long {
        lastFetched ?: return 0
        val now = LocalDateTime.now()
        val nextFetch = lastFetched + frequency
        return if (now.isBefore(nextFetch)) Duration.between(now, nextFetch).toMillis() else 0
    }
}
