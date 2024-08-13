package com.dosbotones.dogactivity

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class AccidentDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_accident, null)

        val editTextAccident: EditText = view.findViewById(R.id.editTextAccident)
        val btnSave: Button = view.findViewById(R.id.btn_save_accident)

        btnSave.setOnClickListener {
            val accidentDetails = editTextAccident.text.toString()
            if (accidentDetails.isNotEmpty()) {
                saveAccidentDetails(accidentDetails)
                dismiss()
            } else {
                Toast.makeText(context, getResources().getString(R.string.describe_the_accident), Toast.LENGTH_SHORT).show()
            }
        }

        builder.setView(view)
            .setTitle(getResources().getString(R.string.register_accident))
            .setNegativeButton(getResources().getString(R.string.cancel), DialogInterface.OnClickListener { dialog, _ ->
                dialog.cancel()
            })

        return builder.create()
    }

    private fun saveAccidentDetails(details: String) {
        val dbHelper = DogActivityDatabaseHelper(requireContext())
        val currentTime = System.currentTimeMillis()
        dbHelper.addActivity("accident", details)
        Toast.makeText(context, getResources().getString(R.string.accident_registered), Toast.LENGTH_SHORT).show()
    }
}
