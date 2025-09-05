package weather

import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


fun main() {
    val url = "https://api.weatherapi.com/v1/current.json?key=2c05f944a3184738b01185438250409&q=London&aqi=no"
    val json = Json{ignoreUnknownKeys=true}
    val client = HttpClient.newHttpClient()
    val request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .GET()
        .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    println(response.body())

}