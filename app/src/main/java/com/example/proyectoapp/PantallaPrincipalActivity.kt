package com.example.proyectoapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Gravity

import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import androidx.gridlayout.widget.GridLayout

import com.example.proyectoapp.retrofit.endPoints.UsuarioInterface
import com.example.proyectoapp.retrofit.instances.UserInterface

import com.example.proyectoapp.retrofit.pojos.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.graphics.Bitmap
import android.view.View
import android.widget.Toast
import androidx.core.view.isInvisible
import com.example.proyectoapp.retrofit.adapter.insertarPassDialog
import java.io.ByteArrayOutputStream


class PantallaPrincipalActivity : AppCompatActivity() {
    private lateinit var users: List<Usuario>
    private lateinit var user: Usuario

    private val userApi: UsuarioInterface =
        UserInterface.retrofit.create(UsuarioInterface::class.java)
    private lateinit var gridLayout: GridLayout
    private lateinit var btnVolver: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.usuariolista)
        gridLayout = findViewById(R.id.idGridLayoutUser)
        getAllUsers()
        btnVolver = findViewById(R.id.btnVolver)
        btnVolver.isInvisible = true


    }

    private fun getAllUsers() {
        val call = userApi.getAllUsers()
        call.enqueue(object : Callback<List<Usuario>> {
            override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                if (!response.isSuccessful) {
                    Log.e("Response err:", response.message())
                    return
                }
                users = response.body() ?: emptyList()
                users.forEach { Log.i("Usuarios:", it.toString()) }
                mostrarUsuarios(users)
            }

            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }

    private fun mostrarUsuarios(usuarios: List<Usuario>) {
        gridLayout.removeAllViews()
        gridLayout.columnCount = 5
        for (usuario in usuarios) {
            if (!usuario.habilitado) {
                Log.i("Usuario", "Usuario deshabilitado: " + usuario.nombre)
            } else {
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
                    text = usuario.nombre
                    setTextColor(ContextCompat.getColor(context, android.R.color.black))
                    textSize = 18f
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(5, 5, 5, 10) }
                }


                val userImage = ImageButton(this).apply {
                    setImageResource(R.drawable.cafe)
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    adjustViewBounds = true
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        250
                    ).apply { gravity = Gravity.CENTER }
                    setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))

                }
                val foto = usuario.fotoPerfil

                try {
                    if (!usuario.fotoPerfil.isNullOrEmpty()) {
                        val bitmap = base64ToBitmap(usuario.fotoPerfil)
                        if (bitmap != null) {
                            userImage.setImageBitmap(bitmap)
                            Log.e("UserAdapter", "imagencorrecta:")
                        } else {
                            userImage.setImageResource(R.drawable.perfil_estandar)
                            Log.e("UserAdapter", "imagenIncorrecta:")
                        }
                    } else {
                        userImage.setImageResource(R.drawable.perfil_estandar)
                    }
                } catch (e: Exception) {
                    Log.e("UserAdapter", "Error al cargar imagen: ${e.message}")
                    userImage.setImageResource(R.drawable.perfil_estandar)
                }
                userImage.setOnClickListener {
                    Log.i("Producto", "Botón IMAGEN pulsado")
                   user = usuario
                    if (usuario.rol == "Empleado") {
                        loginEmpleado(usuario.nombre)
                    }else{
                        val dialog = insertarPassDialog(
                            onPasswordInserted = { password ->
                                loginUser(usuario.nombre, password)

                            }
                        )
                        dialog.show(supportFragmentManager, "insertarPassDialog")
                    }


                }

                val contenedorBotones = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(8, 8, 8, 8) }
                }


                //contenedor de botones


                //contenedor principal
                contenedor.addView(nombre)
                contenedor.addView(userImage)

                //contenedor  GridLayout
                gridLayout.addView(contenedor)
            }
        }

    }

    private fun base64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            Log.e("UserAdapter", "convertido:")
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

    private fun loginUser(username: String, password: String) {
        val call = userApi.loginUser(username, password)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (!response.isSuccessful) {
                    Log.e("Login Error:", response.message())
                    Toast.makeText(
                        this@PantallaPrincipalActivity,
                        "Contraseña incorrecta",
                        Toast.LENGTH_LONG
                    ).show()
                    return

                }
                response.body()?.let {
                    Log.i("Token:", it)
                    getSharedPreferences("app_prefs", MODE_PRIVATE).edit()
                        .putString("auth_token", it)
                        .apply()
                    val intent = Intent(
                        this@PantallaPrincipalActivity,
                        PantallaProductosActivity::class.java
                    )
                    intent.putExtra("usuario", user.idUsuario)
                    intent.putExtra("nombre", user.nombre)
                    intent.putExtra("email", user.email)
                    intent.putExtra("contrasena", password)
                    intent.putExtra("rol", user.rol)
                    intent.putExtra("fotoPerfil", user.fotoPerfil)
                    intent.putExtra("habilitado", user.habilitado)
                    // Iniciar la nueva actividad
                    startActivity(intent)
                    finish()
                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }
    private fun loginEmpleado(username: String) {
        val call = userApi.loginEmpleado(username)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (!response.isSuccessful) {
                    Log.e("Login Error:", response.message())
                    Toast.makeText(
                        this@PantallaPrincipalActivity,
                        "Error al iniciar sesión",
                        Toast.LENGTH_LONG
                    ).show()
                    return

                }
                response.body()?.let {
                    Log.i("Token:", it)
                    getSharedPreferences("app_prefs", MODE_PRIVATE).edit()
                        .putString("auth_token", it)
                        .apply()
                    val intent = Intent(
                        this@PantallaPrincipalActivity,
                        PantallaProductosActivity::class.java
                    )
                    intent.putExtra("usuario", user.idUsuario)
                    intent.putExtra("nombre", user.nombre)
                    intent.putExtra("email", user.email)
                    intent.putExtra("rol", user.rol)
                    intent.putExtra("fotoPerfil", user.fotoPerfil)
                    intent.putExtra("habilitado", user.habilitado)
                    // Iniciar la nueva actividad
                    startActivity(intent)
                    finish()
                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }
}