package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.Provider
import dynamicFetchingScheduler.server.service.errors.AddProviderError
import dynamicFetchingScheduler.server.service.errors.DeleteProviderError
import dynamicFetchingScheduler.server.service.errors.UpdateProviderError
import dynamicFetchingScheduler.utils.Either

/**
 * Result for adding a provider
 * @see AddProviderError
 * @see Provider
 */
typealias AddProviderResult = Either<AddProviderError, Provider>

/**
 * Result for updating a provider
 * @see UpdateProviderError
 * @see Provider
 */
typealias UpdateProviderResult = Either<UpdateProviderError, Provider>

/**
 * Result for deleting a provider
 * @see UpdateProviderError
 */
typealias DeleteProviderResult = Either<DeleteProviderError, Unit>
