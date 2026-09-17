package com.example.vacuno.data.remote.dto

/** Respuesta necesaria de Nominatim, sin mezclarla con la interfaz. */
data class NominatimDireccionDto(
    val displayName: String,
    val latitud: String,
    val longitud: String,
    val ciudad: String?,
    val departamento: String?,
    val pais: String?,
    val codigoPostal: String?
)
