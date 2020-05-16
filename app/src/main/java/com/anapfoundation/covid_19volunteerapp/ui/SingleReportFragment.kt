package com.anapfoundation.covid_19volunteerapp.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.text.SpannableString
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.model.response.ReportResponse
import com.anapfoundation.covid_19volunteerapp.utils.extensions.getLocalisedString
import com.anapfoundation.covid_19volunteerapp.utils.extensions.getName
import com.anapfoundation.covid_19volunteerapp.utils.extensions.setAsSpannable
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_single_report.*

/**
 * A simple [Fragment] subclass.
 */
class SingleReportFragment : Fragment() {
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
        Bitmap.createBitmap(singleReportImage.width, singleReportImage.height, Bitmap.Config.ARGB_8888)
    }
    val canvas by lazy {
        Canvas(capture)
    }

    lateinit var singleReport: ReportResponse
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_report, container, false)
    }

    override fun onStart() {
        super.onStart()

        arguments?.let {
            singleReport = SingleReportFragmentArgs.fromBundle(it).singleReport!!
        }


        singleReportReportTopic.text = singleReport.topic
        singleReportHeadline.text = singleReport.topic
        singleReportReportLocation.text = "${singleReport.localGovernment}, ${singleReport.state}"
        singleReportStory.text = singleReport.story
        singleReportStoryContd.text = ""
        Picasso.get().load(singleReport.mediaURL)
            .placeholder(R.drawable.applogo)
            .into(singleReportImage)


        val imageDrawable = singleReportImage.drawable
        singleReportappBar.background = imageDrawable

        singleReportBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}
