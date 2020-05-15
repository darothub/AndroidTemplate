package com.anapfoundation.covid_19volunteerapp.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.text.SpannableString
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.approveReport
import com.anapfoundation.covid_19volunteerapp.model.DefaultResponse
import com.anapfoundation.covid_19volunteerapp.model.response.ReportResponse
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_report_approval.*
import kotlinx.android.synthetic.main.fragment_single_report.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ReportApprovalFragment : DaggerFragment() {

    val title:String by lazy {
        getName()
    }
    val detailsText:String by lazy {
        requireContext().getLocalisedString(R.string.report_details)
    }
    val spannableString: SpannableString by lazy {
        detailsText.setAsSpannable()
    }

    val capture by lazy {
        Bitmap.createBitmap(reportApprovalImage.width, reportApprovalImage.height, Bitmap.Config.ARGB_8888)
    }

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    val authViewModel: AuthViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel::class.java)
    }
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
    lateinit var singleReport:ReportResponse
    lateinit var  approveBtn:Button
    val progressBar by lazy {
        reportApprovalBottomLayout.findViewById<ProgressBar>(R.id.includedProgressBar)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        requireActivity().setTheme(R.style.AppTheme)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_approval, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    override fun onResume() {
        super.onResume()

        reportApprovalBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        Log.i(title, "OnResume")
        approveBtn = reportApprovalBottomLayout.findViewById<Button>(R.id.includeBtn)
        approveBtn.text = requireContext().getLocalisedString(R.string.approve_report)

        arguments?.let {
            singleReport = ReportApprovalFragmentArgs.fromBundle(it).singleReport!!
        }


        reportApprovalReportTopic.text = singleReport.topic
        reportApprovalHeadline.text = singleReport.topic
        reportApprovalReportLocation.text = "${singleReport.localGovernment}, ${singleReport.state}"
        reportApprovalStory.text = singleReport.story
        reportApprovalStoryContd.text = ""
        Picasso.get().load(singleReport.mediaURL)
            .placeholder(R.drawable.applogo)
            .into(reportApprovalImage)


        val imageDrawable = reportApprovalImage.drawable
        reportApprovalappBar.background = imageDrawable
        approveBtn.setOnClickListener {
            Log.i(title, "id ${singleReport.id}")
            val request = authViewModel.approveReport(singleReport.id.toString(), header)
            val response    = observeRequest(request, progressBar, approveBtn)

            response.observe(viewLifecycleOwner, Observer {
                val (bool, result) = it

                when (bool) {
                    true -> {
                        val res = result as DefaultResponse
                        Log.i(title, res.data.toString())
                        requireContext().toast(res.data.toString())

                    }
                    else -> Log.i(title, "error $result")
                }

            })
        }

        reportApprovalBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }


    }

    override fun onPause() {
        super.onPause()
        Log.i(title, "OnPause")


    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(title, "OnDestroy")

    }

}
