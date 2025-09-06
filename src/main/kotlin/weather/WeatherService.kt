package weather

import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WeatherService {
    private val apiKey = "2c05f944a3184738b01185438250409"
    private val baseUrl = "https://api.weatherapi.com/v1"
    private val client = HttpClient.newHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getWeatherData(location: String): WeatherResponse? {
        return try {
            val url = "$baseUrl/current.json?key=$apiKey&q=$location&aqi=no"

            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            if (response.statusCode()==200){
                json.decodeFromString<WeatherResponse>(response.body())
            }
            else {
                println("${response.statusCode()} - ${response.body()}")
                null
            }
        }
        catch (e: Exception){
            println("Error fetching weather data: ${e.message}")
            null
        }

    }
}
class WeatherDisplay{
    fun displayWeather(weather: WeatherResponse){
        val location = weather.location
        val current = weather.current

        println("=".repeat(20))
        println("Weather Summary")
        println("Last Updated: ${current.last_updated}")
        println("\uD83D\uDCCD ${location.name}, ${location.country}, ${location.region}")
        println("\uD83D\uDD52 ${formatDateTime(location.localtime)}")
        println("=".repeat(50))

        //temp
        println("\uD83C\uDF21\uFE0F Temperature: ${current.temp_c}°C | ${current.temp_f}°F")
        println("\uD83E\uDD0F Feels Like: ${current.feelslike_c}°C | ${current.feelslike_f}°F")

        //condition
        val weatherIcon = getWeatherIcon(current.condition.text)
        println("$weatherIcon Weather: ${current.condition.text}")

        //wind
        println("\uD83D\uDCA8 Wind:${current.wind_mph}mile/h ${current.wind_dir}")

        //others
        println("\uD83D\uDCA7 Humidity: ${current.humidity}")

        println("=".repeat(20))

    }
    private fun formatDateTime(dateTime: String): String {
        return try {
            val localDateTime = LocalDateTime.parse(dateTime.replace(" ","T"))
            localDateTime.format(DateTimeFormatter.ofPattern("dd MMM, YYYY - HH:mm"))
        } catch (e: Exception){
            dateTime
        }
    }
    private fun getWeatherIcon(condition: String): String{
        return when{
            condition.contains("sunny", ignoreCase = true) -> "☀️"
            condition.contains("clear", ignoreCase = true) -> "🌙"
            condition.contains("cloudy", ignoreCase = true) -> "☁️"
            condition.contains("overcast", ignoreCase = true) -> "☁️"
            condition.contains("rain", ignoreCase = true) -> "🌧️"
            condition.contains("drizzle", ignoreCase = true) -> "🌦️"
            condition.contains("snow", ignoreCase = true) -> "❄️"
            condition.contains("storm", ignoreCase = true) -> "⛈️"
            condition.contains("thunder", ignoreCase = true) -> "⚡"
            condition.contains("fog", ignoreCase = true) -> "🌫️"
            condition.contains("mist", ignoreCase = true) -> "🌫️"
            else -> "🌤️"
        }
    }
}