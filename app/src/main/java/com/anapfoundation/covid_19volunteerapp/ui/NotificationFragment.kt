package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.model.request.Report
import com.anapfoundation.covid_19volunteerapp.utils.extensions.hide
import com.anapfoundation.covid_19volunteerapp.utils.extensions.show
import com.utsman.recycling.setupAdapter
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
        val myList = listOf<Report>(
            Report(
                "Covid-19 process attacks",
                "high",
                "This is the details. This is the details" +
                        "This is the details. This is the details. This is the details",
                "Yaba, Lagos"
            )
        )
        notificationRecyclerView.setupAdapter<Report>(R.layout.notification_item){ adapter, context, list ->
            bind { itemView, position, item ->
                itemView.notificationHeadLine.text = item?.topic
                itemView.notificationLocation.text = item?.state
                itemView.notificationDetail.text = item?.story
                if (position == 0){
                    itemView.notificationStatus.show()
                }
                else{
                    itemView.notificationStatus.hide()
                }
                itemView.setOnClickListener {
                    findNavController().navigate(R.id.reportReviewFragment)
                }
            }

            submitList(myList)
//            requireContext().toast("listSize ${list?.size}")
        }
    }
}
