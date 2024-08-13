package com.dosbotones.dogactivity

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DogActivityDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "DogActivities.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_NAME = "activities"
        private const val COLUMN_ID = "id"
        private const val COLUMN_ACTION = "action"
        private const val COLUMN_DATA = "data"
        private const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_ACTION TEXT, " +
                "$COLUMN_DATA TEXT, " +
                "$COLUMN_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        if (oldVersion < 1) {
            onCreate(db)
        }

        if (oldVersion < 2) {
            db?.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_DATA TEXT")
        }
    }

    fun addActivity(action: String, data: String = "") {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ACTION, action)
        values.put(COLUMN_DATA, data)
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllActivities(): List<Activity> {
        val activities = mutableListOf<Activity>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY timestamp DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val action = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACTION))

                val ts = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(ts)
                val data = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATA))

                activities.add(Activity(id, action, timestamp, data))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return activities
    }
}

data class Activity(val id: Int, val action: String, val timestamp: Date?, val data: String?)
