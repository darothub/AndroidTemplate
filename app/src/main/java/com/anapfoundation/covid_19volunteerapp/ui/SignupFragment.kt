package com.anapfoundation.covid_19volunteerapp.ui

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.addCallback
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.helpers.IsEmptyCheck
import com.anapfoundation.covid_19volunteerapp.model.UserData
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_signup.*

/**
 * A simple [Fragment] subclass.
 */
class SignupFragment : DaggerFragment() {

    val title: String by lazy {
        getName()
    }
    val signinText: String by lazy {
        requireContext().getLocalisedString(R.string.signin_link)
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

    lateinit var signupBtn:Button



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
        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigate(R.id.signinFragment)
        }
    }


    override fun onResume() {
        super.onResume()
        setupSignInLink()
        initEnterKeyToSubmitForm(phoneNumberEdit) { signupRequest() }

        passwordCheckAlert()

        signupBtn = signupBottom.findViewById<Button>(R.id.includeBtn)
        signupBtn.setButtonText(requireContext().getLocalisedString(R.string.proceed))

        sendSignupRequest()
    }

    private fun sendSignupRequest() {
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
//        emailEdit.doOnTextChanged { text, start, count, after ->
//            emailEdit
//        }
        emailEdit.doAfterTextChanged {
            emailEdit.text.toString().toLowerCase()
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
        val emailAddress = emailEdit.text.toString().trim().toLowerCase()
        val phoneNumber = phoneNumberEdit.text.toString().trim()
        val passwordString = passwordEdit.text.toString().trim()
        val cpassword = cpasswordEdit.text.toString().trim()

        val checkForEmpty =
            IsEmptyCheck(firstNameEdit, lastNameEdit, emailEdit, phoneNumberEdit, passwordEdit, cpasswordEdit)
        val validation = IsEmptyCheck.fieldsValidation(emailAddress, passwordString)
        when {
            checkForEmpty != null -> {
                checkForEmpty.error = requireContext().getLocalisedString(R.string.field_required)
                requireActivity().toast("${checkForEmpty.hint} is empty")
            }
            !checkboxForSignup.isChecked -> {
                requireActivity().toast(requireContext().getLocalisedString(R.string.agree_to_term))

            }
            validation != null -> requireActivity().toast("$validation is invalid")
            passwordString != cpassword -> requireActivity().toast(requireContext().getLocalisedString(R.string.passwords_do_not_match))
            else -> {

                val userData = UserData(firstName, lastName, emailAddress, phoneNumber, passwordString)
                val action = SignupFragmentDirections.toAddressFragment()
                action.userData = userData
                findNavController().navigate(action)
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
