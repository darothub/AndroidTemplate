package com.anapfoundation.covid_19volunteerapp.utils.extensions

import androidx.fragment.app.Fragment

fun Fragment.getName():String{
    return this::class.qualifiedName!!
}