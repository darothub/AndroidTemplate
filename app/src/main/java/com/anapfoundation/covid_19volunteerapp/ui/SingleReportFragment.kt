package com.anapfoundation.covid_19volunteerapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.text.SpannableString
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.PagedList
import androidx.transition.TransitionInflater

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.paging.ReportDataFactory
import com.anapfoundation.covid_19volunteerapp.data.paging.ReviewerUnapprovedReportsDataFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.model.response.ReportResponse
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_report_home.*
import kotlinx.android.synthetic.main.fragment_single_report.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class SingleReportFragment : DaggerFragment() {
    val title: String by lazy {
        getName()
    }
    val detailsText: String by lazy {
        requireContext().getLocalisedString(R.string.report_details)
    }
    val spannableString: SpannableString by lazy {
        detailsText.setAsSpannable()
    }

    val capture by lazy {
        Bitmap.createBitmap(
            singleReportImage.width,
            singleReportImage.height,
            Bitmap.Config.ARGB_8888
        )
    }
    val canvas by lazy {
        Canvas(capture)
    }

    lateinit var singleReport: ReportResponse
    lateinit var uri: String

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

    var total = 0


    @Inject
    lateinit var reviewerUnapprovedReportsDataFactory: ReviewerUnapprovedReportsDataFactory

    val args: SingleReportFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    val authViewModel: AuthViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_report, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        singleReport = args.singleReport!!
    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()

//        arguments?.let {
//            singleReport = SingleReportFragmentArgs.fromBundle(it).singleReport!!
//        }

        val story = singleReport.story
        val storyLen = story?.length
        if (storyLen != null) {
            when{
                storyLen <= 100 -> singleReportStory.text = story
                storyLen > 100 -> {
                    val fullStopIndex = story.indexOf(".", 100)
                    Log.i(title, "Index $fullStopIndex")
                    if (fullStopIndex == -1){
                        singleReportStory.text = story
                    }else{
                        val contdText = story.substring(fullStopIndex+1)
                        singleReportStory.text = story.substring(0..fullStopIndex)
                        singleReportStoryContd.text = contdText
                        singleReportStoryContd.show()
                    }

                }
            }
        }

        singleReportReportTopic.text = singleReport.topic
        singleReportReportLocation.text = "${singleReport.localGovernment}, ${singleReport.state}"
        Log.i(title, "state ${singleReport.state}")
        Picasso.get().load(singleReport.mediaURL)
            .placeholder(R.drawable.no_image_icon)
            .into(singleReportImage)


        val imageDrawable = singleReportImage.drawable
        singleReportappBar.background = imageDrawable

        singleReportBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        this.displayNotificationBell(
            loggedInUser,
            singleReportNotificationIcon,
            singleReportNotificationCount
        )


    }


}
