package com.anapfoundation.covid_19volunteerapp.utils.extensions

import android.view.View
import androidx.navigation.findNavController

fun View.hide(){
    this.visibility = View.GONE
}

fun View.show(){
    this.visibility = View.VISIBLE
}

fun View.setBackButtonNavigation(){
    this.setOnClickListener {
        findNavController().popBackStack()
    }

}