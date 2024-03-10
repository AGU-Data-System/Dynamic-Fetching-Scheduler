package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.Provider
import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.domain.RawData
import dynamicFetchingScheduler.server.service.errors.AddProviderError
import dynamicFetchingScheduler.server.service.errors.DeleteProviderError
import dynamicFetchingScheduler.server.service.errors.GetProviderError
import dynamicFetchingScheduler.server.service.errors.UpdateProviderError
import dynamicFetchingScheduler.utils.Either
import dynamicFetchingScheduler.utils.PaginationResult

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

/**
 * Result for getting a provider
 * @see GetProviderError
 */
typealias GetProviderResult = Either<GetProviderError, Pair<Provider, PaginationResult<RawData>>>
