package com.anapfoundation.covid_19volunteerapp.utils.extensions

import android.content.Context

fun Context.localized(int: Int):String{
    return this.resources.getText(int).toString()
}