package com.example.vacuno.model

/** Artículo del inventario de la finca. */
data class Inventario(
    val id: String,
    val nombre: String,
    val categoria: CategoriaInventario,
    val cantidad: Int,
    val unidad: String,
    val stockMinimo: Int
) {
    val requiereReposicion: Boolean
        get() = cantidad <= stockMinimo
}
