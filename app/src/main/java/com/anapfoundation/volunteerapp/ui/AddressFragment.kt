package com.anapfoundation.volunteerapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.anapfoundation.volunteerapp.R
import com.anapfoundation.volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.volunteerapp.data.viewmodel.user.UserViewModel
import com.anapfoundation.volunteerapp.helpers.IsEmptyCheck
import com.anapfoundation.volunteerapp.model.StatesList
import com.anapfoundation.volunteerapp.model.User
import com.anapfoundation.volunteerapp.model.UserData
import com.anapfoundation.volunteerapp.model.user.UserResponse
import com.anapfoundation.volunteerapp.network.storage.StorageRequest
import com.anapfoundation.volunteerapp.utils.extensions.*
import com.google.gson.Gson
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_address.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class AddressFragment : DaggerFragment() {

    val title: String by lazy {
        getName()
    }

    val gson: Gson by lazy {
        Gson()
    }
    private val navController by lazy {
        findNavController()
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
    val loggedInUser by lazy {

        storageRequest.checkUser("loggedInUser")
    }

    //Get token
    val token by lazy {
        loggedInUser?.token
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
    val lgaAndDistrict = hashMapOf<String, String>()

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


        //receive userdata
        arguments?.let {
            userData = AddressFragmentArgs.fromBundle(it).userData!!
        }

        //Customize button text
        submitBtn.text = getLocalisedString(R.string.submit_text)

        //Sign-up onclick event
        submitBtn.setOnClickListener {
            completeSignupRequest()
        }
        //Signup on enter-key event
        initEnterKeyToSubmitForm(streetEditText) { completeSignupRequest() }

        //Back button navigation
        addressBackBtn.setOnClickListener {
            goto(R.id.signupFragment)
        }

        //Phone back button navigation
        requireActivity().onBackPressedDispatcher.addCallback {
            goto(R.id.signupFragment)
        }

        crashReportByUser(loggedInUser)

    }

    override fun onResume() {
        super.onResume()
        getStateAndSendToSpinner()
        val u = storageRequest.checkUser("u")
        Log.i(title, "reg1 $u")

    }

    /**
     * Get the state-list and inflate state spinner
     *
     */
    private fun getStateAndSendToSpinner() {
        val stateData = getStates("37", "")
        stateData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            extractStateList(it)
            setupSpinner()
        })
    }

    /**
     * Get states
     *
     * @param first
     * @param after
     * @return
     */
    private fun getStates(first:String, after:String): MediatorLiveData<StatesList> {
        val data = MediatorLiveData<StatesList>()
        val request = userViewModel.getStates(first, after)
        val response = observeRequest(request, null, null)
        data.addSource(response) {
            try {
                data.value = it.second as StatesList
            }
            catch (e:Exception){
                Log.i(title, e.localizedMessage)
            }

        }
        return data
    }

    /**
     * Extract state-list from livedata
     * and put inside hashmap(states)
     * @param it
     */
    private fun extractStateList(it: StatesList) {
        it.data.associateByTo(states, {
            it.state /* key */
        }, {
            "${it.id} ${it.zone}" /* value */
        })
//        Log.i(title, "${states.get("Bauchi")?.split(" ")?.get(0)}")
    }

    /**
     * Set up spinner
     *
     */
    private fun setupSpinner() {
        val stateArray = states.keys.sorted().toMutableList()
        stateArray.add(0, getLocalisedString(R.string.states))
        val adapterState =
            ArrayAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                states.keys.sorted()
            )
        spinnerState.adapter = adapterState

        setLGASpinner(spinnerState, spinnerLGA, lgaAndDistrict, states, userViewModel, null, "Local government")
    }


    /**
     * Sign-up request
     *
     */
    private fun completeSignupRequest() {

        val checkForEmpty =
            IsEmptyCheck(houseNumberEditText, streetEditText)
        when {
            checkForEmpty != null -> {
                checkForEmpty.error = getLocalisedString(R.string.field_required)
                toast("${checkForEmpty.hint} is empty")
            }
            else -> {
                val selectedState = spinnerState.selectedItem
                val selectedLGA = spinnerLGA.selectedItem
                val valueOfStateSelected = states.get(selectedState)?.split(" ")
                val stateGUID = valueOfStateSelected?.get(0).toString()
                val zoneGUID = valueOfStateSelected?.get(1).toString()
                val lgaAndDistrictArray = lgaAndDistrict.get(selectedLGA)?.split(" ")
                val lgaGUID = lgaAndDistrictArray?.get(0).toString()
                val district = lgaAndDistrictArray?.get(1).toString()

                Log.i(title, "ZoneGUID $zoneGUID")

                val houseNumber = houseNumberEditText.text.toString()
                val street = streetEditText.text.toString()
                Log.i(title, "lgaAndDistrict $lgaAndDistrictArray lga $lgaGUID district $district zone $zoneGUID")
                val request = userViewModel.registerUser(
                    userData.firstName,
                    userData.lastName,
                    userData.email,
                    userData.phone,
                    userData.password,
                    houseNumber,
                    street,
                    stateGUID,
                    lgaGUID,
                    zoneGUID,
                    district
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

    /**
     * Handles sign-up request live response
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
                toast(getLocalisedString(R.string.signup_successful))
                val clearRegister = storageRequest.clearByKey<User>("u")
                goto(R.id.signinFragment)
                Log.i(title, "result of registration ${res.data}")
//                Log.i(title, "clearedRegister $clearRegister")
            }
            else -> Log.i(title, "error $result")
        }
    }


}

