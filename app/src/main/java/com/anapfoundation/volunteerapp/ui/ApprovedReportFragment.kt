package com.anapfoundation.volunteerapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.anapfoundation.volunteerapp.R
import com.anapfoundation.volunteerapp.data.paging.ReviewerApprovedReportsDataFactory
import com.anapfoundation.volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.volunteerapp.data.viewmodel.user.UserViewModel
import com.anapfoundation.volunteerapp.data.viewmodel.user.getSingleLGA
import com.anapfoundation.volunteerapp.model.Location
import com.anapfoundation.volunteerapp.model.response.ReportResponse
import com.anapfoundation.volunteerapp.model.response.TopicResponse
import com.anapfoundation.volunteerapp.network.storage.StorageRequest
import com.anapfoundation.volunteerapp.utils.extensions.*
import com.squareup.picasso.Picasso
import com.utsman.recycling.paged.extentions.NetworkState
import com.utsman.recycling.paged.setupAdapterPaged
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_approved_report.*
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


    override fun onResume() {
        super.onResume()
        setRecyclerViewForApprovedReports()
        crashReportByUser(loggedInUser)
    }


    /**
     * Set recyclerview for approved reports
     *
     */
    private fun setRecyclerViewForApprovedReports() {
        try {
            approvedReportsRecyclerView.setupAdapterPaged<ReportResponse>(R.layout.report_item) { _, _, _ ->

                bind { itemView, _, item ->

                    //Get rating
                    val ratingRequest = authViewModel.getRating(item?.topic.toString(), header)
                    val ratingResponse = observeRequest(ratingRequest, null, null)
                    //Observe and un-wrap response
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

                    //Get location
                    val locationRequest = userViewModel.getSingleLGA("${item?.localGovernment}")
                    val stateResponse = observeRequest(locationRequest, null, null)
                    //Observe and un-wrap response
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
                        goto(action)
                    }

                    Picasso.get().load(item?.mediaURL)
                        .placeholder(R.drawable.no_image_icon)
                        .into(itemView.reportImage)
                    itemView.reportImage.clipToOutline = true
                }

                //Adds network state layout
                addLoader(R.layout.network_state_loader) {
                    idLoader = R.id.progress_circular
                    idTextError = R.id.error_text_view
                }

                //Set layout manager
                val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                setLayoutManager(layoutManager)

                //Get paged approved reports
                authViewModel.getApprovedReports(reviewerApprovedReportsDataFactory)
                    .observe(viewLifecycleOwner, Observer {
                        submitList(it)
                    })
                //Observe paged approved reports network state
                authViewModel.approvedLoader(reviewerApprovedReportsDataFactory).observe(viewLifecycleOwner, Observer {
                    //Observe error code
                    reviewerApprovedReportsDataFactory.responseLiveData.observe(viewLifecycleOwner, Observer {
                        val code = it.code
                        if(code == 401){
                            toast(it.message.toString())
                            navigateWithUri("android-app://anapfoundation.navigation/signin".toUri())
                        }
                    })
                    //Submit network state
                    submitNetwork(it as NetworkState)
                    //When user is a reviewer


                })
            }

            when(loggedInUser?.totalApprovedReports){
                0.toLong() ->  noReportApproved.show()
                in 1.toLong()..1000.toLong() -> noReportApproved.hide()
            }


        } catch (e: Exception) {
            Log.e(title, "Erro1 ${e.message.toString()}")
        }
    }
}
