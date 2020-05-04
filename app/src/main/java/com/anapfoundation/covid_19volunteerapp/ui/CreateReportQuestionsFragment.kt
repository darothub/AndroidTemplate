package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.model.ReportQuestionModel
import com.anapfoundation.covid_19volunteerapp.utils.extensions.setBackButtonNavigation
import com.anapfoundation.covid_19volunteerapp.utils.extensions.toast
import com.utsman.recycling.setupAdapter
import kotlinx.android.synthetic.main.create_report_item.view.*
import kotlinx.android.synthetic.main.create_report_questions_item.view.*
import kotlinx.android.synthetic.main.fragment_create_report.*
import kotlinx.android.synthetic.main.fragment_create_report_questions.*

/**
 * A simple [Fragment] subclass.
 */
class CreateReportQuestionsFragment : Fragment() {

    lateinit var question:ReportQuestionModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_create_report_questions, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        receiveQuestion()
        setQuestion()
//        setBackButtonAction()
        backButton.setBackButtonNavigation()

        val myList = question.options
        createReportQuestionRecyclerView.setupAdapter<String>(R.layout.create_report_questions_item){ adapter, context, list ->
            bind { itemView, position, item ->
                itemView.optionsText.text = item
                itemView.setOnClickListener {
//                    val request = NavDeepLinkRequest.Builder
//                        .fromUri("app://uploadreportscreen".toUri())
//                        .build()

                    findNavController().navigate(R.id.reportUploadFragment)
                }
            }


            setLayoutManager(GridLayoutManager(requireContext(), 2))

            submitList(myList)
            requireContext().toast("listSize ${list?.size}")
        }
//        requireContext().toast("$question")


    }

    private fun receiveQuestion(){
        arguments?.let {
            question =  CreateReportQuestionsFragmentArgs.fromBundle(it).question!!
        }

    }

    private fun setQuestion(){
        createReportQuestionHeading.text = question.heading
    }
    private fun setBackButtonAction(){
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}
