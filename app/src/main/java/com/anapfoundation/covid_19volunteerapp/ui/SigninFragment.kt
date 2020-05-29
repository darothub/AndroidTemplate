package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.addCallback
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.paging.ReviewerUnapprovedReportsDataFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.user.UserViewModel
import com.anapfoundation.covid_19volunteerapp.helpers.IsEmptyCheck
import com.anapfoundation.covid_19volunteerapp.model.ProfileData
import com.anapfoundation.covid_19volunteerapp.model.User
import com.anapfoundation.covid_19volunteerapp.model.response.ReportResponse
import com.anapfoundation.covid_19volunteerapp.model.user.UserResponse
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import com.utsman.recycling.paged.setupAdapterPaged
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_report.*
import kotlinx.android.synthetic.main.fragment_report_home.*
import kotlinx.android.synthetic.main.fragment_signin.*
import kotlinx.android.synthetic.main.report_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class SigninFragment : DaggerFragment() {

    val title: String by lazy {
        getName()
    }
    val signupText: String by lazy {
        requireContext().getLocalisedString(R.string.signup_link)
    }
    val spannableString: SpannableString by lazy {
        signupText.setAsSpannable()
    }
    val color: Int by lazy {
        resources.getColor(R.color.colorPrimary)
    }

    private val textLen: Int by lazy {
        signupText.length
    }
    private val progressBar by lazy {
        signinBottomLayout.findViewById<ProgressBar>(R.id.includedProgressBar)
    }

    lateinit var signinBtn:Button

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    @Inject
    lateinit var storageRequest: StorageRequest

    @Inject
    lateinit var reviewerUnapprovedReportsDataFactory: ReviewerUnapprovedReportsDataFactory

    val userViewModel: UserViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(UserViewModel::class.java)
    }

    val authViewModel: AuthViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel::class.java)
    }

    var total = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        showStatusBar()
        return inflater.inflate(R.layout.fragment_signin, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



    }

    override fun onResume() {
        super.onResume()

        val localTime = Locale.getDefault().displayLanguage
        signinBtn = signinBottomLayout.findViewById<Button>(R.id.includeBtn)
        Log.i(title, "onResume $localTime")
        initEnterKeyToSubmitForm(signinPasswordEdit) { loginRequest() }
        submitLoginRequest()
        setButtonText()
    }

    override fun onPause() {
        super.onPause()
        Log.i(title, "onpause")
//        setButtonText()

    }


    override fun onStop() {
        super.onStop()
        Log.i(title, "onStop")
//        setButtonText()
    }
    override fun onStart() {
        super.onStart()
        Log.i(title, "onStart")
        setupSignUpLink()

        checkForReturninUser()
//        setButtonText()

        requireActivity().onBackPressedDispatcher.addCallback {
            requireActivity().finish()

        }
        forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.forgotPasswordFragment)
        }

    }


    private fun setButtonText() {
        signinBtn.setButtonText(requireContext().getLocalisedString(R.string.signin_text))
    }

    override fun onDetach() {
        super.onDetach()
        Log.i(title, "detached")
    }
    private fun submitLoginRequest() {
        signinBtn.setOnClickListener {
            loginRequest()
        }
    }

    private fun checkForReturninUser() {
        val returningUser = storageRequest.checkUser("loggedInUser")

        if (returningUser != null) {
            Log.i("returning", "user $returningUser")
//            requireContext().toast("$returningUser")
            when {
                returningUser.rememberPassword -> {
                    signinEmailEdit.setText(returningUser?.email)
                    signinPasswordEdit.setText(returningUser.password)
                }
            }


        }
    }

    fun setupSignUpLink() {
        val start = 23
        val end = textLen
        spannableString.enableClickOnSubstring(start, end) {
            findNavController().navigate(R.id.signupFragment)
        }
        spannableString.setColorToSubstring(color, start, end)
        spannableString.removeUnderLine(start, end)
        signupLink.text = spannableString
        signupLink.movementMethod = LinkMovementMethod.getInstance()
    }

    fun loginRequest() {
        val emailAddress = signinEmailEdit.text.toString().trim()
        val passwordString = signinPasswordEdit.text.toString()

        val checkForEmpty = IsEmptyCheck(signinEmailEdit, signinPasswordEdit)
        val validation = IsEmptyCheck.fieldsValidation(emailAddress, null)
        when {
            checkForEmpty != null -> {
                checkForEmpty.error = requireContext().getLocalisedString(R.string.field_required)
                requireActivity().toast("${checkForEmpty.hint} field is empty")
            }
            validation != null -> requireActivity().toast("$validation is invalid")
            else -> {
                val request = userViewModel.loginUserRequest(
                   emailAddress, passwordString
                )
                val response = observeRequest(request, progressBar, signinBtn)
                response.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    val (bool, result) = it
                    onRequestResponseTask(bool, result, emailAddress, passwordString)
                })
            }
        }

    }

    private fun onRequestResponseTask(
        bool: Boolean,
        result: Any?,
        emailAddress: String,
        passwordString: String
    ) {
        when (bool) {
            true -> {
                val res = result as UserResponse
                var userExist = User(null, null, emailAddress, passwordString, null)
                userExist.loggedIn = true
                userExist.token = res.data

                userExist.rememberPassword = signinCheckbox.isChecked

                checkIsReviewer(userExist)

                requireContext().toast(requireContext().getLocalisedString(R.string.successful))
                Log.i("UserExist", "${userExist}")
                Log.i(title, "message ${result.data}")

//                findNavController().popBackStack(R.id.reportFragment, false)
            }
            else -> {
                requireContext().toast("$result")
                Log.i(title, "error $result")
            }
        }
    }

    private fun checkIsReviewer(userExist: User) {
        val header = "Bearer ${userExist.token}"
        val request = authViewModel.getProfileData(header)
        val response = observeRequest(request, null, null)
        response.observe(viewLifecycleOwner, Observer {
            val (bool, result) = it
            when (bool) {
                true -> {
                    val res = result as ProfileData
                    val data = res.data
                    userExist.firstName = data.firstName
                    userExist.lastName = data.lastName
                    userExist.email = data.email
                    userExist.imageUrl = data.profileImageURL.toString()
                    userExist.houseNumber = data.houseNumber
                    userExist.street = data.street
                    userExist.lgName = data.lgName
                    userExist.lgID = data.lgID
                    userExist.stateID = data.stateID
                    userExist.stateName = data.stateName
                    userExist.zoneID = data.zoneID
                    userExist.districtID = data.districtID
                    userExist.totalReports = data.totalReports
                    userExist.id = data.id
                    Log.i("Local", "local ${data.lgName}")
                    when (data.isReviewer) {
                        true -> {
                            userExist.isReviewer = true
                            storageRequest.saveData(userExist, "loggedInUser")
                            navigateWithUri("android-app://anapfoundation.navigation/reportfrag".toUri())

                        }
                        false -> {
                            userExist.isReviewer = false
                            storageRequest.saveData(userExist, "loggedInUser")
                            navigateWithUri("android-app://anapfoundation.navigation/reportfrag".toUri())
                        }
                    }
                    Log.i(title, "Reviewer ${res.data.isReviewer}")
                }
                else -> Log.i(title, "error $result")
            }
        })
    }


}
