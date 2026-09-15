package com.example.vacuno.model

/** Categorías permitidas para los insumos de una finca ganadera. */
enum class CategoriaInventario(val etiqueta: String) {
    VACUNAS("Vacunas"),
    DESPARASITANTES("Desparasitantes"),
    MEDICAMENTOS("Medicamentos"),
    SALES_Y_SUPLEMENTOS("Sales y suplementos"),
    ALIMENTACION("Alimentación"),
    HERRAMIENTAS("Herramientas"),
    HIGIENE("Higiene y desinfección")
}
