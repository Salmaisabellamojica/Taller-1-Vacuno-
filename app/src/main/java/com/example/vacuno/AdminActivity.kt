package com.example.vacuno

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button
import android.view.View
import androidx.core.widget.NestedScrollView
import com.example.vacuno.session.SessionPreferences
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

        val sessionPreferences = SessionPreferences(this)
        val nombre = sessionPreferences.nombre()
        val finca = sessionPreferences.finca()

        findViewById<TextView>(R.id.tv_saludo).text = "Hola $nombre"
        findViewById<TextView>(R.id.tv_ubicacion).text = "$finca"
        val btnCerrar = findViewById<Button>(R.id.btn_cerrar_sesio)
        configurarBarrasDinamicas()

        btnCerrar.setOnClickListener {
            sessionPreferences.closeSession()
            val intentLogin = Intent(this, MainActivity::class.java)

            startActivity(intentLogin)
            finish()
        }

    }

    /** Efecto visual tipo frosted glass limitado al encabezado y la navegación. */
    private fun configurarBarrasDinamicas() {
        val header = findViewById<View>(R.id.ll_header)
        val navegacion = findViewById<View>(R.id.ll_menu_inferior)
        findViewById<NestedScrollView>(R.id.scroll_panel_admin).setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val progreso = (scrollY / 160f).coerceIn(0f, 1f)
            header.alpha = 0.94f + (progreso * 0.06f)
            header.translationZ = 2f + (progreso * 14f)
            navegacion.translationZ = 6f + (progreso * 10f)
            navegacion.alpha = 0.96f + (progreso * 0.04f)
        }

        val abrirGanado = { startActivity(Intent(this, GanadoActivity::class.java)) }
        findViewById<Button>(R.id.btn_ver_ganado).setOnClickListener { abrirGanado() }
        findViewById<Button>(R.id.btn_ganado).setOnClickListener { abrirGanado() }
        findViewById<Button>(R.id.btn_menu_ganado).setOnClickListener { abrirGanado() }

        val abrirInventario = { startActivity(Intent(this, InventarioActivity::class.java)) }
        findViewById<Button>(R.id.btn_inventario).setOnClickListener { abrirInventario() }
        findViewById<Button>(R.id.btn_menu_inventario).setOnClickListener { abrirInventario() }
    }
}
