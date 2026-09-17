package com.example.vacuno.model

data class DireccionResultado(
    val displayName: String,
    val lat: String,
    val lon: String,
    val ciudad: String? = null,
    val departamento: String? = null,
    val pais: String? = null,
    val codigoPostal: String? = null
)
