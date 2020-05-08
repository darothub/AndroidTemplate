package com.anapfoundation.covid_19volunteerapp.di.networkmodules.auth

import com.anapfoundation.covid_19volunteerapp.data.repositories.auth.AuthRequestRepository
import com.anapfoundation.covid_19volunteerapp.data.repositories.user.UserRequestRepository
import com.anapfoundation.covid_19volunteerapp.network.auth.AuthRequestInterface
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
     *@param userApiServices is a retrofit instance
     */
    @Provides
    fun provideAuthRequest(authApiRequests: AuthApiRequests): AuthRequestInterface {
        return AuthRequestRepository(
            authApiRequests
        )
    }

}