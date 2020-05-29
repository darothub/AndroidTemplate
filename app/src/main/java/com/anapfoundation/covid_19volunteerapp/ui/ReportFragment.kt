package com.anapfoundation.covid_19volunteerapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import com.google.android.material.bottomnavigation.BottomNavigationMenuView

import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_report.*
import kotlinx.android.synthetic.main.layout_upload_gallery.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    val logoutButton by lazy{
        bottomSheetView.logoutButton
    }
    val exitButton by lazy{
        bottomSheetView.exitButton
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
        customBackDispatcher()

    }

    private fun customBackDispatcher() {
        destinationChangedListener =
            NavController.OnDestinationChangedListener { controller, destination, arguments ->
                when (destination.id) {
                    R.id.reportHomeFragment -> {
                        bottomNav.show()
                        onBackPressed()
                    }
                    R.id.createReportFragment -> {
                        bottomNav.show()
                        requireActivity().onBackPressedDispatcher.addCallback {
                            navController.navigate(R.id.reportHomeFragment)
                        }
                    }
                    R.id.notificationFragment -> {
                        bottomNav.show()
                        requireActivity().onBackPressedDispatcher.addCallback {
                            navController.popBackStack()
                        }

                    }
                    R.id.toSingleReportScreen -> {
                        bottomNav.show()
                        requireActivity().onBackPressedDispatcher.addCallback {
                            navController.popBackStack()
                        }

                    }
                    R.id.profileFragment -> {
                        bottomNav.hide()
                        requireActivity().onBackPressedDispatcher.addCallback {
                            navController.navigate(R.id.reportHomeFragment)
                        }
                        this
                    }
                    R.id.editProfileFragment -> {
                        requireActivity().onBackPressedDispatcher.addCallback {
                            navController.navigate(R.id.profileFragment)
                        }
                    }
                    R.id.reviewerScreenFragment -> {
                        bottomNav.show()
                        requireActivity().onBackPressedDispatcher.addCallback {
                            navController.navigate(R.id.reportHomeFragment)
                        }
                    }
                    else -> {

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

        when(user?.isReviewer){
            true ->{
                val screen = bottomNav.menu.findItem(R.id.reviewerScreenFragment)
                screen.isVisible = true
            }
            false ->{
                val screen = bottomNav.menu.findItem(R.id.reviewerScreenFragment)
                screen.isVisible = false
            }
        }


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

    }

   fun showBottomSheet() {

        logoutLayout.show()
        uploadLayout.hide()
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

       logoutButton.setOnClickListener {
            logout(storageRequest, bottomSheetDialog)
       }

       exitButton.setOnClickListener {
           exitApp()
       }

    }

    private fun exitApp() {
        bottomSheetDialog.dismiss()
        CoroutineScope(Main).launch {
            delay(1000)
            activity?.finish()
        }
    }








}
