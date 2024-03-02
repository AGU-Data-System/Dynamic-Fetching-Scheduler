package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.Provider
import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.service.errors.AddProviderError
import dynamicFetchingScheduler.server.service.errors.DeleteProviderError
import dynamicFetchingScheduler.server.service.errors.UpdateProviderError
import dynamicFetchingScheduler.utils.Either

/**
 * Result for adding a provider
 * @see AddProviderError
 * @see ProviderInput
 */
typealias AddProviderResult = Either<AddProviderError, ProviderSuccess>



/**
 * Result for updating a provider
 * @see UpdateProviderError
 * @see ProviderInput
 */
typealias UpdateProviderResult = Either<UpdateProviderError, ProviderSuccess>

/**
 * Result for deleting a provider
 * @see UpdateProviderError
 */
typealias DeleteProviderResult = Either<DeleteProviderError, Unit>
