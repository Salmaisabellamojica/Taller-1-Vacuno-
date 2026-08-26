package com.example.vacuno

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etCorreo = findViewById<EditText>(R.id.et_correo)
        val etContrasena = findViewById<EditText>(R.id.et_contrasena)
        val btnIngresar = findViewById<Button>(R.id.btn_ingresar)
        val tvCrearCuenta = findViewById<TextView>(R.id.tv_crear_cuenta)
        val tvOlvidasteContrasena = findViewById<TextView>(R.id.tv_olvidaste_contrasena)
        val regexCorreo = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        val regexContrasena = Regex("^(?=.*[A-Za-z])(?=.*\\d).{6,}$")

        val datosRegistro = intent.extras
        val correoRegistrado = datosRegistro?.getString("correo")
        val contrasenaRegistrada = datosRegistro?.getString("contrasena")
        val nombre = datosRegistro?.getString("nombre")
        val finca = datosRegistro?.getString("nombre_finca")

        if (!correoRegistrado.isNullOrBlank()) {
            etCorreo.setText(correoRegistrado)
            Toast.makeText(this, "Cuenta creada. Ahora inicia sesion.", Toast.LENGTH_SHORT).show()
        }


        btnIngresar.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            when {
                correo.isEmpty() -> Toast.makeText(this, "Ingresa tu correo electronico", Toast.LENGTH_SHORT).show()
                !regexCorreo.matches(correo) -> Toast.makeText(this, "Ingresa un correo valido", Toast.LENGTH_SHORT).show()
                contrasena.isEmpty() -> Toast.makeText(this, "Ingresa tu contrasena", Toast.LENGTH_SHORT).show()
                !regexContrasena.matches(contrasena) -> Toast.makeText(this, "La contrasena debe tener letras, numeros y minimo 6 caracteres", Toast.LENGTH_SHORT).show()
                correo == correoRegistrado && contrasena == contrasenaRegistrada -> {
                    val intentAdmin = Intent(this, AdminActivity::class.java).apply {
                        putExtra("nombre", nombre)
                        putExtra("nombre_finca", finca)
                    }

                    startActivity(intentAdmin)
                    finish()
                }
                else -> mostrarAlertaNoRegistrado()
            }
        }

        tvCrearCuenta.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }

        tvOlvidasteContrasena.setOnClickListener {
            Toast.makeText(this, "Recuperacion de contrasena pendiente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarAlertaNoRegistrado() {
        AlertDialog.Builder(this)
            .setTitle("Usuario no registrado")
            .setMessage("El correo o la contrasena no coinciden con una cuenta registrada.")
            .setPositiveButton("Aceptar", null)
            .show()
    }
}
