package com.example.vacuno.data.remote

import com.example.vacuno.data.remote.dto.GeocodingResponseDto
import com.example.vacuno.data.remote.dto.GeocodingResultDto
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/** Cliente HTTP de Open-Meteo Geocoding: API pública, HTTPS y sin clave. */
class OpenMeteoGeocodingApi {
    fun search(query: String): Result<GeocodingResponseDto> = runCatching {
        val encodedQuery = URLEncoder.encode(query, Charsets.UTF_8.name())
        val url = URL("https://geocoding-api.open-meteo.com/v1/search?name=$encodedQuery&count=6&language=es&format=json")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 12_000
            readTimeout = 12_000
            setRequestProperty("Accept", "application/json")
        }
        val responseCode = connection.responseCode
        if (responseCode !in 200..299) {
            throw IllegalStateException("La API respondió HTTP $responseCode")
        }
        val body = connection.inputStream.bufferedReader().use { it.readText() }
        connection.disconnect()
        parse(body)
    }

    private fun parse(json: String): GeocodingResponseDto {
        val response = JSONObject(json)
        val results = response.optJSONArray("results") ?: return GeocodingResponseDto(emptyList())
        return GeocodingResponseDto(
            List(results.length()) { index ->
                val item = results.getJSONObject(index)
                GeocodingResultDto(
                    name = item.optString("name"),
                    admin1 = item.optString("admin1").ifBlank { null },
                    country = item.optString("country").ifBlank { null },
                    latitude = item.getDouble("latitude"),
                    longitude = item.getDouble("longitude")
                )
            }
        )
    }
}
