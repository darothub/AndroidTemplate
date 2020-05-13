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
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.user.UserViewModel
import com.anapfoundation.covid_19volunteerapp.model.CityClass
import com.anapfoundation.covid_19volunteerapp.model.LGA
import com.anapfoundation.covid_19volunteerapp.model.response.Data
import com.anapfoundation.covid_19volunteerapp.services.ServicesResponseWrapper
import java.lang.Exception

inline fun Fragment.getName():String{
    return this::class.qualifiedName!!
}

inline fun Fragment.showStatusBar(){
    requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
}

inline fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

inline fun Activity.hideKeyboard() {
    if (currentFocus == null) View(this) else currentFocus?.let { hideKeyboard(it) }
}

@SuppressLint("ServiceCast")
inline fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

inline fun Context.toast(message:String){
    val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
    toast.setGravity(Gravity.CENTER, 0, 0)
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

inline fun Context.setSpinnerAdapterData(spinnerOne:Spinner, spinnerTwo:Spinner, stateLgaMap:HashMap<String, List<CityClass>> ) {

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

inline fun Fragment.observeRequest(request: LiveData<ServicesResponseWrapper<Data>>,
                                   progressBar: ProgressBar?, button: Button?
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
                progressBar?.show()
                button?.hide()
                Log.i(title, "Loading..")
            }
            is ServicesResponseWrapper.Success -> {
                progressBar?.hide()
                button?.show()
                result.postValue(Pair(true, responseData))
//                requireContext().toast(requireContext().localized(R.string.successful))
                Log.i(title, "success ${it.data}")
            }
            is ServicesResponseWrapper.Error -> {
                progressBar?.hide()
                button?.show()
                result.postValue(Pair(false, errorResponse))
                requireContext().toast("$errorResponse")
                Log.i(title, "Error $errorResponse")
            }
            is ServicesResponseWrapper.Logout ->{
                progressBar?.hide()
                button?.show()
                requireContext().toast("$errorResponse")
                Log.i(title, "Log out $errorResponse")
                val request = NavDeepLinkRequest.Builder
                    .fromUri("android-app://anapfoundation.navigation/signin".toUri())
                    .build()
                findNavController().navigate(request)
            }
        }
    })

    return result
}

inline fun Fragment.initEnterKeyToSubmitForm(editText: EditText, crossinline request:()->Unit) {
    editText.setOnKeyListener { view, keyCode, keyEvent ->
        if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

            request()
            return@setOnKeyListener true
        }
        return@setOnKeyListener false
    }
}

fun Fragment.setLGASpinner(spinnerState:Spinner, spinnerLGA:Spinner, lgaAndDistrict:HashMap<String, String>,
                           states:HashMap<String, String>, userViewModel: UserViewModel
) {
    spinnerState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            TODO("Not yet implemented")
        }

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            lgaAndDistrict.clear()
            val selectedState = spinnerState.selectedItem
            val stateID = states.get(selectedState)
            val request = userViewModel.getLocal(stateID.toString(), "47", "")
            val response = observeRequest(request, null, null)
            response.observe(viewLifecycleOwner, Observer {
                try {
                    val (bool, result) = it
                    val res = result as LGA
                    res.data.associateByTo(lgaAndDistrict, {
                        it.localGovernment
                    }, {
                        "${it.id} ${it.district}"
                    })
                    val lga = lgaAndDistrict.keys.sorted()
                    Log.i("$this", "LGA $lga")
                    val adapterLga = ArrayAdapter(
                        requireContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        lga
                    )
                    spinnerLGA.adapter = adapterLga

                } catch (e: Exception) {

                }
            })


        }

    }
}
