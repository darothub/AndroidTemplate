package com.anapfoundation.volunteerapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.anapfoundation.volunteerapp.R
import com.anapfoundation.volunteerapp.data.paging.ReviewerUnapprovedReportsDataFactory
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
import com.utsman.recycling.paged.setupAdapterPaged
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_unapproved_report.*
import kotlinx.android.synthetic.main.report_item.view.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class UnapprovedReportFragment : DaggerFragment() {
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
    lateinit var reviewerUnapprovedReportsDataFactory: ReviewerUnapprovedReportsDataFactory

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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_unapproved_report, container, false)
    }

    override fun onStart() {
        super.onStart()


//        Log.i(title, "Onstart")
    }


    override fun onPause() {
        super.onPause()

//        Log.i(title, "onPause")
    }

    override fun onResume() {
        super.onResume()
        setRecyclerViewForUnapprovedReports()
//        Log.i(title, "OnResume")
    }

    /**
     * Set up recyclerview for unapproved reports
     *
     */
    private fun setRecyclerViewForUnapprovedReports() {
        try {

//            Log.i(title, "header $header")
            reviewerRecyclerView.setupAdapterPaged<ReportResponse>(R.layout.report_item) { adapter, context, list ->

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
//                                Log.i("State", "${res.data.stateName}")
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

//                        Log.i(title, "LOCAL $localGovernment")
                        singleReport.localGovernment = localGovernment
                        singleReport.state = itemState
                        val action = ReviewerScreenFragmentDirections.toApprovalFragment()
                        action.singleReport = singleReport
                        goto(action)
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

                authViewModel.getUnapprovedReports(reviewerUnapprovedReportsDataFactory)
                    .observe(viewLifecycleOwner, Observer {
                        submitList(it)
                    })
                authViewModel.unApprovedReportLoader(reviewerUnapprovedReportsDataFactory).observe(viewLifecycleOwner, Observer {
                    //Observe unapproved report error code
                    reviewerUnapprovedReportsDataFactory.responseLiveData.observe(viewLifecycleOwner, Observer {
                        val code = it.code
                        if(code == 401){
                            toast(it.message.toString())
                            goto("android-app://anapfoundation.navigation/signin".toUri())
                        }
                    })
                    submitNetwork(it)

                })

            }
            when (loggedInUser?.totalUnapprovedReports) {
                0.toLong() -> {
                    noReportUnapproved.setOnClickListener {
                        goto(R.id.createReportFragment)
                    }
                    noReportUnapproved.show()
                }
                else ->  noReportUnapproved.hide()
            }
            Log.i(title, "unapproved report total ${loggedInUser?.totalUnapprovedReports}")

        } catch (e: Exception) {
            Log.e(title, e.message.toString())
        }
    }

}
