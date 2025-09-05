package weather

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val name: String,
    val region: String,
    val country: String,
    val localtime: String
)
data class Current(
    val last_updated: String,
    val temp_c: Double,
    val temp_f: Double,
    val condition: Condition,
    val wind_mph: Double,
    val humidity: Int,
    val feelslike_c: Double,
    val feelslike_f: Double

)
data class Condition(
    val text: String,
    val icon: String,

)
data class WeatherResponse(
    val location: Location,
    val current: Current
)