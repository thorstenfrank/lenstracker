package de.tfsw.lenstracker

import java.time.LocalDate

class LensData {
    var version: Int = 0
    var dateOpened: LocalDate? = null
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
}