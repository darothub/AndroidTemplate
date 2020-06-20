package com.anapfoundation.volunteerapp.di.fragmentmodules

import com.anapfoundation.volunteerapp.ui.ReportApprovalFragment
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