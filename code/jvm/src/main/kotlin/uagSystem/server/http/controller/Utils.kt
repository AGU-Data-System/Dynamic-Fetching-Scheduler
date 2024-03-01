package uagSystem.server.http.controller

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * Makes a request to the given URL.
 *
 * @param url The URL to make the request to
 * @return The response body
 */
fun fetch(url: String): String {
	val client = HttpClient.newHttpClient()
	val request = HttpRequest.newBuilder()
		.uri(URI.create(url))
		.GET()
		.build()
	val response = client.send(request, HttpResponse.BodyHandlers.ofString())
	return response.body()
}
