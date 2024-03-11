package dynamicFetchingScheduler.server.service

import dynamicFetchingScheduler.server.domain.PaginatedProviderWithData
import dynamicFetchingScheduler.server.domain.ProviderInput
import dynamicFetchingScheduler.server.domain.ScheduledProvider
import dynamicFetchingScheduler.server.service.errors.AddProviderError
import dynamicFetchingScheduler.server.service.errors.DeleteProviderError
import dynamicFetchingScheduler.server.service.errors.GetProviderError
import dynamicFetchingScheduler.server.service.errors.UpdateProviderError
import dynamicFetchingScheduler.utils.Either

/**
 * Result for adding a provider
 * @see AddProviderError
 * @see ProviderInput
 */
typealias AddProviderResult = Either<AddProviderError, ScheduledProvider>

/**
 * Result for updating a provider
 * @see UpdateProviderError
 * @see ProviderInput
 */
typealias UpdateProviderResult = Either<UpdateProviderError, ScheduledProvider>

/**
 * Result for deleting a provider
 * @see UpdateProviderError
 */
typealias DeleteProviderResult = Either<DeleteProviderError, Unit>

/**
 * Result for getting a provider
 * @see GetProviderError
 */
typealias GetProviderResult = Either<GetProviderError, PaginatedProviderWithData>
