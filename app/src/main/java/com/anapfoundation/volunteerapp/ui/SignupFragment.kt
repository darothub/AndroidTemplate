package com.anapfoundation.volunteerapp.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider

import com.anapfoundation.volunteerapp.R
import com.anapfoundation.volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.volunteerapp.data.viewmodel.user.UserViewModel
import com.anapfoundation.volunteerapp.helpers.IsEmptyCheck
import com.anapfoundation.volunteerapp.model.User
import com.anapfoundation.volunteerapp.model.UserData
import com.anapfoundation.volunteerapp.utils.extensions.*
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_signup.*
import java.lang.Exception
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class SignupFragment : DaggerFragment() {

    val title: String by lazy {
        getName()
    }
    val signinText: String by lazy {
        getLocalisedString(R.string.signin_link)
    }
    val spannableString: SpannableString by lazy {
        signinText.setAsSpannable()
    }
    var color:Int = 0
    val textLen: Int by lazy {
        signinText.length
    }

    lateinit var signupBtn:Button

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
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
        requireActivity().onBackPressedDispatcher.addCallback {
            goto(R.id.signinFragment)
        }
    }


    override fun onResume() {
        super.onResume()
        setupSignInLink()
        initEnterKeyToSubmitForm(phoneNumberEdit) { signupRequest() }

        passwordCheckAlert()

        signupBtn = signupBottom.findViewById<Button>(R.id.includeBtn)
        signupBtn.setButtonText(getLocalisedString(R.string.proceed))

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
                validateEmailAndPassword(text, passwordStandard)
            }
            return@doOnTextChanged
        }
        emailEdit.doAfterTextChanged {
            emailEdit.text.toString().toLowerCase()
        }
    }

    /**
     * Custom sign in link
     * Using spannable string
     */
    private fun setupSignInLink() {
        val start = 25
        val end = textLen
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            color = resources.getColor(R.color.colorPrimary, requireContext().theme)
        } else {
           color = resources.getColor(R.color.colorPrimary)
        }
        spannableString.enableClickOnSubstring(start, end) {
            goto(R.id.signinFragment)
        }
        spannableString.setColorToSubstring(color, start, end)
        spannableString.removeUnderLine(start, end)
        signinLink.text = spannableString
        signinLink.movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * Sign up request
     *
     */
    private fun signupRequest() {

        val firstName = firstNameEdit.text.toString().trim()
        val lastName = lastNameEdit.text.toString().trim()
        val emailAddress = emailEdit.text.toString().trim().toLowerCase()
        val phoneNumber = phoneNumberEdit.text.toString().trim()
        val passwordString = passwordEdit.text.toString().trim()
        val cpassword = cpasswordEdit.text.toString().trim()
        val user = User(firstName, lastName, emailAddress, passwordString, phoneNumber)
        user.rememberPassword = checkboxForSignup.isChecked

        userViewModel.registerFormData = user
        val checkForEmpty =
            IsEmptyCheck(firstNameEdit, lastNameEdit, emailEdit, phoneNumberEdit, passwordEdit, cpasswordEdit)
        val validation = IsEmptyCheck.fieldsValidation(emailAddress, passwordString)
        when {
            checkForEmpty != null -> {
                checkForEmpty.error = getLocalisedString(R.string.field_required)
                toast("${checkForEmpty.hint} is empty")
            }
            !checkboxForSignup.isChecked -> {
                toast(getLocalisedString(R.string.agree_to_term))

            }
            validation != null -> toast("$validation is invalid")
            passwordString != cpassword -> toast(getLocalisedString(R.string.passwords_do_not_match))
            else -> {

                val userData = UserData(firstName, lastName, emailAddress, phoneNumber, passwordString)
                val action = SignupFragmentDirections.toAddressFragment()
                action.userData = userData
                goto(action)
            }
        }


    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
//        Log.i(title, "RestoredInstance")
        if (userViewModel.state.contains("registeringUser")){
            try {
                val registeringUser = userViewModel.registerFormData
                firstNameEdit.setText(registeringUser?.firstName)
                lastNameEdit.setText(registeringUser?.lastName)
                emailEdit.setText(registeringUser?.email)
                passwordEdit.setText(registeringUser?.password)
                cpasswordEdit.setText(registeringUser?.password)
                phoneNumberEdit.setText(registeringUser?.phone)
                checkboxForSignup.isChecked = registeringUser?.rememberPassword!!


            }
            catch (e:Exception){
                Log.i(title, "RestoringError ${e.localizedMessage}")
            }
        }


    }

    /**
     * validate email and password field
     *
     * @param text
     * @param passwordStandard
     */
    fun validateEmailAndPassword(text: CharSequence, passwordStandard:TextView) {
        val passwordPattern = Regex("""^[a-zA-Z0-9@$.!%*#?&]{6,}$""")
        val matchedPassword = passwordPattern.matches(text)
        if (!matchedPassword) {
            passwordStandard.show()
            passwordStandard.setTextColor(Color.RED)
        } else {
            passwordStandard.hide()
        }
    }
}
