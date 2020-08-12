package de.tfsw.lenstracker

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

    // FAB
    private var fabOpen = false
    private var fabBGLayout: View? = null
    private var fabLayout1: LinearLayout? = null
    private var fabLayout2: LinearLayout? = null
    private var fab: FloatingActionButton? = null
    private var fab1: FloatingActionButton? = null
    private var fab2: FloatingActionButton? = null
    private var fabAnimationListener: FabAnimationListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        JsonUtil.readLensDataFromFile(applicationContext.filesDir, lensData)

        val usageListView = findViewById<RecyclerView>(R.id.usageListView)
        usageListView.adapter = adapter
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        usageListView.layoutManager = llm

        initFAB()

        updateUI()
    }

    override fun onBackPressed() {
        when(fabOpen) {
            true -> closeFabMenu()
            else -> super.onBackPressed()
        }
    }

    private fun initFAB() {
        fabBGLayout = findViewById(R.id.fabBGLayout)
        fabBGLayout?.setOnClickListener { _ -> closeFabMenu() }

        fab = findViewById(R.id.fab)

        fabLayout1 = findViewById(R.id.fabLayout1)
        fab1 = findViewById(R.id.fab1)

        fabLayout2 = findViewById(R.id.fabLayout2)
        fab2 = findViewById(R.id.fab2)

        fabAnimationListener = FabAnimationListener(arrayOf(fabLayout1!!, fabLayout2!!))
    }

    fun newLensesClicked(view: View) {
        closeFabMenu()
        showDatePicker(newLensesTag)
    }

    fun addUsageClicked(view: View) {
        closeFabMenu()
        showDatePicker(addUsageTag)
    }

    fun deleteUsageClicked(view: View) {
        showConfirmDeleteUsageDialog(view.tag as Int)
    }

    fun fabClicked(view: View) {
        if (!fabOpen) {
            showFabMenu()
        } else {
            closeFabMenu()
        }
    }

    private fun showFabMenu() {
        fabOpen = true
        fabAnimationListener?.isFabOpen = fabOpen
        fabBGLayout?.visibility = View.VISIBLE
        fabLayout1?.visibility = View.VISIBLE
        fabLayout2?.visibility = View.VISIBLE

        fab?.animate()?.rotationBy(180F)

        fabLayout1?.animate()?.translationY(10F)
        fabLayout2?.animate()?.translationY(20F)
    }

    private fun closeFabMenu() {
        fabOpen = false
        fabBGLayout?.visibility = View.GONE
        fab?.animate()?.rotation(0F)
        fabAnimationListener?.isFabOpen = fabOpen
        fabLayout1?.animate()?.translationY(0F)
        fabLayout2?.animate()?.translationY(0F)?.setListener(fabAnimationListener)
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
            .setPositiveButton(R.string.OK) { _, _ ->  showDatePicker(tag)}
            .show()
    }

    private fun showConfirmDeleteUsageDialog(index: Int) {
        val message = resources.getString(
            R.string.confirmDeleteDialogMessage,
            lensData.timesUsed[index].format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))

        AlertDialog.Builder(this)
            .setTitle(R.string.confirmDeleteDialogTitle)
            .setMessage(message)
            .setPositiveButton(R.string.OK) { _, _: Int -> deleteTimeUsed(index) }
            .setNegativeButton(R.string.Cancel) {_, _  -> } // do nothing...
            .show()
    }

    private fun deleteTimeUsed(index: Int) {
        lensData.timesUsed.removeAt(index)
        saveDataAndRefreshUI()
    }

    private fun updateValues(date: LocalDate, tag: String?) {
        var refresh = false
        when(tag) {
            newLensesTag -> {
                lensData.newLens(date)
                refresh = true
            }
            addUsageTag -> {
                lensData.addLensUsage(date)
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
                lensData.dateOpened?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                numberOfDays
            )
        }

        val timesUsedText: TextView = findViewById(R.id.textTimesUsed)
        val timesUsed = lensData.timesUsed.size
        timesUsedText.text = resources.getQuantityString(R.plurals.textTimesUsed, timesUsed, timesUsed)

        adapter.notifyDataSetChanged()
    }
}
