package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.model.ProfileData
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*

import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_report.*
import kotlinx.android.synthetic.main.layout_upload_gallery.view.*
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

    val bottomSheetDialog by lazy {
        BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
    //inflate bottomSheetView
    val bottomSheetView by lazy {
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

    val yesButton by lazy{
        bottomSheetView.yesButton
    }
    val noButton by lazy{
        bottomSheetView.noButton
    }

    @Inject
    lateinit var storageRequest: StorageRequest
    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    val authViewModel: AuthViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel::class.java)
    }
    //Get logged-in user
    val user by lazy {
        storageRequest.checkUser("loggedInUser")
    }
    val token by lazy {
        user?.token
    }
    val header by lazy {
        "Bearer $token"
    }

     lateinit var destinationChangedListener: NavController.OnDestinationChangedListener

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

    override fun onStart() {
        super.onStart()
        destinationChangedListener =
            NavController.OnDestinationChangedListener { controller, destination, arguments ->
                when (destination.id) {
                    R.id.reportHomeFragment -> {
                        onBackPressed()
                    }
                    R.id.createReportFragment -> {
                        requireActivity().onBackPressedDispatcher.addCallback {


                            navController.navigate(R.id.reportHomeFragment)

                        }
                    }
                    R.id.notificationFragment -> {
                        requireActivity().onBackPressedDispatcher.addCallback {

                            navController.popBackStack()

                        }

                    }
                    R.id.toSingleReportScreen -> {
                        requireActivity().onBackPressedDispatcher.addCallback {

                            navController.popBackStack()

                        }

                    }
                    R.id.profileFragment -> {
                        requireActivity().onBackPressedDispatcher.addCallback {
                            navController.navigate(R.id.reportHomeFragment)
                        }
                    }
                    R.id.reviewerScreenFragment->{
                        requireActivity().onBackPressedDispatcher.addCallback {
                            navController.navigate(R.id.reportHomeFragment)
                        }
                    }
                    else -> {
                        bottomNav.show()
                        reportFragmentProgressView1.show()
                    }
                }
            }
    }

    fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback {


            showBottomSheet()

        }
    }


    override fun onResume() {
        super.onResume()
        //
        Log.i(title, "OnResume")

        val navController = Navigation.findNavController(requireActivity(), R.id.fragment2)

        val request = authViewModel.getProfileData(header)
        val response = observeRequest(request, null, null)
        response.observe(viewLifecycleOwner, Observer {
            val (bool, result) = it
            when (bool) {
                true -> {
                    val res = result as ProfileData

                    when(res.data.isReviewer){
                        true ->{
                            val screen = bottomNav.menu.findItem(R.id.reviewerScreenFragment)
                            screen.isVisible = true
                        }
                        false ->{
                            val screen = bottomNav.menu.findItem(R.id.reviewerScreenFragment)
                            screen.isVisible = false
                        }
                    }
                    Log.i(title, "Reviewer ${res.data.isReviewer}")
                }
                else -> Log.i(title, "error $result")
            }
        })
        bottomNav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener(destinationChangedListener)
    }

    override fun onPause() {
        super.onPause()
        Log.i(title, "OnPause")
        navController.removeOnDestinationChangedListener(destinationChangedListener)


    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(title, "OnDestroy")
        onBackPressed()


    }

   fun showBottomSheet() {

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
