package com.example.vacuno.model

/** Modelo inmutable que representa la información que se muestra en el inventario. */
data class Animal(
    val id: String,
    val nombre: String,
    val raza: String,
    val sexo: Sexo,
    val edad: String,
    val pesoKg: Int,
    val estado: EstadoSalud
)

enum class Sexo(val etiqueta: String) { HEMBRA("Hembra"), MACHO("Macho"), JOVEN("Joven") }

enum class EstadoSalud(val etiqueta: String) { SALUDABLE("Saludable"), ATENCION("Atención") }
