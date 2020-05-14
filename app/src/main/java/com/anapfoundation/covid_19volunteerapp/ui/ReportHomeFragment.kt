package com.anapfoundation.covid_19volunteerapp.ui

import android.annotation.SuppressLint
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
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.user.UserViewModel
import com.anapfoundation.covid_19volunteerapp.model.LGA
import com.anapfoundation.covid_19volunteerapp.model.StatesList
import com.anapfoundation.covid_19volunteerapp.model.response.Data
import com.anapfoundation.covid_19volunteerapp.model.response.ReportResponse
import com.anapfoundation.covid_19volunteerapp.model.response.Reports
import com.anapfoundation.covid_19volunteerapp.model.response.TopicResponse
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import com.utsman.recycling.paged.setupAdapterPaged
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
    private val bottomSheetDialog by lazy {
        BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
    //inflate bottomSheetView
    private val bottomSheetView by lazy {
        LayoutInflater.from(requireContext()).inflate(
            R.layout.layout_upload_gallery,
            requireActivity().findViewById(R.id.uploadBottomSheetContainer)
        )
    }
    //Get included layout in the parent layout/* layout.fragment_report_upload */
    private val logoutLayout by lazy {
        bottomSheetView.findViewById<View>(R.id.logoutLayout)
    }

    private val uploadLayout by lazy {
        bottomSheetView.findViewById<View>(R.id.imageUploadLayout)
    }

    private val yesButton by lazy{
        bottomSheetView.yesButton
    }
    private val noButton by lazy{
        bottomSheetView.noButton
    }

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

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        Log.i(title, "onResume")
        try {

            Log.i(title, "header $header")
            recyclerView.setupAdapterPaged<ReportResponse>(R.layout.report_item){ adapter, context, list ->

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

                    val stateRequest = userViewModel.getStates("37")
                    val stateResponse = observeRequest(stateRequest, null, null)
                   stateResponse.observe(viewLifecycleOwner, Observer {
                       val(bool, result) = it
                       when(bool){
                           true -> {
                               val res = result as StatesList
                               val state =res.data.filter {
                                   it.id == item?.state
                               }
                               Log.i("State", "$state")
                               val localGovtRequest = userViewModel.getLocal(state[0].id, "45", "")
                               val localGovtResponse = observeRequest(localGovtRequest, null, null)
                               localGovtResponse.observe(viewLifecycleOwner, Observer {
                                   val(boolLocal, resultLocal) = it
                                   when(boolLocal){
                                       true -> {
                                           val res = resultLocal as LGA
                                           val lga =res.data.filter {
                                               it.id == item?.localGovernment
                                           }

                                           itemView.reportLocation.text = "${lga[0].localGovernment}, ${state[0].state}"
                                       }
                                   }
                               })

                           }
                       }
                   })

                    itemView.reportStory.text = item?.story

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
