package com.anapfoundation.covid_19volunteerapp.utils.extensions

import android.content.Context

inline fun Context.getLocalisedString(int: Int):String{
    return this.resources.getText(int).toString()
}