package com.example.vacuno

import com.example.vacuno.model.DireccionResultado
import com.example.vacuno.session.SessionPreferences
import com.example.vacuno.registro.RegistroValidator
import com.example.vacuno.registro.map.DireccionMapController

import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import java.io.File


class RegistroActivity : AppCompatActivity() {
    private val handlerBusqueda = Handler(Looper.getMainLooper())
    private lateinit var direccionMapController: DireccionMapController
    private var direccionSeleccionada: DireccionResultado? = null
    private var direccionPendiente: DireccionResultado? = null
    private var completandoDireccion = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue =
            "VacunoApp/1.0 (gestion finca ganadera; contacto soporte@vacunoapp.com)"
        Configuration.getInstance().osmdroidBasePath = File(applicationContext.cacheDir, "osmdroid")
        Configuration.getInstance().osmdroidTileCache =
            File(Configuration.getInstance().osmdroidBasePath, "tiles")
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
        val etDireccion = findViewById<EditText>(R.id.et_direccion)
        val mapaDirecciones = findViewById<MapView>(R.id.mapa_direcciones)
        val lvDirecciones = findViewById<ListView>(R.id.lv_direcciones)
        val rgRol = findViewById<RadioGroup>(R.id.rg_rol)
        val switchInformacion = findViewById<Switch>(R.id.switch_informacion)
        val cbTerminos = findViewById<CheckBox>(R.id.cb_terminos)
        val btnRegistrar = findViewById<Button>(R.id.btn_registrar)
        val btnGuardarUbicacion = findViewById<Button>(R.id.btn_guardar_ubicacion)
        val btnLimpiarUbicacion = findViewById<Button>(R.id.btn_limpiar_ubicacion)
        val tvVolverLogin = findViewById<TextView>(R.id.tv_volver_login)
        direccionMapController = DireccionMapController(this, mapaDirecciones, lvDirecciones) { ubicacion ->
            direccionPendiente = ubicacion
            Toast.makeText(this, "Ubicación lista. Pulsa Guardar ubicación para confirmarla.", Toast.LENGTH_SHORT).show()
        }
        mapaDirecciones.setTilesScaledToDpi(true)
        mapaDirecciones.minZoomLevel = 3.0
        mapaDirecciones.maxZoomLevel = 19.0
        mapaDirecciones.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> v.parent.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                    v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }

        etDireccion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (completandoDireccion) return
                direccionSeleccionada = null
                direccionPendiente = null
                val query = s.toString().trim()
                if (query.length < 3) {
                    direccionMapController.limpiar()
                    return
                }
                handlerBusqueda.removeCallbacksAndMessages(null)
                handlerBusqueda.postDelayed({ direccionMapController.buscarDireccion(query) }, 1_100)
            }
        })

        etDireccion.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = etDireccion.text.toString().trim()
                if (query.length >= 3) {
                    handlerBusqueda.removeCallbacksAndMessages(null)
                    direccionMapController.buscarDireccion(query)
                }
                true
            } else {
                false
            }
        }

        lvDirecciones.setOnItemClickListener { _, _, position, _ ->
            val adapter = lvDirecciones.adapter
            val item = adapter.getItem(position) as DireccionResultado
            completandoDireccion = true
            etDireccion.setText(item.displayName)
            etDireccion.setSelection(etDireccion.text.length)
            completandoDireccion = false
            lvDirecciones.visibility = View.GONE
            lvDirecciones.adapter = null
            direccionMapController.seleccionarDireccion(item)
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(etDireccion.windowToken, 0)
        }

        btnGuardarUbicacion.setOnClickListener {
            val ubicacion = direccionPendiente
            if (ubicacion == null) {
                Toast.makeText(this, "Busca una dirección o mantén presionado el mapa para elegir un punto", Toast.LENGTH_SHORT).show()
            } else {
                direccionSeleccionada = ubicacion
                Toast.makeText(this, "Ubicación guardada", Toast.LENGTH_SHORT).show()
            }
        }

        btnLimpiarUbicacion.setOnClickListener {
            handlerBusqueda.removeCallbacksAndMessages(null)
            completandoDireccion = true
            etDireccion.text.clear()
            completandoDireccion = false
            direccionPendiente = null
            direccionSeleccionada = null
            direccionMapController.limpiar()
        }


        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()
            val nombreFinca = etNombreFinca.text.toString().trim()

            val idRolSeleccionado = rgRol.checkedRadioButtonId

            val mensajeValidacion = RegistroValidator.validate(nombre, correo, telefono, contrasena, nombreFinca)
            when {
                mensajeValidacion != null -> Toast.makeText(this, mensajeValidacion, Toast.LENGTH_SHORT).show()
                idRolSeleccionado == -1 -> Toast.makeText(this, "Selecciona tu rol en la finca", Toast.LENGTH_SHORT).show()
                !cbTerminos.isChecked -> Toast.makeText(this, "Debes aceptar los terminos", Toast.LENGTH_SHORT).show()
                direccionSeleccionada == null -> Toast.makeText(this, "Busca y selecciona la direccion de la finca", Toast.LENGTH_SHORT).show()
                else -> {
                    val rol = findViewById<RadioButton>(idRolSeleccionado).text.toString()
                    SessionPreferences(this).saveAccount(
                        nombre, correo, contrasena, nombreFinca,
                        direccionSeleccionada?.displayName.orEmpty(),
                        direccionSeleccionada?.lat.orEmpty(),
                        direccionSeleccionada?.lon.orEmpty()
                    )
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
