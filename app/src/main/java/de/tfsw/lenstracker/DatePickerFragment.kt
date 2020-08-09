package de.tfsw.lenstracker

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.time.LocalDate

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    var listener: DatePickerFragmentListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val now = LocalDate.now();

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(activity!!, this, now.year, now.monthValue - 1, now.dayOfMonth)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val dateSelected = LocalDate.of(year, month + 1, day)
        Log.d("DatePickerFragment", "date selected: " + dateSelected.toString())
        listener?.dateSelected(dateSelected, tag)
    }

    interface DatePickerFragmentListener {
        fun dateSelected(date: LocalDate, tag: String?)
    }
}
