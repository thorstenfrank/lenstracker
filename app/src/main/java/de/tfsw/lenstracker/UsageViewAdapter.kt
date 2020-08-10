package de.tfsw.lenstracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class UsageViewAdapter(private val dataset: List<LocalDate>): RecyclerView.Adapter<UsageViewHolder>() {

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: UsageViewHolder, position: Int) {
        holder.updateText(dataset[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsageViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.usage_list_item, parent,false)

        return UsageViewHolder(itemView)
    }
}