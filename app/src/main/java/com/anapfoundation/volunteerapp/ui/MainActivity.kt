package com.anapfoundation.volunteerapp.ui

import android.os.Bundle
import android.util.Log
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.anapfoundation.volunteerapp.R
import com.anapfoundation.volunteerapp.network.storage.StorageRequest
import com.anapfoundation.volunteerapp.utils.extensions.getName
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject


class MainActivity : DaggerAppCompatActivity() {

    val title:String by lazy {
        getName()
    }
    @Inject
    lateinit var storageRequest: StorageRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
    override fun onSupportNavigateUp(): Boolean {

        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.fragment), null
        )
    }



    override fun onBackPressed() {
        super.onBackPressed()
        Log.i(title, "backpressed")
    }

}

