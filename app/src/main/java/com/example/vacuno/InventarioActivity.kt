package com.example.vacuno

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vacuno.adapter.InventarioAdapter
import com.example.vacuno.model.CategoriaInventario
import com.example.vacuno.model.Inventario
import com.google.android.material.button.MaterialButton

class InventarioActivity : AppCompatActivity() {
    private val articulos = listOf(
        Inventario("INV-01", "Vacuna aftosa", CategoriaInventario.VACUNAS, 8, "dosis", 10),
        Inventario("INV-02", "Sal mineralizada", CategoriaInventario.SALES_Y_SUPLEMENTOS, 25, "kg", 10),
        Inventario("INV-03", "Ivermectina", CategoriaInventario.DESPARASITANTES, 5, "dosis", 8),
        Inventario("INV-04", "Melaza", CategoriaInventario.ALIMENTACION, 30, "litros", 15),
        Inventario("INV-05", "Jeringas", CategoriaInventario.HERRAMIENTAS, 40, "unidades", 20),
        Inventario("INV-06", "Desinfectante", CategoriaInventario.HIGIENE, 4, "litros", 6)
    )
    private val adapter = InventarioAdapter()
    private var categoriaActual: CategoriaInventario? = null
    private var mostrarOtros = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inventario)

        findViewById<View>(R.id.btn_volver_inventario).setOnClickListener { finish() }
        setupRecyclerView()
        findViewById<EditText>(R.id.et_buscar_inventario).addTextChangedListener { actualizarLista() }
        configurarFiltros()
        actualizarLista()
    }

    private fun configurarFiltros() {
        val filtros = listOf(
            R.id.btn_inventario_todos to null,
            R.id.btn_vacunas to CategoriaInventario.VACUNAS,
            R.id.btn_desparasitantes to CategoriaInventario.DESPARASITANTES,
            R.id.btn_sales to CategoriaInventario.SALES_Y_SUPLEMENTOS,
            R.id.btn_alimentos to CategoriaInventario.ALIMENTACION,
            R.id.btn_otros to null
        )
        filtros.forEach { (id, categoria) ->
            findViewById<MaterialButton>(id).setOnClickListener {
                categoriaActual = categoria
                mostrarOtros = id == R.id.btn_otros
                filtros.forEach { (buttonId, _) ->
                    findViewById<MaterialButton>(buttonId).isChecked = buttonId == id
                }
                actualizarLista()
            }
        }
    }

    private fun setupRecyclerView() {
        findViewById<RecyclerView>(R.id.rv_inventario).apply {
            layoutManager = LinearLayoutManager(this@InventarioActivity)
            adapter = this@InventarioActivity.adapter
            setHasFixedSize(true)
        }
    }

    private fun actualizarLista() {
        val consulta = findViewById<EditText>(R.id.et_buscar_inventario).text.toString().trim()
        val resultado = articulos.filter { item ->
            val coincideCategoria = categoriaActual == null || item.categoria == categoriaActual
            val esOtro = item.categoria in setOf(
                CategoriaInventario.MEDICAMENTOS,
                CategoriaInventario.HERRAMIENTAS,
                CategoriaInventario.HIGIENE
            )
            val coincideBusqueda = consulta.isBlank() || listOf(item.nombre, item.categoria.etiqueta, item.id)
                .any { it.contains(consulta, ignoreCase = true) }
            (coincideCategoria && (!mostrarOtros || esOtro)) && coincideBusqueda
        }
        adapter.submitList(resultado)
        findViewById<TextView>(R.id.tv_inventario_vacio).visibility =
            if (resultado.isEmpty()) View.VISIBLE else View.GONE
    }
}
