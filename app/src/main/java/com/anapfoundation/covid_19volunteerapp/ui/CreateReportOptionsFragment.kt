package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.utils.extensions.toast
import kotlinx.android.synthetic.main.fragment_create_report_options.*

/**
 * A simple [Fragment] subclass.
 */
class CreateReportOptionsFragment : Fragment() {

    var h = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_create_report_options, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            h += CreateReportOptionsFragmentArgs.fromBundle(it).option
        }

        requireContext().toast(h)
        setBackButtonAction()
    }

    private fun setBackButtonAction(){
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}
