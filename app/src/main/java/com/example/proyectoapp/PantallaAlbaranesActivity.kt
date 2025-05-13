package com.example.proyectoapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.gridlayout.widget.GridLayout
import com.example.proyectoapp.retrofit.adapter.anadirAlbaranDialog
import com.example.proyectoapp.retrofit.adapter.anadirProveedorDialog
import com.example.proyectoapp.retrofit.adapter.anadirUserDialog
import com.example.proyectoapp.retrofit.endPoints.AlbaranInterface
import com.example.proyectoapp.retrofit.endPoints.ProveedorInterface
import com.example.proyectoapp.retrofit.instances.UserInterface
import com.example.proyectoapp.retrofit.instances.UserInterface.getAuthToken
import com.example.proyectoapp.retrofit.pojos.Albaran
import com.example.proyectoapp.retrofit.pojos.Proveedores
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.time.LocalDate

class PantallaAlbaranesActivity : AppCompatActivity() {
    private lateinit var albaranes: List<Albaran>
    private lateinit var proveedores: List<Proveedores>
    private val albaranApi: AlbaranInterface =
        UserInterface.retrofit.create(AlbaranInterface::class.java)
    private val proveedorApi: ProveedorInterface =
        UserInterface.retrofit.create(ProveedorInterface::class.java)
    private lateinit var botonAñadir: Button
    private lateinit var botonProveedor: Button
    private lateinit var botonInforme: Button
    private lateinit var btnVolver: ImageButton
    private lateinit var gridLayout: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.albaraneslista)

        gridLayout = findViewById(R.id.idAlbaranesGridLayout)

        val datos = this.intent.extras
        val nombre = datos?.getString("nombre")
        val email = datos?.getString("email")
        val contrasena = datos?.getString("contrasena")
        val rol = datos?.getString("rol")
        val foto = datos?.getString("foto")
        val id = datos?.getInt("userId")

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
            intent.putExtra("userId", id)
            intent.putExtra("nombre", nombre)
            intent.putExtra("email", email)
            intent.putExtra("contrasena", contrasena)
            intent.putExtra("rol", rol)
            intent.putExtra("foto", foto)
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
            Toast.makeText(this, "Informe", Toast.LENGTH_SHORT).show()
        }

        getAllAlbaranes()
        //getAllProveedores()


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

    private fun mostrarAlbaranes(albaranes: List<Albaran>) {
        gridLayout.removeAllViews()
        if (albaranes.count() <= 2) {
            gridLayout.columnCount = 1
        } else if (albaranes.count() <= 3) {
            gridLayout.columnCount = 3
        } else if (albaranes.count() >= 4) {
            gridLayout.columnCount = 3
        }

        for (albaran in albaranes) {
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
                        Log.e("UserAdapter", "imagencorrecta:")
                    } else {
                        imagen.setImageResource(R.drawable.perfil_estandar)
                        Log.e("UserAdapter", "imagenIncorrecta:")
                    }
                } else {
                    imagen.setImageResource(R.drawable.perfil_estandar)
                }
            } catch (e: Exception) {
                Log.e("UserAdapter", "Error al cargar imagen: ${e.message}")
                imagen.setImageResource(R.drawable.perfil_estandar)
            }

            imagen.setOnClickListener {
                Log.i("Imagen", "Botón IMAGEN pulsado")
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


}