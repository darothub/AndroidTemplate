package com.anapfoundation.covid_19volunteerapp.di.fragmentmodules

import com.anapfoundation.covid_19volunteerapp.ui.ReportFragment
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

