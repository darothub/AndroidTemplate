package com.anapfoundation.covid_19volunteerapp.di.networkmodules.auth

import androidx.paging.DataSource
import com.anapfoundation.covid_19volunteerapp.data.paging.ReportDataFactory
import com.anapfoundation.covid_19volunteerapp.data.repositories.auth.AuthRequestRepository
import com.anapfoundation.covid_19volunteerapp.data.repositories.user.UserRequestRepository
import com.anapfoundation.covid_19volunteerapp.model.response.ReportResponse
import com.anapfoundation.covid_19volunteerapp.model.response.Reports
import com.anapfoundation.covid_19volunteerapp.network.auth.AuthRequestInterface
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.network.user.UserRequestInterface
import com.anapfoundation.covid_19volunteerapp.services.authservices.AuthApiRequests
import com.anapfoundation.covid_19volunteerapp.services.userservices.UserApiRequests
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit


/**
 * A class to provide retrofit instance for auth api services
 *
 */
@Module
class AuthRequestModule {
    @Provides
    fun provideAuthServices(retrofit: Retrofit): AuthApiRequests {
        return retrofit.create(AuthApiRequests::class.java)
    }
    /**
     * A function provides an instance of the user repository
     *@param authApiRequests is a retrofit instance
     */
    @Provides
    fun provideAuthRequest(authApiRequests: AuthApiRequests): AuthRequestInterface {
        return AuthRequestRepository(
            authApiRequests
        )
    }

    @Provides
    fun provideReportDataFactory(authApiRequests: AuthApiRequests, storageRequest: StorageRequest):DataSource.Factory<Long, ReportResponse>{
        return ReportDataFactory(
            authApiRequests, storageRequest
        )
    }

}

