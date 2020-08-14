package de.tfsw.lenstracker

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import de.tfsw.lenstracker.storage.JsonUtil
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        JsonUtil.readLensDataFromFile(applicationContext.filesDir)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration
            .Builder(setOf(R.id.navigation_home, R.id.navigation_history))
            .build()

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    /*override fun onBackPressed() {
        super.onBackPressed()
        when(fabOpen) {
            true -> closeFabMenu()
            else -> super.onBackPressed()
        }
    }*/

    fun deleteUsageClicked(view: View) {
        showConfirmDeleteUsageDialog(view.tag as Int)
    }

    private fun saveLensData() {
        LensData.timesUsed.sort()
        JsonUtil.saveLensDataToFile(applicationContext.filesDir)
        Snackbar.make(
            findViewById(R.id.container),
            R.string.dataSavedMessage,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun deleteTimeUsed(index: Int) {
        LensData.deleteLensUsage(index)
        saveLensData()
    }

    private fun showConfirmDeleteUsageDialog(index: Int) {
        val message = resources.getString(
            R.string.confirmDeleteDialogMessage,
            LensData.timesUsed[index].format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))

        AlertDialog.Builder(this)
            .setTitle(R.string.confirmDeleteDialogTitle)
            .setMessage(message)
            .setPositiveButton(R.string.OK) { _, _: Int -> deleteTimeUsed(index) }
            .setNegativeButton(R.string.Cancel) {_, _  -> } // do nothing...
            .show()
    }
}
