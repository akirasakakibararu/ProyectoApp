package com.example.proyectoapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.gridlayout.widget.GridLayout
import com.example.proyectoapp.retrofit.adapter.EditarUserDialog
import com.example.proyectoapp.retrofit.adapter.anadirProductDialog
import com.example.proyectoapp.retrofit.adapter.anadirUserDialog
import com.example.proyectoapp.retrofit.adapter.insertarPassDialog
import com.example.proyectoapp.retrofit.endPoints.UsuarioInterface
import com.example.proyectoapp.retrofit.instances.UserInterface
import com.example.proyectoapp.retrofit.instances.UserInterface.getAuthToken
import com.example.proyectoapp.retrofit.objetos.Productos
import com.example.proyectoapp.retrofit.objetos.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class PantallaPerfilesActivity : AppCompatActivity() {
    private lateinit var users: List<Usuario>

    private val userApi: UsuarioInterface =
        UserInterface.retrofit.create(UsuarioInterface::class.java)
    private lateinit var gridLayout: GridLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.usuariolista)
        gridLayout = findViewById(R.id.idGridLayout)
        getAllUsers()


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

    private fun anadirUsuario(newUser: Usuario) {
        val token = "Bearer ${getAuthToken()}"
        val call = userApi.registerUser(token, newUser)

        call.enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (!response.isSuccessful) {
                    Log.e("Añadir Producto Error:", response.message())
                    Toast.makeText(
                        this@PantallaPerfilesActivity,
                        "Error al añadir el usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                response.body()?.let {
                    Log.i("Usuario añadido:", it.toString())
                    Toast.makeText(
                        this@PantallaPerfilesActivity,
                        "Usuario añadido",
                        Toast.LENGTH_SHORT
                    ).show()
                    getAllUsers()
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }

    private fun mostrarUsuarios(usuarios: List<Usuario>) {
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
                val dialog = anadirUserDialog { nuevoUsuario ->
                    anadirUsuario(nuevoUsuario)
                }
                dialog.show(supportFragmentManager, "AñadirProductoDialog")
            }


        }
        contAñadir.addView(btnAnadir)
        gridLayout.addView(contAñadir)
        for (usuario in usuarios) {

                val contenedor = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 400
                        height = 500
                        setMargins(16, 16, 16, 16)
                    }
                    gravity = Gravity.CENTER
                    setPadding(16, 16, 16, 16)
                    if (usuario.habilitado) {
                        setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                    } else {
                        setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray))
                    }
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

                    val dialog = EditarUserDialog(usuario,
                        onUsuarioEditado = { usuarioEditado ->
                            editarUsuario(usuarioEditado)
                        },
                        onUsuarioEliminado = { idUsuario ->
                            eliminarUsuario(idUsuario)
                        }
                    )
                    dialog.show(supportFragmentManager, "insertarPassDialog")

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
    private fun editarUsuario(usuario: Usuario) {
        val token = "Bearer ${getAuthToken()}"
        val call = userApi.editarUsuario(token, usuario.idUsuario, usuario)
        Log.i("Producto enviado:", usuario.toString())

        call.enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (!response.isSuccessful) {
                    Log.e("Update Usuario Error:", response.message())
                    Toast.makeText(
                        this@PantallaPerfilesActivity,
                        "Error al actualizar el Usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                response.body()?.let {
                    Log.i("Usuario actualizado:", it.toString())
                    Toast.makeText(
                        this@PantallaPerfilesActivity,
                        "Usuario actualizado",
                        Toast.LENGTH_SHORT
                    ).show()
                    getAllUsers()
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }
    fun eliminarUsuario(idUsuario: Int) {
        val token = "Bearer ${getAuthToken()}"
        val call = userApi.eliminarUsuario(token, idUsuario)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@PantallaPerfilesActivity,
                        "Usuario eliminado",
                        Toast.LENGTH_SHORT
                    ).show()
                    getAllUsers()
                } else {
                    Log.e("Eliminar Error:", response.message())
                    Toast.makeText(
                        this@PantallaPerfilesActivity,
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
}