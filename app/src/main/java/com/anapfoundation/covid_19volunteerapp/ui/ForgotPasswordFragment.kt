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
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.user.UserViewModel
import com.anapfoundation.covid_19volunteerapp.helpers.IsEmptyCheck
import com.anapfoundation.covid_19volunteerapp.model.DefaultResponse
import com.anapfoundation.covid_19volunteerapp.model.User
import com.anapfoundation.covid_19volunteerapp.model.user.UserResponse
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.android.synthetic.main.fragment_signin.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ForgotPasswordFragment : DaggerFragment() {

    val title: String by lazy {
        getName()
    }


    private val progressBar by lazy {
        forgotPasswordBottomLayout.findViewById<ProgressBar>(R.id.includedProgressBar)
    }
    private val btn by lazy {
        forgotPasswordBottomLayout.findViewById<Button>(R.id.includeBtn)
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
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onResume() {
        super.onResume()
        btn.setButtonText(requireContext().getLocalisedString(R.string.submit_text))

        btn.setOnClickListener {
            forgotPasswordRequest()
        }

        initEnterKeyToSubmitForm(forgotPasswordEmailEdit) { forgotPasswordRequest() }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigateUp()

        }
    }

    private fun forgotPasswordRequest(){

        val email = forgotPasswordEmailEdit.text.toString().trim()
        val checkForEmpty = IsEmptyCheck(forgotPasswordEmailEdit)
        val validation = IsEmptyCheck.fieldsValidation(email, null)
        when {
            checkForEmpty != null -> {
                checkForEmpty.error = requireContext().getLocalisedString(R.string.field_required)
                requireActivity().toast("${checkForEmpty.hint} field is empty")
            }
            validation != null -> requireActivity().toast("$validation is invalid")
            else -> {
                val request = userViewModel.forgotPasswordRequest(
                    email
                )
                val response = observeRequest(request, progressBar, btn)
                response.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    val (bool, result) = it
                    onRequestResponseTask(bool, result)
                })
            }
        }
    }
    private fun onRequestResponseTask(
        bool: Boolean,
        result: Any?
    ) {
        when (bool) {
            true -> {
                val res = result as UserResponse
                requireActivity().toast("${res.data}")
                findNavController().popBackStack()
            }
            else -> Log.i(title, "error $result")
        }
    }

}
