package dynamicFetchingScheduler.server

import dynamicFetchingScheduler.server.repository.configureWithAppRequirements
import kotlinx.datetime.Clock
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class UAGSystemApplication {

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
     * Creates a clock.
     */
    @Bean
    fun clock() = Clock.System

}

fun main(args: Array<String>) {
    runApplication<UAGSystemApplication>(*args)
}
