package com.dosbotones.dogactivity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
//import android.widget.Button
import android.widget.TextView
import android.widget.Toast
//import android.widget.GridLayout
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
//import androidx.core.content.ContentProviderCompat.requireContext
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DogActivityDatabaseHelper
    private lateinit var textLog: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DogActivityDatabaseHelper(this)

        val buttonWalk: ImageButton = findViewById(R.id.button_walk)
        val buttonPiss: ImageButton = findViewById(R.id.button_piss)
        val buttonPoop: ImageButton = findViewById(R.id.button_poop)
        val buttonShowLog: ImageButton = findViewById(R.id.button_show_log)

        buttonWalk.setOnClickListener {
            Toast.makeText(this,R.string.walk_registered, Toast.LENGTH_SHORT).show()
            dbHelper.addActivity("walk")
        }

        //val buttonPiss: ImageButton = findViewById(R.id.button_piss)
        buttonPiss.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            var items: Array<String> = arrayOf( getResources().getString(R.string.low),
                                                getResources().getString(R.string.medium),
                                                getResources().getString(R.string.high))

            builder.setTitle(getResources().getString(R.string.select_amount_of_piss))
            builder.setItems(items, DialogInterface.OnClickListener() { _, index ->
                val quantity = when (index) {
                    0 -> "low"
                    1 -> "medium"
                    2 -> "high"
                    else -> ""
                }
                dbHelper.addActivity("pee", quantity)
                Toast.makeText(this, R.string.piss_registered, Toast.LENGTH_SHORT).show()
            })

            builder.setNegativeButton(R.string.cancel, DialogInterface.OnClickListener() { dialog, index ->

            })
            builder.show()
        }


        buttonPoop.setOnClickListener {
            Toast.makeText(this,R.string.poop_registered, Toast.LENGTH_SHORT).show()
            dbHelper.addActivity("poop")
        }

        // Configurar el botón "Accident"
        val buttonAccident: ImageButton = findViewById(R.id.button_accident)
        buttonAccident.setOnClickListener {
            val dialog = AccidentDialogFragment()
            dialog.show(supportFragmentManager, "AccidentDialogFragment")
        }


        buttonShowLog.setOnClickListener {
            val intent = Intent(this, ActivityLogActivity::class.java)
            startActivity(intent)
        }
    }
}
