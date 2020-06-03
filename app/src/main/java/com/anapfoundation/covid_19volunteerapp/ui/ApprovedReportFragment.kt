package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager

import com.anapfoundation.covid_19volunteerapp.R
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
import com.squareup.picasso.Picasso
import com.utsman.recycling.paged.extentions.NetworkState
import com.utsman.recycling.paged.setupAdapterPaged
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.approved_report_item.view.*
import kotlinx.android.synthetic.main.fragment_approved_report.*
import kotlinx.android.synthetic.main.fragment_unapproved_report.*
import kotlinx.android.synthetic.main.report_item.view.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ApprovedReportFragment : DaggerFragment() {
    val title: String by lazy {
        getName()
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

    var lga = ""
    var state = ""

    var singleReport = ReportResponse()
    var total = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_approved_report, container, false)
    }

    override fun onStart() {
        super.onStart()


    }

    override fun onResume() {
        super.onResume()
        setRecyclerViewForApprovedReports()
    }


    private fun setRecyclerViewForApprovedReports() {
        try {
            approvedReportsRecyclerView.setupAdapterPaged<ReportResponse>(R.layout.report_item) { adapter, context, list ->

                bind { itemView, position, item ->

                    val ratingRequest = authViewModel.getRating(item?.topic.toString(), header)
                    val ratingResponse = observeRequest(ratingRequest, null, null)
                    ratingResponse.observe(viewLifecycleOwner, Observer {
                        val (bool, result) = it
                        when (bool) {
                            true -> {
                                val res = result as TopicResponse
                                val topic = res.data.filter {
                                    it.topic != ""
                                }
                                itemView.reportTopic.text = topic[0].topic

                            }
                        }
                    })

                    val locationRequest = userViewModel.getSingleLGA("${item?.localGovernment}")
                    val stateResponse = observeRequest(locationRequest, null, null)
                    stateResponse.observe(viewLifecycleOwner, Observer {
                        val (bool, result) = it
                        when (bool) {
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
                        val location = itemView.reportLocation.text.toString()
                        val localGovernment = location.split(",")[0]
                        val itemState = location.split(",")[1]
                        singleReport.localGovernment = localGovernment
                        singleReport.state = itemState
                        val action = ReviewerScreenFragmentDirections.fromReviewToSingleReport()
                        action.singleReport = singleReport
                        Navigation.findNavController(requireView()).navigate(action)
                    }

                    Picasso.get().load(item?.mediaURL)
                        .placeholder(R.drawable.no_image_icon)
                        .into(itemView.reportImage)
                    itemView.reportImage.clipToOutline = true
                }

                addLoader(R.layout.network_state_loader) {
                    idLoader = R.id.progress_circular
                    idTextError = R.id.error_text_view
                }

                val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                setLayoutManager(layoutManager)
                authViewModel.getApprovedReports(reviewerApprovedReportsDataFactory)
                    .observe(viewLifecycleOwner, Observer {
                        submitList(it)
                    })
                authViewModel.approvedLoader(reviewerApprovedReportsDataFactory).observe(viewLifecycleOwner, Observer {
                    reviewerApprovedReportsDataFactory.responseLiveData.observe(viewLifecycleOwner, Observer {
                        val code = it.code
                        if(code == 401){
                            requireContext().toast(it.message.toString())
                            navigateWithUri("android-app://anapfoundation.navigation/signin".toUri())
                        }
                    })
                    submitNetwork(it as NetworkState)
                    when(loggedInUser?.isReviewer){
                        true -> {
                            if(loggedInUser?.totalApprovedReports == 0.toLong()){
                                noReportApproved.show()
                            }
                            else{
                                noReportApproved.hide()
                            }
                        }
                    }
                })
            }

        } catch (e: Exception) {
            Log.e(title, "Erro1 ${e.message.toString()}")
        }
    }
}
