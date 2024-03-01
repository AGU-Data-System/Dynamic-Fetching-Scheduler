package uagSystem.server.service

import uagSystem.server.domain.Provider
import uagSystem.server.service.errors.AddOrUpdateProviderError
import uagSystem.server.service.errors.DeleteProviderError
import uagSystem.utils.Either

/**
 * Result for adding or updating a provider
 * @see AddOrUpdateProviderError
 */
typealias AddOrUpdateProviderResult = Either<AddOrUpdateProviderError, Provider>

/**
 * Result for deleting a provider
 * @see DeleteProviderError
 */
typealias DeleteProviderResult = Either<DeleteProviderError, Unit>