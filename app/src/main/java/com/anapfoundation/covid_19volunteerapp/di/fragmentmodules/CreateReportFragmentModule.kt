package com.anapfoundation.covid_19volunteerapp.di.fragmentmodules

import com.anapfoundation.covid_19volunteerapp.ui.CreateReportFragment
import com.anapfoundation.covid_19volunteerapp.ui.ReportHomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class CreateReportFragmentModule {
    /**
     * A abstract function inject ReportHome Fragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideCreateReportFragment(): CreateReportFragment
}