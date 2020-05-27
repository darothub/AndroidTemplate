package com.anapfoundation.covid_19volunteerapp.ui

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.paging.ReviewerUnapprovedReportsDataFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.model.ProfileData
import com.anapfoundation.covid_19volunteerapp.model.User
import com.anapfoundation.covid_19volunteerapp.model.UserData
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.displayNotificationBell

import com.anapfoundation.covid_19volunteerapp.utils.extensions.getName
import com.anapfoundation.covid_19volunteerapp.utils.extensions.observeRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.toast
import com.squareup.picasso.Picasso
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

    //Get logged-in user
    val loggedInUser by lazy {
        storageRequest.checkUser("loggedInUser")
    }
    val token by lazy {
        loggedInUser?.token
    }
    val header by lazy {
        "Bearer $token"
    }

    @Inject
    lateinit var storageRequest: StorageRequest

    @Inject
    lateinit var reviewerUnapprovedReportsDataFactory: ReviewerUnapprovedReportsDataFactory

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    val authViewModel: AuthViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel::class.java)
    }
    lateinit var profileData: UserData

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

//
//        val request = authViewModel.getProfileData(header)
//        val response = observeRequest(request, null, null)
//        response.observe(viewLifecycleOwner, Observer {
//            val (bool, result) = it
//            try {
//                when (bool) {
//                    true -> {
//                        val imagePlaceholder: Drawable
//                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//                            imagePlaceholder = resources.getDrawable(
//                                R.drawable.ic_person_primary_24dp,
//                                requireContext().theme
//                            )
//                        } else {
//                            imagePlaceholder =
//                                resources.getDrawable(R.drawable.ic_person_primary_24dp)
//                        }
//
//                        val res = result as ProfileData
//                        val user = res.data
//                        profileName.text =
//                            "${user.lastName.capitalize()} ${user.firstName.capitalize()}"
//                        profileEmail.text = "${user.email}"
//                        profileAddress.text = "${user.houseNumber} ${user.street} ${user.stateName}"
//                        profileUploadNumber.text = "${user.totalReports}"
//                        Picasso.get().load(user.profileImageURL).placeholder(imagePlaceholder)
//                            .into(profileImage)
//                        Log.i(title, "name ${user.firstName}")
//
//
//                        navigateToEditProfile(user)
//
//
//                    }
//                    false -> {
//                        Log.i(title, "false")
//                    }
//                }
//            } catch (e: Exception) {
//                Log.i(title, "error ${e.localizedMessage}")
//            }
//
//        })
        val imagePlaceholder: Drawable
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            imagePlaceholder = resources.getDrawable(
                R.drawable.ic_person_primary_24dp,
                requireContext().theme
            )
        } else {
            imagePlaceholder =
                resources.getDrawable(R.drawable.ic_person_primary_24dp)
        }

        profileName.text =
            "${loggedInUser?.lastName?.capitalize()} ${loggedInUser?.firstName?.capitalize()}"
        profileEmail.text = "${loggedInUser?.email}"
        profileAddress.text = "${loggedInUser?.houseNumber} ${loggedInUser?.street}, ${loggedInUser?.stateName}"
        profileUploadNumber.text = "${loggedInUser?.totalReports}"
        Picasso.get().load(loggedInUser?.imageUrl).placeholder(imagePlaceholder)
            .into(profileImage)
        Log.i(title, "name ${loggedInUser?.firstName}")

        navigateToEditProfile(loggedInUser)


    }

    override fun onStart() {
        super.onStart()

        this.displayNotificationBell(
            authViewModel,
            loggedInUser,
            reviewerUnapprovedReportsDataFactory,
            profileNotificationIcon,
            profileNotificationCount
        )
        profileNotificationIcon.setOnClickListener {
            findNavController().navigate(R.id.reviewerScreenFragment)
        }
    }

    private fun navigateToEditProfile(user: User?) {
        editProfileBtn.setOnClickListener {
            val action = ProfileFragmentDirections.toEditProfile()
            action.profileData = user
            Navigation.findNavController(requireView()).navigate(action)
        }
    }
}
