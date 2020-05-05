package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.model.CityClass
import com.anapfoundation.covid_19volunteerapp.utils.extensions.getName
import com.anapfoundation.covid_19volunteerapp.utils.extensions.readCitiesAndLgaData
import com.anapfoundation.covid_19volunteerapp.utils.extensions.setBackButtonNavigation
import com.anapfoundation.covid_19volunteerapp.utils.extensions.setSpinnerAdapterData
import kotlinx.android.synthetic.main.fragment_edit_profile.*
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
    }


    private fun setAdapterData() {
        val newList = arrayListOf<String>()
        newList.add("States")

        newList.addAll(stateLgaMap.keys.toSortedSet())

        val adapterState =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, newList)
        reportUploadState.adapter = adapterState
        reportUploadState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val lga = ArrayList<String>()
                stateLgaMap.get(newList[position])!!.toList().mapTo(lga, {
                    it.name
                })

                val adapterLga = ArrayAdapter(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    lga
                )
                reportUploadLGA.adapter = adapterLga
            }

        }
    }
}
