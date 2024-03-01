package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.Provider
import dynamicFetchingScheduler.server.repository.TransactionManager
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

@Service
class ProviderSchedulerService(
    private val transactionManager: TransactionManager, //TODO: Fix
    private val fetchDataService: FetchDataService
) {
    private val scheduledTasks: MutableMap<Int, ScheduledFuture<*>> = ConcurrentHashMap() //TODO: FIX
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(10) //TODO: Probably make this a variable

    @PostConstruct
    fun initialize() {
        scheduleActiveProviders()
    }

    //TODO: Be aware of transactional issues
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

    fun scheduleProviderTask(provider: Provider) {
        val delay = calculateInitialDelay(provider.lastFetch, provider.frequency)
        val future: ScheduledFuture<*> = scheduler.scheduleAtFixedRate({
            fetchDataService.fetchAndSave(provider.id) //TODO: Fix
        }, delay, provider.frequency.toMillis(), TimeUnit.MILLISECONDS)

        scheduledTasks[provider.id] = future //TODO: Fix
    }

    fun stopProviderTask(providerId: Int) { //TODO: Fix
        val future = scheduledTasks[providerId]
        future?.cancel(false)
        scheduledTasks.remove(providerId)
    }

    private fun calculateInitialDelay(lastFetched: LocalDateTime?, frequency: Duration): Long {
        lastFetched ?: return 0
        val now = LocalDateTime.now()
        val nextFetch = lastFetched.plus(frequency)
        return if (now.isBefore(nextFetch)) Duration.between(now, nextFetch).toMillis() else 0
    }
}
