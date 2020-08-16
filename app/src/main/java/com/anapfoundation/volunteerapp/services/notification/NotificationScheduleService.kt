package com.anapfoundation.volunteerapp.services.notification

import android.app.job.JobParameters
import android.app.job.JobService
import com.anapfoundation.volunteerapp.data.viewmodel.auth.AuthViewModel

import com.anapfoundation.volunteerapp.network.storage.StorageRequest
import javax.inject.Inject

class NotificationScheduleService @Inject constructor(val authViewModel: AuthViewModel, val storageRequest: StorageRequest) : JobService() {
    override fun onStopJob(params: JobParameters?): Boolean {
        TODO("Not yet implemented")
        fetchOnBackground(params, 10, 0)
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        TODO("Not yet implemented")
    }

    fun fetchOnBackground(params: JobParameters?, first:Long?, after:Long?){

        val user = storageRequest.checkUser("loggedInUser")
        val header = "Bearer ${user?.token}"

    }
}