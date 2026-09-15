package com.example.vacuno.model

/** Datos de un animal. La edad se calcula fuera del modelo con EdadAnimalCalculator. */
data class Animal(
    val id: String,
    val nombre: String,
    val raza: String,
    val sexo: Sexo,
    /** Formato: dd/MM/yyyy. Ejemplo: 15/08/2024. */
    val fechaNacimiento: String,
    val pesoKg: Int,
    val estado: EstadoSalud
)
