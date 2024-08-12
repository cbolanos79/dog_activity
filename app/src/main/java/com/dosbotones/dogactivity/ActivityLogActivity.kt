package com.dosbotones.dogactivity

import ActivityLog
import ActivityLogAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Locale

class ActivityLogActivity : AppCompatActivity() {

    private lateinit var dbHelper: DogActivityDatabaseHelper
    private val REQUEST_CODE_WRITE_STORAGE = 1
    private val activityLogs = mutableListOf<ActivityLog>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        // Configure the Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getResources().getString(R.string.activities)
        toolbar.setTitleTextColor(Color.WHITE)

        dbHelper = DogActivityDatabaseHelper(this)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView_activities)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val activities = dbHelper.getAllActivities()

        val buttonShare: Button = findViewById(R.id.button_share)

        for (it in activities) {
            val parts = it.split(";")
            val activity = when (parts[0].lowercase()) {
                "walk" -> getResources().getString(R.string.walk)
                "pee" -> getResources().getString(R.string.piss)
                "poop" -> getResources().getString(R.string.poop)
                else -> parts[0]
            }

            val timestamp = parts[1]
            val data: String

            if (parts[0].lowercase() == "pee") {
                data = when (parts[2].lowercase()) {
                    "low" -> getResources().getString(R.string.low)
                    "medium" -> getResources().getString(R.string.medium)
                    "high" -> getResources().getString(R.string.high)
                    else -> parts[2]
                }
            } else {
                data = parts[2]
            }

            // Format timestamp
            val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val targetFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val date = originalFormat.parse(timestamp)
            val formattedDate = targetFormat.format(date)

            this.activityLogs.add(ActivityLog(formattedDate, activity, data))
        }

        recyclerView.adapter = ActivityLogAdapter(this.activityLogs)

        buttonShare.setOnClickListener {
            checkAndRequestPermissions()
            shareActivityLogAsCSV()
        }
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
        return try {
            val csvFile = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "dog_activity_log.csv")
            val fileWriter = FileWriter(csvFile)

            // Write CSV header
            fileWriter.append("${R.string.date},${R.string.activity}\n")

            // Write a line for each activity
            val activities = dbHelper.getAllActivities()
            activities.forEach {
                val parts = it.split(";")
                val activity = parts[0]
                val timestamp = parts[1]
                val data:String = when (parts[2]) {
                    "low" -> R.string.low.toString()
                    "medium" -> R.string.medium.toString()
                    "high" -> R.string.high.toString()
                    else -> ""
                }

                // Format date
                val date = android.text.format.DateFormat.getDateFormat(getApplicationContext()).format(timestamp)
                fileWriter.append("${date},${activity},${data}\n")
            }

            fileWriter.flush()
            fileWriter.close()

            csvFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Handle back button
    override fun onSupportNavigateUp(): Boolean {
        // Finish and goes back
        finish()
        return true
    }

    private fun checkAndRequestPermissions() {
        val permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_CODE_WRITE_STORAGE)
        } else {
            shareActivityLogAsCSV() // Permission already granted
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_WRITE_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                } else {
                    // Permiso denegado
                    Toast.makeText(this, R.string.writing_permission_required_to_share_files, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
