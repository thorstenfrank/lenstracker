package de.tfsw.lenstracker

import android.animation.Animator
import android.util.Log
import android.view.View
import android.widget.LinearLayout

class FabAnimationListener(private val layouts: Array<LinearLayout>): Animator.AnimatorListener {

    var isFabOpen = false

    override fun onAnimationRepeat(animation: Animator?) {

    }

    override fun onAnimationEnd(animation: Animator?) {
        if (!isFabOpen) {
            Log.d("FabAnimationListener", "Closing FAB Menu")
            layouts.forEach { layout -> layout.visibility = View.GONE }
        }
    }

    override fun onAnimationCancel(animation: Animator?) {

    }

    override fun onAnimationStart(animation: Animator?) {

    }
}