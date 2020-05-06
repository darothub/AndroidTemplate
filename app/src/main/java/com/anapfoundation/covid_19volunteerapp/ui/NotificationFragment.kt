package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.model.Report
import com.anapfoundation.covid_19volunteerapp.utils.extensions.hide
import com.anapfoundation.covid_19volunteerapp.utils.extensions.show
import com.anapfoundation.covid_19volunteerapp.utils.extensions.toast
import com.utsman.recycling.setupAdapter
import kotlinx.android.synthetic.main.create_report_questions_item.view.*
import kotlinx.android.synthetic.main.fragment_create_report_questions.*
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.notification_item.view.*

/**
 * A simple [Fragment] subclass.
 */
class NotificationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val myList = listOf<Report>(Report("Covid-19 process attacks", "This is the details. This is the details" +
                "This is the details. This is the details. This is the details", "Yaba, Lagos"), Report("Covid-19 process attacks", "This is the details. This is the details" +
                "This is the details. This is the details. This is the details", "Yaba, Lagos"),
            Report("Covid-19 process attacks", "This is the details. This is the details" +
                    "This is the details. This is the details. This is the details", "Yaba, Lagos"), Report("Covid-19 process attacks", "This is the details. This is the details" +
                    "This is the details. This is the details. This is the details", "Yaba, Lagos"),
            Report("Covid-19 process attacks", "This is the details. This is the details" +
                    "This is the details. This is the details. This is the details", "Yaba, Lagos"), Report("Covid-19 process attacks", "This is the details. This is the details" +
                    "This is the details. This is the details. This is the details", "Yaba, Lagos"))
        notificationRecyclerView.setupAdapter<Report>(R.layout.notification_item){ adapter, context, list ->
            bind { itemView, position, item ->
                itemView.notificationHeadLine.text = item?.headline
                itemView.notificationLocation.text = item?.location
                itemView.notificationDetail.text = item?.details
                if (position == 0){
                    itemView.notificationStatus.show()
                }
                else{
                    itemView.notificationStatus.hide()
                }
                itemView.setOnClickListener {
                    findNavController().navigate(R.id.singleReportFragment)
                }
            }

            submitList(myList)
            requireContext().toast("listSize ${list?.size}")
        }
    }
}
