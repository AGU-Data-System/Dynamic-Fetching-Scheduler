package dynamicFetchingScheduler.server.http

import java.net.URI

/**
 * URIs for the HTTP server.
 */
object URIs {

	private const val PREFIX = "/api"
	const val PROVIDER = "$PREFIX/provider"
	const val PROVIDERS = "$PREFIX/providers"
	const val UPDATE_PROVIDER = "$PREFIX/provider/{id}" // TODO: change names to be more consistent

	/**
	 * Creates the URI for a provider.
	 */
	fun provider(id: Int) = URI("$PROVIDER/$id")
}
