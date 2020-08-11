package de.tfsw.lenstracker

import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.LocalDate
import java.util.function.Consumer

object JsonUtil {

    private const val FILE_NAME = "lensdata.json"
    private const val KEY_VERSION = "version"
    private const val KEY_DATE_OPENED = "dateOpened"
    private const val KEY_TIMES_USED = "timesUsed"
    private const val VERSION = 1

    fun readLensDataFromFile(filesDir: File, lensData: LensData) {
        val file = File(filesDir, FILE_NAME)

        if (file.exists()) {
            val json = JSONObject(String(file.readBytes()))

            //val version = json.getInt(KEY_VERSION)

            val dateOpened = json.getString(KEY_DATE_OPENED)
            lensData.dateOpened = LocalDate.parse(dateOpened)

            val timesUsed = json.getJSONArray(KEY_TIMES_USED)
            if (timesUsed.length() > 0) {
                for (i in 0 until timesUsed.length()) {
                    lensData.timesUsed.add(LocalDate.parse(timesUsed.getString(i)))
                }
            }
        }
    }

    fun saveLensDataToFile(filesDir: File, lensData: LensData) {
        if (lensData.dateOpened != null) {
            val file = File(filesDir, FILE_NAME)

            val json = JSONObject()
            json.put(KEY_VERSION, VERSION)
            json.put(KEY_DATE_OPENED, lensData.dateOpened.toString())

            val timesUsedJson = JSONArray()
            lensData.timesUsed.forEach(Consumer { t -> timesUsedJson.put(t.toString()) })
            json.put(KEY_TIMES_USED, timesUsedJson)

            file.writeBytes(json.toString().toByteArray())
        }
    }
}