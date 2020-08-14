package de.tfsw.lenstracker

import java.time.LocalDate

object LensData {
    var version: Int = 0
    var dateOpened: LocalDate? = null
    // TODO make this private and restrict outside access to read-only
    var timesUsed: MutableList<LocalDate> = ArrayList()
    var historyFirstLensOpened: LocalDate? = null
    var historyLensesUsed: Int = 0
    var historyTotalUsage: Int = 0

    fun newLens(opened: LocalDate) {
        dateOpened = opened
        historyLensesUsed++
        timesUsed.clear()
    }

    fun addLensUsage(used: LocalDate) {
        timesUsed.add(used)
        historyTotalUsage++
    }

    fun deleteLensUsage(index: Int) {
        timesUsed.removeAt(index)
        historyTotalUsage--
    }
}