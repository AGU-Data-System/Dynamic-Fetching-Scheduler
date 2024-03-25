package dynamicFetchingScheduler.server

import dynamicFetchingScheduler.server.repository.configureWithAppRequirements
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

@SpringBootApplication
class DynamicFetchingSchedulerApplication {

	/**
	 * Creates a JDBI instance.
	 */
	@Bean
	fun jdbi() = Jdbi.create(
		PGSimpleDataSource().apply {
			setURL(Environment.getDbUrl())
		}
	).configureWithAppRequirements()

	/**
	 * Creates a RestTemplate.
	 */
	@Bean
	fun restTemplate() = RestTemplate()

}

/**
 * Entry point of the application.
 */
fun main(args: Array<String>) {
	runApplication<DynamicFetchingSchedulerApplication>(*args)
}
