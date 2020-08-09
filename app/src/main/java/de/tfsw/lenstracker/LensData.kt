package de.tfsw.lenstracker

import java.time.LocalDate

class LensData {
    var version: Int = 0
    var dateOpened: LocalDate? = null
    var timesUsed: MutableList<LocalDate> = ArrayList()
}