package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
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
import com.anapfoundation.covid_19volunteerapp.utils.extensions.hide
import com.anapfoundation.covid_19volunteerapp.utils.extensions.show
import kotlinx.android.synthetic.main.fragment_report.*

/**
 * A simple [Fragment] subclass.
 */
class ReportFragment : Fragment() {

    private val navController by lazy {
        Navigation.findNavController(requireActivity(), R.id.fragment2)
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


        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id){
                R.id.reportUploadFragment -> {
                    bottomNav.hide()
                    reportFragmentProgressView1.hide()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback {

            requireActivity().finish()

        }
    }

    override fun onResume() {
        super.onResume()
        bottomNav.show()
        reportFragmentProgressView1.show()
    }



}
