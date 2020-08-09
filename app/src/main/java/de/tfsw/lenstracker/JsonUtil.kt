package de.tfsw.lenstracker

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.LocalDate
import java.util.function.Consumer

object JsonUtil {

    private const val FILE_NAME = "lensdata.json"
    private const val LOG_TAG = "JsonUtil"
    private const val KEY_VERSION = "version"
    private const val KEY_DATE_OPENED = "dateOpened"
    private const val KEY_TIMES_USED = "timesUsed"
    private const val VERSION = 1

    fun readLensDataFromFile(filesDir: File, lensData: LensData) {
        val file = File(filesDir, FILE_NAME)

        if (file.exists()) {
            Log.d(LOG_TAG, "Storage file exists")

            val json = JSONObject(String(file.readBytes()))

            val version = json.getInt(KEY_VERSION)
            Log.d(LOG_TAG, "Storage file version: $version")

            val dateOpened = json.getString(KEY_DATE_OPENED)
            lensData.dateOpened = LocalDate.parse(dateOpened)

            val timesUsed = json.getJSONArray(KEY_TIMES_USED)
            if (timesUsed.length() > 0) {
                for (i in 0 until timesUsed.length()) {
                    lensData.timesUsed.add(LocalDate.parse(timesUsed.getString(i)))
                }
            }
        } else {
            Log.d(LOG_TAG, "No storage file found")
        }
    }

    fun saveLensDataToFile(filesDir: File, lensData: LensData) {
        Log.d(LOG_TAG, "Saving to storage...")

        if (lensData.dateOpened != null) {
            val file = File(filesDir, FILE_NAME)

            val json = JSONObject()
            json.put(KEY_VERSION, VERSION)
            json.put(KEY_DATE_OPENED, lensData.dateOpened.toString())

            val timesUsedJson = JSONArray()
            lensData.timesUsed.forEach(Consumer { t -> timesUsedJson.put(t.toString()) })
            json.put(KEY_TIMES_USED, timesUsedJson)

            Log.d(LOG_TAG, "Now writing: " + json.toString())

            file.writeBytes(json.toString().toByteArray())
        } else {
            Log.d(LOG_TAG, "No data to write, aborting")
        }
    }
}