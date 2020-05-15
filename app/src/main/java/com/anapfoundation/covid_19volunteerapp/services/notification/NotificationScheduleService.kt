package com.anapfoundation.covid_19volunteerapp.services.notification

import android.app.job.JobParameters
import android.app.job.JobService
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.getUnApprovedReports
import com.anapfoundation.covid_19volunteerapp.network.auth.AuthRequestInterface
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
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
        CoroutineScope(IO).launch {
            authViewModel.getUnApprovedReports(header, first, after)
        }
    }
}