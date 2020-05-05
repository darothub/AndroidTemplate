package com.anapfoundation.covid_19volunteerapp.utils.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.model.CityClass
import kotlinx.android.synthetic.main.fragment_edit_profile.*

fun Fragment.getName():String{
    return this::class.qualifiedName!!
}

fun Fragment.showStatusBar(){
    requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    if (currentFocus == null) View(this) else currentFocus?.let { hideKeyboard(it) }
}

@SuppressLint("ServiceCast")
fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.toast(message:String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.setSpinnerAdapterData(spinnerOne:Spinner, spinnerTwo:Spinner, stateLgaMap:HashMap<String, List<CityClass>> ) {

    val newList = arrayListOf<String>()
    newList.add("States")

    newList.addAll(stateLgaMap.keys.toSortedSet())

    val adapterState =
        ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, newList)
    spinnerOne.adapter = adapterState
    spinnerOne.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            TODO("Not yet implemented")
        }

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            val context:Context = spinnerOne.context
            val lga = ArrayList<String>()
            stateLgaMap.get(newList[position])!!.toList().mapTo(lga, {
                it.name
            })

            val adapterLga = ArrayAdapter(
                context,
                R.layout.support_simple_spinner_dropdown_item,
                lga
            )
            spinnerTwo.adapter = adapterLga
        }

    }
}