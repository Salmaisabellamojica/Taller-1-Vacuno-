package com.example.vacuno.data.repository

import com.example.vacuno.data.remote.OpenMeteoGeocodingApi
import com.example.vacuno.model.DireccionResultado

/** Convierte DTOs de red en el modelo que usa la interfaz. */
class DireccionRepository(private val api: OpenMeteoGeocodingApi = OpenMeteoGeocodingApi()) {
    fun buscar(query: String): Result<List<DireccionResultado>> = api.search(query).map { response ->
        response.results.map { dto ->
            val detalle = listOfNotNull(dto.admin1, dto.country).joinToString(", ")
            DireccionResultado(
                displayName = listOf(dto.name, detalle).filter { it.isNotBlank() }.joinToString(", "),
                lat = dto.latitude.toString(),
                lon = dto.longitude.toString()
            )
        }
    }
}
