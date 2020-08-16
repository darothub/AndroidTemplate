package com.anapfoundation.volunteerapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.anapfoundation.volunteerapp.R
import com.anapfoundation.volunteerapp.adapter.ViewPagerAdapter
import com.anapfoundation.volunteerapp.data.paging.ReviewerApprovedReportsDataFactory
import com.anapfoundation.volunteerapp.data.paging.ReviewerUnapprovedReportsDataFactory
import com.anapfoundation.volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.volunteerapp.data.viewmodel.user.UserViewModel
import com.anapfoundation.volunteerapp.model.response.ReportResponse
import com.anapfoundation.volunteerapp.network.storage.StorageRequest
import com.anapfoundation.volunteerapp.utils.extensions.*
import com.google.android.material.tabs.TabLayoutMediator
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_reviewer_screen.*
import kotlinx.android.synthetic.main.fragment_reviewer_screen.reviewerNotificationCount
import kotlinx.android.synthetic.main.fragment_reviewer_screen.reviewerNotificationIcon
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ReviewerScreenFragment : DaggerFragment() {

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
    var singleReport = ReportResponse()

    @Inject
    lateinit var reviewerUnapprovedReportsDataFactory: ReviewerUnapprovedReportsDataFactory

    @Inject
    lateinit var reviewerApprovedReportsDataFactory: ReviewerApprovedReportsDataFactory

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    val authViewModel: AuthViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel::class.java)
    }
    val userViewModel: UserViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(UserViewModel::class.java)
    }
    val title: String by lazy {
        getName()
    }

    var lga = ""
    var state = ""
    var total = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reviewer_screen, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.displayNotificationBell(
            loggedInUser,
            reviewerNotificationIcon,
            reviewerNotificationCount
        )
    }
    override fun onResume() {
        super.onResume()

//        Log.i(title, "Onresume")
        val pageAdapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager2.adapter = pageAdapter
        var names = arrayListOf<String>("Unapproved reports", "Approved reports")
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = names[position]
        }.attach()

    }



}
