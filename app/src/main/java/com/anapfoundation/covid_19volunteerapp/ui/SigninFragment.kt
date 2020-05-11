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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.user.UserViewModel
import com.anapfoundation.covid_19volunteerapp.helpers.IsEmptyCheck
import com.anapfoundation.covid_19volunteerapp.model.User
import com.anapfoundation.covid_19volunteerapp.model.servicesmodel.ServiceResult
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_signin.*
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
    private val signinBtn by lazy {
        signinBottomLayout.findViewById<Button>(R.id.includeBtn)
    }

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    @Inject
    lateinit var storageRequest: StorageRequest

    val userViewModel: UserViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(UserViewModel::class.java)
    }

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

        setupSignUpLink()
        initEnterKeyToSubmitForm(signinPasswordEdit) { loginRequest() }
        checkForReturninUser()
        signinBtn.setButtonText(requireContext().getLocalisedString(R.string.signin_text))
        submitLoginRequest()
        requireActivity().onBackPressedDispatcher.addCallback {
            requireActivity().finish()


        }


    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
        Log.i(title, "onpause")
    }


    override fun onStart() {
        super.onStart()
        Log.i(title, "onStart")
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
        val returningUser = storageRequest.checkUser("loggedOutUser")
        signinEmailEdit.setText(returningUser?.email)


        if (returningUser != null) {
            Log.i("returning", "user $returningUser")
//            requireContext().toast("$returningUser")
            when {
                returningUser.rememberPassword -> signinPasswordEdit.setText(returningUser.password)
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
        val passwordString = signinPasswordEdit.text.toString().trim()

        val checkForEmpty = IsEmptyCheck(signinEmailEdit, signinPasswordEdit)
        val validation = IsEmptyCheck.fieldsValidation(emailAddress, passwordString)
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
                val res = result as ServiceResult
                var userExist = storageRequest.checkUser(emailAddress)
                if (userExist != null) {
                    userExist?.loggedIn = true
                    userExist?.token = res.token
                } else {
                    userExist = User(
                        "Not found", "Not found",
                        emailAddress, passwordString, "Not found"
                    )
                }
                requireContext().toast(requireContext().getLocalisedString(R.string.successful))
                userExist?.rememberPassword = signinCheckbox.isChecked
                storageRequest.saveData(userExist, emailAddress)
                storageRequest.saveData(userExist, "loggedInUser")
                Log.i("UserExist", "${userExist}")
                Log.i(title, "message ${result.token}")
                findNavController().navigate(R.id.reportFragment)
            }
            else -> Log.i(title, "error $result")
        }
    }


}
