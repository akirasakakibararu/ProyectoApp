package com.example.proyectoapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoapp.retrofit.adapter.InventarioAdapter
import com.example.proyectoapp.retrofit.endPoints.ProductoInterface
import com.example.proyectoapp.retrofit.instances.UserInterface
import com.example.proyectoapp.retrofit.instances.UserInterface.getAuthToken
import com.example.proyectoapp.retrofit.pojos.Productos
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InventarioActivity : AppCompatActivity() {

    private val productoApi: ProductoInterface =
        UserInterface.retrofit.create(ProductoInterface::class.java)

    private lateinit var spinnerFiltro: Spinner
    private lateinit var recyclerProductos: RecyclerView
    private lateinit var btnEnviarCorreo: Button
    private lateinit var inputEmail: EditText
    private lateinit var btnCancelarFiltro: Button  // <-- Nuevo botón

    private lateinit var adaptador: InventarioAdapter
    private var filtroActual: String? = null
    private val authHeader: String?
        get() = this.getAuthToken()?.let { "Bearer $it" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario)
        val datos = this.intent.extras
        val nombre = datos?.getString("nombre")
        val email = datos?.getString("email")
        val contrasena = datos?.getString("contrasena")
        val rol = datos?.getString("rol")
        val foto = datos?.getString("fotoPerfil")
        val id = datos?.getInt("usuario")
        // 1) Vistas
        spinnerFiltro     = findViewById(R.id.spinnerFiltro)
        recyclerProductos = findViewById(R.id.recyclerProductos)
        btnEnviarCorreo   = findViewById(R.id.btnEnviarCorreo)
        inputEmail        = findViewById(R.id.inputEmail)
        btnCancelarFiltro = findViewById(R.id.btnCancelar) // Inicializa nuevo botón

        // 2) Spinner
        spinnerFiltro.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.filtros)
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spinnerFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                filtroActual = when (position) {
                    1 -> "minimos"
                    2 -> "deshabilitados"
                    else -> null
                }
                cargarProductos()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                filtroActual = null
                cargarProductos()
            }
        }

        // 3) RecyclerView
        adaptador = InventarioAdapter(emptyList())
        recyclerProductos.layoutManager = LinearLayoutManager(this)
        recyclerProductos.adapter = adaptador

        // 4) Botón correo
        btnEnviarCorreo.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            if (email.isBlank()) {
                Toast.makeText(this, "Introduce un email válido", Toast.LENGTH_SHORT).show()
            } else {
                enviarCorreo(email)
            }
        }

        // 5) Botón cancelar filtro
        btnCancelarFiltro.setOnClickListener {
            val intent = Intent(this, PantallaProductosActivity::class.java)
            intent.putExtra("nombre", nombre)
            intent.putExtra("usuario", email)
            intent.putExtra("contrasena", contrasena)
            intent.putExtra("rol", rol)
            intent.putExtra("fotoPerfil", foto)
            intent.putExtra("usuario", id)
            startActivity(intent)
            finish()
        }

        // 6) Carga inicial
        cargarProductos()
    }

    private fun cargarProductos() {
        val token = authHeader ?: run {
            Toast.makeText(this, "No autorizado", Toast.LENGTH_LONG).show()
            return
        }

        Log.d("Inventario", "API inventario filtro=$filtroActual")
        productoApi.getProductos(token, filtroActual)
            .enqueue(object : Callback<List<Productos>> {
                override fun onResponse(call: Call<List<Productos>>, response: Response<List<Productos>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val lista = response.body()!!
                        Log.d("Inventario", "Recibidos ${lista.size} productos")
                        adaptador.updateLista(lista)
                    } else {
                        Log.e("Inventario", "Error ${response.code()}")
                        Toast.makeText(this@InventarioActivity,
                            "Error al obtener datos: ${response.code()}",
                            Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<List<Productos>>, t: Throwable) {
                    Log.e("Inventario", "Fallo red", t)
                    Toast.makeText(this@InventarioActivity,
                        "Error de red: ${t.localizedMessage}",
                        Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun enviarCorreo(email: String) {
        val token = authHeader ?: run {
            Toast.makeText(this, "No autorizado", Toast.LENGTH_LONG).show()
            return
        }

        productoApi.enviarInventario(token, email, filtroActual)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@InventarioActivity,
                            "Correo enviado correctamente",
                            Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@InventarioActivity,
                            "Error correo: ${response.code()}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@InventarioActivity,
                        "Fallo conexión: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT).show()
                }
            })
    }
}
