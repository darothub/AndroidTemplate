package com.anapfoundation.covid_19volunteerapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.model.ProfileData
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest

import com.anapfoundation.covid_19volunteerapp.utils.extensions.getName
import com.anapfoundation.covid_19volunteerapp.utils.extensions.observeRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.toast
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_profile.*
import java.lang.Exception
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : DaggerFragment() {
    val title: String by lazy {
        getName()
    }

    @Inject
    lateinit var storageRequest: StorageRequest
    //Get logged-in user
    val user by lazy {
        storageRequest.checkUser("loggedInUser")
    }
    val token by lazy {
        user?.token
    }
    val header by lazy {
        "Bearer $token"
    }
    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    val authViewModel: AuthViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        profileImage.clipToOutline = true
        navigateToEditProfile()




        val request = authViewModel.getProfileData(header)
        val response = observeRequest(request, null, null)
        response.observe(viewLifecycleOwner, Observer {
            val (bool, result) = it
            try {
                when(bool){
                    true ->{
                        val res = result as ProfileData
                        val user = res.data
                        profileName.text = "${user.lastName.capitalize()} ${user.firstName.capitalize()}"
                        profileEmail.text = "${user.email}"
                        profileAddress.text = "${user.houseNumber} ${user.street} ${user.state}"
                        profileUploadNumber.text = "${user.totalReports}"

                        Log.i(title, "name ${user.firstName}")

                    }
                    false ->{
                        Log.i(title, "false")
                    }
                }
            }
            catch (e:Exception){
                Log.i(title, "error ${e.localizedMessage}")
            }

        })

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigateUp()

        }



    }

    private fun navigateToEditProfile() {
        editProfileBtn.setOnClickListener {
            findNavController().navigate(R.id.editProfileFragment)
        }
    }
}
