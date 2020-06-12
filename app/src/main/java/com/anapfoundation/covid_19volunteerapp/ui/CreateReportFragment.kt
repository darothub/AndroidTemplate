package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.helpers.ToggleableRadioButton
import com.anapfoundation.covid_19volunteerapp.model.request.Report
import com.anapfoundation.covid_19volunteerapp.model.response.Topic
import com.anapfoundation.covid_19volunteerapp.model.response.TopicResponse
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.utsman.recycling.extentions.Recycling
import com.utsman.recycling.setupAdapter
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.create_report_item.view.*
import kotlinx.android.synthetic.main.fragment_create_report.*
import kotlinx.android.synthetic.main.layout_bottom_sheet.view.*
import kotlinx.android.synthetic.main.options_item.*
import kotlinx.android.synthetic.main.options_item.view.*
import kotlinx.android.synthetic.main.options_item.view.optionRadio
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class CreateReportFragment : DaggerFragment() {

    //Get class title
    val title: String by lazy {
        getName()
    }

    //Declare hash map for check box values
    val checkBoxMap by lazy {
        hashMapOf<Int, Topic>()
    }
    //Declare hash map for check box views
    val checkBoxMapper by lazy {
        hashMapOf<Int, View>()
    }
    //Get logged in user
    val loggedInUser by lazy {
        storageRequest.checkUser("loggedInUser")
    }
    //Get logged in user token
    val token by lazy {
        loggedInUser?.token
    }
    //Set header
    val header by lazy {
        "Bearer $token"
    }

    //Set bottom dialog
    val bottomSheetDialog by lazy {
        BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
    //Inflate bottom view
    val bottomSheetView by lazy {
        LayoutInflater.from(requireContext()).inflate(
            R.layout.layout_bottom_sheet, requireActivity().findViewById(R.id.bottomSheetContainer)
        )
    }
    //New report
    val newReport by lazy {
        Report(
            "",
            "",
            "",
            ""
        )
    }

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    val authViewModel: AuthViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel::class.java)
    }

    @Inject
    lateinit var storageRequest: StorageRequest

    var ratingList: ArrayList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_report, container, false)
    }


    override fun onResume() {
        super.onResume()
        extractAndSetTopics()
        bottomSheetDialog.dismiss()
        crashReportByUser(loggedInUser)
    }

    private fun extractAndSetTopics() {
        val topics = getTopic(header)


        createReportRecyclerView.setupAdapter<Topic>(R.layout.create_report_item) { adapter, context, list ->
            bind { itemView, position, item ->
                itemView.createReportSubject.text = item?.topic

                itemView.setOnClickListener {
                    Log.i(title, "${item?.id}")
                    newReport.topic = item?.id.toString()

                    val rating = item?.id?.let { id -> getRating(id, header) }
                    rating?.observe(viewLifecycleOwner, Observer {

                        val ratingItems = it.data
                        ratingItems.let { data -> setUpBottomSheet(data) }
//                            Log.i(title, "${ratingItems.size}")
                    })
                    bottomSheetView.reportQuestionHeading.text = item?.topic
                }
            }
            //Adds network state layout
            addLoader(R.layout.network_state_loader) {
                idLoader = R.id.progress_circular
                idTextError = R.id.error_text_view
            }
            //Set layout manager
            setLayoutManager(GridLayoutManager(requireContext(), 2))

            //Set recyclerview data
            setRecyclerViewData(this, topics)

        }

    }

    override fun onPause() {
        super.onPause()
        bottomSheetDialog.dismiss()
        Log.i(title, "Paused")
    }

    override fun onStart() {
        super.onStart()
        bottomSheetDialog.dismiss()
        Log.i(title, "started")

    }

    /**
     * Set recyclerview data
     *
     * @param recycling
     * @param topics
     */
    private fun setRecyclerViewData(
        recycling: Recycling<Topic>,
        topics: MediatorLiveData<TopicResponse>
    ) {
        //Get and observe topics
        topics.observe(viewLifecycleOwner, Observer {

            recycling.submitList(it.data)
        })

        //Get and Observer network state
        authViewModel.getAuthLoader().observe(viewLifecycleOwner, Observer {
            recycling.submitNetworkState(it)
        })
    }

    /**
     * Get topics
     *
     * @param header
     * @return
     */
    private fun getTopic(header: String): MediatorLiveData<TopicResponse> {
        val data = MediatorLiveData<TopicResponse>()
        val request = authViewModel.getTopic(header)

        val response = observeRequest(request, null, null)

        data.addSource(response) {
            try {
                data.value = it.second as TopicResponse
            } catch (e: Exception) {
                Log.e(title, e.message)
            }

        }
        return data

    }

    /**
     * Get rating
     *
     * @param topicID
     * @param header
     * @return
     */
    private fun getRating(topicID: String, header: String): MediatorLiveData<TopicResponse> {
        val data = MediatorLiveData<TopicResponse>()
        val request = authViewModel.getRating(topicID, header)
        val response = observeRequest(request, null, null)
        data.addSource(response) {
            try {
                data.value = it.second as TopicResponse
            } catch (e: Exception) {
                Log.e(title, e.message)
            }

        }
        return data
    }

    /**
     * Set up bottom sheet
     *
     * @param item
     */
    private fun setUpBottomSheet(item: List<Topic>) {

        val myList = item
        setupBottomSheetRecyclerView(myList)

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

        clearBottomDialogOnDismiss()

        submitRatingSelection()
    }

    /**
     * Submit rating selection
     *
     */
    private fun submitRatingSelection() {
        bottomSheetView.selectBtn.setOnClickListener {
            val checkBoxesKeys = checkBoxMap.keys
//            Log.i(title, "checkBoxMapChanged ${checkBoxMap}")
            if (ratingList.isEmpty()) {
                bottomSheetDialog.dismiss()
                val action = CreateReportFragmentDirections.toUploadFragment()
                action.report = newReport
                goto(action)
            } else if (checkBoxesKeys.size != 1) {
                toast(getLocalisedString(R.string.pick_one_rating))
            } else {
                bottomSheetDialog.dismiss()
                val topicItem = checkBoxMap.values.elementAt(0)
//                newReport.topic = topicItem.topic
                newReport.rating = topicItem.id
                Log.i(title, "new report " + newReport.topic + " " + newReport.rating)
                val action = CreateReportFragmentDirections.toUploadFragment()
                action.report = newReport
                goto(action)
            }

        }
    }

    /**
     * Clear bottom dialog
     *
     */
    private fun clearBottomDialogOnDismiss() {
        bottomSheetDialog.setOnDismissListener {
            checkBoxMap.clear()
            ratingList.clear()
        }
    }

    /**
     * Set data for bottom sheet recyclerview
     *
     * @param myList
     */
    private fun setupBottomSheetRecyclerView(myList: List<Topic>) {
        bottomSheetView.reportQuestionRecyclerView.setupAdapter<Topic>(R.layout.options_item) { adapter, context, list ->
            bind { itemView, position, item ->

                when {
                    item?.rating?.length!! >= 1 -> itemView.optionText.text = item.rating.capitalize()
                    else -> bottomSheetView.selectBtn.setOnClickListener {
                        goto(R.id.reportUploadFragment)
                    }
                }

                checkBoxMapper[position] = itemView
                val one = checkBoxMapper[0] as View
                val two = checkBoxMapper[1]
                val three = checkBoxMapper [2]

                if (two != null && three != null) {
                    viewRadioClickImpl(one, two, three)
                    viewRadioClickImpl(one.optionRadio, two.optionRadio, three.optionRadio)
                }
                if (two != null && three != null ) {
                    viewRadioClickImpl(two, one, three)
                    viewRadioClickImpl(two.optionRadio, one.optionRadio,three.optionRadio)
                }
                if (two != null && three != null) {
                    viewRadioClickImpl(three, one, two)
                    viewRadioClickImpl(three.optionRadio, two.optionRadio, one.optionRadio)
                }

                ratingList.add(item.rating)
                itemView.optionRadio.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        checkBoxMap.put(position, item)
                        Log.i(title, "ratingtext ${item.rating}")
                    } else {
                        checkBoxMap.remove(position)
                    }
                }
            }
            submitList(myList)
        }
    }

    /**
     * View and radio manipulation respect to radio events
     *
     * @param view
     * @param views
     */
   private fun viewRadioClickImpl(view: View, vararg views: View){
        view.setOnClickListener {
            view.optionRadio.isChecked = true
            for (otherView in views){
                otherView.optionRadio.isChecked = !view.optionRadio.isChecked
            }
        }
    }

    /**
     * View and radio manipulation respect to radio events
     *
     * @param radio
     * @param radios
     */
    private fun viewRadioClickImpl(radio: ToggleableRadioButton, vararg radios: ToggleableRadioButton){
        radio.setOnClickListener {
            radio.optionRadio.isChecked = true
            for (otherView in radios){
                otherView.optionRadio.isChecked = !radio.optionRadio.isChecked
            }
        }
    }

}
