package com.anapfoundation.covid_19volunteerapp.helpers

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton



class ToggleableRadioButton(
    context: Context?,
    attrs: AttributeSet?
) :
    AppCompatRadioButton(context, attrs) {
    override fun toggle() {
        isChecked = !isChecked
    }
}