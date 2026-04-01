package com.example.weatherappars


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// 1. Модель данных
data class WeatherResponse(
    val current_weather: CurrentWeather
)
data class CurrentWeather(
    val temperature: Double,
    val windspeed: Double
)

// 2. API Интерфейс (Open-Meteo)
interface WeatherApi {
    @GET("v1/forecast?current_weather=true")
    suspend fun getWeather(
        @Query("latitude") lat: Double = 55.75, // Москва по умолчанию
        @Query("longitude") lon: Double = 37.61
    ): WeatherResponse
}

// 3. Ретрофит клиент
object RetrofitClient {
    val api: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                WeatherScreen()
            }
        }
    }
}

@Composable
fun WeatherScreen() {
    val scope = rememberCoroutineScope()
    var temperature by remember { mutableStateOf("--") }
    var windSpeed by remember { mutableStateOf("--") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Погода в Москве", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(text = "$temperature°C", fontSize = 48.sp)
            Text(text = "Ветер: $windSpeed км/ч", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            scope.launch {
                isLoading = true
                try {
                    val response = RetrofitClient.api.getWeather()
                    temperature = response.current_weather.temperature.toString()
                    windSpeed = response.current_weather.windspeed.toString()
                } catch (e: Exception) {
                    temperature = "Ошибка"
                } finally {
                    isLoading = false
                }
            }
        }) {
            Text("Обновить прогноз")
        }
    }
}