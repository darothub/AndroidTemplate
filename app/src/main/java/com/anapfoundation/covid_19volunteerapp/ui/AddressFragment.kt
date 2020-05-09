package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.user.UserViewModel
import com.anapfoundation.covid_19volunteerapp.helpers.IsEmptyCheck
import com.anapfoundation.covid_19volunteerapp.model.*
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import com.google.gson.Gson
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_address.*
import kotlinx.android.synthetic.main.fragment_signup.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class AddressFragment : DaggerFragment() {

    val title: String by lazy {
        getName()
    }
    val stateLgaMap: HashMap<String, List<CityClass>> by lazy {
        requireActivity().readCitiesAndLgaData()
    }

    val gson: Gson by lazy {
        Gson()
    }

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    val authViewModel: AuthViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel::class.java)
    }


    val userViewModel: UserViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(UserViewModel::class.java)
    }
    @Inject
    lateinit var storageRequest: StorageRequest

    //Get logged-in user
    val getUser by lazy {
        storageRequest.checkUser("loggedInUser")
    }

    //Get token
    val token by lazy {
        getUser?.token
    }

    //Set header
    val header by lazy {
        "Bearer $token"
    }
    val progressBar by lazy {
        addressbottomIndicator.findViewById<ProgressBar>(R.id.includedProgressBar)
    }
    val submitBtn by lazy {
        addressbottomIndicator.findViewById<Button>(R.id.includeBtn)
    }
    val states = hashMapOf<String, String>()

    lateinit var userData: UserData
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_address, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


//        requireContext().setSpinnerAdapterData(spinnerState, spinnerLGA, stateLgaMap)


        submitBtn.text = requireContext().localized(R.string.submit_text)
        submitBtn.setOnClickListener {
            completeSignupRequest()
        }
        initEnterKeyToSubmitForm(streetEditText) { completeSignupRequest() }

        getStateAndSendToSpinner()
        arguments?.let {
            userData = AddressFragmentArgs.fromBundle(it).userData!!
        }

        Log.i(title, "password ${userData.password}")

    }

    private fun getStateAndSendToSpinner() {
        val stateData = getStates(header)
        stateData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            extractStateList(it)
            setupSpinner()
        })
    }

    private fun getStates(header: String): MediatorLiveData<StatesList> {
        val data = MediatorLiveData<StatesList>()
        val request = authViewModel.getStates(header)
        val response = observeRequest(request, null, null)
        data.addSource(response) {
            data.value = it.second as StatesList
        }
        return data
    }

    private fun extractStateList(it: StatesList) {
        it.data.associateByTo(states, {
            it.state /* key */
        }, {
            it.id /* value */
        })
    }

    private fun setupSpinner() {
        val adapterState =
            ArrayAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                states.keys.sorted()
            )
        spinnerState.adapter = adapterState
//        Log.i(title, "states $states")
    }

    private fun completeSignupRequest() {

        val checkForEmpty =
            IsEmptyCheck(houseNumberEditText, streetEditText)
        when {
            checkForEmpty != null -> {
                checkForEmpty.error = requireContext().localized(R.string.field_required)
                requireActivity().toast("${checkForEmpty.hint} is empty")
            }
            else -> {
                val selectedState = spinnerState.selectedItem
                val stateGUID = states.get(selectedState)
                val request = userViewModel.registerUser(
                    userData.firstName,
                    userData.lastName,
                    userData.email,
                    userData.phone,
                    userData.password.toString(),
                    userData.houseNumber.toString(),
                    userData.street.toString(),
                    stateGUID.toString()
                )
                val response = observeRequest(request, progressBar, submitBtn)
                response.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    val (bool, result) = it
                    onRequestResponseTask(
                        bool,
                        result
                    )
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
                val res = result as Data
                requireContext().toast(requireContext().localized(R.string.signup_successful))
                findNavController().navigate(R.id.signinFragment)
                Log.i(title, "result of registration ${res.token}")
            }
            else -> Log.i(title, "error $result")
        }
    }


}
