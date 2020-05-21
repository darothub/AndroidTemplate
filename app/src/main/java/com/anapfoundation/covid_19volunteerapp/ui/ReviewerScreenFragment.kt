package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.adapter.ViewPagerAdapter
import com.anapfoundation.covid_19volunteerapp.data.paging.ReviewerApprovedReportsDataFactory
import com.anapfoundation.covid_19volunteerapp.data.paging.ReviewerUnapprovedReportsDataFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.user.UserViewModel
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.user.getSingleLGA
import com.anapfoundation.covid_19volunteerapp.model.Location
import com.anapfoundation.covid_19volunteerapp.model.response.ReportResponse
import com.anapfoundation.covid_19volunteerapp.model.response.TopicResponse
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import com.utsman.recycling.extentions.Recycling
import com.utsman.recycling.paged.setupAdapterPaged
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.approved_report_item.view.*
import kotlinx.android.synthetic.main.fragment_report_home.*
import kotlinx.android.synthetic.main.fragment_reviewer_screen.*
import kotlinx.android.synthetic.main.fragment_reviewer_screen.reviewerNotificationCount
import kotlinx.android.synthetic.main.fragment_reviewer_screen.reviewerNotificationIcon
import kotlinx.android.synthetic.main.report_item.view.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ReviewerScreenFragment : DaggerFragment() {

    @Inject
    lateinit var storageRequest: StorageRequest

    //Get logged-in user
    val user by lazy {
        storageRequest.checkUser("loggedInUser")
    }

    //Get token
    val token by lazy {
        user?.token
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

    override fun onResume() {
        super.onResume()

        val pageAdapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager2.adapter = pageAdapter
        var names = arrayListOf<String>("Unapproved reports", "Approved reports")
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = names[position]
        }.attach()

        this.displayNotificationBell(
            authViewModel,
            user,
            reviewerUnapprovedReportsDataFactory,
            reviewerNotificationIcon,
            reviewerNotificationCount
        )


    }


}
