package weather

import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val weatherService = WeatherService()
    val weatherDisplay = WeatherDisplay()

    print("Enter city name: ")
    val location = (readlnOrNull() ?: "London").lowercase()
    println("🔍 Fetching weather data for: $location...")

    val weather = weatherService.getWeatherData(location)
    if (weather!=null){
        weatherDisplay.displayWeather(weather)
    }
    else {
        println("❌ Could not fetch weather data. Please check your location and API key.")
    }
}