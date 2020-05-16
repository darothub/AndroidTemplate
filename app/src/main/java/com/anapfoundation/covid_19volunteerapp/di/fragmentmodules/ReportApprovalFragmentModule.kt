package com.anapfoundation.covid_19volunteerapp.di.fragmentmodules

import com.anapfoundation.covid_19volunteerapp.ui.ForgotPasswordFragment
import com.anapfoundation.covid_19volunteerapp.ui.ReportApprovalFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ReportApprovalFragmentModule {
    /**
     * A abstract function inject Report Approval Fragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideReportApprovalFragment(): ReportApprovalFragment
}