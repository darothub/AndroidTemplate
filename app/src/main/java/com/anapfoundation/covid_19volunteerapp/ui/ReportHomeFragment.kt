package com.anapfoundation.covid_19volunteerapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.model.response.ReportResponse
import com.anapfoundation.covid_19volunteerapp.model.response.Reports
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.getName
import com.anapfoundation.covid_19volunteerapp.utils.extensions.observeRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.toast
import com.squareup.picasso.Picasso
import com.utsman.recycling.paged.setupAdapterPaged
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_report_home.*
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
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    val authViewModel: AuthViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel::class.java)
    }
    val title: String by lazy {
        getName()
    }
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

        val request = authViewModel.getReports(header)

//        val response = observeRequest(request, null, null)
//        response.observe(viewLifecycleOwner, Observer {
//
//        })


//        recyclerView.setupAdapter<String>(R.layout.report_item){adapter, context, list ->
//            bind { itemView, position, item ->
//                itemView.reportTitle.text = item
//                itemView.reportImage.clipToOutline = true
//                itemView.setOnClickListener {
////                    requireActivity().toast("Pos $position")
//                }
//            }
//
//            submitList(myList)
//        }



        try {

            recyclerView.setupAdapterPaged<ReportResponse>(R.layout.report_item){ adapter, context, list ->

                bind { itemView, position, item ->
                    Log.i(title, "report items ${item}")
                    itemView.reportTopic.text = item?.topic
                    itemView.reportStory.text = item?.story
                    itemView.reportLocation.text = "${item?.localGovernment} ${item?.state}"
                    Picasso.get().load(item?.mediaURL)
                        .placeholder(R.drawable.applogo)
                        .into(itemView.reportImage)
                    itemView.reportImage.clipToOutline = true
                }

                authViewModel.getReportss().observe(viewLifecycleOwner, Observer {
                    submitList(it)
                })

            }

        }catch (e:Exception){
            Log.e(title, e.message)
        }

        notificationIcon.setOnClickListener {
            findNavController().navigate(R.id.notificationFragment)
        }



    }



}
