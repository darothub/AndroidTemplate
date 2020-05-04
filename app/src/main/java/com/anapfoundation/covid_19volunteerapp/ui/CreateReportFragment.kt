package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.model.ReportQuestionModel
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import com.utsman.recycling.setupAdapter
import kotlinx.android.synthetic.main.create_report_item.view.*
import kotlinx.android.synthetic.main.fragment_create_report.*

/**
 * A simple [Fragment] subclass.
 */
class CreateReportFragment : Fragment() {
    private val nav by lazy {
        Navigation.findNavController(createReportAppBar)
    }

    private val navController by lazy {
        findNavController()
    }
    private val indexQuestion by lazy {
        requireContext().indexCases()

    }
    private val awarenessQuestion by lazy {
        requireContext().awareness()
    }
    private val enlightenmentQuestion by lazy {
        requireContext().publicEnglightenment()
    }
    private val measuresQuestion by lazy {
        requireContext().measures()
    }
    private val complianceQuestion by lazy {
        requireContext().compliance()
    }
    private val challengesQuestion by lazy {
        requireContext().challenges()
    }
    private val palliativesQuestion by lazy {
        requireContext().palliatives()
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

        val myList = listOf<ReportQuestionModel>(indexQuestion, awarenessQuestion,
            enlightenmentQuestion, measuresQuestion, complianceQuestion, challengesQuestion,
        palliativesQuestion)
//        NavigationUI.setupWithNavController(createReportToolbar, nav)


        createReportRecyclerView.setupAdapter<ReportQuestionModel>(R.layout.create_report_item){ adapter, context, list ->
            bind { itemView, position, item ->
                itemView.createReportSubject.text = item?.title
                itemView.setOnClickListener {
                    val action = CreateReportFragmentDirections.actionCreateReportFragmentToCreateReportOptionsFragment()
                    action.question = item
                    findNavController().navigate(action)
                }
            }
            setLayoutManager(GridLayoutManager(requireContext(), 2))

            submitList(myList)
        }


    }




}
