package com.anapfoundation.covid_19volunteerapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.net.toUri
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.paging.ReportDataFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.user.UserViewModel
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.user.getSingleLGA
import com.anapfoundation.covid_19volunteerapp.model.LGA
import com.anapfoundation.covid_19volunteerapp.model.Location
import com.anapfoundation.covid_19volunteerapp.model.StatesList
import com.anapfoundation.covid_19volunteerapp.model.response.Data
import com.anapfoundation.covid_19volunteerapp.model.response.ReportResponse
import com.anapfoundation.covid_19volunteerapp.model.response.Reports
import com.anapfoundation.covid_19volunteerapp.model.response.TopicResponse
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import com.utsman.recycling.extentions.Recycling
import com.utsman.recycling.paged.setupAdapterPaged
import com.utsman.recycling.setupAdapter
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_report_home.*
import kotlinx.android.synthetic.main.layout_upload_gallery.view.*
import kotlinx.android.synthetic.main.report_item.view.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ReportHomeFragment : DaggerFragment() {

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


    @Inject
    lateinit var reportDataFactory: ReportDataFactory
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
    var singleReport = ReportResponse()
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


    }
    var lga = ""
    var state = ""

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        Log.i(title, "onResume")
        try {

            Log.i(title, "header $header")
            recyclerView.setupAdapterPaged<ReportResponse>(R.layout.report_item){ adapter, context, list ->
                bind { itemView, position, item ->
                    Log.i(title, "report items ${item}")

                    getTopicAndRatingById(item, itemView)

                    getStateAndLgaById(item, itemView)

                    itemView.reportStory.text = item?.story

                    Log.i(title, "${singleReport.mediaURL}")

                    itemView.setOnClickListener {
                        prepareReportForReading(item, itemView)
                        sendReportToSingleReportScreen()

                        Log.i(title, "report items ${singleReport.localGovernment}")
                    }

                    loadItemImage(item, itemView)
                }


                authViewModel.getReportss(reportDataFactory).observe(viewLifecycleOwner, Observer {
                    submitList(it)
                })


//                setupData(this, 100, 0)


            }

        }catch (e:Exception){
            Log.e(title, e.message)
        }

        notificationIcon.setOnClickListener {
            findNavController().navigate(R.id.notificationFragment)
        }



    }

    private fun loadItemImage(
        item: ReportResponse?,
        itemView: View
    ) {
        Picasso.get().load(item?.mediaURL)
            .placeholder(R.drawable.applogo)
            .into(itemView.reportImage)
        itemView.reportImage.clipToOutline = true
    }

    private fun sendReportToSingleReportScreen() {
        val action = ReportHomeFragmentDirections.toSingleReportScreen()
        action.singleReport = singleReport
        Navigation.findNavController(requireView()).navigate(action)
    }

    private fun prepareReportForReading(
        item: ReportResponse?,
        itemView: View
    ) {
        singleReport.id = item?.id
        singleReport.topic = itemView.reportTopic.text.toString()
        singleReport.story = itemView.reportStory.text.toString()
        singleReport.mediaURL = item?.mediaURL

        singleReport.localGovernment = lga
        singleReport.state = state
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

    // function for setup data
    private fun setupData(recycling: Recycling<ReportResponse>, first: Long?, after:Long?) {
        val request =  authViewModel.getReports(header, first, after)
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
    override fun onPause() {
        super.onPause()
        Log.i(title, "onpause")
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
