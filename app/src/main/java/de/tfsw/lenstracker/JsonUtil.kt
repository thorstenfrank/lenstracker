package de.tfsw.lenstracker

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.time.LocalDate
import java.util.function.Consumer

object JsonUtil {

    private const val FILE_NAME = "lensdata.json"
    private const val KEY_VERSION = "version"
    private const val KEY_DATE_OPENED = "dateOpened"
    private const val KEY_TIMES_USED = "timesUsed"
    private const val KEY_HISTORY = "history"
    private const val KEY_FIRST_LENS_OPENED = "firstLensOpened"
    private const val KEY_LENSES_USED = "lensesUsed"
    private const val KEY_TOTAL_LENS_USAGE = "totalLensUsage"
    private const val VERSION = 2

    fun readLensDataFromFile(filesDir: File, lensData: LensData) {
        val file = File(filesDir, FILE_NAME)

        if (file.exists()) {
            val json = JSONObject(String(file.readBytes()))

            //Log.d("JsonUtil", "Read JSON: $json")

            lensData.version = json.getInt(KEY_VERSION)

            val dateOpened = json.getString(KEY_DATE_OPENED)
            lensData.dateOpened = LocalDate.parse(dateOpened)

            val timesUsed = json.getJSONArray(KEY_TIMES_USED)
            if (timesUsed.length() > 0) {
                for (i in 0 until timesUsed.length()) {
                    lensData.timesUsed.add(LocalDate.parse(timesUsed.getString(i)))
                }
            }

            try {
                val history = json.getJSONObject(KEY_HISTORY)
                val firstLensOpened = history.getString(KEY_FIRST_LENS_OPENED)
                lensData.historyFirstLensOpened = LocalDate.parse(firstLensOpened)
                lensData.historyLensesUsed = history.getInt(KEY_LENSES_USED)
                lensData.historyTotalUsage = history.getInt(KEY_TOTAL_LENS_USAGE)
            } catch (e: JSONException) {
                if (lensData.version == 1) {
                    lensData.historyFirstLensOpened = lensData.dateOpened
                    lensData.historyLensesUsed = 1
                    lensData.historyTotalUsage = lensData.timesUsed.size
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

            val history = JSONObject()
            history.put(KEY_FIRST_LENS_OPENED, lensData.historyFirstLensOpened.toString())
            history.put(KEY_LENSES_USED, lensData.historyLensesUsed)
            history.put(KEY_TOTAL_LENS_USAGE, lensData.historyTotalUsage)
            json.put(KEY_HISTORY, history)

            file.writeBytes(json.toString().toByteArray())
        }
    }
}