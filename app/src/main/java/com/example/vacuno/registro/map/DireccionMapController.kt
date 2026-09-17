package com.example.vacuno.registro.map

import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vacuno.data.repository.DireccionRepository
import com.example.vacuno.model.DireccionResultado
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

/** Búsqueda, selección visual y geocodificación inversa de una ubicación. */
class DireccionMapController(
    private val activity: AppCompatActivity,
    private val mapa: MapView,
    private val listaDirecciones: ListView,
    private val onUbicacionActualizada: (DireccionResultado) -> Unit,
    private val repository: DireccionRepository = DireccionRepository()
) {
    private val eventosMapa = MapEventsOverlay(object : MapEventsReceiver {
        override fun singleTapConfirmedHelper(punto: GeoPoint) = false

        override fun longPressHelper(punto: GeoPoint): Boolean {
            moverMarcadorYBuscarDireccion(punto)
            return true
        }
    })

    init {
        mapa.setMultiTouchControls(true)
        mapa.overlays.add(eventosMapa)
    }

    fun buscarDireccion(consulta: String) {
        Thread {
            val resultado = repository.buscar(consulta)
            activity.runOnUiThread {
                resultado.onFailure {
                    Toast.makeText(activity, "No fue posible buscar la dirección. Revisa tu conexión.", Toast.LENGTH_SHORT).show()
                }
                mostrarSugerencias(resultado.getOrDefault(emptyList()))
            }
        }.start()
    }

    fun seleccionarSugerencia(direccion: DireccionResultado) {
        colocarMarcador(direccion)
        onUbicacionActualizada(direccion)
    }

    fun limpiar() {
        listaDirecciones.visibility = View.GONE
        listaDirecciones.adapter = null
        limpiarMarcadores()
        mapa.visibility = View.GONE
    }

    fun onResume() = mapa.onResume()
    fun onPause() = mapa.onPause()
    fun onDestroy() = mapa.onDetach()

    private fun mostrarSugerencias(resultados: List<DireccionResultado>) {
        if (resultados.isEmpty()) {
            limpiar()
            Toast.makeText(activity, "No encontramos esa dirección completa", Toast.LENGTH_SHORT).show()
            return
        }
        listaDirecciones.adapter = object : ArrayAdapter<DireccionResultado>(
            activity, android.R.layout.simple_list_item_1, resultados
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
                super.getView(position, convertView, parent).also { vista ->
                    vista.findViewById<TextView>(android.R.id.text1).text = getItem(position)?.displayName
                }
        }
        listaDirecciones.visibility = View.VISIBLE
        mostrarResultadosEnMapa(resultados)
    }

    private fun mostrarResultadosEnMapa(resultados: List<DireccionResultado>) {
        limpiarMarcadores()
        val primero = resultados.first()
        mapa.controller.setZoom(17.0)
        mapa.controller.setCenter(GeoPoint(primero.lat.toDouble(), primero.lon.toDouble()))
        resultados.forEach { mapa.overlays.add(crearMarcador(it)) }
        mapa.visibility = View.VISIBLE
    }

    private fun moverMarcadorYBuscarDireccion(punto: GeoPoint) {
        limpiarMarcadores()
        mapa.overlays.add(crearMarcadorTemporal(punto))
        mapa.visibility = View.VISIBLE
        buscarDireccionInversa(punto)
    }

    private fun buscarDireccionInversa(punto: GeoPoint) {
        Thread {
            val resultado = repository.buscarInversa(punto.latitude.toString(), punto.longitude.toString())
            activity.runOnUiThread {
                resultado.onSuccess { direccion ->
                    colocarMarcador(direccion)
                    onUbicacionActualizada(direccion)
                }.onFailure {
                    Toast.makeText(activity, "No se encontró una dirección para este punto", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun colocarMarcador(direccion: DireccionResultado) {
        limpiarMarcadores()
        mapa.overlays.add(crearMarcador(direccion))
        mapa.controller.setZoom(17.0)
        mapa.controller.setCenter(GeoPoint(direccion.lat.toDouble(), direccion.lon.toDouble()))
        mapa.visibility = View.VISIBLE
        mapa.invalidate()
    }

    private fun limpiarMarcadores() {
        mapa.overlays.clear()
        mapa.overlays.add(eventosMapa)
    }

    private fun crearMarcador(direccion: DireccionResultado) = Marker(mapa).apply {
        position = GeoPoint(direccion.lat.toDouble(), direccion.lon.toDouble())
        title = direccion.displayName
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        isDraggable = true
        setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) = Unit
            override fun onMarkerDrag(marker: Marker) = Unit
            override fun onMarkerDragEnd(marker: Marker) {
                moverMarcadorYBuscarDireccion(marker.position)
            }
        })
    }

    private fun crearMarcadorTemporal(punto: GeoPoint) = Marker(mapa).apply {
        position = punto
        title = "Buscando dirección…"
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        isDraggable = true
    }
}
