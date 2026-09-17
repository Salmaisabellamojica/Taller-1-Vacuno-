package com.example.vacuno

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vacuno.model.DireccionResultado
import com.example.vacuno.registro.RegistroValidator
import com.example.vacuno.registro.map.DireccionMapController
import com.example.vacuno.session.SessionPreferences
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import java.io.File

class RegistroActivity : AppCompatActivity() {
    private val handlerBusqueda = Handler(Looper.getMainLooper())
    private lateinit var direccionMapController: DireccionMapController
    private var direccionPendiente: DireccionResultado? = null
    private var direccionGuardada: DireccionResultado? = null
    private var actualizandoTextoDireccion = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configurarOsmdroid()
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { vista, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            vista.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            insets
        }

        val etNombre = findViewById<EditText>(R.id.et_nombre)
        val etCorreo = findViewById<EditText>(R.id.et_correo_registro)
        val etTelefono = findViewById<EditText>(R.id.et_telefono)
        val etContrasena = findViewById<EditText>(R.id.et_contrasena_registro)
        val etNombreFinca = findViewById<EditText>(R.id.et_nombre_finca)
        val etDireccion = findViewById<EditText>(R.id.et_direccion)
        val mapa = findViewById<MapView>(R.id.mapa_direcciones)
        val listaDirecciones = findViewById<ListView>(R.id.lv_direcciones)
        val tvUbicacion = findViewById<TextView>(R.id.tv_ubicacion_seleccionada)
        val rgRol = findViewById<RadioGroup>(R.id.rg_rol)
        val cbTerminos = findViewById<CheckBox>(R.id.cb_terminos)
        val btnRegistrar = findViewById<Button>(R.id.btn_registrar)
        val btnGuardarUbicacion = findViewById<Button>(R.id.btn_guardar_ubicacion)
        val btnLimpiarUbicacion = findViewById<Button>(R.id.btn_limpiar_ubicacion)

        direccionMapController = DireccionMapController(
            activity = this,
            mapa = mapa,
            listaDirecciones = listaDirecciones,
            onUbicacionActualizada = { ubicacion ->
                direccionPendiente = ubicacion
                tvUbicacion.text = formatoUbicacion(ubicacion)
                tvUbicacion.visibility = View.VISIBLE
                actualizandoTextoDireccion = true
                etDireccion.setText(ubicacion.displayName)
                etDireccion.setSelection(etDireccion.text.length)
                actualizandoTextoDireccion = false
            }
        )
        configurarMapaDesplazable(mapa)
        configurarBusqueda(etDireccion, listaDirecciones, tvUbicacion)

        btnGuardarUbicacion.setOnClickListener {
            val ubicacion = direccionPendiente
            if (ubicacion == null) {
                Toast.makeText(this, "Busca una dirección o mantén pulsado el mapa para marcar un punto", Toast.LENGTH_SHORT).show()
            } else {
                direccionGuardada = ubicacion
                Toast.makeText(this, "Ubicación guardada", Toast.LENGTH_SHORT).show()
            }
        }

        btnLimpiarUbicacion.setOnClickListener {
            handlerBusqueda.removeCallbacksAndMessages(null)
            actualizandoTextoDireccion = true
            etDireccion.text.clear()
            actualizandoTextoDireccion = false
            direccionPendiente = null
            direccionGuardada = null
            tvUbicacion.visibility = View.GONE
            direccionMapController.limpiar()
        }

        btnRegistrar.setOnClickListener {
            val mensaje = RegistroValidator.validate(
                etNombre.text.toString().trim(),
                etCorreo.text.toString().trim(),
                etTelefono.text.toString().trim(),
                etContrasena.text.toString().trim(),
                etNombreFinca.text.toString().trim()
            )
            when {
                mensaje != null -> toast(mensaje)
                rgRol.checkedRadioButtonId == -1 -> toast("Selecciona tu rol en la finca")
                !cbTerminos.isChecked -> toast("Debes aceptar los términos")
                direccionGuardada == null -> toast("Selecciona y guarda la ubicación de la finca")
                else -> {
                    SessionPreferences(this).saveAccount(
                        etNombre.text.toString().trim(),
                        etCorreo.text.toString().trim(),
                        etContrasena.text.toString().trim(),
                        etNombreFinca.text.toString().trim(),
                        direccionGuardada!!
                    )
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }

        findViewById<TextView>(R.id.tv_volver_login).setOnClickListener { finish() }
    }

    private fun configurarBusqueda(
        etDireccion: EditText,
        listaDirecciones: ListView,
        tvUbicacion: TextView
    ) {
        etDireccion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                if (actualizandoTextoDireccion) return
                direccionPendiente = null
                direccionGuardada = null
                tvUbicacion.visibility = View.GONE
                val consulta = s?.toString()?.trim().orEmpty()
                if (consulta.length < 5) {
                    handlerBusqueda.removeCallbacksAndMessages(null)
                    direccionMapController.limpiar()
                    return
                }
                handlerBusqueda.removeCallbacksAndMessages(null)
                handlerBusqueda.postDelayed({ direccionMapController.buscarDireccion(consulta) }, 1_300)
            }
        })

        etDireccion.setOnEditorActionListener { _, actionId, _ ->
            if (actionId != EditorInfo.IME_ACTION_SEARCH) return@setOnEditorActionListener false
            val consulta = etDireccion.text.toString().trim()
            if (consulta.length >= 5) {
                handlerBusqueda.removeCallbacksAndMessages(null)
                direccionMapController.buscarDireccion(consulta)
            }
            true
        }

        listaDirecciones.setOnItemClickListener { _, _, posicion, _ ->
            val direccion = listaDirecciones.adapter.getItem(posicion) as DireccionResultado
            actualizandoTextoDireccion = true
            etDireccion.setText(direccion.displayName)
            etDireccion.setSelection(etDireccion.text.length)
            actualizandoTextoDireccion = false
            listaDirecciones.visibility = View.GONE
            direccionMapController.seleccionarSugerencia(direccion)
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(etDireccion.windowToken, 0)
        }
    }

    private fun configurarMapaDesplazable(mapa: MapView) {
        mapa.setTilesScaledToDpi(true)
        mapa.minZoomLevel = 3.0
        mapa.maxZoomLevel = 19.0
        mapa.setOnTouchListener { vista, evento ->
            when (evento.actionMasked) {
                MotionEvent.ACTION_DOWN -> vista.parent.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> vista.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }
    }

    private fun formatoUbicacion(ubicacion: DireccionResultado): String = buildString {
        append("Dirección seleccionada:\n")
        append(ubicacion.displayName)
        append("\n\nLatitud: ${ubicacion.lat}\nLongitud: ${ubicacion.lon}")
    }

    private fun configurarOsmdroid() {
        Configuration.getInstance().userAgentValue = "VacunoApp/1.0 (soporte@vacunoapp.local)"
        Configuration.getInstance().osmdroidBasePath = File(applicationContext.cacheDir, "osmdroid")
        Configuration.getInstance().osmdroidTileCache = File(Configuration.getInstance().osmdroidBasePath, "tiles")
    }

    private fun toast(mensaje: String) = Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()

    override fun onResume() {
        super.onResume()
        direccionMapController.onResume()
    }

    override fun onPause() {
        direccionMapController.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        handlerBusqueda.removeCallbacksAndMessages(null)
        direccionMapController.onDestroy()
        super.onDestroy()
    }
}
