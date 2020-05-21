package com.anapfoundation.covid_19volunteerapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.getName
import com.skydoves.progressview.OnProgressChangeListener
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_home.*
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
                        user?.loggedIn == true -> findNavController().navigate(R.id.reportFragment)
                        user?.loggedIn == false -> findNavController().navigate(R.id.signinFragment)
                        else -> findNavController().navigate(R.id.signinFragment)
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
