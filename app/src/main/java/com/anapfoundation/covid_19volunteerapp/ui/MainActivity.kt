package com.anapfoundation.covid_19volunteerapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.utils.extensions.getName
import com.anapfoundation.covid_19volunteerapp.utils.extensions.show
import com.cloudinary.android.MediaManager
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_report.*
import java.util.*

class MainActivity : DaggerAppCompatActivity() {

    val title:String by lazy {
        getName()
    }

    private val navController by lazy {
        Navigation.findNavController(this, R.id.fragment)
    }
    lateinit var destinationChangedListener: NavController.OnDestinationChangedListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = Navigation.findNavController(this, R.id.fragment)

    }
    override fun onSupportNavigateUp(): Boolean {

        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.fragment), null
        )
    }

    override fun onStart() {
        super.onStart()

//        destinationChangedListener =
//            NavController.OnDestinationChangedListener { controller, destination, arguments ->
//                when (destination.id) {
//                    R.id.notificationFragment -> {
//                        this.onBackPressedDispatcher.addCallback {
//
//                            navController.navigateUp()
//
//                        }
//
//                    }
//
//                }
//            }
    }

    override fun onResume() {
        super.onResume()

//        navController.addOnDestinationChangedListener(destinationChangedListener)
    }
    override fun onPause() {
        super.onPause()
        Log.i(title, "OnPause")
//        navController.removeOnDestinationChangedListener(destinationChangedListener)


    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.i(title, "backpressed")
    }
}

