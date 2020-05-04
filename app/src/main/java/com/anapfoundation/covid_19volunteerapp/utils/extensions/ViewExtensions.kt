package com.anapfoundation.covid_19volunteerapp.utils.extensions

import android.view.View

fun View.hide(){
    this.visibility = View.GONE
}

fun View.show(){
    this.visibility = View.VISIBLE
}