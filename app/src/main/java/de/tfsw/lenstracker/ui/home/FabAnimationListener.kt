package de.tfsw.lenstracker.ui.home

import android.animation.Animator
import android.view.View
import android.widget.LinearLayout

class FabAnimationListener(private val layouts: Array<LinearLayout>): Animator.AnimatorListener {

    var isFabOpen = false

    override fun onAnimationStart(animation: Animator?) {}

    override fun onAnimationEnd(animation: Animator?) {
        if (!isFabOpen) {
            layouts.forEach { layout -> layout.visibility = View.GONE }
        }
    }

    override fun onAnimationRepeat(animation: Animator?) {}

    override fun onAnimationCancel(animation: Animator?) {}


}