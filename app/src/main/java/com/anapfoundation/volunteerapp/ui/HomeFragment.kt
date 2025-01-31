package com.anapfoundation.volunteerapp.ui

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.anapfoundation.volunteerapp.R
import com.anapfoundation.volunteerapp.network.storage.StorageRequest
import com.anapfoundation.volunteerapp.utils.extensions.getName
import com.anapfoundation.volunteerapp.utils.extensions.goto
import dagger.android.support.DaggerFragment
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */

class HomeFragment : DaggerFragment() {

    val title:String by lazy {
        getName()
    }
    @Inject
    lateinit var storageRequest: StorageRequest
    //Get logged-in user
    val user by lazy {
        storageRequest.checkUser("loggedInUser")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            requireActivity().window.statusBarColor = resources.getColor(R.color.colorPrimary, requireContext().theme)
        } else {
            requireActivity().window.statusBarColor = resources.getColor(R.color.colorPrimary)
        }
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadProgressBar()

    }

    fun loadProgressBar(){

        val myThread = Thread(){
            try {
                kotlin.run {
                    Thread.sleep(5505)
                    when{
                        user?.loggedIn == true -> goto(R.id.reportFragment)
                        user?.loggedIn == false -> goto(R.id.signinFragment)
                        else -> goto(R.id.signinFragment)
                    }

                }
            }
            catch(e: Exception){
                e.printStackTrace()
            }
        }
        myThread.start()
    }





}
