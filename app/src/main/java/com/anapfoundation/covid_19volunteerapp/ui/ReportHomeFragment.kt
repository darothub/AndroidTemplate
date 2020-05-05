package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.utils.extensions.toast
import com.utsman.recycling.setupAdapter
import kotlinx.android.synthetic.main.fragment_report_home.*
import kotlinx.android.synthetic.main.report_item.view.*

/**
 * A simple [Fragment] subclass.
 */
class ReportHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val myList = listOf<String>("Hello", "Nigeria", "Ghana", "Algeria", "Panama", "Antarctica")
        recyclerView.setupAdapter<String>(R.layout.report_item){adapter, context, list ->
            bind { itemView, position, item ->
                itemView.reportTitle.text = item
                itemView.setOnClickListener {
                    requireActivity().toast("Pos $position")
                }
            }

            submitList(myList)
        }

        notificationIcon.setOnClickListener {
            findNavController().navigate(R.id.notificationFragment)
        }
    }

}
