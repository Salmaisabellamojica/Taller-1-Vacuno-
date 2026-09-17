package com.example.vacuno.data.remote

import com.example.vacuno.data.remote.dto.NominatimDireccionDto
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/** Cliente de Nominatim/OpenStreetMap para geocodificación directa e inversa. */
class NominatimGeocodingApi {
    fun buscarDireccion(consulta: String): Result<List<NominatimDireccionDto>> = runCatching {
        val query = URLEncoder.encode(consulta, Charsets.UTF_8.name())
        solicitarLista(
            "https://nominatim.openstreetmap.org/search?format=jsonv2&addressdetails=1&limit=6&accept-language=es&q=$query"
        )
    }

    fun buscarDireccionInversa(latitud: String, longitud: String): Result<NominatimDireccionDto> = runCatching {
        val url = "https://nominatim.openstreetmap.org/reverse?format=jsonv2&addressdetails=1&zoom=18&lat=$latitud&lon=$longitud&accept-language=es"
        parseDireccion(JSONObject(solicitar(url)))
    }

    private fun solicitarLista(url: String): List<NominatimDireccionDto> {
        val arreglo = JSONArray(solicitar(url))
        return List(arreglo.length()) { indice -> parseDireccion(arreglo.getJSONObject(indice)) }
    }

    private fun solicitar(url: String): String {
        esperarTurno()
        val conexion = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 12_000
            readTimeout = 12_000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "VacunoApp/1.0 (contacto: soporte@vacunoapp.local)")
        }
        return try {
            if (conexion.responseCode !in 200..299) {
                throw IllegalStateException("Nominatim respondió HTTP ${conexion.responseCode}")
            }
            conexion.inputStream.bufferedReader().use { it.readText() }
        } finally {
            conexion.disconnect()
        }
    }

    private fun parseDireccion(item: JSONObject): NominatimDireccionDto {
        val direccion = item.optJSONObject("address") ?: JSONObject()
        fun campo(vararg nombres: String): String? = nombres
            .asSequence()
            .map { direccion.optString(it).trim() }
            .firstOrNull { it.isNotBlank() }

        return NominatimDireccionDto(
            displayName = item.optString("display_name").trim(),
            latitud = item.getString("lat"),
            longitud = item.getString("lon"),
            ciudad = campo("city", "town", "village", "municipality", "county"),
            departamento = campo("state", "state_district", "region"),
            pais = campo("country"),
            codigoPostal = campo("postcode")
        )
    }

    private fun esperarTurno() {
        synchronized(controlSolicitudes) {
            val espera = INTERVALO_MINIMO_MS - (System.currentTimeMillis() - ultimaSolicitudMs)
            if (espera > 0) Thread.sleep(espera)
            ultimaSolicitudMs = System.currentTimeMillis()
        }
    }

    private companion object {
        const val INTERVALO_MINIMO_MS = 1_100L
        val controlSolicitudes = Any()
        var ultimaSolicitudMs = 0L
    }
}
