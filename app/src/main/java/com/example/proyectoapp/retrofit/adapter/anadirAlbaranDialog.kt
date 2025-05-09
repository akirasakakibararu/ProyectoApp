package com.example.proyectoapp.retrofit.adapter

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CalendarView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.proyectoapp.R
import com.example.proyectoapp.retrofit.pojos.Albaran
import java.io.ByteArrayOutputStream

class anadirAlbaranDialog(
    val onAlbaranAñadido: (Albaran) -> Unit
) : DialogFragment() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var photoURI: Uri
    private lateinit var fecha: CalendarView
    private lateinit var imageButtonLogo: ImageButton
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.anadiralbaran, null)

        val nombre = view.findViewById<EditText>(R.id.editNombre)
        val cif = view.findViewById<EditText>(R.id.editCif)
        val cantidad = view.findViewById<EditText>(R.id.editCantidad)

        val boton = view.findViewById<Button>(R.id.btnAnadir)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancel)
        fecha = view.findViewById(R.id.calendarView)
        imageButtonLogo = view.findViewById(R.id.txtFecha)
        val habilitado = view.findViewById<CheckBox>(R.id.checkEstado)
        cantidad.inputType = InputType.TYPE_CLASS_NUMBER

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

        boton.setOnClickListener {
            val bitmap =
                (imageButtonLogo.drawable as BitmapDrawable).bitmap
            val fotoBase64 = bitmapToBase64(bitmap)
            if (nombre == null || cif == null || cantidad == null) {
                Toast.makeText(requireContext(), "Rellene todos los campos", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val albaran = Albaran(
                    0,
                    fecha.date.toString(),
                    habilitado.text.toString(),
                    cif.text.toString(),
                    fotoBase64
                )

                Log.i("Albaran", "Albaran añadido: " + albaran.toString())
                onAlbaranAñadido(albaran)
                dialog.dismiss()
            }


        }
        habilitado.setOnCheckedChangeListener { _, isChecked ->
            habilitado.setText(if (isChecked) "Pagado" else "Pendiente")
        }
        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }
        imageButtonLogo.setOnClickListener {
            takePicture()
        }
        // Lista de todos los EditTexts a limpiar
        val editTexts = listOf(
            nombre,
            cif,
            cantidad
        )
        // Aplicar la función de limpieza a todos los EditTexts
        setOnFocusClearListener(editTexts)
        return dialog
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {//Función para recibir la foto
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val extras = data?.extras
            val imgBitmap = extras?.get("data") as Bitmap
            imageButtonLogo.setImageBitmap(imgBitmap)
        }
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val base64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
        return base64
    }

    fun base64ToBitmap(base64Str: String): Bitmap {//Función para convertir un Base64 a bitmap
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun takePicture() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: Exception) {
            Log.e("Error", "No se puede abrir la cámara")
        }
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