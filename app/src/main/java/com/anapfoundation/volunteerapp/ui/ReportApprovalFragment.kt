package com.anapfoundation.volunteerapp.ui

import android.os.Bundle
import android.text.SpannableString
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.anapfoundation.volunteerapp.R
import com.anapfoundation.volunteerapp.data.paging.ReviewerUnapprovedReportsDataFactory
import com.anapfoundation.volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.volunteerapp.data.viewmodel.auth.approveReport
import com.anapfoundation.volunteerapp.data.viewmodel.auth.dismissReport
import com.anapfoundation.volunteerapp.model.DefaultResponse
import com.anapfoundation.volunteerapp.model.response.ReportResponse
import com.anapfoundation.volunteerapp.network.storage.StorageRequest
import com.anapfoundation.volunteerapp.utils.extensions.*
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_report_approval.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ReportApprovalFragment : DaggerFragment() {

    val title: String by lazy {
        getName()
    }
    val detailsText: String by lazy {
        getLocalisedString(R.string.report_details)
    }
    val spannableString: SpannableString by lazy {
        detailsText.setAsSpannable()
    }


    @Inject
    lateinit var reviewerUnapprovedReportsDataFactory: ReviewerUnapprovedReportsDataFactory

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    val authViewModel: AuthViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel::class.java)
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
    lateinit var singleReport: ReportResponse
    lateinit var approveBtn: Button
    lateinit var dismissBtn: Button
    val progressBar by lazy {
        reportApprovalBottomLayout.findViewById<ProgressBar>(R.id.includedProgressBar)
    }
    var total = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        requireActivity().setTheme(R.style.AppTheme)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_approval, container, false)
    }

    override fun onResume() {
        super.onResume()


        Log.i(title, "OnResume")


        arguments?.let {
            singleReport = ReportApprovalFragmentArgs.fromBundle(it).singleReport!!
        }

        val story = singleReport.story
        val storyLen = story?.length
        if (storyLen != null) {
            when{
                storyLen <= 80 -> reportApprovalStory.text = story
                storyLen > 80 -> {
                    val fullStopIndex = story.indexOf(".", 100)
                    if (fullStopIndex == -1){
                        reportApprovalStory.text = story
                    }else{
                        val contdText = story.substring(fullStopIndex+1)
                        reportApprovalStory.text = story.substring(0..fullStopIndex)
                        reportApprovalStoryContd.text = contdText
                        reportApprovalStoryContd.show()
                    }

                }
            }
        }

        Log.i(title, "reportID ${singleReport.id}")
        reportApprovalReportTopic.text = singleReport.topic
        reportApprovalReportLocation.text = "${singleReport.localGovernment}, ${singleReport.state}"
        Picasso.get().load(singleReport.mediaURL)
            .placeholder(R.drawable.no_image_icon)
            .into(reportApprovalImage)


        approveBtn = reportApprovalBottomLayout.findViewById(R.id.btn)
        approveBtn.text = getLocalisedString(R.string.approve_report)
        dismissBtn = reportApprovalBottomLayout.findViewById(R.id.secondBtn)
        dismissBtn.text = getLocalisedString(R.string.dismissText)
        dismissBtn.show()

        approveBtn.setOnClickListener {
            approveOrDismiss(singleReport.id.toString(), true)
        }
        dismissBtn.setOnClickListener {
            approveOrDismiss(singleReport.id.toString(), false)
        }



        reportApprovalBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        this.displayNotificationBell(
            loggedInUser,
            reportApprovalNotificationIcon,
            reportApprovalNotificationCount
        )


    }

    private fun approveOrDismiss(id:String, approve:Boolean=false) {
        val response:LiveData<Pair<Boolean, *>>
        when(approve){
            true -> {
                Log.i(title, "id ${singleReport.id}")
                val request = authViewModel.approveReport(id, header)
                response = observeRequest(request, progressBar, approveBtn)
            }
            false->{
                val request = authViewModel.dismissReport(singleReport.id.toString(), header)
                response = observeRequest(request, progressBar, approveBtn)
            }
        }
        onResponseObserver(response, approve)
    }

    private fun onResponseObserver(
        response: LiveData<Pair<Boolean, *>>,
        approve: Boolean
    ) {
        response.observe(viewLifecycleOwner, Observer {
            val (bool, result) = it
            when (bool) {
                true -> {
                    val res = result as DefaultResponse
                    Log.i(title, res.data.toString())
                    if (approve) {

                        toast(getLocalisedString(R.string.approved_successful))
                    } else {
                        toast(getLocalisedString(R.string.dismissed_successfully))
                    }
                    loggedInUser?.totalUnapprovedReports =
                        loggedInUser?.totalUnapprovedReports?.minus(1.toLong())
                    storageRequest.saveData(loggedInUser, "loggedInUser")
                    findNavController().navigate(R.id.reviewerScreenFragment)
                }
                else -> Log.i(title, "error $result")
            }

        })
    }


//
//    override fun onPause() {
//        super.onPause()
//        Log.i(title, "OnPause")
//
//
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        Log.i(title, "OnDestroy")
//
//    }

}
