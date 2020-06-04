package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.resetPassword
import com.anapfoundation.covid_19volunteerapp.helpers.IsEmptyCheck
import com.anapfoundation.covid_19volunteerapp.model.DefaultResponse
import com.anapfoundation.covid_19volunteerapp.model.ProfileData
import com.anapfoundation.covid_19volunteerapp.model.UserData
import com.anapfoundation.covid_19volunteerapp.model.user.UserResponse
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_reset_password.*
import kotlinx.android.synthetic.main.fragment_signin.*
import kotlinx.android.synthetic.main.fragment_signup.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ResetPasswordFragment : DaggerFragment() {
    val title: String by lazy {
        getName()
    }


    private val progressBar by lazy {
        resetPasswordBottomLayout.findViewById<ProgressBar>(R.id.includedProgressBar)
    }
    lateinit var resetButton: Button

    var signupFragment = SignupFragment()

    @Inject
    lateinit var storageRequest: StorageRequest
    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    val authViewModel: AuthViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel::class.java)
    }


    lateinit var token: String
    var header = "Bearer "
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password, container, false)
    }

    override fun onStart() {
        super.onStart()

//        Log.i(title, "onStart")
        arguments?.let {
            token = ResetPasswordFragmentArgs.fromBundle(it).token!!
        }

        header += token
        requireActivity().onBackPressedDispatcher.addCallback {

            goto(R.id.signinFragment)
        }

//        Log.i(title, "token $token")
    }

    override fun onResume() {
        super.onResume()
        Log.i(title, "onResume")
        resetButton = resetPasswordBottomLayout.findViewById<Button>(R.id.includeBtn)
        resetButton.setButtonText(requireContext().getLocalisedString(R.string.submit_text))

        initEnterKeyToSubmitForm(resetPasswordPasswordEdit) { resetPasswordRequest() }
        resetButton.setOnClickListener {
            resetPasswordRequest()
        }

        resetPasswordPasswordEdit.doOnTextChanged { text, start, count, after ->
            if (text != null) {
                signupFragment.validateEmailAndPassword(text, resetPasswordStandard)
            }
            return@doOnTextChanged
        }

        val user = storageRequest.checkUser("loggedInUser")

    }

    /**
     * Reset password request
     *
     */
    private fun resetPasswordRequest() {
        val password = resetPasswordPasswordEdit.text.toString().trim()
        val checkForEmpty =
            IsEmptyCheck(resetPasswordPasswordEdit)
        val validation = IsEmptyCheck.fieldsValidation(null, password)

        when {
            checkForEmpty != null -> {
                checkForEmpty.error = requireContext().getLocalisedString(R.string.field_required)
                requireActivity().toast("${checkForEmpty.hint} is empty")
            }
            validation != null -> requireActivity().toast(requireContext().getLocalisedString(R.string.password_invalid))
            else -> {
                val request = authViewModel.resetPassword(password, token)
                val response = observeRequest(request, progressBar, resetButton)

                Log.i(title, "token $token")
                response.observe(viewLifecycleOwner, Observer {
                    val (bool, result) = it
                    onRequestResponseTask(bool, result)
                })
            }
        }
    }

    /**
     * Handles request live response
     *
     * @param bool
     * @param result
     */
    private fun onRequestResponseTask(
        bool: Boolean,
        result: Any?
    ) {
        when (bool) {
            true -> {
                val res = result as UserResponse
                requireContext().toast(res.data.toString())

                activity?.finish()
//                Log.i(title, "result of reset ${res.data}")
            }
            else -> Log.i(title, "error $result")
        }
    }



    override fun onPause() {
        super.onPause()

//        Log.i(title, "onPause")
    }

}
