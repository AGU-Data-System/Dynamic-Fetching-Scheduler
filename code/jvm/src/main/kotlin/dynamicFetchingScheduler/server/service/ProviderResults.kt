package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.Provider
import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.service.errors.AddProviderError
import dynamicFetchingScheduler.server.service.errors.DeleteProviderError
import dynamicFetchingScheduler.server.service.errors.UpdateProviderError
import dynamicFetchingScheduler.utils.Either

/**
 * Represents the success case of adding a providers,
 * Contains the provider, and if the scheduling for that provider is on or not
 */
typealias ProviderSuccess = Pair<Provider, Boolean>

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
