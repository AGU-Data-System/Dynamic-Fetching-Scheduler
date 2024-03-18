package dynamicFetchingScheduler.server.http

import java.net.URI

/**
 * URIs for the HTTP server.
 */
object URIs {

	private const val PREFIX = "/api"
	const val PROVIDER = "$PREFIX/provider"
	const val PROVIDERS = "$PREFIX/providers"
	const val PROVIDER_WITH_ID = "$PREFIX/provider/{id}"

	/**
	 * Creates the URI for a provider.
	 */
	fun provider(id: Int) = URI("$PROVIDER/$id")
}
