package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*

import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_report.*
import kotlinx.android.synthetic.main.layout_upload_gallery.*
import kotlinx.android.synthetic.main.layout_upload_gallery.view.*
import kotlinx.android.synthetic.main.layout_upload_gallery.view.imageUploadLayout
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ReportFragment : DaggerFragment() {

    private val title by lazy {
        getName()
    }
    private val navController by lazy {
        Navigation.findNavController(requireActivity(), R.id.fragment2)
    }

    private val bottomSheetDialog by lazy {
        BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
    //inflate bottomSheetView
    private val bottomSheetView by lazy {
        LayoutInflater.from(requireContext()).inflate(
            R.layout.layout_upload_gallery,
            requireActivity().findViewById(R.id.uploadBottomSheetContainer)
        )
    }
    //Get included layout in the parent layout/* layout.fragment_report_upload */
    private val logoutLayout by lazy {
        bottomSheetView.findViewById<View>(R.id.logoutLayout)
    }

    private val uploadLayout by lazy {
        bottomSheetView.findViewById<View>(R.id.imageUploadLayout)
    }

    private val yesButton by lazy{
        bottomSheetView.yesButton
    }
    private val noButton by lazy{
        bottomSheetView.noButton
    }



    private val uploadListener = NavController.OnDestinationChangedListener{controller, destination, arguments ->
        when(destination.id){
            else -> {
                bottomNav.show()
                reportFragmentProgressView1.show()
            }
        }
    }
    @Inject
    lateinit var storageRequest: StorageRequest
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



        Log.i(title, "OnActivity")
        onBackPressed()


    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback {

//            requireContext().toast("Report parent fragment")

            showBottomSheet()

        }
    }


    override fun onResume() {
        super.onResume()
        //
        Log.i(title, "OnResume")
        bottomNav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener(uploadListener)
    }

    override fun onPause() {
        super.onPause()
        Log.i(title, "OnPause")
        navController.removeOnDestinationChangedListener(uploadListener)


    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(title, "OnDestroy")
        logout()

    }

    private fun showBottomSheet() {

        logoutLayout.show()
        uploadLayout.hide()
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

        logout()

    }

    private fun logout(){
        yesButton.setOnClickListener {
            val user = storageRequest.checkUser("loggedInUser")
            user?.loggedIn = false
            storageRequest.saveData(user, "loggedOutUser")
            bottomSheetDialog.dismiss()
//            requireActivity().finishFromChild(activity)
            navigateWithUri("android-app://anapfoundation.navigation/signin".toUri())
        }

        noButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
    }


}
