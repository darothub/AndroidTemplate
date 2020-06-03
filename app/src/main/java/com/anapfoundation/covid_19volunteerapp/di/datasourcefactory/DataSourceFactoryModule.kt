package com.anapfoundation.covid_19volunteerapp.di.datasourcefactory

import androidx.paging.DataSource
import com.anapfoundation.covid_19volunteerapp.data.paging.ReportDataFactory
import com.anapfoundation.covid_19volunteerapp.data.paging.ReviewerApprovedReportsDataFactory
import com.anapfoundation.covid_19volunteerapp.data.paging.ReviewerUnapprovedReportsDataFactory
import com.anapfoundation.covid_19volunteerapp.model.response.ReportResponse
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
    fun provideReviewerUnapprovedReportDataSourceFactory(authApiRequests: AuthApiRequests, storageRequest: StorageRequest): DataSource.Factory<Long, ReportResponse> {
        return ReviewerUnapprovedReportsDataFactory(
            authApiRequests,
            storageRequest
        )
    }
    @Provides
    fun provideReviewerApprovedReportDataSourceFactory(authApiRequests: AuthApiRequests, storageRequest: StorageRequest): DataSource.Factory<Long, ReportResponse> {
        return ReviewerApprovedReportsDataFactory(
            authApiRequests,
            storageRequest
        )
    }
}