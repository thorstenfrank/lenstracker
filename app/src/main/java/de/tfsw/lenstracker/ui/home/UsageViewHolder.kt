package de.tfsw.lenstracker.ui.home

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.tfsw.lenstracker.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class UsageViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val textView: TextView = view.findViewById(R.id.usageListItemTextView)
    private val deleteButton: ImageButton = view.findViewById(R.id.usageListItemDeleteButton)

    fun updateText(date: LocalDate) {
        textView.text = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
    }

    fun setItemIndex(index: Int) {
        deleteButton.tag = index
    }
}