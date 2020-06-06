package com.anapfoundation.covid_19volunteerapp.utils.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
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
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.paging.ReviewerUnapprovedReportsDataFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.user.UserViewModel
import com.anapfoundation.covid_19volunteerapp.model.CityClass
import com.anapfoundation.covid_19volunteerapp.model.LGA
import com.anapfoundation.covid_19volunteerapp.model.User
import com.anapfoundation.covid_19volunteerapp.model.response.Data
import com.anapfoundation.covid_19volunteerapp.model.response.ReportResponse
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.services.ServicesResponseWrapper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tfb.fbtoast.FBCustomToast
import com.utsman.recycling.paged.setupAdapterPaged
import kotlinx.android.synthetic.main.fragment_reviewer_screen.*
import kotlinx.android.synthetic.main.report_item.view.*


/**
 * show status bar
 *
 */
 fun Fragment.showStatusBar() {
    requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    requireActivity().window.statusBarColor = resources.getColor(R.color.colorNeutral)
}

 fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

 fun Activity.hideKeyboard() {
    if (currentFocus == null) View(this) else currentFocus?.let { hideKeyboard(it) }
}

/**
 * Hide keyboard
 *
 * @param view
 */
@SuppressLint("ServiceCast")
fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * Show toast message
 *
 * @param message
 */
fun Context.toast(message: String) {
    val toastie = FBCustomToast(this)
    toastie.setMsg(message)
    toastie.setIcon(resources.getDrawable(R.drawable.applogo, theme))
    toastie.setGravity(Gravity.CENTER_VERTICAL)

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        toastie.setBackgroundColor(resources.getColor(R.color.colorNeutral, theme))
        toastie.setToastMsgColor(resources.getColor(R.color.colorPrimaryDark, theme))
    } else {
        toastie.setBackgroundColor(resources.getColor(R.color.colorNeutral))
        toastie.setToastMsgColor(resources.getColor(R.color.colorPrimaryDark))
    }
    toastie.show()

}


/**
 * Display error toast
 *
 * @param message
 */
fun Context.errorToast(message: String) {
    val toastie = FBCustomToast(this)
    toastie.setMsg(message)
    toastie.setIcon(resources.getDrawable(R.drawable.bad, theme))
    toastie.setGravity(Gravity.CENTER_VERTICAL)

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        toastie.setBackgroundColor(resources.getColor(R.color.errorRed, theme))
        toastie.setToastMsgColor(resources.getColor(R.color.colorNeutral, theme))
    } else {
        toastie.setBackgroundColor(resources.getColor(R.color.errorRed))
        toastie.setToastMsgColor(resources.getColor(R.color.colorNeutral))
    }
    toastie.show()

}

inline fun Context.setSpinnerAdapterData(
    spinnerOne: Spinner,
    spinnerTwo: Spinner,
    stateLgaMap: HashMap<String, List<CityClass>>
) {

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
            val context: Context = spinnerOne.context
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

/**
 * Observe request response
 * and manipulate progressbar
 * and button behaviour
 *
 * @param request
 * @param progressBar
 * @param button
 * @return
 */
inline fun Fragment.observeRequest(
    request: LiveData<ServicesResponseWrapper<Data>>,
    progressBar: ProgressBar?, button: Button?
): LiveData<Pair<Boolean, Any?>> {
    val result = MutableLiveData<Pair<Boolean, Any?>>()
    val title: String by lazy {
        this.getName()
    }

    hideKeyboard()
    request.observe(viewLifecycleOwner, Observer {
        try {
            val responseData = it.data
            val errorResponse = it.message
            val errorCode = it.code
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
                    when(errorCode){
                        0 -> {
                            Log.i(title, "Errorcode ${errorCode}")
                            requireContext().errorToast(requireContext().getLocalisedString(R.string.bad_network))
                        }
                        in 500..600 ->{
                            requireContext().errorToast(requireContext().getLocalisedString(R.string.server_error))
                        }
                        else ->{
                            result.postValue(Pair(false, errorResponse))
                            requireContext().toast("$errorResponse")
                        }
                    }

                    Log.i(title, "Error ${it.message}")
                }
                is ServicesResponseWrapper.Logout -> {
                    progressBar?.hide()
                    button?.show()
                    result.postValue(Pair(false, errorResponse))
                    requireContext().toast("$errorResponse")
                    Log.i(title, "Log out $errorResponse")
                    navigateWithUri("android-app://anapfoundation.navigation/signin".toUri())
                }
            }
        } catch (e: Exception) {
            Log.i(title, e.localizedMessage)
        }

    })

    return result
}

/**
 * Set enter key for form submission
 *
 * @param editText
 * @param request
 */
inline fun Fragment.initEnterKeyToSubmitForm(editText: EditText, crossinline request: () -> Unit) {
    editText.setOnKeyListener { view, keyCode, keyEvent ->
        if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

            request()
            return@setOnKeyListener true
        }
        return@setOnKeyListener false
    }
}

/**
 * To set up local government spinner
 *
 * @param spinnerState
 * @param spinnerLGA
 * @param lgaAndDistrict
 * @param states
 * @param userViewModel
 * @param user
 * @param lgaHeader
 */
fun Fragment.setLGASpinner(
    spinnerState: Spinner,
    spinnerLGA: Spinner,
    lgaAndDistrict: HashMap<String, String>,
    states: HashMap<String, String>,
    userViewModel: UserViewModel,
    user: User? = null,
    lgaHeader: String? = null
) {

    var defaultList = arrayListOf<String>()
    if (user != null) {
        Log.i("Spinner", "${user.lgName.toString()}")
        defaultList.add(0, user.lgName.toString())
    } else if (lgaHeader != null) {
        defaultList.add(0, lgaHeader)
    }
    var adapterLga = ArrayAdapter(
        requireContext(),
        R.layout.support_simple_spinner_dropdown_item,
        defaultList
    )
    spinnerLGA.adapter = adapterLga

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
            val valueOfStateSelected = states.get(selectedState)?.split(" ")
            val stateID = valueOfStateSelected?.get(0).toString()
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
                    val lga = lgaAndDistrict.keys.sorted().toMutableList()
                    if (spinnerState.selectedItem == user?.stateName.toString()){
                        lga.add(0, user?.lgName.toString())
                        adapterLga = ArrayAdapter(
                            requireContext(),
                            R.layout.support_simple_spinner_dropdown_item,
                            lga
                        )
                        spinnerLGA.adapter = adapterLga
                    }else{
                        Log.i("$this", "LGA $lga")
                        adapterLga = ArrayAdapter(
                            requireContext(),
                            R.layout.support_simple_spinner_dropdown_item,
                            lga
                        )
                        spinnerLGA.adapter = adapterLga
                    }



                } catch (e: Exception) {

                }
            })


        }

    }
}

/**
 * Log out
 *
 * @param storageRequest
 * @param bottomSheetDialog
 */
fun Fragment.logout(storageRequest: StorageRequest, bottomSheetDialog: BottomSheetDialog? = null) {
    val user = storageRequest.checkUser("loggedInUser")
    user?.loggedIn = false
    user?.isReviewer = false
    user?.token = ""
    storageRequest.saveData(user, "loggedInUser")
    bottomSheetDialog?.dismiss()
    navigateWithUri("android-app://anapfoundation.navigation/signin".toUri())

}

fun Fragment.navigateWithUri(uri: Uri) {
    val request = NavDeepLinkRequest.Builder
        .fromUri(uri)
        .build()
    findNavController().navigate(request)
}

/**
 * Event listener for upload card or upload forward icon
 *
 * @param views
 * @param action
 */
fun Fragment.setOnClickEventForPicture(vararg views: View, action: () -> Unit) {
    for (view in views) {
        view.setOnClickListener {
            action()
        }
    }

}

/***
 * Display notification bell
 */
internal fun Fragment.displayNotificationBell(loggedInUser: User?,
   icon: ImageView, countTextView: TextView
) {
    when(loggedInUser?.isReviewer){
        true -> {
            icon.show()
            countTextView.show()
            countTextView.text = loggedInUser.totalUnapprovedReports.toString()
            icon.setOnClickListener {
                goto(R.id.reviewerScreenFragment)
            }
        }
    }
}


fun Fragment.getUnapprovedReportCounts(
    recyclerView: RecyclerView,
    dataFactory: ReviewerUnapprovedReportsDataFactory,
    authViewModel: AuthViewModel
):LiveData<Int> {
    var dataReturn = MutableLiveData<Int>()
    var total = 0
    recyclerView.setupAdapterPaged<ReportResponse>(R.layout.report_item) { adapter, context, list ->
        bind { itemView, position, item ->

            itemView.reportImage.transitionName = item?.mediaURL
            itemView.reportStory.text = item?.story

        }
        authViewModel.getUnapprovedReports(dataFactory).observe(viewLifecycleOwner, Observer {
            submitList(it)
        })

    }
    authViewModel.getUnapprovedReportCount(dataFactory).observe(viewLifecycleOwner, Observer {
        total += it
        dataReturn.postValue(total)
        Log.i("UnapprovedCount", "NewUnapprovedcount $")

    })
    return dataReturn
}

/**
 * Navigate to destination id
 *
 * @param destinationId
 */
fun Fragment.goto(destinationId: Int){
    findNavController().navigate(destinationId)
}

/**
 * Navigate to destination id
 *
 * @param destinationId
 */
fun Fragment.goto(destinationId: NavDirections){
    findNavController().navigate(destinationId)
}

/**
 * Navigate up
 *
 */
fun Fragment.gotoUp(){
    findNavController().navigateUp()
}
/**
 * Navigate to uri
 *
 * @param uri
 */
fun Fragment.goto(uri: Uri) {
    val request = NavDeepLinkRequest.Builder
        .fromUri(uri)
        .build()
    findNavController().navigate(request)
}
