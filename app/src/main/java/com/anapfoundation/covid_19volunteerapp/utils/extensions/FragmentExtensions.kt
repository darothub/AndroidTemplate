package com.anapfoundation.covid_19volunteerapp.utils.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.model.CityClass
import com.anapfoundation.covid_19volunteerapp.model.servicesmodel.ServiceResult
import com.anapfoundation.covid_19volunteerapp.services.ServicesResponseWrapper
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_signup.*

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
    val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
    val view = toast.view.findViewById<TextView>(android.R.id.message)
    when {
        view != null -> {
            view.gravity = Gravity.CENTER
            toast.show()
        }
        else ->{
            toast.show()
        }
    }
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

fun Fragment.observeRequest(request: LiveData<ServicesResponseWrapper<ServiceResult>>,
                            progressBar: ProgressBar, button: Button
): LiveData<Pair<Boolean, Any?>> {
    val result = MutableLiveData<Pair<Boolean, Any?>>()
    val title:String by lazy{
        this.getName()
    }

    hideKeyboard()
    request.observe(viewLifecycleOwner, Observer {
        val responseData = it.data
        val errorResponse = it.message
        when (it) {
            is ServicesResponseWrapper.Loading<*> -> {
                progressBar.show()
                button.hide()
                Log.i(title, "Loading..")
            }
            is ServicesResponseWrapper.Success -> {
                progressBar.hide()
                button.show()
                result.postValue(Pair(true, responseData))
                requireContext().toast(requireContext().localized(R.string.successful))
                Log.i(title, "success ${it.data}")
            }
            is ServicesResponseWrapper.Error -> {
                progressBar.hide()
                button.show()
                result.postValue(Pair(false, errorResponse))
                requireContext().toast("$errorResponse")
                Log.i(title, "Error $errorResponse")
            }
        }
    })

    return result
}

fun initEnterKeyToSubmitForm(editText: EditText, request:()->Unit) {
    editText.setOnKeyListener { view, keyCode, keyEvent ->
        if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

            request()
            return@setOnKeyListener true
        }
        return@setOnKeyListener false
    }
}