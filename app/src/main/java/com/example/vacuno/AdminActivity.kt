package com.example.vacuno

import android.os.Bundle
import android.content.Context
import android.content.SharedPreferences
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button
class AdminActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences
    private val prefsName = "vacuno_preferences"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)
        preferences = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nombre = preferences.getString("nombre", "Usuario") ?: "Usuario"
        val finca = preferences.getString("finca", "Finca") ?: "Finca"

        findViewById<TextView>(R.id.tv_saludo).text = "Hola $nombre"
        findViewById<TextView>(R.id.tv_ubicacion).text = "$finca"
        val btnCerrar = findViewById<Button>(R.id.btn_cerrar_sesio)

        btnCerrar.setOnClickListener {
            preferences.edit().putBoolean("sesion_activa", false).apply()
            val intentLogin = Intent(this, MainActivity::class.java)

            startActivity(intentLogin)
            finish()
        }

        val abrirGanado = {
            startActivity(Intent(this, GanadoActivity::class.java))
        }
        findViewById<Button>(R.id.btn_ver_ganado).setOnClickListener { abrirGanado() }
        findViewById<Button>(R.id.btn_ganado).setOnClickListener { abrirGanado() }
        findViewById<Button>(R.id.btn_menu_ganado).setOnClickListener { abrirGanado() }

    }
}
