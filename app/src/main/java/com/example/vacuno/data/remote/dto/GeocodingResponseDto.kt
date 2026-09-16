package com.example.vacuno.data.remote.dto

/** DTO: estructura exacta recibida desde la API de geocodificación. */
data class GeocodingResponseDto(val results: List<GeocodingResultDto>)

data class GeocodingResultDto(
    val name: String,
    val admin1: String?,
    val country: String?,
    val latitude: Double,
    val longitude: Double
)
