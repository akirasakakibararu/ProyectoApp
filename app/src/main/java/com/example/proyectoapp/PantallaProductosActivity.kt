package com.example.proyectoapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
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
import com.example.proyectoapp.retrofit.objetos.Productos
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.proyectoapp.retrofit.adapter.editarProductDialog

class PantallaProductosActivity : AppCompatActivity() {

    private val userApi: UsuarioInterface =
        UserInterface.retrofit.create(UsuarioInterface::class.java)
    private val productoApi: ProductoInterface =
        UserInterface.retrofit.create(ProductoInterface::class.java)
    private lateinit var productos: List<Productos>
    private lateinit var productoA: Productos
    private lateinit var gridLayout: GridLayout
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
        val id = datos?.getInt("userId")

        Log.e("Datos recibidos", "Nombre: $nombre, Email: $email, ID: $id")

        loginUser(nombre.orEmpty(), contrasena.orEmpty())

        gridLayout = findViewById(R.id.idGridLayout)

        val btnEnviarFinal = findViewById<Button>(R.id.btnEnviarFinal)
        btnEnviarFinal.setOnClickListener {
            Log.i("Producto", "Botón ENVIAR FINAL pulsado")
            productos.forEach { Log.i("Productos:", it.toString()) }
            actualizarProductos()
        }

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
            }

            override fun onFailure(call: Call<List<Productos>>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }


    private fun mostrarProductos(productos: List<Productos>) {
        gridLayout.removeAllViews()
        gridLayout.columnCount = 5
        val contAñadir = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = GridLayout.LayoutParams().apply {
                width = 400
                height = 500
                setMargins(16, 16, 16, 16)
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
                    getAllProductos()
                }
                dialog.show(supportFragmentManager, "AñadirProductoDialog")
            }


        }
        contAñadir.addView(btnAnadir)
        gridLayout.addView(contAñadir)
        for (producto in productos) {

            val contenedor = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 400
                    height = 500
                    setMargins(16, 16, 16, 16)
                }
                gravity = Gravity.CENTER
                setPadding(16, 16, 16, 16)
                setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
            }


            val nombre = TextView(this).apply {
                text = producto.nombre
                setTextColor(ContextCompat.getColor(context, android.R.color.black))
                textSize = 18f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(5, 5, 5, 10) }
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
                val dialog = editarProductDialog(productoA) { productoEditado ->
                    editarProducto(productoEditado)
                    mostrarProductos(productos)

                }
                dialog.show(supportFragmentManager, "EditarProductoDialog")
            }

            val contenedorBotones = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(8, 8, 8, 8) }
            }

            val numero = EditText(this).apply {
                setText(producto.stockActual.toString())
                setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                setTextColor(ContextCompat.getColor(context, android.R.color.black))

                inputType = InputType.TYPE_CLASS_NUMBER
                gravity = Gravity.CENTER
                layoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val btnMenos = Button(this).apply {
                text = "-"
                setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
                setTextColor(ContextCompat.getColor(context, android.R.color.white))
                layoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setOnClickListener {
                    Log.i("Producto", "Disminuir producto")
                    val numeroActual = numero.text.toString().toIntOrNull() ?: 0
                    if (numeroActual <= producto.stockMinimo) {
                        numero.setText((numeroActual - 1).toString())
                        producto.stockActual = numero.getText().toString().toInt()
                        numero.setTextColor(
                            ContextCompat.getColor(
                                context,
                                android.R.color.holo_red_light
                            )
                        )
                        Toast.makeText(
                            this@PantallaProductosActivity,
                            "Este producto se encuentra en el limite minimo",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else if (numeroActual > 0) {
                        numero.setText((numeroActual - 1).toString())
                        producto.stockActual = numero.getText().toString().toInt()
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
                }
            }

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

    private fun loginUser(username: String, password: String) {
        val call = userApi.loginUser(username, password)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (!response.isSuccessful) {
                    Log.e("Login Error:", response.message())
                    return
                }
                response.body()?.let {
                    Log.i("Token:", it)
                    getSharedPreferences("app_prefs", MODE_PRIVATE).edit()
                        .putString("auth_token", it)
                        .apply()
                    getAllProductos()
                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }

    private fun actualizarProductos() {
        val token = "Bearer ${getAuthToken()}"
        val call = productoApi.actualizarProductos(token, productos)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (!response.isSuccessful) {
                    Log.e("Update Error:", response.message())
                    return
                }
                response.body()?.let {
                    Log.i("Respuesta:", it)
                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })

    }
    private fun editarProducto(producto: Productos) {
        val token = "Bearer ${getAuthToken()}"
        val call = productoApi.editarProducto(token, producto)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (!response.isSuccessful) {
                    Log.e("Update Producto Error:", response.message())
                    return
                }
                response.body()?.let {
                    Log.i("Producto actualizado:", it)
                    Toast.makeText(this@PantallaProductosActivity, "Producto actualizado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }
    private fun añadirProducto() {
        val token = "Bearer ${getAuthToken()}"
        val call = productoApi.añadirProducto(token, productoA)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (!response.isSuccessful) {
                    Log.e("Update Error:", response.message())
                    return
                }
                response.body()?.let {
                    Log.i("Respuesta:", it)
                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })

    }
    fun identificarImagen(input: String): String {
        return when {
            input.startsWith("data:image", ignoreCase = true) && input.contains("base64,") -> "base64"
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