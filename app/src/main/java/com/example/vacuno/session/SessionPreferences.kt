package com.example.vacuno.session

import android.content.Context

/** Único punto de acceso a las preferencias locales de cuenta y sesión. */
class SessionPreferences(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun isSessionActive(): Boolean = preferences.getBoolean(KEY_SESION_ACTIVA, false)
    fun startSession() = preferences.edit().putBoolean(KEY_SESION_ACTIVA, true).apply()
    fun closeSession() = preferences.edit().putBoolean(KEY_SESION_ACTIVA, false).apply()

    fun saveAccount(nombre: String, correo: String, contrasena: String, finca: String, direccion: String, latitud: String, longitud: String) {
        preferences.edit()
            .putString(KEY_NOMBRE, nombre)
            .putString(KEY_CORREO, correo)
            .putString(KEY_CONTRASENA, contrasena)
            .putString(KEY_FINCA, finca)
            .putString(KEY_DIRECCION, direccion)
            .putString(KEY_LATITUD, latitud)
            .putString(KEY_LONGITUD, longitud)
            .putBoolean(KEY_SESION_ACTIVA, false)
            .apply()
    }

    fun credentialsMatch(correo: String, contrasena: String): Boolean =
        correo == email() && contrasena == preferences.getString(KEY_CONTRASENA, "")

    fun email(): String = preferences.getString(KEY_CORREO, "") ?: ""
    fun nombre(): String = preferences.getString(KEY_NOMBRE, "Usuario") ?: "Usuario"
    fun finca(): String = preferences.getString(KEY_FINCA, "Finca") ?: "Finca"

    private companion object {
        const val PREFERENCES_NAME = "vacuno_preferences"
        const val KEY_SESION_ACTIVA = "sesion_activa"
        const val KEY_NOMBRE = "nombre"
        const val KEY_CORREO = "correo"
        const val KEY_CONTRASENA = "contrasena"
        const val KEY_FINCA = "finca"
        const val KEY_DIRECCION = "direccion"
        const val KEY_LATITUD = "direccion_lat"
        const val KEY_LONGITUD = "direccion_lon"
    }
}
