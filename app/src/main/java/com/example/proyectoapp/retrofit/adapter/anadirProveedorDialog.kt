package com.example.proyectoapp.retrofit.adapter

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.proyectoapp.R
import com.example.proyectoapp.retrofit.pojos.Proveedores

class anadirProveedorDialog (
    val onProveedorAñadido: (Proveedores) -> Unit

) : DialogFragment() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var photoURI: Uri

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.anadirproveedores, null)

        val nombre = view.findViewById<EditText>(R.id.editNombre)
        val nif = view.findViewById<EditText>(R.id.editNif)
        val email = view.findViewById<EditText>(R.id.editEmail)
        val direccion = view.findViewById<EditText>(R.id.editDireccion)
        val telefono = view.findViewById<EditText>(R.id.editTelefono)

        val boton = view.findViewById<Button>(R.id.btnAnadir)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancel)



        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

        boton.setOnClickListener {

            if (nombre == null || nif == null || email == null) {
                Toast.makeText(requireContext(), "Rellene todos los campos", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val proveedor = Proveedores(
                    0,
                    nombre.text.toString(),
                    telefono.text.toString(),
                    direccion.text.toString(),
                    email.text.toString(),
                    nif.text.toString()


                )
                Log.i("Proveedor", "Proveedor añadido: " + proveedor.toString())
                onProveedorAñadido(proveedor)
                dialog.dismiss()
            }


        }
        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }
        // Lista de todos los EditTexts a limpiar
        val editTexts = listOf(
            nombre,
            nif,
            email,
            direccion,
            telefono
        )
        // Aplicar la función de limpieza a todos los EditTexts
        setOnFocusClearListener(editTexts)
        return dialog
    }



    fun setOnFocusClearListener(editTexts: List<EditText>) {
        editTexts.forEach { editText ->
            editText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    editText.text.clear()
                }
            }
        }
    }
}