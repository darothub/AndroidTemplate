package com.anapfoundation.covid_19volunteerapp.utils.extensions

import android.view.View
import androidx.navigation.findNavController

inline fun View.hide(){
    this.visibility = View.GONE
}

inline fun View.show(){
    this.visibility = View.VISIBLE
}

inline fun View.setBackButtonNavigation(){
    this.setOnClickListener {
        findNavController().popBackStack()
    }

}