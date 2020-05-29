package com.anapfoundation.covid_19volunteerapp.ui

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.paging.ReviewerUnapprovedReportsDataFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.model.User
import com.anapfoundation.covid_19volunteerapp.model.UserData
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import com.anapfoundation.covid_19volunteerapp.utils.extensions.displayNotificationBell
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_reviewer_screen.*
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

        profileToolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.logout ->{
                    parentFragment?.logout(storageRequest)
                    true
                }
                else -> false
            }
        }


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

        if(loggedInUser?.token.isNullOrEmpty()){
            requireContext().toast(requireContext().getLocalisedString(R.string.unauthorized))
            navigateWithUri("android-app://anapfoundation.navigation/signin".toUri())
        }else{
            profileName.text =
                "${loggedInUser?.lastName?.capitalize()} ${loggedInUser?.firstName?.capitalize()}"
            profileEmail.text = "${loggedInUser?.email}"
            profileAddress.text = "${loggedInUser?.houseNumber} ${loggedInUser?.street}, ${loggedInUser?.stateName}"
            profileUploadNumber.text = "${loggedInUser?.totalReports}"
            Picasso.get().load(loggedInUser?.imageUrl).placeholder(imagePlaceholder)
                .into(profileImage)
            Log.i(title, "name ${loggedInUser?.firstName}")

            navigateToEditProfile(loggedInUser)

            profileBackBtn.setOnClickListener {
                findNavController().navigate(R.id.reportHomeFragment)
            }
        }



    }

    override fun onStart() {
        super.onStart()

        when(loggedInUser?.isReviewer){
            true -> {
                profileNotificationIcon.show()
                profileNotificationCount.show()
                profileNotificationCount.text = loggedInUser?.totalUnapprovedReports.toString()
            }
        }
//        this.displayNotificationBell(
//            authViewModel,
//            loggedInUser,
//            reviewerUnapprovedReportsDataFactory,
//            profileNotificationIcon,
//            profileNotificationCount
//        )
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
