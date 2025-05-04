package com.example.proyectoapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.gridlayout.widget.GridLayout
import com.example.proyectoapp.retrofit.adapter.EditarUserDialog
import com.example.proyectoapp.retrofit.adapter.anadirAlbaranDialog
import com.example.proyectoapp.retrofit.adapter.anadirUserDialog
import com.example.proyectoapp.retrofit.endPoints.AlbaranInterface
import com.example.proyectoapp.retrofit.endPoints.UsuarioInterface
import com.example.proyectoapp.retrofit.instances.UserInterface
import com.example.proyectoapp.retrofit.instances.UserInterface.getAuthToken
import com.example.proyectoapp.retrofit.objetos.Albaran
import com.example.proyectoapp.retrofit.objetos.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class PantallaAlbaranesActivity : AppCompatActivity() {
    private lateinit var albaranes: List<Albaran>
    private val albaranApi: AlbaranInterface = UserInterface.retrofit.create(AlbaranInterface::class.java)
    private lateinit var botonAñadir: Button
    private lateinit var botonInforme: Button
    private lateinit var botonEditar: Button

    private lateinit var gridLayout: GridLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.albaraneslista)
        gridLayout = findViewById(R.id.idGridLayout)
        val datos = this.intent.extras
        val nombre = datos?.getString("nombre")
        val email = datos?.getString("email")
        val contrasena = datos?.getString("contrasena")
        val rol = datos?.getString("rol")
        val foto = datos?.getString("foto")
        val id = datos?.getInt("userId")
        botonAñadir=findViewById(R.id.butNew)
        botonInforme=findViewById(R.id.butInforme)

        botonAñadir.setOnClickListener{
            val dialog = anadirAlbaranDialog { nuevoAlbaran ->
                anadirAlbaran(nuevoAlbaran)
            }
            dialog.show(supportFragmentManager, "AñadirUsuarioDialog")
        }
        botonInforme.setOnClickListener{
            Toast.makeText(this, "Informe", Toast.LENGTH_SHORT).show()
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
                albaranes.forEach { Log.i("Usuarios:", it.toString()) }
                mostrarAlbaranes(albaranes)
            }

            override fun onFailure(call: Call<List<Albaran>>, t: Throwable) {
                Toast.makeText(this@PantallaAlbaranesActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
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

    private fun mostrarAlbaranes(albaranes: List<Albaran>) {
        gridLayout.removeAllViews()
        gridLayout.columnCount = 3
        val albaranesOrdenados = albaranes.sortedByDescending { it.fechaAlbaran }
        for (albaran in albaranesOrdenados) {

            val contenedor = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 400
                    height = 500
                    setMargins(16, 16, 16, 16)
                }
                gravity = Gravity.CENTER
                setPadding(16, 16, 16, 16)
                if (albaran.estado == "Pendiente") {
                    setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                } else {
                    setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray))
                }
            }


            val nombre = TextView(this).apply {
                text = albaran.fechaAlbaran.toString()
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
            val foto = albaran.fotoAlbaran

            try {
                if (!albaran.fotoAlbaran.isNullOrEmpty()) {
                    val bitmap = base64ToBitmap(albaran.fotoAlbaran)
                    if (bitmap != null) {
                        userImage.setImageBitmap(bitmap)
                            Log.e("Imagen", "imagencorrecta:")
                    } else {
                        userImage.setImageResource(R.drawable.perfil_estandar)
                        Log.e("Imagen", "imagenIncorrecta:")
                    }
                } else {
                    userImage.setImageResource(R.drawable.perfil_estandar)
                }
            } catch (e: Exception) {
                Log.e("Imagen", "Error al cargar imagen: ${e.message}")
                userImage.setImageResource(R.drawable.perfil_estandar)
            }
            userImage.setOnClickListener {
                Log.i("Imagen", "Botón IMAGEN pulsado")

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
}