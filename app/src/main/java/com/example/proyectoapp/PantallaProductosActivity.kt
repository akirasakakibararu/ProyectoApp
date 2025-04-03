package com.example.proyectoapp

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import androidx.gridlayout.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.proyectoapp.retrofit.endPoints.UsuarioInterface
import com.example.proyectoapp.retrofit.instances.UserInterface
import com.example.proyectoapp.retrofit.objetos.Productos
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PantallaProductosActivity : AppCompatActivity() {

    private val userApi: UsuarioInterface =
        UserInterface.retrofit.create(UsuarioInterface::class.java)

    private lateinit var gridLayout: GridLayout
    private lateinit var producto: Productos
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.productoslista)

        // Obtener los datos enviados por Intent
        val datos = this.intent.extras
        val nombre = datos?.getString("nombre")
        val email = datos?.getString("email")
        val contrasena = datos?.getString("contrasena")
        val rol = datos?.getString("rol")
        val foto = datos?.getString("foto") // Aquí podrías recibir la imagen base64 o URL
        val id = datos?.getInt("userId")

        Log.e("Datos recibidos", "Nombre: $nombre, Email: $email, ID: $id")

        loginUser(nombre.orEmpty(), contrasena.orEmpty())

        gridLayout = findViewById(R.id.idGridLayout)
        producto = Productos(1, "Producto 1", 10.0, "Descripción 1", "imagen1.jpg", 10, 5, true, 1)
        // Simulacion de objetos con imágenes
        val productos = listOf(
            R.drawable.cafe,
            R.drawable.cafe,
            R.drawable.cafe,
            R.drawable.cafe,
            R.drawable.cafe,
            R.drawable.cafe,
            R.drawable.cafe,
            R.drawable.cafe,
            R.drawable.cafe,
            R.drawable.cafe,
            R.drawable.cafe,
            R.drawable.cafe,
            R.drawable.cafe,
            R.drawable.cafe,
            R.drawable.cafe,
            R.drawable.cafe
        )

        mostrarProductos(productos)
    }

    private fun mostrarProductos(productos: List<Int>) {
        gridLayout.removeAllViews()
        gridLayout.columnCount = 5

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
                text = "Producto"
                setTextColor(ContextCompat.getColor(context, android.R.color.black))
                textSize = 18f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(5, 5, 5, 10) }
            }


            val imageView = ImageView(this).apply {
                setImageResource(producto)
                scaleType = ImageView.ScaleType.FIT_CENTER
                adjustViewBounds = true
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    250
                ).apply { gravity = Gravity.CENTER }
            }


            val contenedorBotones = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(8, 8, 8, 8) }
            }


            val btnMenos = Button(this).apply {
                text = "-"
                setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
                setTextColor(ContextCompat.getColor(context, android.R.color.white))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setOnClickListener {
                    Log.i("Producto", "Disminuir producto")
                }
            }


            val numero = EditText(this).apply {
                setText("1") // Valor inicial
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }


            val btnMas = Button(this).apply {
                text = "+"
                setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_light))
                setTextColor(ContextCompat.getColor(context, android.R.color.white))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setOnClickListener {
                    Log.i("Producto", "Aumentar producto")
                }
            }

            // Agregar elementos al contenedor de botones
            contenedorBotones.addView(btnMenos)
            contenedorBotones.addView(numero)
            contenedorBotones.addView(btnMas)

            // Agregar elementos al contenedor principal
            contenedor.addView(nombre)
            contenedor.addView(imageView)
            contenedor.addView(contenedorBotones)

            //Agregar contenedor al GridLayout
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
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }
}
