package weather

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val name: String,
    val region: String,
    val country: String,
    val localtime: String
)
@Serializable
data class Current(
    val last_updated: String,
    val temp_c: Double,
    val temp_f: Double,
    val condition: Condition,
    val wind_mph: Double,
    val wind_dir: String,
    val humidity: Int,
    val feelslike_c: Double,
    val feelslike_f: Double

)
@Serializable
data class Condition(
    val text: String,

)
@Serializable
data class WeatherResponse(
    val location: Location,
    val current: Current
)