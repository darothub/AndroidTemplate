package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import android.text.SpannableString
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.utils.extensions.getName
import com.anapfoundation.covid_19volunteerapp.utils.extensions.getLocalisedString
import com.anapfoundation.covid_19volunteerapp.utils.extensions.setAsSpannable
import com.anapfoundation.covid_19volunteerapp.utils.extensions.toast
import kotlinx.android.synthetic.main.fragment_report_review.*

/**
 * A simple [Fragment] subclass.
 */
class ReportReviewFragment : Fragment() {

    val title:String by lazy {
        getName()
    }
    val detailsText:String by lazy {
        requireContext().getLocalisedString(R.string.report_details)
    }
    val spannableString: SpannableString by lazy {
        detailsText.setAsSpannable()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        requireActivity().setTheme(R.style.AppTheme)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_review, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


//        val ssb = SpannableStringBuilder()
//        ssb.append(requireContext().localized(R.string.report_details))
//        ssb.setSpan(ImageSpan(requireContext(), R.drawable.very_bad), 20, detailsText.length-1,
//            Spanned.SPAN_MARK_MARK)


    }

    override fun onResume() {
        super.onResume()

        reviewBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }


        requireActivity().onBackPressedDispatcher.addCallback {

            findNavController().popBackStack()

//            requireContext().toast("Review fragment")

        }
//
//        val res = detailsText.split(".").dropLastWhile { it.isEmpty() }.toTypedArray().size - 1
//        val occ = detailsText.indexOf(".", 100)
        Log.i(title, "OnResume")
        val approveBtn = reviewBottomLayout.findViewById<Button>(R.id.btn)
        approveBtn.text = requireContext().getLocalisedString(R.string.approve_report)
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
