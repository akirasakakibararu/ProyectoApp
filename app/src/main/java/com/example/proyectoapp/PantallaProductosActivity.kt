package com.example.proyectoapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import androidx.gridlayout.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.proyectoapp.retrofit.adapter.anadirProductDialog
import com.example.proyectoapp.retrofit.endPoints.ProductoInterface
import com.example.proyectoapp.retrofit.endPoints.UsuarioInterface
import com.example.proyectoapp.retrofit.instances.UserInterface
import com.example.proyectoapp.retrofit.instances.UserInterface.getAuthToken
import com.example.proyectoapp.retrofit.pojos.Productos
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
import android.widget.SearchView
import com.example.proyectoapp.retrofit.adapter.editarProductDialog
import com.example.proyectoapp.retrofit.endPoints.MovimientosInterface
import com.example.proyectoapp.retrofit.pojos.Movimientos
import com.example.proyectoapp.retrofit.pojos.Usuario

class PantallaProductosActivity : AppCompatActivity() {

    private val userApi: UsuarioInterface =
        UserInterface.retrofit.create(UsuarioInterface::class.java)
    private val productoApi: ProductoInterface =
        UserInterface.retrofit.create(ProductoInterface::class.java)
    private val movimientoApi: MovimientosInterface =
        UserInterface.retrofit.create(MovimientosInterface::class.java)
    private lateinit var movimientos: List<Movimientos>
    private lateinit var productos: List<Productos>
    private lateinit var productoA: Productos
    private lateinit var movimientoA: Movimientos
    private lateinit var gridLayout: GridLayout
    private lateinit var searchView: SearchView
    private lateinit var productosFiltrados: List<Productos>
    private lateinit var perfiles: Button
    private lateinit var albaranes: Button
    private lateinit var inventario: Button
    private lateinit var btnVolver: ImageButton
    var usuarioId: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.productoslista)

        // Obtener los datos enviados por Intent
        val datos = this.intent.extras
        val nombre = datos?.getString("nombre")
        val email = datos?.getString("email")
        val contrasena = datos?.getString("contrasena")
        val rol = datos?.getString("rol")
        val foto = datos?.getString("foto")
        val id = datos?.getInt("usuario")

        Log.e("Datos recibidos", "Nombre: $nombre, Email: $email, ID: $id")

        gridLayout = findViewById(R.id.idProductoLayout)
        searchView = findViewById(R.id.searchViewProductos)
        perfiles = findViewById(R.id.butPerfil)
        albaranes = findViewById(R.id.buttAlbaran)
        inventario = findViewById(R.id.buttInventario)
        btnVolver = findViewById(R.id.btnVolver)
        if (id != null) {
            usuarioId = id
        }
        getAllProductos()

        btnVolver.setOnClickListener {
            val intent = Intent(this, PantallaPrincipalActivity::class.java)
            startActivity(intent)
            finish()
        }
        albaranes.setOnClickListener {
            val intent = Intent(this, PantallaAlbaranesActivity::class.java)

            intent.putExtra("userId", id)
            intent.putExtra("nombre", nombre)
            intent.putExtra("email", email)
            intent.putExtra("contrasena", contrasena)
            intent.putExtra("rol", rol)
            intent.putExtra("foto", foto)
            startActivity(intent)
            finish()
        }
        inventario.setOnClickListener {
            val intent = Intent(this, InventarioActivity::class.java)
            startActivity(intent)
        }


        perfiles.setOnClickListener {
            val intent = Intent(this, PantallaPerfilesActivity::class.java)
            intent.putExtra("userId", id)
            intent.putExtra("nombre", nombre)
            intent.putExtra("email", email)
            intent.putExtra("contrasena", contrasena)
            intent.putExtra("rol", rol)
            startActivity(intent)
            finish()
        }
        if (rol == "Empleado") {
            perfiles.visibility = View.INVISIBLE
            albaranes.visibility = View.INVISIBLE
            inventario.visibility = View.INVISIBLE

        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filtrarProductos(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarProductos(newText)
                return true
            }
        })
    }

    private fun filtrarProductos(query: String?) {
        val texto = query?.trim()?.lowercase().orEmpty()
        productosFiltrados = productos.filter {
            it.nombre.contains(texto, ignoreCase = true)
        }
        Log.i("Productos filtrados:", productosFiltrados.toString())

        mostrarProductos(productosFiltrados)
    }

    private fun getAuthToken(): String? {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", null)
    }

    private fun getAllProductos() {
        val token = "Bearer ${getAuthToken()}"

        val call = productoApi.getAllProductos(token)
        call.enqueue(object : Callback<List<Productos>> {
            override fun onResponse(
                call: Call<List<Productos>>,
                response: Response<List<Productos>>
            ) {
                if (!response.isSuccessful) {
                    Log.e("Response err:", response.message())
                    return
                }
                productos = response.body() ?: emptyList()
                productos.forEach { Log.i("Productos:", it.toString()) }
                mostrarProductos(productos)
                getAllMovimientos(usuarioId)
            }

            override fun onFailure(call: Call<List<Productos>>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }

    private fun getAllMovimientos(id: Int) {
        val token = "Bearer ${getAuthToken()}"
        val call = movimientoApi.getbyUser(token, id)
        call.enqueue(object : Callback<List<Movimientos>> {
            override fun onResponse(
                call: Call<List<Movimientos>>,
                response: Response<List<Movimientos>>
            ) {
                if (!response.isSuccessful) {
                    Log.e("Response err:", response.message())
                    return
                }
                movimientos = response.body() ?: emptyList()
                movimientos.forEach { Log.i("Productos:", it.toString()) }
                val idsConMovimientos= mutableListOf<Int>()
                idsConMovimientos.addAll(movimientos.map { it.productos })
                Log.i("Ids con movimientos:", idsConMovimientos.toString())
                val conteoIds = idsConMovimientos.groupingBy { it }.eachCount()

                // 2. Ordenamos la lista de productos
                val productosOrdenados = productos.sortedWith(compareByDescending<Productos> {
                    conteoIds[it.idProducto] ?: 0  // Primero los más frecuentes en idProductos
                }.thenBy {
                    it.idProducto  // Desempatar si tienen el mismo conteo (opcional)
                })
                mostrarProductos(productosOrdenados)
            }

            override fun onFailure(call: Call<List<Movimientos>>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }


    private fun mostrarProductos(productos: List<Productos>) {
        gridLayout.removeAllViews()
        if (productos.count() <= 2) {
            gridLayout.columnCount = 1
        } else if (productos.count() == 3) {
            gridLayout.columnCount = 3
        } else {
            gridLayout.columnCount = 5
        }

        val contAñadir = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = GridLayout.LayoutParams().apply {
                width = 400
                height = 500
                setMargins(16, 16, 16, 16)
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 3f)
            }
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
            gravity = Gravity.CENTER
            setPadding(16, 16, 16, 16)
        }
        val btnAnadir = ImageButton(this).apply {
            setImageResource(R.drawable.masomenos)
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                300
            ).apply { gravity = Gravity.CENTER }
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
            setOnClickListener {
                Log.i("Producto", "Botón AÑADIR pulsado")
                val dialog = anadirProductDialog { nuevoProducto ->
                    productoA = nuevoProducto
                    añadirProducto()

                }
                dialog.show(supportFragmentManager, "AñadirProductoDialog")
            }


        }
        contAñadir.addView(btnAnadir)
        gridLayout.addView(contAñadir)

        for (producto in productos) {
            if (producto.habilitado == false) {
                Log.i("Producto", "Producto deshabilitado: " + producto.nombre)
            } else {


                val contenedor = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 400
                        height = 500
                        columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 3f)
                        setMargins(16, 16, 16, 16)
                    }
                    gravity = Gravity.CENTER
                    setPadding(16, 16, 16, 16)
                    setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))

                }


                val nombre = TextView(this).apply {
                    text = producto.nombre
                    textSize = 18f
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(5, 5, 5, 10) }
                    setTextColor(ContextCompat.getColor(context, android.R.color.black))

                }


                val imageView = ImageButton(this).apply {
                    setImageResource(R.drawable.cafe)
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    adjustViewBounds = true
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        250
                    ).apply { gravity = Gravity.CENTER }
                    setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                }
                cargarImagenDesdeString(producto.foto, imageView)
                imageView.setOnClickListener {
                    Log.i("Producto", "Botón IMAGEN pulsado")
                    productoA = producto
                    val dialog = editarProductDialog(producto,
                        onProductoEditado = { productoEditado ->
                            editarProducto(productoEditado)
                        },
                        onProductoEliminado = { idProducto ->
                            eliminarProducto(idProducto)
                        }
                    )

                    dialog.show(supportFragmentManager, "EditarProductoDialog")
                }

                val contenedorBotones = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(8, 8, 8, 8) }
                    setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                }

                val numero = TextView(this).apply {
                    setText(producto.stockActual.toString())
                    setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                    setTextColor(ContextCompat.getColor(context, android.R.color.black))

                    gravity = Gravity.CENTER
                    layoutParams =
                        LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                val btnMenos = Button(this).apply {
                    text = "-"
                    setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            android.R.color.holo_red_light
                        )
                    )
                    setTextColor(ContextCompat.getColor(context, android.R.color.white))
                    layoutParams =
                        LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    setOnClickListener {
                        Log.i("Producto", "Disminuir producto")
                        val numeroActual = numero.text.toString().toIntOrNull() ?: 0
                        if (numeroActual <= 0) {
                            Toast.makeText(
                                this@PantallaProductosActivity,
                                "No hay mas Stock de este producto",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            numero.setText((numeroActual - 1).toString())
                            producto.stockActual = numero.getText().toString().toInt()
                            disminuirStock(producto.idProducto)
                            val fecha = System.currentTimeMillis()
                            movimientoA= Movimientos(
                                0,
                                producto.idProducto,
                                usuarioId,
                                "Menos",
                                fecha.toString()
                            )
                            crearMovimiento()

                        }
                    }
                }
                val btnMas = Button(this).apply {
                    text = "+"
                    setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            android.R.color.holo_green_light
                        )
                    )
                    setTextColor(ContextCompat.getColor(context, android.R.color.white))
                    layoutParams =
                        LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    setOnClickListener {
                        Log.i("Producto", "Aumentar producto")
                        val numeroActual = numero.text.toString().toIntOrNull() ?: 0
                        numero.setText((numeroActual + 1).toString())
                        producto.stockActual = numero.getText().toString().toInt()
                        aumentarStock(producto.idProducto)
                        val fecha = System.currentTimeMillis()
                        movimientoA= Movimientos(
                            0,
                            producto.idProducto,
                            usuarioId,
                            "Mas",
                            fecha.toString()
                        )
                        crearMovimiento()
                    }
                }
                aplicarEstiloSegunStock(producto, contenedor, numero, btnMenos, nombre)
                //contenedor de botones
                contenedorBotones.addView(btnMenos)
                contenedorBotones.addView(numero)
                contenedorBotones.addView(btnMas)

                //contenedor principal
                contenedor.addView(nombre)
                contenedor.addView(imageView)
                contenedor.addView(contenedorBotones)

                //contenedor  GridLayout
                gridLayout.addView(contenedor)
            }
        }

    }

    fun aplicarEstiloSegunStock(
        producto: Productos,
        contenedor: LinearLayout,
        numero: TextView,
        btnMenos: Button,
        nombre: TextView
    ) {
        if (producto.stockActual <= 0) {
            btnMenos.isEnabled = false
            nombre.setTextColor(ContextCompat.getColor(this, R.color.white))
            btnMenos.setBackgroundColor(ContextCompat.getColor(this, R.color.grisazul))
            contenedor.setBackgroundColor(ContextCompat.getColor(this, R.color.rojo))
        } else if (producto.stockActual <= producto.stockMinimo) {
            btnMenos.isEnabled = true
            numero.setTextColor(ContextCompat.getColor(this, R.color.rojo))
            btnMenos.setBackgroundColor(ContextCompat.getColor(this, R.color.rojo))
            contenedor.setBackgroundColor(ContextCompat.getColor(this, R.color.amarillo))
        } else {
            numero.setTextColor(ContextCompat.getColor(this, R.color.black))
            contenedor.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }
    }


    private fun aumentarStock(idProducto: Int) {
        val token = "Bearer ${getAuthToken()}"
        val call = productoApi.aumentarStock(token, idProducto)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.i("Stock aumentado", "Stock aumentado con éxito")
                    getAllProductos()
                } else {
                    Log.e("Aumentar Stock Error:", response.message())
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }

    fun eliminarProducto(idProducto: Int) {
        val token = "Bearer ${getAuthToken()}"
        val call = productoApi.eliminarProducto(token, idProducto)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.i("Producto eliminado", "Producto eliminado con éxito")
                    getAllProductos()
                } else {
                    Log.e("Eliminar Error:", response.message())
                    Toast.makeText(
                        this@PantallaProductosActivity,
                        "Error al eliminar",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }

    private fun disminuirStock(idProducto: Int) {
        val token = "Bearer ${getAuthToken()}"
        val call = productoApi.disminuirStock(token, idProducto)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.i("Stock disminuido", "Stock disminuido con éxito")
                    getAllProductos()
                } else {
                    Log.e("Disminuir Stock Error:", response.message())
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }

    private fun actualizarProductos() {
        val token = "Bearer ${getAuthToken()}"
        for (producto in productos) {
            val call = productoApi.editarProducto(token, producto.idProducto, producto)
            call.enqueue(object : Callback<Productos> {
                override fun onResponse(call: Call<Productos>, response: Response<Productos>) {
                    if (!response.isSuccessful) {
                        Log.e("Update Producto Error:", response.message())
                    }
                }

                override fun onFailure(call: Call<Productos>, t: Throwable) {
                    Log.e("Error al actualizar:", t.message ?: "Error desconocido")
                }
            })
        }
        Toast.makeText(this@PantallaProductosActivity, "Productos actualizados", Toast.LENGTH_SHORT)
            .show()
        getAllProductos()
    }

    private fun editarProducto(producto: Productos) {
        val token = "Bearer ${getAuthToken()}"
        val call = productoApi.editarProducto(token, producto.idProducto, producto)
        Log.i("Producto enviado:", producto.toString())

        call.enqueue(object : Callback<Productos> {
            override fun onResponse(call: Call<Productos>, response: Response<Productos>) {
                if (!response.isSuccessful) {
                    Log.e("Update Producto Error:", response.message())
                    Toast.makeText(
                        this@PantallaProductosActivity,
                        "Error al actualizar el producto",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                response.body()?.let {
                    Log.i("Producto actualizado:", it.toString())
                    Toast.makeText(
                        this@PantallaProductosActivity,
                        "Producto actualizado",
                        Toast.LENGTH_SHORT
                    ).show()
                    getAllProductos()
                }
            }

            override fun onFailure(call: Call<Productos>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }

    private fun añadirProducto() {
        val token = "Bearer ${getAuthToken()}"
        val call = productoApi.añadirProducto(token, productoA)

        call.enqueue(object : Callback<Productos> {
            override fun onResponse(call: Call<Productos>, response: Response<Productos>) {
                if (!response.isSuccessful) {
                    Log.e("Añadir Producto Error:", response.message())
                    Toast.makeText(
                        this@PantallaProductosActivity,
                        "Error al añadir el producto",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                response.body()?.let {
                    Log.i("Producto añadido:", it.toString())
                    Toast.makeText(
                        this@PantallaProductosActivity,
                        "Producto añadido",
                        Toast.LENGTH_SHORT
                    ).show()
                    getAllProductos()
                }
            }

            override fun onFailure(call: Call<Productos>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }
    private fun crearMovimiento() {
        val token = "Bearer ${getAuthToken()}"
        val call = movimientoApi.añadirMovimiento(token, movimientoA)

        call.enqueue(object : Callback<Movimientos> {
            override fun onResponse(call: Call<Movimientos>, response: Response<Movimientos>) {
                if (!response.isSuccessful) {
                    Log.e("Añadir Movimiento Error:", response.message())

                    return
                }
                response.body()?.let {
                    Log.i("Movimiento añadido:", it.toString())
                    getAllProductos()
                }
            }

            override fun onFailure(call: Call<Movimientos>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }

    fun identificarImagen(input: String): String {
        return when {
            input.startsWith(
                "data:image",
                ignoreCase = true
            ) && input.contains("base64,") -> "base64"

            input.startsWith("http", ignoreCase = true) -> "url"
            else -> "desconocido"
        }
    }

    fun cargarImagenDesdeString(input: String, imageView: ImageView) {
        when (identificarImagen(input)) {
            "url" -> {
                Picasso.get().load(input).into(imageView)
            }

            "base64" -> {
                try {
                    val base64Data = input.substringAfter("base64,")
                    val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    imageView.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Puedes mostrar una imagen de error si falla el decode
                    imageView.setImageResource(R.drawable.cafe)
                }
            }

            else -> {
                // Imagen desconocida
                imageView.setImageResource(R.drawable.cafe)
            }
        }
    }

}