package com.dosbotones.dogactivity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Locale

class ActivityLogActivity : AppCompatActivity() {
    private val dbHelper = DogActivityDatabaseHelper(this)
    private val targetFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        // Configure the Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getResources().getString(R.string.activities)
        toolbar.setTitleTextColor(Color.WHITE)

        val tableLayout: TableLayout = findViewById(R.id.tableLayout)

        val activities = this.dbHelper.getAllActivities()

        for (activity in activities) {
            val tableRow = TableRow(this)

            val textViewDate = TextView(this)
            val textViewActivity = TextView(this)
            val textViewData = TextView(this)

            textViewDate.text = targetFormat.format(activity.timestamp)
            textViewActivity.text = actionName(activity.action)
            textViewData.text = dataName(activity.data.toString())

            textViewDate.setPadding(8, 8, 8, 8)
            textViewActivity.setPadding(8, 8, 8, 8)
            textViewData.setPadding(8, 8, 8, 8)

            tableRow.addView(textViewDate)
            tableRow.addView(textViewActivity)
            tableRow.addView(textViewData)

            tableLayout.addView(tableRow)
        }

        val buttonShareCsv: Button = findViewById(R.id.button_share)

        // Share CSV button
        buttonShareCsv.setOnClickListener {
            shareActivityLogAsCSV()
        }
    }

    // Handle back button
    override fun onSupportNavigateUp(): Boolean {
        // Finish and goes back
        finish()
        return true
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(timestamp)
    }

    private fun actionName(action: String): String {
        val name = when(action) {
            "walk" -> getResources().getString(R.string.walk)
            "piss" -> getResources().getString(R.string.piss)
            "pee" -> getResources().getString(R.string.piss)
            "poop" -> getResources().getString(R.string.poop)
            "accident" -> getResources().getString(R.string.accident)
            else -> action
        }
        return name
    }

    private fun dataName(data: String): String {
        val name = when (data.lowercase()) {
            "low" -> getResources().getString(R.string.low)
            "medium" -> getResources().getString(R.string.medium)
            "high" -> getResources().getString(R.string.high)
            else -> data
        }
        return name
    }

    private fun shareActivityLogAsCSV() {
        val csvFile = createCSVFile()
        if (csvFile != null) {
            val uri: Uri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.fileprovider", csvFile)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, R.string.activity_log)
                putExtra(Intent.EXTRA_TEXT, R.string.activity_log_csv)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Allow other apps to read the file
            }
            startActivity(Intent.createChooser(intent, "Compartir usando"))
        }
    }

    private fun createCSVFile(): File? {
        val csvFile: File
        val activities = dbHelper.getAllActivities()

        try {
            csvFile = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "dog_activity_log.csv")
            val fileWriter = FileWriter(csvFile)

            // Write CSV header
            fileWriter.append("${getResources().getString(R.string.date)},${getResources().getString(R.string.activity)},${getResources().getString(R.string.data)}\n")

            // Write a line for each activity
            for (activity in activities) {
                fileWriter.append("${targetFormat.format(activity.timestamp)},${actionName(activity.action)},${dataName(activity.data.toString())}\n")
            }

            fileWriter.flush()
            fileWriter.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return csvFile
    }
}
