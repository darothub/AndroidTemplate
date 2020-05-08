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
import com.anapfoundation.covid_19volunteerapp.model.Report
import com.anapfoundation.covid_19volunteerapp.model.ReportQuestionModel
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.utsman.recycling.setupAdapter
import kotlinx.android.synthetic.main.create_report_item.view.*
import kotlinx.android.synthetic.main.fragment_create_report.*
import kotlinx.android.synthetic.main.layout_bottom_sheet.view.*
import kotlinx.android.synthetic.main.options_item.view.*

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

    val newReport by  lazy {
        Report("null", "null", "null", "null")
    }

    val checkBoxMap by lazy {
        hashMapOf<Int, Boolean>()
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
//                    val action = CreateReportFragmentDirections.actionCreateReportFragmentToCreateReportOptionsFragment()
//                    action.question = item
//                    findNavController().navigate(action)


                    item?.let { it1 -> setUpBottomSheet(it1) }
                }
            }
            setLayoutManager(GridLayoutManager(requireContext(), 2))

            submitList(myList)
        }


    }

    private fun setUpBottomSheet(item:ReportQuestionModel){
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val bottomSheetView = LayoutInflater.from(requireContext()).inflate(
            R.layout.layout_bottom_sheet,  requireActivity().findViewById(R.id.bottomSheetContainer)
        )
        bottomSheetView.reportQuestionHeading.text = item.title

        newReport.topic = item?.topic.toString()
        val myList = item.ratings
        bottomSheetView.reportQuestionRecyclerView.setupAdapter<String>(R.layout.options_item){ adapter, context, list ->
            bind { itemView, position, item ->

                itemView.optionText.text = item

                itemView.setOnClickListener {
                    itemView.optionRadio.isChecked = !itemView.optionRadio.isChecked

                }
                itemView.optionRadio.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        checkBoxMap.put(position, itemView.optionRadio.isChecked)
                        newReport.rating = item.toString()
//                        requireContext().toast("add")
                    }
                    else{
                        checkBoxMap.remove(position)
//                        requireContext().toast("remove")
                    }
                }

            }
            submitList(myList)
        }


        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
        bottomSheetView.selectBtn.setOnClickListener {
            val checkBoxValues = checkBoxMap.values

            if(checkBoxValues.size != 1) {
                requireContext().toast(requireContext().localized(R.string.pick_one_rating))
            }
            else{
                requireContext().toast("size ${newReport.rating}")
                bottomSheetDialog.dismiss()
                val action = CreateReportFragmentDirections.toUploadFragment()
                action.report = newReport
                findNavController().navigate(action)
            }


        }
    }
}
