package com.anapfoundation.covid_19volunteerapp.utils.extensions

import android.app.Activity

fun Activity.getName():String{
    return this::class.qualifiedName!!
}