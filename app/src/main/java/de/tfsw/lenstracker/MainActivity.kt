package de.tfsw.lenstracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.TemporalAmount

class MainActivity : AppCompatActivity() {

    private val logTag = "MainActiviy"
    private val lensData = LensData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        readLensData()

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

    override fun onPause() {
        Log.d(logTag, "*** onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(logTag, "*** onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(logTag, "*** onDestroy")
        super.onDestroy()
    }

    private fun readLensData() {
        lensData.dateOpened = LocalDate.of(2020, 8, 2)
        lensData.timesUsed.add(LocalDate.of(2020, 8, 1))
        lensData.timesUsed.add(LocalDate.of(2020, 8, 3))
        lensData.timesUsed.add(LocalDate.of(2020, 8, 4))
        lensData.timesUsed.add(LocalDate.of(2020, 8, 7))
    }
}