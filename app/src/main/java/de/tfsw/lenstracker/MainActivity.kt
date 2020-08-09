package de.tfsw.lenstracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private val TAG: String = "MainActiviy"

    var lensData: LensData = LensData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readLensData()
        setContentView(R.layout.activity_main)
    }

    override fun onPause() {
        Log.d(TAG, "*** onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "*** onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "*** onDestroy")
        super.onDestroy()
    }

    fun readLensData() {
        lensData.dateOpened = LocalDate.of(2020, 8, 1)
        lensData.timesUsed.add(LocalDate.of(2020, 8, 1))
        lensData.timesUsed.add(LocalDate.of(2020, 8, 3))
        lensData.timesUsed.add(LocalDate.of(2020, 8, 4))
        lensData.timesUsed.add(LocalDate.of(2020, 8, 7))
        // TODO actually read from storage
    }
}