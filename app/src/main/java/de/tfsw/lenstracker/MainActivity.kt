package de.tfsw.lenstracker

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MainActivity : AppCompatActivity(), DatePickerFragment.DatePickerFragmentListener {

    private val logTag = "MainActiviy"
    private val newLensesTag = "newLensesPicker"
    private val addUsageTag = "addUsagePicker"
    private val lensData = LensData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        JsonUtil.readLensDataFromFile(applicationContext.filesDir, lensData)

        updateUI()

        Log.d(logTag, "App Context File Dir: " + applicationContext.filesDir)
        Log.d(logTag, "Base Context File Dir: " + baseContext.filesDir)
    }

    fun newLensesClicked(view: View) {
        val newFragment = DatePickerFragment()
        newFragment.listener = this
        newFragment.show(supportFragmentManager, newLensesTag)
    }

    fun addUsageClicked(view: View) {
        val newFragment = DatePickerFragment()
        newFragment.listener = this
        newFragment.show(supportFragmentManager, addUsageTag)
    }

    override fun dateSelected(date: LocalDate, tag: String?) {
        Log.d(logTag, "Date selected for tag $tag: " + date.toString())
        var refresh = false
        when(tag) {
            newLensesTag -> {
                lensData.dateOpened = date
                lensData.timesUsed.clear()
                refresh = true
            }
            addUsageTag -> {
                lensData.timesUsed.add(date)
                refresh = true

            }
            else -> Log.w(logTag, "Unknown tag encountered: $tag")
        }

        if (refresh) {
            JsonUtil.saveLensDataToFile(applicationContext.filesDir, lensData)
            updateUI()
        }
    }

    private fun updateUI() {
        val inUseSinceText: TextView = findViewById(R.id.textInUseSince)

        if (lensData.dateOpened == null) {
            inUseSinceText.text = getString(R.string.noLensesInUse)
        } else {
            val numberOfDays = Period.between(lensData.dateOpened, LocalDate.now()).days
            inUseSinceText.text = resources.getQuantityString(
                R.plurals.textInUseSince,
                numberOfDays,
                lensData.dateOpened?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)),
                numberOfDays
            )
        }

        val timesUsedText: TextView = findViewById(R.id.textTimesUsed)
        val timesUsed = lensData.timesUsed.size
        timesUsedText.text = resources.getQuantityString(R.plurals.textTimesUsed, timesUsed, timesUsed)
    }
}