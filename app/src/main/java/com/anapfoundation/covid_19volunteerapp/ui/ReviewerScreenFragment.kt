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

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.paging.ReviewerScreenReportsDataFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.getUnApprovedReports
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.user.UserViewModel
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.user.getSingleLGA
import com.anapfoundation.covid_19volunteerapp.model.Location
import com.anapfoundation.covid_19volunteerapp.model.response.ReportResponse
import com.anapfoundation.covid_19volunteerapp.model.response.Reports
import com.anapfoundation.covid_19volunteerapp.model.response.TopicResponse
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.getName
import com.anapfoundation.covid_19volunteerapp.utils.extensions.observeRequest
import com.squareup.picasso.Picasso
import com.utsman.recycling.extentions.Recycling
import com.utsman.recycling.paged.setupAdapterPaged
import com.utsman.recycling.setupAdapter
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_reviewer_screen.*
import kotlinx.android.synthetic.main.report_item.view.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ReviewerScreenFragment : DaggerFragment() {

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
    var singleReport = ReportResponse()

    @Inject
    lateinit var reviewerScreenReportsDataFactory: ReviewerScreenReportsDataFactory

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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reviewer_screen, container, false)
    }

    override fun onResume() {
        super.onResume()

        reviewerRecyclerView
        try {

            Log.i(title, "header $header")
            reviewerRecyclerView.setupAdapter<ReportResponse>(R.layout.report_item){ adapter, context, list ->

                bind { itemView, position, item ->
                    Log.i(title, "report items ${item}")
                    val ratingRequest = authViewModel.getRating(item?.topic.toString(), header)
                    val ratingResponse = observeRequest(ratingRequest, null, null)
                    ratingResponse.observe(viewLifecycleOwner, Observer {
                        val(bool, result) = it
                        when(bool){
                            true -> {
                                val res = result as TopicResponse
                                val topic =res.data.filter {
                                    it.topic != ""
                                }
                                itemView.reportTopic.text = topic[0].topic

                            }
                        }
                    })

                    val locationRequest = userViewModel.getSingleLGA("${item?.localGovernment}")
                    val stateResponse = observeRequest(locationRequest, null, null)
                    stateResponse.observe(viewLifecycleOwner, Observer {
                        val(bool, result) = it
                        when(bool){
                            true -> {
                                val res = result as Location
                                lga = res.data.localGovernment.toString()
                                state = res.data.stateName.toString()
                                Log.i("State", "${res.data.stateName}")
                                itemView.reportLocation.text = "$lga, $state"


                            }
                        }
                    })

                    itemView.reportStory.text = item?.story

                    itemView.setOnClickListener {
                        singleReport.id = item?.id
                        singleReport.topic = itemView.reportTopic.text.toString()
                        singleReport.story = itemView.reportStory.text.toString()
                        singleReport.mediaURL = item?.mediaURL

                        singleReport.localGovernment = lga
                        singleReport.state = state
                        val action = ReviewerScreenFragmentDirections.toApprovalFragment()
                        action.singleReport = singleReport
                        Navigation.findNavController(requireView()).navigate(action)
                    }

                    Picasso.get().load(item?.mediaURL)
                        .placeholder(R.drawable.applogo)
                        .into(itemView.reportImage)
                    itemView.reportImage.clipToOutline = true
                }

                setupData(this, 100, 0)
//                authViewModel.getUnapprovedReports(reviewerScreenReportsDataFactory).observe(viewLifecycleOwner, Observer {
//
//                    submitList(it)
//                })

            }

        }catch (e:Exception){
            Log.e(title, e.message)
        }
    }

    // function for setup data
    private fun setupData(recycling: Recycling<ReportResponse>, first: Long?, after:Long?) {
        val request =  authViewModel.getUnApprovedReports(header, first, after)
        val response = observeRequest(request, null, null)
        response.observe(viewLifecycleOwner, Observer {
            val (bool, result) = it
            when(bool){
                true -> {
                    val res = result as Reports
                    // submit list from viewmodel into recycling
                    recycling.submitList(result.data)
                }
            }


        })

    }
}
