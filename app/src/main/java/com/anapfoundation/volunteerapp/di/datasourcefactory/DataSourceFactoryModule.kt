package com.anapfoundation.volunteerapp.di.datasourcefactory

import androidx.paging.DataSource
import com.anapfoundation.volunteerapp.data.paging.ReportDataFactory
import com.anapfoundation.volunteerapp.data.paging.ReviewerApprovedReportsDataFactory
import com.anapfoundation.volunteerapp.data.paging.ReviewerUnapprovedReportsDataFactory
import com.anapfoundation.volunteerapp.model.response.ReportResponse
import com.anapfoundation.volunteerapp.network.storage.StorageRequest
import com.anapfoundation.volunteerapp.services.authservices.AuthApiRequests
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