package com.anapfoundation.covid_19volunteerapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : DaggerFragment() {

    @Inject
    lateinit var storageRequest: StorageRequest
    //Get logged-in user
    val user by lazy {
        storageRequest.checkUser("loggedInUser")
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

        profileName.text = "${user?.firstName} ${user?.lastName}"
        profileEmail.text = "${user?.email}"
    }

    private fun navigateToEditProfile() {
        editProfileBtn.setOnClickListener {
            findNavController().navigate(R.id.editProfileFragment)
        }
    }
}
