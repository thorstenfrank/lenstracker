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

        readLensData()

        updateUI()
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
        if (newLensesTag.equals(tag)) {
            lensData.dateOpened = date
            lensData.timesUsed.clear()
            updateUI()
        } else if (addUsageTag.equals(tag)) {
            lensData.timesUsed.add(date)
            updateUI()
        } else {
            Log.w(logTag, "Unknown tag encountered: $tag")
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

    private fun readLensData() {
        lensData.dateOpened = LocalDate.of(2020, 8, 2)
        lensData.timesUsed.add(LocalDate.of(2020, 8, 1))
        lensData.timesUsed.add(LocalDate.of(2020, 8, 3))
        lensData.timesUsed.add(LocalDate.of(2020, 8, 4))
        lensData.timesUsed.add(LocalDate.of(2020, 8, 7))
    }
}