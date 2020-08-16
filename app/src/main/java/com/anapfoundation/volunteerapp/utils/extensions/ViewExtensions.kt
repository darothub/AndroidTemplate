package com.anapfoundation.volunteerapp.utils.extensions

import android.view.View
import androidx.navigation.findNavController

/**
 * Hide view
 *
 */
fun View.hide(){
    this.visibility = View.GONE
}

/**
 * Show view
 *
 */
fun View.show(){
    this.visibility = View.VISIBLE
}

/**
 * Pop back stack
 *
 */
fun View.setBackButtonNavigation(){
    this.setOnClickListener {
        findNavController().popBackStack()
    }

}