package com.example.vacuno.network

import com.example.vacuno.model.DireccionResultado
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object NominatimService {

    fun buscarDireccion(query: String): List<DireccionResultado> {
        val url = "https://nominatim.openstreetmap.org/search?format=jsonv2&limit=6&accept-language=es&q=" +
                URLEncoder.encode(query, "UTF-8")
        var resultados = listOf<DireccionResultado>()
        try {
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 10000
            conn.readTimeout = 10000
            conn.setRequestProperty("User-Agent", "VacunoApp/1.0 (gestion finca ganadera)")
            conn.setRequestProperty("Accept", "application/json")
            if (conn.responseCode == 200) {
                val texto = conn.inputStream.bufferedReader().use { it.readText() }
                resultados = parseResultados(texto)
            }
            conn.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return resultados
    }

    private fun parseResultados(json: String): List<DireccionResultado> {
        val lista = mutableListOf<DireccionResultado>()
        try {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                lista.add(
                    DireccionResultado(
                        displayName = obj.optString("display_name"),
                        lat = obj.optString("lat"),
                        lon = obj.optString("lon")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return lista
    }
}
