package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.getName
import com.anapfoundation.covid_19volunteerapp.utils.extensions.hide
import com.anapfoundation.covid_19volunteerapp.utils.extensions.show
import kotlinx.android.synthetic.main.fragment_report.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ReportFragment : Fragment() {

    private val title by lazy {
        getName()
    }
    private val navController by lazy {
        Navigation.findNavController(requireActivity(), R.id.fragment2)
    }




    private val uploadListener = NavController.OnDestinationChangedListener{controller, destination, arguments ->
        when(destination.id){
            R.id.reportUploadFragment -> {
                bottomNav.hide()
                reportFragmentProgressView1.hide()
            }
            R.id.notificationFragment ->{
                bottomNav.hide()
                reportFragmentProgressView1.hide()
            }
            R.id.reportReviewFragment ->{
                bottomNav.hide()
                reportFragmentProgressView1.hide()
            }
            else -> {
                bottomNav.show()
                reportFragmentProgressView1.show()
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bottomNav.setupWithNavController(navController)

        Log.i(title, "OnActivity")


    }

    override fun onResume() {
        super.onResume()
        //
        Log.i(title, "OnResume")
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
    }


}
