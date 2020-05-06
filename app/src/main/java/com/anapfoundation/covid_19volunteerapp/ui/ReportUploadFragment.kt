package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.model.CityClass
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_report_review.*
import kotlinx.android.synthetic.main.fragment_report_upload.*

/**
 * A simple [Fragment] subclass.
 */
class ReportUploadFragment : Fragment() {

    val title: String by lazy {
        getName()
    }
    val stateLgaMap: HashMap<String, List<CityClass>> by lazy {
        requireActivity().readCitiesAndLgaData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_upload, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        reportUploadBackButton.setBackButtonNavigation()

        requireContext().setSpinnerAdapterData(reportUploadState, reportUploadLGA, stateLgaMap)
        val submitReportBtn = reportUploadBottomLayout.findViewById<Button>(R.id.btn)
        submitReportBtn.text = requireContext().localized(R.string.submit_text)
        submitReportBtn.setOnClickListener{
            Toast.makeText(requireContext(), "hey", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.reportReviewFragment)
        }
    }



}
