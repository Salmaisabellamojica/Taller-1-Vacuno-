package com.example.vacuno.data.repository

import com.example.vacuno.data.remote.NominatimGeocodingApi
import com.example.vacuno.model.DireccionResultado

/** Convierte la respuesta de Nominatim a un modelo utilizable por la interfaz. */
class DireccionRepository(private val api: NominatimGeocodingApi = NominatimGeocodingApi()) {
    fun buscar(consulta: String): Result<List<DireccionResultado>> =
        api.buscarDireccion(consulta).map { resultados -> resultados.map(::aModelo) }

    fun buscarInversa(latitud: String, longitud: String): Result<DireccionResultado> =
        api.buscarDireccionInversa(latitud, longitud).map(::aModelo)

    private fun aModelo(dto: com.example.vacuno.data.remote.dto.NominatimDireccionDto) = DireccionResultado(
        displayName = dto.displayName,
        lat = dto.latitud,
        lon = dto.longitud,
        ciudad = dto.ciudad,
        departamento = dto.departamento,
        pais = dto.pais,
        codigoPostal = dto.codigoPostal
    )
}
