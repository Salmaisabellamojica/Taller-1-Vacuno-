package com.example.vacuno.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Reglas para calcular la edad a partir de una fecha dd/MM/yyyy. */
object EdadAnimalCalculator {
    private const val FORMATO_FECHA = "dd/MM/yyyy"

    fun edadTexto(fechaNacimiento: String): String {
        val anios = edadEnAnios(fechaNacimiento) ?: return "Sin fecha"
        return if (anios < 1) "Menor de 1 año" else "$anios años"
    }

    fun esMenorDeDosAnios(fechaNacimiento: String): Boolean {
        val nacimiento = fechaValida(fechaNacimiento) ?: return false
        val limite = Calendar.getInstance().apply { add(Calendar.YEAR, -2) }.time
        return nacimiento.after(limite)
    }

    private fun edadEnAnios(fechaNacimiento: String): Int? {
        val nacimiento = fechaValida(fechaNacimiento) ?: return null
        val hoy = Calendar.getInstance()
        val fecha = Calendar.getInstance().apply { time = nacimiento }
        var anios = hoy.get(Calendar.YEAR) - fecha.get(Calendar.YEAR)
        if (hoy.get(Calendar.DAY_OF_YEAR) < fecha.get(Calendar.DAY_OF_YEAR)) anios--
        return anios.coerceAtLeast(0)
    }

    private fun fechaValida(texto: String): Date? = runCatching {
        SimpleDateFormat(FORMATO_FECHA, Locale.getDefault()).apply { isLenient = false }.parse(texto)
    }.getOrNull()
}
