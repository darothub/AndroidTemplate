package com.anapfoundation.volunteerapp.di.fragmentmodules

import com.anapfoundation.volunteerapp.ui.ReportFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ReportParentFragmentModule {
    /**
     * A abstract function inject ReportHome Fragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideReportHomeFragment(): ReportFragment
}

