package com.example.proyectoapp

import AlbaranesAdapter
import android.R.attr.text
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.gridlayout.widget.GridLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoapp.retrofit.adapter.anadirAlbaranDialog
import com.example.proyectoapp.retrofit.adapter.anadirProveedorDialog
import com.example.proyectoapp.retrofit.adapter.anadirUserDialog
import com.example.proyectoapp.retrofit.endPoints.AlbaranInterface
import com.example.proyectoapp.retrofit.endPoints.DetalleAlbaranInterfaz
import com.example.proyectoapp.retrofit.endPoints.ProductoInterface
import com.example.proyectoapp.retrofit.endPoints.ProveedorInterface
import com.example.proyectoapp.retrofit.instances.UserInterface
import com.example.proyectoapp.retrofit.instances.UserInterface.getAuthToken
import com.example.proyectoapp.retrofit.pojos.Albaran
import com.example.proyectoapp.retrofit.pojos.DetallesAlbaran
import com.example.proyectoapp.retrofit.pojos.EnviarInformeRequest
import com.example.proyectoapp.retrofit.pojos.Productos
import com.example.proyectoapp.retrofit.pojos.Proveedores
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.temporal.IsoFields
import java.util.Calendar

class PantallaAlbaranesActivity : AppCompatActivity() {
    private val detalleAlbaranApi = UserInterface.retrofit.create(DetalleAlbaranInterfaz::class.java)
    private val productoApi: ProductoInterface =
        UserInterface.retrofit.create(ProductoInterface::class.java)
    private lateinit var albaranes: List<Albaran>
    private lateinit var proveedores: List<Proveedores>
    private val albaranApi: AlbaranInterface =
        UserInterface.retrofit.create(AlbaranInterface::class.java)
    private val proveedorApi: ProveedorInterface =
        UserInterface.retrofit.create(ProveedorInterface::class.java)

    private lateinit var butCompletarAlbaran: Button
    private var albaranesSeleccionados: List<Albaran> = emptyList()

    private lateinit var botonAñadir: Button
    private lateinit var botonProveedor: Button
    private lateinit var botonInforme: Button
    private lateinit var btnVolver: ImageButton
    private lateinit var gridLayout: GridLayout
    private var fechaInicioFiltro: LocalDate? = null
    private var fechaFinFiltro: LocalDate? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.albaraneslista)

        gridLayout = findViewById(R.id.idAlbaranesGridLayout)

        val datos = this.intent.extras

        val nombre = datos?.getString("nombre")
        val email = datos?.getString("email")
        val contrasena = datos?.getString("contrasena")
        val rol = datos?.getString("rol")
        val foto = datos?.getString("fotoPerfil")
        val id = datos?.getInt("usuario")


        val btnFiltro = findViewById<Button>(R.id.butFiltrarMesSemana)
        val listaAlbaranes = findViewById<GridLayout>(R.id.idAlbaranesGridLayout)
        val butCompletarAlbaran = findViewById<Button>(R.id.butCompletarAlbaran)


        btnFiltro.text = filtroActual
        val btnFiltroRango = findViewById<Button>(R.id.btnFiltroRango)

        btnFiltroRango.setOnClickListener {
            mostrarDatePickers()
        }
        butCompletarAlbaran.setOnClickListener {
            añadirDetalleAlbaran()
            mostrarDialogoAlbaranes(albaranes, multiSelect = false) // Mostrar diálogo para seleccionar albarán

        }

        btnFiltro.setOnClickListener {
            filtroActual = if (filtroActual == "Mes actual") "Semana actual" else "Mes actual"
            btnFiltro.text = filtroActual
            mostrarAlbaranes(albaranes)
        }

        botonAñadir = findViewById(R.id.butNew)
        botonInforme = findViewById(R.id.butInforme)
        btnVolver = findViewById(R.id.btnVolver)
        botonProveedor = findViewById(R.id.butProveedor)
        botonProveedor.setOnClickListener {
            val dialog = anadirProveedorDialog { nuevoProveedor ->
                anadirProveedor(nuevoProveedor)
            }
            dialog.show(supportFragmentManager, "AñadirUsuarioDialog")
        }
        btnVolver.setOnClickListener {
            val intent = android.content.Intent(this, PantallaProductosActivity::class.java)
            intent.putExtra("usuario", id)
            intent.putExtra("nombre", nombre)
            intent.putExtra("email", email)
            intent.putExtra("contrasena", contrasena)
            intent.putExtra("rol", rol)
            intent.putExtra("fotoPerfil", foto)

            startActivity(intent)
            finish()
        }
        botonAñadir.setOnClickListener {
            val dialog = anadirAlbaranDialog { nuevoAlbaran ->
                anadirAlbaran(nuevoAlbaran)
            }
            dialog.show(supportFragmentManager, "AñadirUsuarioDialog")
        }
        botonInforme.setOnClickListener {
            mostrarDialogoAlbaranes(albaranes, multiSelect = true) // Mostrar diálogo para seleccionar albarán

        }

        getAllAlbaranes()

    }

    private fun getAllAlbaranes() {
        val token = "Bearer ${getAuthToken()}"
        val call = albaranApi.getAllAlbaranes(token)
        call.enqueue(object : Callback<List<Albaran>> {
            override fun onResponse(call: Call<List<Albaran>>, response: Response<List<Albaran>>) {
                if (!response.isSuccessful) {
                    Log.e("Response err:", response.message())
                    return
                }
                albaranes = response.body() ?: emptyList()
                albaranes.forEach { Log.i("Albaranes:", it.toString()) }
                mostrarAlbaranes(albaranes)

            }

            override fun onFailure(call: Call<List<Albaran>>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }

    private fun mostrarDialogoAlbaranes(albaranes: List<Albaran>, multiSelect: Boolean) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_albaranes, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerViewAlbaranes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (multiSelect) {
            // Modo selección múltiple
            val adapter = AlbaranesAdapter(
                albaranes,
                multiSelectMode = true,
                onSelectionChanged = { seleccionados ->

                    botonInforme.isEnabled = seleccionados.isNotEmpty()
                    // Guardas la lista para usar cuando pulses "Generar informe"
                    albaranesSeleccionados = seleccionados
                }
            )
            recyclerView.adapter = adapter

        } else {
            // Modo selección simple
            val adapter = AlbaranesAdapter(
                albaranes,
                multiSelectMode = false,
                onItemClick = { albaran ->
                    comprobarDetallesAlbaran(albaran.idAlbaran)
                    abrirDialogoNuevoDetalle(albaran.idAlbaran)
                }
            )
            recyclerView.adapter = adapter
        }

        AlertDialog.Builder(this)
            .setTitle(if (multiSelect) "Selecciona uno o varios Albaranes" else "Selecciona un Albarán")
            .setView(dialogView)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton(if (multiSelect) "Generar Informe" else null) { dialog, _ ->
                if (multiSelect) {
                    generarInforme(this, albaranesSeleccionados) { resumen ->
                        // Puedes mostrar aquí el diálogo con el resumen si quieres
                        // Por ejemplo:
                        mostrarDialogoInforme(this, resumen, albaranesSeleccionados)
                    }
                }
                dialog.dismiss()
            }
            .setNeutralButton(if (multiSelect) "Enviar Correo" else null) { dialog, _ ->
                if (multiSelect) {
                    generarInforme(this, albaranesSeleccionados) { resumen ->
                        mostrarDialogoEmail(this, albaranesSeleccionados, resumen)
                    }
                }
                dialog.dismiss()
            }
            .show()
    }

    private fun mostrarDialogoEmail(context: Context, albaranes: List<Albaran>, resumen: Map<String, Any>) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_email, null)
        val editTextEmail = view.findViewById<EditText>(R.id.editTextEmail)

        AlertDialog.Builder(context)
            .setTitle("Enviar informe por correo")
            .setView(view)
            .setPositiveButton("Enviar") { dialog, _ ->
                val email = editTextEmail.text.toString().trim()
                if (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    enviarCorreoConPdf(context, albaranes, resumen, email)
                } else {
                    Toast.makeText(context, "Introduce un email válido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun generarInforme(
        context: Context,
        albaranes: List<Albaran>,
        onResumenListo: (Map<String, Any>) -> Unit
    ) {
        if (albaranes.isEmpty()) return

        val ids = albaranes.map { it.idAlbaran }

        val token = context.getAuthToken()
        if (token.isNullOrEmpty()) return

        val call = UserInterface.retrofit.create(AlbaranInterface::class.java).generarInforme("Bearer $token", ids)

        call.enqueue(object : retrofit2.Callback<Map<String, Any>> {
            override fun onResponse(
                call: retrofit2.Call<Map<String, Any>>,
                response: retrofit2.Response<Map<String, Any>>
            ) {
                if (response.isSuccessful) {
                    val resumen = response.body()

                    if (resumen != null) {
                        onResumenListo(resumen)  // Devuelve el resumen al callback
                    } else {
                        Toast.makeText(context, "No se recibió resumen", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("GenerarInforme", "Error en la respuesta del servidor: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<Map<String, Any>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Función opcional para mostrar el diálogo de resumen (puedes llamarla desde Generar Informe si quieres)
    private fun mostrarDialogoInforme(context: Context, resumen: Map<String, Any>, albaranes: List<Albaran>) {
        val numPagados = resumen["pagados"]?.toString()?.toDoubleOrNull()?.toInt() ?: 0
        val numPendientes = resumen["pendientes"]?.toString()?.toDoubleOrNull()?.toInt() ?: 0

        val idsPagadosList = resumen["idesPagados"] as? List<*> ?: emptyList<Any>()
        val idsPendientesList = resumen["idesPendientes"] as? List<*> ?: emptyList<Any>()

        val idsPagados = idsPagadosList.joinToString(", ") { it.toString().replace(".0", "") }
        val idsPendientes = idsPendientesList.joinToString(", ") { it.toString().replace(".0", "") }

        val totalAlbaranesInt = (resumen["totalAlbaranes"] as? Number)?.toInt()
            ?: resumen["totalAlbaranes"]?.toString()?.toDoubleOrNull()?.toInt() ?: 0
        val totalImporteDouble = (resumen["totalImporte"] as? Number)?.toDouble()
            ?: resumen["totalImporte"]?.toString()?.toDoubleOrNull() ?: 0.0
        val totalImporteFormateado = String.format("%.2f", totalImporteDouble)

        val cifs = albaranes.map { it.nif }.distinct().joinToString(", ")

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_informe, null)
        val tableLayout = dialogView.findViewById<TableLayout>(R.id.tableLayout)

        fun crearFila(concepto: String, valor: String, extra: String? = null): TableRow {
            val tr = TableRow(context)

            val tvConcepto = TextView(context).apply {
                text = concepto
                setPadding(16, 16, 16, 16)
                background = ContextCompat.getDrawable(context, R.drawable.border_cell)
            }

            val tvValor = TextView(context).apply {
                text = valor
                setPadding(16, 16, 16, 16)
                gravity = Gravity.CENTER
                background = ContextCompat.getDrawable(context, R.drawable.border_cell)
            }

            tr.addView(tvConcepto)
            tr.addView(tvValor)

            if (extra != null) {
                val tvExtra = TextView(context).apply {
                    text = extra
                    setPadding(16, 16, 16, 16)
                    gravity = Gravity.CENTER
                    background = ContextCompat.getDrawable(context, R.drawable.border_cell)
                }
                tr.addView(tvExtra)
            }

            return tr
        }

        tableLayout.addView(crearFila("Total Albaranes", totalAlbaranesInt.toString()))
        tableLayout.addView(crearFila("Pagados", numPagados.toString(), idsPagados))
        tableLayout.addView(crearFila("Pendientes", numPendientes.toString(), idsPendientes))
        tableLayout.addView(crearFila("Total Importe", "$totalImporteFormateado€"))
        tableLayout.addView(crearFila("CIFs", cifs))

        AlertDialog.Builder(context)
            .setTitle("Informe de Albaranes")
            .setView(dialogView)
            .setPositiveButton("Cerrar", null)
            .show()
    }

    private fun comprobarDetallesAlbaran(idAlbaran: Int) {
        val token = "Bearer ${getAuthToken()}"
        val call = detalleAlbaranApi .getDetallesPorAlbaran(token, idAlbaran)
        call.enqueue(object : Callback<List<DetallesAlbaran>> {
            override fun onResponse(call: Call<List<DetallesAlbaran>>, response: Response<List<DetallesAlbaran>>) {
                if (!response.isSuccessful) {
                    Log.e("Response err:", response.message())
                    return
                }
                val detalles = response.body() ?: emptyList()
                if (detalles.isNotEmpty()) {
                    Toast.makeText(this@PantallaAlbaranesActivity,
                        "No puede modificar el albarán porque ya tiene detalles.", Toast.LENGTH_LONG).show()
                } else {
                    abrirDialogoNuevoDetalle(idAlbaran)
                }
            }

            override fun onFailure(call: Call<List<DetallesAlbaran>>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })

    }

    private fun abrirDialogoNuevoDetalle(idAlbaran: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_nuevo_detalle, null)

        val spinnerProducto = dialogView.findViewById<Spinner>(R.id.spinnerProducto)
        val inputPrecio = dialogView.findViewById<EditText>(R.id.inputPrecio)
        val inputCantidad = dialogView.findViewById<EditText>(R.id.inputCantidad)

        val token = "Bearer ${getAuthToken()}"
        val call = productoApi.getAllProductos(token)

        call.enqueue(object : Callback<List<Productos>> {
            override fun onResponse(
                call: Call<List<Productos>>,
                response: Response<List<Productos>>
            ) {
                if (!response.isSuccessful) {
                    Log.e("Productos", "Error en la respuesta: ${response.message()}")
                    Toast.makeText(
                        this@PantallaAlbaranesActivity,
                        "Error al cargar productos",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                val productos = response.body() ?: emptyList()
                val nombresProductos = productos.map { it.nombre }

                val adapter = ArrayAdapter(
                    this@PantallaAlbaranesActivity,
                    android.R.layout.simple_spinner_item,
                    nombresProductos
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerProducto.adapter = adapter

                // Autocompletar precio al seleccionar producto
                spinnerProducto.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val productoSeleccionado = productos[position]
                        inputPrecio.setText(productoSeleccionado.precioUnitario.toString())
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }

                // Mostrar diálogo después de cargar productos
                AlertDialog.Builder(this@PantallaAlbaranesActivity)
                    .setTitle("Añadir detalle al albarán $idAlbaran")
                    .setView(dialogView)
                    .setPositiveButton("Guardar") { _, _ ->
                        val posicionSeleccionada = spinnerProducto.selectedItemPosition
                        val productoSeleccionado = productos[posicionSeleccionada]
                        val idProducto = productoSeleccionado.idProducto

                        val precio = inputPrecio.text.toString().toBigDecimalOrNull()
                        val cantidad = inputCantidad.text.toString().toIntOrNull()

                        if (precio != null && cantidad != null) {
                            val nuevoDetalle = DetallesAlbaran(
                                producto = idProducto,
                                albaran = idAlbaran,
                                precioUnitario = precio,
                                cantidad = cantidad
                            )
                            insertarDetalle(nuevoDetalle)
                        } else {
                            Toast.makeText(
                                this@PantallaAlbaranesActivity,
                                "Datos inválidos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }

            override fun onFailure(call: Call<List<Productos>>, t: Throwable) {
                Log.e("Productos", "Error en la solicitud: ${t.message}")
                Toast.makeText(
                    this@PantallaAlbaranesActivity,
                    "Fallo al conectar con el servidor",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun insertarDetalle(detalle: DetallesAlbaran) {
        val token = "Bearer ${getAuthToken()}"
        val detallesLista = listOf(detalle)
        val call = detalleAlbaranApi.insertarLoteDetalles(token, detallesLista)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@PantallaAlbaranesActivity,
                        "Detalle añadido correctamente", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@PantallaAlbaranesActivity,
                        "Error al añadir detalle: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@PantallaAlbaranesActivity,
                    "Error: ${t.message ?: "Desconocido"}", Toast.LENGTH_LONG).show()
            }
        })
    }
    private fun filtrarConRango(fechaInicio: String, fechaFin: String) {
        fechaInicioFiltro = LocalDate.parse(fechaInicio)
        fechaFinFiltro = LocalDate.parse(fechaFin)
        filtroActual = "Rango fechas"
        mostrarAlbaranes(albaranes)
    }
    private fun mostrarDatePickers() {
        val calendar = Calendar.getInstance()

        val dateSetListenerInicio = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val fechaInicio = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)

            val dateSetListenerFin = DatePickerDialog.OnDateSetListener { _, year2, month2, day2 ->
                val fechaFin = "%04d-%02d-%02d".format(year2, month2 + 1, day2)
                filtrarConRango(fechaInicio, fechaFin)
            }

            DatePickerDialog(
                this,
                dateSetListenerFin,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        DatePickerDialog(
            this,
            dateSetListenerInicio,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun getAllProveedores() {
        val token = "Bearer ${getAuthToken()}"
        val call = proveedorApi.getAllProveedores(token)
        call.enqueue(object : Callback<List<Proveedores>> {
            override fun onResponse(call: Call<List<Proveedores>>, response: Response<List<Proveedores>>) {
                if (!response.isSuccessful) {
                    Log.e("Response err:", response.message())
                    return
                }
                proveedores = response.body() ?: emptyList()
                proveedores.forEach { Log.i("Albaranes:", it.toString()) }

            }

            override fun onFailure(call: Call<List<Proveedores>>, t: Throwable) {

                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }

    private fun anadirAlbaran(newAlbaran: Albaran) {
        val token = "Bearer ${getAuthToken()}"
        val call = albaranApi.anadirAlbaran(token, newAlbaran)

        call.enqueue(object : Callback<Albaran> {
            override fun onResponse(call: Call<Albaran>, response: Response<Albaran>) {
                if (!response.isSuccessful) {
                    Log.e("Añadir Producto Error:", response.message())

                    return
                }
                response.body()?.let {
                    Log.i("Usuario añadido:", it.toString())

                    getAllAlbaranes()
                }
            }

            override fun onFailure(call: Call<Albaran>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }

    private fun anadirProveedor(proveedor: Proveedores) {
        val token = "Bearer ${getAuthToken()}"
        val call = proveedorApi.anadirProveedor(token, proveedor)

        call.enqueue(object : Callback<Proveedores> {
            override fun onResponse(call: Call<Proveedores>, response: Response<Proveedores>) {
                if (!response.isSuccessful) {
                    Log.e("Proveedores Error:", response.message())

                    return
                }
                response.body()?.let {
                    Log.i("Proveedore añadido:", it.toString())

                }
            }

            override fun onFailure(call: Call<Proveedores>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }

    private var filtroActual = "Mes actual" // Estado inicial del filtro

    private fun mostrarAlbaranes(albaranes: List<Albaran>) {
        val now = LocalDate.now()
        val albaranesFiltrados = when (filtroActual) {
            "Semana actual" -> {
                val semanaActual = now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
                val añoActual = now.year

                albaranes.filter {
                    try {
                        val fecha = LocalDate.parse(it.fechaAlbaran.substring(0, 10))
                        val semana = fecha.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
                        val año = fecha.year
                        semana == semanaActual && año == añoActual
                    } catch (e: Exception) {
                        false
                    }
                }.sortedByDescending {
                    // Ordenamos de más reciente a menos reciente
                    LocalDate.parse(it.fechaAlbaran.substring(0, 10))
                }
            }
            "Rango fechas" -> {
                if (fechaInicioFiltro != null && fechaFinFiltro != null) {
                    albaranes.filter {
                        try {
                            val fecha = LocalDate.parse(it.fechaAlbaran.substring(0, 10))
                            !fecha.isBefore(fechaInicioFiltro) && !fecha.isAfter(fechaFinFiltro)
                        } catch (e: Exception) {
                            false
                        }
                    }
                    // Aquí puedes ordenar si quieres, o dejar tal cual
                } else {
                    emptyList()
                }
            }
            else -> { // "Mes actual"
                val mesActual = now.monthValue
                val añoActual = now.year

                albaranes.filter {
                    try {
                        val fecha = LocalDate.parse(it.fechaAlbaran.substring(0, 10))
                        fecha.monthValue == mesActual && fecha.year == añoActual
                    } catch (e: Exception) {
                        false
                    }
                }.sortedByDescending {
                    LocalDate.parse(it.fechaAlbaran.substring(0, 10))
                }
            }
        }
        gridLayout.removeAllViews()

        gridLayout.columnCount = when {
            albaranesFiltrados.size <= 2 -> 1
            albaranesFiltrados.size <= 3 -> 3
            else -> 3
        }

        for (albaran in albaranesFiltrados) {
            val contenedor = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 500
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 3f)
                    setMargins(16, 16, 16, 16)
                }
                gravity = Gravity.CENTER
                setPadding(16, 16, 16, 16)
                setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            }

            val nombre = TextView(this).apply {
                val fecha = LocalDate.parse(albaran.fechaAlbaran.substring(0, 10))
                text = fecha.toString()

                setTextColor(ContextCompat.getColor(context, android.R.color.black))
                textSize = 25f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(5, 5, 5, 10) }
            }

            val estado = TextView(this).apply {
                text = albaran.estado

                setTextColor(ContextCompat.getColor(context, android.R.color.black))
                textSize = 25f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(5, 5, 5, 10) }
            }

            val imagen = ImageButton(this).apply {
                setImageResource(R.drawable.cafe)
                scaleType = ImageView.ScaleType.FIT_CENTER
                adjustViewBounds = true
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    300
                ).apply { gravity = Gravity.CENTER }
                setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
            }


            try {
                if (!albaran.fotoAlbaran.isNullOrEmpty()) {
                    val bitmap = base64ToBitmap(albaran.fotoAlbaran)
                    if (bitmap != null) {
                        imagen.setImageBitmap(bitmap)
                    } else {
                        imagen.setImageResource(R.drawable.perfil_estandar)
                    }
                } else {
                    imagen.setImageResource(R.drawable.perfil_estandar)
                }
            } catch (e: Exception) {
                Log.e("UserAdapter", "Error al cargar imagen: ${e.message}")
                imagen.setImageResource(R.drawable.perfil_estandar)
            }

            imagen.setOnClickListener {
                val base64 = albaran.fotoAlbaran
                if (!base64.isNullOrEmpty()) {
                    val bitmap = base64ToBitmap(base64)
                    if (bitmap != null) {
                        val dialog = Dialog(this)  // o requireContext() si es fragment
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setCancelable(true)
                        dialog.setContentView(R.layout.dialog_imagen_grande)

                        val imageView = dialog.findViewById<ImageView>(R.id.imagenGrande)
                        imageView.setImageBitmap(bitmap)

                        val btnCerrar = dialog.findViewById<ImageButton>(R.id.btnCerrar)
                        btnCerrar.setOnClickListener { dialog.dismiss() }

                        dialog.show()

                        // Ajustar tamaño del dialogo para que sea ancho completo y alto ajustado al contenido
                        dialog.window?.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    } else {
                        Toast.makeText(this, "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "No hay imagen disponible", Toast.LENGTH_SHORT).show()
                }
            }


            contenedor.addView(nombre)
            contenedor.addView(imagen)
            contenedor.addView(estado)
            gridLayout.addView(contenedor)
        }
    }


    private fun base64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            Log.e("ImagenCheck", "convertido:")
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        } catch (e: Exception) {
            Log.e("Base64Conversion", "Error al convertir Base64: ${e.message}")
            null
        }
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun PantallaAlbaranesActivity.añadirDetalleAlbaran() {
        getAllAlbaranes()    }
    private fun enviarCorreoConPdf(context: Context, albaranes: List<Albaran>, resumen: Map<String, Any>, email: String) {
        val token = context.getAuthToken() ?: return
        val ids = albaranes.map { it.idAlbaran }

        val request = EnviarInformeRequest(ids, email)

        val call = UserInterface.retrofit.create(AlbaranInterface::class.java)
            .enviarInformePorCorreo("Bearer $token", request)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Correo enviado con éxito", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("EnviarCorreo", "Error al enviar correo: Código ${response.code()}, mensaje: ${response.message()}")

                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("EnviarCorreo", "Fallo en la red: Código  ${t.message} ")

            }
        })
    }
}


