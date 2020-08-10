package de.tfsw.lenstracker

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MainActivity : AppCompatActivity(), DatePickerFragment.DatePickerFragmentListener {

    private val logTag = "MainActiviy"
    private val newLensesTag = "newLensesPicker"
    private val addUsageTag = "addUsagePicker"
    private val lensData = LensData()
    private var adapter = UsageViewAdapter(lensData.timesUsed)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        JsonUtil.readLensDataFromFile(applicationContext.filesDir, lensData)

        updateUI()

        val usageListView = findViewById<RecyclerView>(R.id.usageListView)
        usageListView.adapter = adapter
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        usageListView.layoutManager = llm
    }

    fun newLensesClicked(view: View) {
        showDatePicker(newLensesTag)
    }

    fun addUsageClicked(view: View) {
        showDatePicker(addUsageTag)
    }

    fun deleteUsageClicked(view: View) {
        val index: Int = view.tag as Int
        lensData.timesUsed.removeAt(index)
        saveDataAndRefreshUI()
    }

    private fun showDatePicker(tag: String?) {
        if (!tag.isNullOrEmpty()) {
            val newFragment = DatePickerFragment()
            newFragment.listener = this
            newFragment.show(supportFragmentManager, tag)
        }
    }

    override fun dateSelected(date: LocalDate, tag: String?) {
        if (validateDateSelection(date, tag)) {
            updateValues(date, tag)
        }
    }

    private fun validateDateSelection(date: LocalDate, tag: String?): Boolean {
        var validationPassed = true
        if (date.isAfter(LocalDate.now())) {
            showValidationErrorDialog(R.string.validationErrorDateFuture, tag)
            validationPassed = false
        } else if (addUsageTag == tag && date.isBefore(lensData.dateOpened)) {
            showValidationErrorDialog(R.string.validationErrorUsageBeforeOpening, tag)
            validationPassed = false
        }

        return validationPassed
    }

    private fun showValidationErrorDialog(messageId: Int, tag: String?) {
        AlertDialog.Builder(this)
            .setTitle(R.string.validationErrorTitle)
            .setMessage(messageId)
            .setCancelable(false)
            .setPositiveButton(R.string.validationDialogOK) { _, _ ->  showDatePicker(tag)}
            .show()
    }

    private fun updateValues(date: LocalDate, tag: String?) {
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
            saveDataAndRefreshUI()
        }

    }

    private fun saveDataAndRefreshUI() {
        lensData.timesUsed.sort()
        JsonUtil.saveLensDataToFile(applicationContext.filesDir, lensData)
        updateUI()

        Snackbar.make(
            findViewById(R.id.myCoordinatorLayout),
            R.string.dataSavedMessage,
            Snackbar.LENGTH_SHORT
        ).show()
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

        adapter.notifyDataSetChanged()
    }
}