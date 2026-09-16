package com.example.vacuno

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vacuno.adapter.AnimalAdapter
import com.example.vacuno.model.Animal
import com.example.vacuno.model.EstadoSalud
import com.example.vacuno.model.Sexo
import com.example.vacuno.util.EdadAnimalCalculator
import com.example.vacuno.session.SessionPreferences
import com.google.android.material.button.MaterialButton

class GanadoActivity : AppCompatActivity() {
    private val animales = listOf(
        Animal("0248", "Luna", "Gyr", Sexo.HEMBRA, "10/03/2022", 425, EstadoSalud.SALUDABLE),
        Animal("0187", "Mora", "Brahman", Sexo.HEMBRA, "20/01/2020", 510, EstadoSalud.ATENCION),
        Animal("0312", "Sol", "Angus", Sexo.MACHO, "08/09/2023", 390, EstadoSalud.SALUDABLE),
        Animal("0416", "Canela", "Normando", Sexo.HEMBRA, "15/08/2025", 218, EstadoSalud.SALUDABLE)
    )
    private val adapter = AnimalAdapter()
    private var filtroActual: Sexo? = null
    private var filtrarJovenes = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ganado)

        findViewById<View>(R.id.btn_volver).setOnClickListener { finish() }
        setupRecyclerView()
        findViewById<EditText>(R.id.et_buscar).addTextChangedListener { actualizarLista() }

        configurarFiltros()
        configurarMenuInferior()
        actualizarLista()
    }

    private fun configurarMenuInferior() {
        findViewById<Button>(R.id.btn_inicio).setOnClickListener {
            startActivity(Intent(this, AdminActivity::class.java))
            finish()
        }
        findViewById<Button>(R.id.btn_menu_ganado).setOnClickListener { }
        findViewById<Button>(R.id.btn_menu_inventario).setOnClickListener {
            startActivity(Intent(this, InventarioActivity::class.java))
            finish()
        }
        findViewById<Button>(R.id.btn_menu_actividad).setOnClickListener {
            Toast.makeText(this, "Actividad próximamente", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.btn_cerrar_sesio).setOnClickListener {
            SessionPreferences(this).closeSession()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    /** Configura cómo se dibuja y recicla la lista de animales. */
    private fun setupRecyclerView() {
        findViewById<RecyclerView>(R.id.rv_animales).apply {
            layoutManager = LinearLayoutManager(this@GanadoActivity)
            adapter = this@GanadoActivity.adapter
            setHasFixedSize(true)
        }
    }

    private fun configurarFiltros() {
        val filtros = listOf(
            R.id.btn_todos to null,
            R.id.btn_hembras to Sexo.HEMBRA,
            R.id.btn_machos to Sexo.MACHO,
            R.id.btn_jovenes to null
        )
        filtros.forEach { (id, sexo) ->
            findViewById<MaterialButton>(id).setOnClickListener {
                filtroActual = sexo
                filtrarJovenes = id == R.id.btn_jovenes
                filtros.forEach { (buttonId, _) -> findViewById<MaterialButton>(buttonId).isChecked = buttonId == id }
                actualizarLista()
            }
        }
    }

    private fun actualizarLista() {
        val consulta = findViewById<EditText>(R.id.et_buscar).text.toString().trim()
        val resultado = animales.filter { animal ->
            (filtroActual == null || animal.sexo == filtroActual) &&
                (!filtrarJovenes || EdadAnimalCalculator.esMenorDeDosAnios(animal.fechaNacimiento)) &&
                (consulta.isBlank() || listOf(animal.id, animal.nombre, animal.raza)
                    .any { it.contains(consulta, ignoreCase = true) })
        }
        adapter.submitList(resultado)
        findViewById<TextView>(R.id.tv_sin_resultados).visibility =
            if (resultado.isEmpty()) View.VISIBLE else View.GONE
    }
}
