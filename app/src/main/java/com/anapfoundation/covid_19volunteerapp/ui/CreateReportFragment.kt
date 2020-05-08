package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.model.*
import com.anapfoundation.covid_19volunteerapp.model.servicesmodel.ServiceResult
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.utsman.recycling.setupAdapter
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.create_report_item.view.*
import kotlinx.android.synthetic.main.fragment_create_report.*
import kotlinx.android.synthetic.main.fragment_signin.*
import kotlinx.android.synthetic.main.layout_bottom_sheet.view.*
import kotlinx.android.synthetic.main.options_item.view.*
import java.lang.Exception
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class CreateReportFragment : DaggerFragment() {

    val title: String by lazy {
        getName()
    }
    private val nav by lazy {
        Navigation.findNavController(createReportAppBar)
    }

    private val navController by lazy {
        findNavController()
    }

    val checkBoxMap by lazy {
        hashMapOf<Int, Boolean>()
    }
    val getUser by lazy {
        storageRequest.checkUser("loggedInUser")
    }
    val token by lazy {
        getUser?.token
    }
    val header by lazy {
        "Bearer $token"
    }

    val bottomSheetDialog by lazy {
        BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
    val bottomSheetView by lazy {
        LayoutInflater.from(requireContext()).inflate(
            R.layout.layout_bottom_sheet,  requireActivity().findViewById(R.id.bottomSheetContainer)
        )
    }
    val newReport by  lazy {
        Report("", "", "", "")
    }

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    val authViewModel: AuthViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel::class.java)
    }
    @Inject
    lateinit var storageRequest: StorageRequest
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_report, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        NavigationUI.setupWithNavController(createReportToolbar, nav)

        extractAndSetTopics()


    }

    private fun extractAndSetTopics() {
        val topics = getTopic(header)

        topics.observe(viewLifecycleOwner, Observer {

            createReportRecyclerView.setupAdapter<Topic>(R.layout.create_report_item) { adapter, context, list ->
                bind { itemView, position, item ->
                    itemView.createReportSubject.text = item?.topic
                    itemView.setOnClickListener {
    //                    val action = CreateReportFragmentDirections.actionCreateReportFragmentToCreateReportOptionsFragment()
    //                    action.question = item
    //                    findNavController().navigate(action)

                        val rating = item?.id?.let { id -> getRating(id, header) }
                        rating?.observe(viewLifecycleOwner, Observer {
                            Log.i(title, it.data.toString())
                            val ratingItems = it.data
                            ratingItems?.let { data -> setUpBottomSheet(data) }

                        })

                        bottomSheetView.reportQuestionHeading.text = item?.topic



                    }
                }
                setLayoutManager(GridLayoutManager(requireContext(), 2))

                submitList(it.data)
            }

        })
    }

    private fun getTopic(header:String):MediatorLiveData<TopicData>{
        val data = MediatorLiveData<TopicData>()
        val request = authViewModel.getTopic(header)

        val response = observeRequest(request, null, null)

        data.addSource(response) {
            try {
                data.value = it.second as TopicData
            }
            catch (e:Exception){
               Log.e(title, e.message)
            }

        }



        return data

    }

    private fun getRating(topicID:String, header:String):MediatorLiveData<TopicData>{
        val data = MediatorLiveData<TopicData>()
        val request = authViewModel.getRating(topicID, header)
        val response = observeRequest(request, null, null)
        data.addSource(response) {
            data.value = it.second as TopicData
        }
        return data
    }

    private fun setUpBottomSheet(item:List<Topic>){

        val myList = item
        bottomSheetView.reportQuestionRecyclerView.setupAdapter<Topic>(R.layout.options_item){ adapter, context, list ->
            bind { itemView, position, item ->

                itemView.optionText.text = item?.rating

                itemView.setOnClickListener {
                    itemView.optionRadio.isChecked = !itemView.optionRadio.isChecked

                }
                itemView.optionRadio.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        checkBoxMap.put(position, itemView.optionRadio.isChecked)
                        newReport.rating = item?.id.toString()

//                        requireContext().toast(item?.topic.toString())
                    }
                    else{
                        checkBoxMap.remove(position)
//                        requireContext().toast("remove")
                    }
                    newReport.topic = item?.topic.toString()
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
//                requireContext().toast("size ${newReport.rating}")
                bottomSheetDialog.dismiss()
                val action = CreateReportFragmentDirections.toUploadFragment()
                action.report = newReport
                findNavController().navigate(action)
            }


        }
    }
}
