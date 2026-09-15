package com.example.vacuno

import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etNombre = findViewById<EditText>(R.id.et_nombre)
        val etCorreo = findViewById<EditText>(R.id.et_correo_registro)
        val etTelefono = findViewById<EditText>(R.id.et_telefono)
        val etContrasena = findViewById<EditText>(R.id.et_contrasena_registro)
        val etNombreFinca = findViewById<EditText>(R.id.et_nombre_finca)
        val rgRol = findViewById<RadioGroup>(R.id.rg_rol)
        val switchInformacion = findViewById<Switch>(R.id.switch_informacion)
        val cbTerminos = findViewById<CheckBox>(R.id.cb_terminos)
        val btnRegistrar = findViewById<Button>(R.id.btn_registrar)
        val tvVolverLogin = findViewById<TextView>(R.id.tv_volver_login)
        //regex
        val regexNombre = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{3,50}$")
        val regexCorreo = Regex("^[A-Za-z0-9+_.-]+@gmail\\.com\$")
        val regexTelefono = Regex("^[0-9]{7,10}$")
        val regexContrasena = Regex("^(?=.*[A-Za-z])(?=.*\\d).{6,}$")
        val regexTextoFinca = Regex("^[A-Za-z0-9ÁÉÍÓÚáéíóúÑñ #.,-]{3,60}$")


        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()
            val nombreFinca = etNombreFinca.text.toString().trim()

            val idRolSeleccionado = rgRol.checkedRadioButtonId

            //Validaciones
            when {
                nombre.isEmpty() -> Toast.makeText(this, "Ingresa tu nombre completo", Toast.LENGTH_SHORT).show()
                !regexNombre.matches(nombre) -> Toast.makeText(this, "El nombre solo debe tener letras y minimo 3 caracteres", Toast.LENGTH_SHORT).show()
                correo.isEmpty() -> Toast.makeText(this, "Ingresa tu correo electronico", Toast.LENGTH_SHORT).show()
                !regexCorreo.matches(correo) -> Toast.makeText(this, "Ingresa un correo valido", Toast.LENGTH_SHORT).show()
                telefono.isEmpty() -> Toast.makeText(this, "Ingresa tu telefono", Toast.LENGTH_SHORT).show()
                !regexTelefono.matches(telefono) -> Toast.makeText(this, "El telefono debe tener entre 7 y 10 numeros", Toast.LENGTH_SHORT).show()
                contrasena.isEmpty() -> Toast.makeText(this, "Ingresa una contrasena", Toast.LENGTH_SHORT).show()
                !regexContrasena.matches(contrasena) -> Toast.makeText(this, "La contrasena debe tener minimo 6 caracteres, letras y numeros", Toast.LENGTH_SHORT).show()
                nombreFinca.isEmpty() -> Toast.makeText(this, "Ingresa el nombre de la finca", Toast.LENGTH_SHORT).show()
                !regexTextoFinca.matches(nombreFinca) -> Toast.makeText(this, "Ingresa un nombre de finca valido", Toast.LENGTH_SHORT).show()

                idRolSeleccionado == -1 -> Toast.makeText(this, "Selecciona tu rol en la finca", Toast.LENGTH_SHORT).show()
                !cbTerminos.isChecked -> Toast.makeText(this, "Debes aceptar los terminos", Toast.LENGTH_SHORT).show()
                else -> {
                    val rol = findViewById<RadioButton>(idRolSeleccionado).text.toString()
                    getSharedPreferences("vacuno_preferences", Context.MODE_PRIVATE)
                        .edit()
                        .putString("nombre", nombre)
                        .putString("correo", correo)
                        .putString("contrasena", contrasena)
                        .putString("finca", nombreFinca)
                        .putBoolean("sesion_activa", false)
                        .apply()
                    val intentLogin = Intent(this, MainActivity::class.java)
                    startActivity(intentLogin)
                    finish()
                }
            }
        }

        tvVolverLogin.setOnClickListener {
            finish()
        }
    }
}
