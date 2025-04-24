package com.example.proyectoapp.retrofit.adapter

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.proyectoapp.R

class insertarPassDialog( val onPasswordInserted: (String) -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.insertarpass, null)
        val editTextPass = view.findViewById<EditText>(R.id.editTextPassword)
        val boton = view.findViewById<Button>(R.id.boton)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
        boton.setOnClickListener {
            onPasswordInserted(editTextPass.text.toString())

            dialog.dismiss()
        }

        return dialog
    }


}