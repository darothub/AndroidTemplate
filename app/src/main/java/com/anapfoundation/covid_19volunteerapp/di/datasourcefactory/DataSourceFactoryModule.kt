package com.anapfoundation.covid_19volunteerapp.di.datasourcefactory

import androidx.paging.DataSource
import com.anapfoundation.covid_19volunteerapp.data.paging.ReportDataFactory
import com.anapfoundation.covid_19volunteerapp.data.paging.ReviewerScreenReportsDataFactory
import com.anapfoundation.covid_19volunteerapp.data.repositories.auth.AuthRequestRepository
import com.anapfoundation.covid_19volunteerapp.model.response.ReportResponse
import com.anapfoundation.covid_19volunteerapp.network.auth.AuthRequestInterface
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.services.authservices.AuthApiRequests
import dagger.Module
import dagger.Provides

@Module
class DataSourceFactoryModule {
    @Provides
    fun provideReportDataSourceFactory(authApiRequests: AuthApiRequests, storageRequest: StorageRequest): DataSource.Factory<Long, ReportResponse> {
        return ReportDataFactory(
            authApiRequests,
            storageRequest
        )
    }

    @Provides
    fun provideReviewerReportDataSourceFactory(authApiRequests: AuthApiRequests, storageRequest: StorageRequest): DataSource.Factory<Long, ReportResponse> {
        return ReviewerScreenReportsDataFactory(
            authApiRequests,
            storageRequest
        )
    }
}