package de.tfsw.lenstracker.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.tfsw.lenstracker.LensData
import de.tfsw.lenstracker.R

class UsageViewAdapter: RecyclerView.Adapter<UsageViewHolder>() {

    override fun getItemCount(): Int {
        return LensData.timesUsed.size
    }

    override fun onBindViewHolder(holder: UsageViewHolder, position: Int) {
        holder.updateText(LensData.timesUsed[position])
        holder.setItemIndex(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsageViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.usage_list_item, parent,false)

        return UsageViewHolder(itemView)
    }
}