package com.example.vacuno

import com.example.vacuno.model.DireccionResultado
import com.example.vacuno.network.NominatimService

import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
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
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File


class RegistroActivity : AppCompatActivity() {
    private val handlerBusqueda = Handler(Looper.getMainLooper())
    private var direccionSeleccionada: DireccionResultado? = null
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
        val tvVolverLogin = findViewById<TextView>(R.id.tv_volver_login)
        //regex
        val regexNombre = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{3,50}$")
        val regexCorreo = Regex("^[A-Za-z0-9+_.-]+@gmail\\.com\$")
        val regexTelefono = Regex("^[0-9]{7,10}$")
        val regexContrasena = Regex("^(?=.*[A-Za-z])(?=.*\\d).{6,}$")
        val regexTextoFinca = Regex("^[A-Za-z0-9ÁÉÍÓÚáéíóúÑñ #.,-]{3,60}$")

        mapaDirecciones.setMultiTouchControls(false)
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
                val query = s.toString().trim()
                if (query.length < 3) {
                    lvDirecciones.visibility = View.GONE
                    lvDirecciones.adapter = null
                    mapaDirecciones.overlays.clear()
                    mapaDirecciones.visibility = View.GONE
                    return
                }
                handlerBusqueda.removeCallbacksAndMessages(null)
                handlerBusqueda.postDelayed({ buscarDireccion(query) }, 500)
            }
        })

        etDireccion.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = etDireccion.text.toString().trim()
                if (query.length >= 3) {
                    handlerBusqueda.removeCallbacksAndMessages(null)
                    buscarDireccion(query)
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
            direccionSeleccionada = item
            lvDirecciones.visibility = View.GONE
            lvDirecciones.adapter = null
            centrarMapaEn(item)
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(etDireccion.windowToken, 0)
        }


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
                direccionSeleccionada == null -> Toast.makeText(this, "Busca y selecciona la direccion de la finca", Toast.LENGTH_SHORT).show()
                else -> {
                    val rol = findViewById<RadioButton>(idRolSeleccionado).text.toString()
                    getSharedPreferences("vacuno_preferences", Context.MODE_PRIVATE)
                        .edit()
                        .putString("nombre", nombre)
                        .putString("correo", correo)
                        .putString("contrasena", contrasena)
                        .putString("finca", nombreFinca)
                        .putString("direccion", direccionSeleccionada?.displayName ?: "")
                        .putString("direccion_lat", direccionSeleccionada?.lat ?: "")
                        .putString("direccion_lon", direccionSeleccionada?.lon ?: "")
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

    private fun buscarDireccion(query: String) {
        Thread {
            var resultados = listOf<DireccionResultado>()
            try {
                resultados = NominatimService.buscarDireccion(query)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            runOnUiThread {
                val lv = findViewById<ListView>(R.id.lv_direcciones)
                if (resultados.isEmpty()) {
                    lv.visibility = View.GONE
                    lv.adapter = null
                    findViewById<MapView>(R.id.mapa_direcciones).also { mapa ->
                        mapa.overlays.clear()
                        mapa.visibility = View.GONE
                    }
                } else {
                    lv.adapter = object : ArrayAdapter<DireccionResultado>(
                        this, android.R.layout.simple_list_item_1, resultados
                    ) {
                        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val v = super.getView(position, convertView, parent)
                            v.findViewById<TextView>(android.R.id.text1).text = getItem(position)?.displayName
                            return v
                        }
                    }
                    lv.visibility = View.VISIBLE
                    mostrarResultadosEnMapa(resultados)
                }
            }
        }.start()
    }

    private fun mostrarResultadosEnMapa(resultados: List<DireccionResultado>) {
        val mapa = findViewById<MapView>(R.id.mapa_direcciones)
        mapa.overlays.clear()
        val primero = resultados.first()
        val centroInicial = GeoPoint(primero.lat.toDouble(), primero.lon.toDouble())
        mapa.controller.setZoom(15.0)
        mapa.controller.setCenter(centroInicial)
        resultados.forEach { d ->
            val marcador = Marker(mapa)
            marcador.position = GeoPoint(d.lat.toDouble(), d.lon.toDouble())
            marcador.title = d.displayName
            mapa.overlays.add(marcador)
        }
        mapa.visibility = View.VISIBLE
    }

    private fun centrarMapaEn(d: DireccionResultado) {
        val mapa = findViewById<MapView>(R.id.mapa_direcciones)
        if (mapa.visibility != View.VISIBLE) {
            mostrarResultadosEnMapa(listOf(d))
            return
        }
        mapa.overlays.clear()
        val marcador = Marker(mapa)
        marcador.position = GeoPoint(d.lat.toDouble(), d.lon.toDouble())
        marcador.title = d.displayName
        mapa.overlays.add(marcador)
        mapa.controller.setZoom(15.0)
        mapa.controller.setCenter(GeoPoint(d.lat.toDouble(), d.lon.toDouble()))
    }

    override fun onResume() {
        super.onResume()
        findViewById<MapView>(R.id.mapa_direcciones)?.onResume()
    }

    override fun onPause() {
        super.onPause()
        findViewById<MapView>(R.id.mapa_direcciones)?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        findViewById<MapView>(R.id.mapa_direcciones)?.onDetach()
    }
}
