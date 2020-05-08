package com.anapfoundation.covid_19volunteerapp.di.networkmodules.user

import com.anapfoundation.covid_19volunteerapp.data.repositories.user.UserRequestRepository
import com.anapfoundation.covid_19volunteerapp.network.user.UserRequestInterface
import com.anapfoundation.covid_19volunteerapp.services.userservices.UserApiRequests
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit


/**
 * A class to provide retrofit instance for user api services
 *
 */
@Module
class UserRequestModule {
    @Provides
    fun provideUserServices(retrofit: Retrofit): UserApiRequests {
        return retrofit.create(UserApiRequests::class.java)
    }

    /**
     * A function provides an instance of the user repository
     *@param userApiRequests is a retrofit instance
     */
    @Provides
    fun provideUserCall(userApiRequests: UserApiRequests): UserRequestInterface {
        return UserRequestRepository(
            userApiRequests
        )
    }

}

