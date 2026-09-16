package com.example.vacuno.registro

/** Validaciones puras del formulario; la Activity solo muestra el resultado. */
object RegistroValidator {
    private val nombre = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{3,50}$")
    private val correo = Regex("^[A-Za-z0-9+_.-]+@gmail\\.com$")
    private val telefono = Regex("^[0-9]{7,10}$")
    private val contrasena = Regex("^(?=.*[A-Za-z])(?=.*\\d).{6,}$")
    private val finca = Regex("^[A-Za-z0-9ÁÉÍÓÚáéíóúÑñ #.,-]{3,60}$")

    fun validate(nombreTexto: String, correoTexto: String, telefonoTexto: String, contrasenaTexto: String, fincaTexto: String): String? = when {
        nombreTexto.isEmpty() -> "Ingresa tu nombre completo"
        !nombre.matches(nombreTexto) -> "El nombre solo debe tener letras y mínimo 3 caracteres"
        correoTexto.isEmpty() || !correo.matches(correoTexto) -> "Ingresa un correo válido de Gmail"
        telefonoTexto.isEmpty() || !telefono.matches(telefonoTexto) -> "El teléfono debe tener entre 7 y 10 números"
        contrasenaTexto.isEmpty() || !contrasena.matches(contrasenaTexto) -> "La contraseña debe tener mínimo 6 caracteres, letras y números"
        fincaTexto.isEmpty() || !finca.matches(fincaTexto) -> "Ingresa un nombre de finca válido"
        else -> null
    }
}
