package com.anapfoundation.covid_19volunteerapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.paging.ReportDataFactory
import com.anapfoundation.covid_19volunteerapp.data.paging.ReviewerApprovedReportsDataFactory
import com.anapfoundation.covid_19volunteerapp.data.paging.ReviewerUnapprovedReportsDataFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.user.UserViewModel
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.user.getSingleLGA
import com.anapfoundation.covid_19volunteerapp.model.Location
import com.anapfoundation.covid_19volunteerapp.model.response.ReportResponse
import com.anapfoundation.covid_19volunteerapp.model.response.Reports
import com.anapfoundation.covid_19volunteerapp.model.response.TopicResponse
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import com.squareup.picasso.Picasso
import com.utsman.recycling.extentions.Recycling
import com.utsman.recycling.paged.setupAdapterPaged
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_approved_report.*
import kotlinx.android.synthetic.main.fragment_report_home.*
import kotlinx.android.synthetic.main.fragment_reviewer_screen.*
import kotlinx.android.synthetic.main.report_item.view.*
import javax.inject.Inject
import kotlin.math.absoluteValue

/**
 * A simple [Fragment] subclass.
 */
class ReportHomeFragment : DaggerFragment() {

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
    lateinit var reportDataFactory: ReportDataFactory
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
        return inflater.inflate(R.layout.fragment_report_home, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        this.displayNotificationBell(
//            authViewModel,
//            loggedInUser,
//            reviewerUnapprovedReportsDataFactory,
//            reporterNotificationIcon,
//            reporterNotificationCount
//        )
        getReportCount()

    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        Log.i(title, "onResume")
        try {

            Log.i(title, "header $header")
            recyclerView.setupAdapterPaged<ReportResponse>(R.layout.report_item){ adapter, context, list ->
                bind { itemView, position, item ->
                    Log.i(title, "report items ${list}")

                    getTopicAndRatingById(item, itemView)

                    getStateAndLgaById(item, itemView)

                    itemView.reportStory.text = item?.story


                    itemView.setOnClickListener {
                        var singleReport = prepareSingleReport(item, itemView)
                        val action = ReportHomeFragmentDirections.toSingleReportScreen()
                        action.singleReport = singleReport
                        Navigation.findNavController(requireView()).navigate(action)
                        sendReportToSingleReportScreen()

                        Log.i(title, "report items ${singleReport.localGovernment}")
                    }

                    loadItemImage(item, itemView)
                }
                addLoader(R.layout.network_state_loader) {
                    idLoader = R.id.progress_circular
                    idTextError = R.id.error_text_view
                }
                authViewModel.getReportss(reportDataFactory).observe(viewLifecycleOwner, Observer {
                    submitList(it)
                })

                authViewModel.getReporterLoader(reportDataFactory).observe(viewLifecycleOwner, Observer {
                    reportDataFactory.responseLiveData.observe(viewLifecycleOwner, Observer {
                        val code = it.code
                        if(code == 401){
                            requireContext().toast(it.message.toString())
                            navigateWithUri("android-app://anapfoundation.navigation/signin".toUri())
                        }
                    })
                    submitNetwork(it)
                })
            }
            if (loggedInUser?.totalReports == 0.toLong()){
                noReportHome.show()
                noReportHome.setOnClickListener {
                    findNavController().navigate(R.id.createReportFragment)
                }

            }else{
                noReportHome.hide()

            }

        }catch (e:Exception){
            Log.e(title, e.message)
        }

        reporterNotificationIcon.setOnClickListener {
            findNavController().navigate(R.id.reviewerScreenFragment)
        }


    }

    private fun getReportCount() {
        when (loggedInUser?.isReviewer) {
            true -> {

                var unApprovedTotal = 0
                authViewModel.getUnapprovedReports(reviewerUnapprovedReportsDataFactory).observe(viewLifecycleOwner, Observer {
                    it.addWeakCallback(null, object :PagedList.Callback(){
                        override fun onChanged(position: Int, count: Int) {}

                        override fun onInserted(position: Int, count: Int) {
                            unApprovedTotal += count
                            Log.i(title, "NewUnapprovedcount ${unApprovedTotal}")
                            reporterNotificationCount.text = "${unApprovedTotal}"
                            loggedInUser?.totalUnapprovedReports = unApprovedTotal.toLong()
                            storageRequest.saveData(loggedInUser, "loggedInUser")
                        }

                        override fun onRemoved(position: Int, count: Int) {}

                    })
                })
                var approvedTotal = 0
                authViewModel.getApprovedReports(reviewerApprovedReportsDataFactory)
                    .observe(viewLifecycleOwner, Observer {

                        it.addWeakCallback(null, object: PagedList.Callback(){
                            override fun onChanged(position: Int, count: Int) {}

                            override fun onInserted(position: Int, count: Int) {
                                approvedTotal += count
                                Log.i(title, "NewApprovedcount ${approvedTotal}")
                                loggedInUser?.totalApprovedReports = approvedTotal.toLong()
                            }

                            override fun onRemoved(position: Int, count: Int) {}

                        })
                    })
                reporterNotificationIcon.show()
                reporterNotificationCount.show()
            }
            false -> reporterNotificationIcon.hide()
        }
    }

    private fun prepareSingleReport(
        item: ReportResponse?,
        itemView: View
    ): ReportResponse {
        var singleReport = ReportResponse()
        singleReport.id = item?.id
        singleReport.topic = itemView.reportTopic.text.toString()
        singleReport.story = itemView.reportStory.text.toString()
        singleReport.mediaURL = item?.mediaURL
        val location = itemView.reportLocation.text.toString()
        val lga = location.split(",")[0]
        val state = location.split(",")[1]
        singleReport.localGovernment = lga
        Log.i(title, "LGA ${singleReport.localGovernment}  lga $lga")
        singleReport.state = state
        return singleReport
    }

    private fun loadItemImage(
        item: ReportResponse?,
        itemView: View
    ) {
        Picasso.get().load(item?.mediaURL)
            .placeholder(R.drawable.no_image_icon)
            .into(itemView.reportImage)
        itemView.reportImage.clipToOutline = true
    }

    private fun sendReportToSingleReportScreen() {

    }


    private fun getTopicAndRatingById(
        item: ReportResponse?,
        itemView: View
    ) {
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
    }

    private fun getStateAndLgaById(
        item: ReportResponse?,
        itemView: View
    ) {
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
    }

    override fun onPause() {
        super.onPause()
        Log.i(title, "onpause")
        total = 0
    }


    override fun onStart() {
        super.onStart()
        Log.i(title, "onStart")
    }

    override fun onDetach() {
        super.onDetach()
        Log.i(title, "detached")
    }



}
