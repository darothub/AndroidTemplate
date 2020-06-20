package com.anapfoundation.volunteerapp.utils.extensions

import androidx.fragment.app.Fragment


fun Fragment.getLocalisedString(resId: Int):String{
    return getString(resId)
}