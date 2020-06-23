package com.anapfoundation.volunteerapp.di.networkmodules.auth

import androidx.paging.DataSource
import com.anapfoundation.volunteerapp.data.paging.ReportDataFactory
import com.anapfoundation.volunteerapp.data.repositories.auth.AuthRequestRepository
import com.anapfoundation.volunteerapp.model.response.ReportResponse
import com.anapfoundation.volunteerapp.network.auth.AuthRequestInterface
import com.anapfoundation.volunteerapp.network.storage.StorageRequest
import com.anapfoundation.volunteerapp.services.authservices.AuthApiRequests
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

    val anything:Hey
           @Provides get()=Hey()

    @Provides
    fun provideReportDataFactory(authApiRequests: AuthApiRequests, storageRequest: StorageRequest):DataSource.Factory<Long, ReportResponse>{
        return ReportDataFactory(
            authApiRequests, storageRequest
        )
    }

}

class Hey(){

}
