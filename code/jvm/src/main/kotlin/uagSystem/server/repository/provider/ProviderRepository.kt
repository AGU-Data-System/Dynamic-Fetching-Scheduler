package uagSystem.server.repository.provider

import java.net.URL
import uagSystem.server.domain.Provider

/**
 * Repository for the Provider database operations.
 */
interface ProviderRepository {

	/**
	 * Find a provider by its url.
	 */
	fun findByUrl(url: URL): Provider?

	/**
	 * Add a provider to the database.
	 */
	fun add(provider: Provider)

	/**
	 * Update a provider in the database.
	 */
	fun update(provider: Provider)

	/**
	 * Delete a provider from the database.
	 */
	fun delete(url: URL)
}