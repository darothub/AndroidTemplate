package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.utils.extensions.localized
import com.anapfoundation.covid_19volunteerapp.utils.extensions.toast
import com.utsman.recycling.setupAdapter
import kotlinx.android.synthetic.main.create_report_item.view.*
import kotlinx.android.synthetic.main.fragment_create_report.*
import kotlinx.android.synthetic.main.fragment_report_home.*
import kotlinx.android.synthetic.main.report_item.view.*

/**
 * A simple [Fragment] subclass.
 */
class CreateReportFragment : Fragment() {
    private val nav by lazy {
        Navigation.findNavController(createReportAppBar)
    }
    private val hunger by lazy {
        requireContext().localized(R.string.hunger)
    }
    private val foodPrices by lazy {
        requireContext().localized(R.string.food_prices)
    }
    private val health by lazy {
        requireContext().localized(R.string.health)
    }
    private val lockdown by lazy {
        requireContext().localized(R.string.lockdown)
    }
    private val category by lazy {
        requireContext().localized(R.string.category)
    }
    private val death by lazy {
        requireContext().localized(R.string.death)
    }
    private val navController by lazy {
        findNavController()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_report, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val myList = listOf<String>(hunger, death, foodPrices, category, health, lockdown)
//        NavigationUI.setupWithNavController(createReportToolbar, nav)

        createReportRecyclerView.setupAdapter<String>(R.layout.create_report_item){adapter, context, list ->
            bind { itemView, position, item ->
                itemView.createReportSubject.text = item
                itemView.setOnClickListener {
                    val action = CreateReportFragmentDirections.actionCreateReportFragmentToCreateReportOptionsFragment()
                    action.option = it.createReportSubject.text.toString()
                    findNavController().navigate(action)
                }
            }
            setLayoutManager(GridLayoutManager(requireContext(), 2))

            submitList(myList)
        }


    }


}
