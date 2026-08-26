package com.example.vacuno

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button
class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nombre = intent.getStringExtra("nombre")?: "Usuario"
        val finca = intent.getStringExtra("nombre_finca")?: "Finca"

        findViewById<TextView>(R.id.tv_saludo).text = "Hola $nombre"
        findViewById<TextView>(R.id.tv_ubicacion).text = "$finca"
        val btnCerrar = findViewById<Button>(R.id.btn_cerrar_sesio)

        btnCerrar.setOnClickListener {
            val intentLogin = Intent(this, MainActivity::class.java)

            startActivity(intentLogin)
            finish()
        }

    }
}
