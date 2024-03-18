package dynamicFetchingScheduler.server.testUtils

import dynamicFetchingScheduler.utils.Either
import dynamicFetchingScheduler.utils.Failure
import dynamicFetchingScheduler.utils.Success

/**
 * Accesses if the given [Either] is a [Success]
 * and returns its value if not returns null
 * @receiver The [Either] to access
 * @return The value of the [Success] or null
 */
fun <L, R> Either<L, R>.successOrNull(): R? =
	when (this) {
		is Success -> this.value
		else -> null
	}

/**
 * Accesses if the given [Either] is a [Failure]
 * and returns its value if not returns null
 * @receiver The [Either] to access
 * @return The value of the [Failure] or null
 */
fun <L, R> Either<L, R>.failureOrNull(): L? =
	when (this) {
		is Failure -> this.value
		else -> null
	}