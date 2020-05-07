package com.anapfoundation.covid_19volunteerapp.utils.extensions

import androidx.lifecycle.ViewModel

fun ViewModel.getName():String{
    return this::class.qualifiedName!!
}