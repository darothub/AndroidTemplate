package com.anapfoundation.covid_19volunteerapp.ui

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.user.UserViewModel
import com.anapfoundation.covid_19volunteerapp.helpers.IsEmptyCheck
import com.anapfoundation.covid_19volunteerapp.model.User
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_signin.*
import kotlinx.android.synthetic.main.fragment_signup.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class SignupFragment : DaggerFragment() {

    val title: String by lazy {
        getName()
    }
    val signinText: String by lazy {
        requireContext().localized(R.string.signin_link)
    }
    val spannableString: SpannableString by lazy {
        signinText.setAsSpannable()
    }
    val color: Int by lazy {
        resources.getColor(R.color.colorPrimary)
    }
    val textLen: Int by lazy {
        signinText.length
    }
    val progressBar by lazy {
        signupBottom.findViewById<ProgressBar>(R.id.includedProgressBar)
    }
    val signupBtn by lazy {
        signupBottom.findViewById<Button>(R.id.includeBtn)
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
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupSignInLink()
        initEnterKeyToSubmitForm(phoneNumberEdit) { signupRequest() }

        passwordCheckAlert()

        signupBtn.text = requireContext().localized(R.string.signup_text)

        signupBtn.setOnClickListener {
            signupRequest()
        }

    }


    private fun passwordCheckAlert() {
        passwordEdit.doOnTextChanged { text, start, count, after ->
            if (text != null) {
                validateEmailAndPassword(text)
            }
            return@doOnTextChanged
        }
    }

    private fun setupSignInLink() {
        val start = 25
        val end = textLen
        spannableString.enableClickOnSubstring(start, end) {
            findNavController().navigate(R.id.signinFragment)
        }
        spannableString.setColorToSubstring(color, start, end)
        spannableString.removeUnderLine(start, end)
        signinLink.text = spannableString
        signinLink.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun signupRequest() {

        val firstName = firstNameEdit.text.toString().trim()
        val lastName = lastNameEdit.text.toString().trim()
        val emailAddress = emailEdit.text.toString().trim()
        val phoneNumber = phoneNumberEdit.text.toString().trim()
        val passwordString = passwordEdit.text.toString().trim()
        val cpassword = cpasswordEdit.text.toString().trim()


        val checkForEmpty =
            IsEmptyCheck(firstNameEdit, lastNameEdit, emailEdit, phoneNumberEdit, passwordEdit, cpasswordEdit)
        val validation = IsEmptyCheck.fieldsValidation(emailAddress, passwordString)
        when {
            checkForEmpty != null -> {
                checkForEmpty.error = requireContext().localized(R.string.field_required)
                requireActivity().toast("${checkForEmpty.hint} is empty")
            }
            !checkboxForSignup.isChecked -> {
                requireActivity().toast(requireContext().localized(R.string.agree_to_term))

            }
            validation != null -> requireActivity().toast("$validation is invalid")
            passwordString != cpassword -> requireActivity().toast(requireContext().localized(R.string.passwords_do_not_match))
            else -> {
                val request = userViewModel.registerUser(
                    firstName,
                    lastName,
                    emailAddress,
                    phoneNumber,
                    passwordString
                )
                val response = observeRequest(request, progressBar, signupBtn)
                response.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    val (bool, result) = it
                    when (bool) {
                        true -> {
                            val registeredUser = User(firstName, lastName, emailAddress, phoneNumber, passwordString)
                            registeredUser.email?.let { it1 ->
                                storageRequest.saveData(registeredUser,
                                    it1
                                )
                            }
                            findNavController().navigate(R.id.signinFragment)
                        }
                        else -> Log.i(title, "error $result")
                    }
                })
            }
        }


    }

    private fun validateEmailAndPassword(text: CharSequence) {
        val passwordPattern = Regex("""^[a-zA-Z0-9@$!%*#?&]{6,}$""")
        val matchedPassword = passwordPattern.matches(text)
        if (!matchedPassword) {
            passwordStandard.show()
            passwordStandard.setTextColor(Color.RED)
        } else {
            passwordStandard.hide()
        }
    }
}
