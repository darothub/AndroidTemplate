package com.anapfoundation.covid_19volunteerapp.utils.extensions

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.anapfoundation.covid_19volunteerapp.R

fun Fragment.getName():String{
    return this::class.qualifiedName!!
}
