package de.tfsw.lenstracker.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import de.tfsw.lenstracker.LensData
import de.tfsw.lenstracker.R
import de.tfsw.lenstracker.storage.JsonUtil
import de.tfsw.lenstracker.storage.LensDataChangeListener
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class HomeFragment: Fragment(), DatePickerFragment.DatePickerFragmentListener, LensDataChangeListener {

    private val logTag = "HomeFragment"

    private val newLensesTag = "newLensesPicker"
    private val addUsageTag = "addUsagePicker"

    private val usageViewAdapter = UsageViewAdapter()

    private var fabOpen = false
    private var fabBGLayout: View? = null
    private var fabLayout1: LinearLayout? = null
    private var fabLayout2: LinearLayout? = null
    private var fab: FloatingActionButton? = null
    private var fab1: FloatingActionButton? = null
    private var fab2: FloatingActionButton? = null
    private var fabAnimationListener: FabAnimationListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        JsonUtil.addListener(this)

        initUsageList(root)
        initFAB(root)

        root.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { _ -> fabClicked() }
        root.findViewById<FloatingActionButton>(R.id.newLensesButton).setOnClickListener { _ -> newLensesClicked() }
        root.findViewById<FloatingActionButton>(R.id.addUsageButton).setOnClickListener { _ -> addUsageClicked() }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        updateUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        JsonUtil.removeListener(this)
    }

    override fun lensDataSaved() {
        updateUI()
    }

    private fun newLensesClicked() {
        closeFabMenu()
        showDatePicker(newLensesTag)
    }

    private fun addUsageClicked() {
        closeFabMenu()
        showDatePicker(addUsageTag)
    }

    private fun fabClicked() {
        if (!fabOpen) {
            showFabMenu()
        } else {
            closeFabMenu()
        }
    }

    private fun showDatePicker(tag: String) {
        val newFragment = DatePickerFragment(this)
        newFragment.show(requireActivity().supportFragmentManager, tag)
    }

    private fun updateValues(date: LocalDate, tag: String?) {
        var refresh = false
        when(tag) {
            newLensesTag -> {
                LensData.newLens(date)
                refresh = true
            }
            addUsageTag -> {
                LensData.addLensUsage(date)
                refresh = true

            }
            else -> Log.w(logTag, "Unknown tag encountered: $tag")
        }

        if (refresh) {
            saveDataAndRefreshUI()
        }
    }

    private fun saveDataAndRefreshUI() {
        LensData.timesUsed.sort()
        JsonUtil.saveLensDataToFile(requireActivity().applicationContext.filesDir)

        Snackbar.make(
            requireActivity().findViewById(R.id.container),
            R.string.dataSavedMessage,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun updateUI() {
        if (view != null) {
            val inUseSinceText: TextView = requireView().findViewById(R.id.textInUseSince)

            if (LensData.dateOpened == null) {
                inUseSinceText.text = getString(R.string.noLensesInUse)
            } else {
                val numberOfDays = Period.between(LensData.dateOpened, LocalDate.now()).days
                inUseSinceText.text = resources.getQuantityString(
                    R.plurals.textInUseSince,
                    numberOfDays,
                    LensData.dateOpened?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                    numberOfDays
                )
            }

            val timesUsedText: TextView = requireView().findViewById(R.id.textTimesUsed)
            val timesUsed = LensData.timesUsed.size
            timesUsedText.text = resources.getQuantityString(R.plurals.textTimesUsed, timesUsed, timesUsed)

            usageViewAdapter.notifyDataSetChanged()
        }
    }

    override fun dateSelected(date: LocalDate, tag: String) {
        if (validateDateSelection(date, tag)) {
            updateValues(date, tag)
        }
    }

    private fun validateDateSelection(date: LocalDate, tag: String): Boolean {
        var validationPassed = true
        if (date.isAfter(LocalDate.now())) {
            showValidationErrorDialog(R.string.validationErrorDateFuture, tag)
            validationPassed = false
        } else if (addUsageTag == tag && date.isBefore(LensData.dateOpened)) {
            showValidationErrorDialog(R.string.validationErrorUsageBeforeOpening, tag)
            validationPassed = false
        }

        return validationPassed
    }

    private fun showValidationErrorDialog(messageId: Int, tag: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.validationErrorTitle)
            .setMessage(messageId)
            .setCancelable(false)
            .setPositiveButton(R.string.OK) { _, _ ->  showDatePicker(tag)}
            .show()
    }

    private fun initUsageList(view: View) {
        val usageListView = view.findViewById<RecyclerView>(R.id.usageListView)
        usageListView.adapter = usageViewAdapter
        val llm = LinearLayoutManager(requireContext())
        llm.orientation = LinearLayoutManager.VERTICAL
        usageListView.layoutManager = llm
    }

    private fun initFAB(view: View) {
        fabBGLayout = view.findViewById(R.id.fabBGLayout)
        fabBGLayout?.setOnClickListener { _ -> closeFabMenu() }

        fab = view.findViewById(R.id.fab)

        fabLayout1 = view.findViewById(R.id.fabLayout1)
        fab1 = view.findViewById(R.id.newLensesButton)

        fabLayout2 = view.findViewById(R.id.fabLayout2)
        fab2 = view.findViewById(R.id.addUsageButton)

        fabAnimationListener = FabAnimationListener(
            arrayOf(
                fabLayout1!!,
                fabLayout2!!
            )
        )
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

}