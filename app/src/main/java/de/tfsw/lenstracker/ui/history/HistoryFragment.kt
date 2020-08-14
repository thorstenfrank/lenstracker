package de.tfsw.lenstracker.ui.history

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import de.tfsw.lenstracker.LensData
import de.tfsw.lenstracker.R
import de.tfsw.lenstracker.storage.JsonUtil
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class HistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView = inflater.inflate(R.layout.fragment_history, container, false)
        updateUI(mView)
        return mView
    }

    private fun updateUI(mView: View) {
        var firstOpened = LensData.historyFirstLensOpened?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)) ?: ""
        mView.findViewById<TextView>(R.id.historyLabelFirstLensOpened).text =
            resources.getString(R.string.history_label_first_opened, firstOpened)

        mView.findViewById<TextView>(R.id.historyLabelTotalLensesOpened).text =
            resources.getString(R.string.history_label_total_lenses, LensData.historyLensesUsed)

        mView.findViewById<TextView>(R.id.historyLabelTotalLensUsage).text =
            resources.getString(R.string.history_label_total_usage, LensData.historyTotalUsage)

        val resetButton = mView.findViewById<Button>(R.id.historyResetButton)
        resetButton.setOnClickListener(View.OnClickListener { _ -> showConfirmResetButton() })
    }

    private fun showConfirmResetButton() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.history_reset_confirm_title)
            .setMessage(R.string.history_reset_confirm_message)
            .setPositiveButton(R.string.OK) { _, _ -> resetHistory() }
            .setNegativeButton(R.string.Cancel) {_, _  -> } // do nothing...
            .show()
    }

    private fun resetHistory() {
        LensData.historyFirstLensOpened = LensData.dateOpened
        if (LensData.historyFirstLensOpened == null) {
            LensData.historyLensesUsed = 0
        } else {
            LensData.historyLensesUsed = 1
        }
        LensData.historyTotalUsage = LensData.timesUsed.size

        JsonUtil.saveLensDataToFile(requireContext().filesDir)
        updateUI(requireView())
    }
}