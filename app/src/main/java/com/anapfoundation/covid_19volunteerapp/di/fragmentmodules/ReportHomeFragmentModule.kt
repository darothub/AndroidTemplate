package com.anapfoundation.covid_19volunteerapp.di.fragmentmodules

import com.anapfoundation.covid_19volunteerapp.ui.ReportHomeFragment
import com.anapfoundation.covid_19volunteerapp.ui.SigninFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ReportHomeFragmentModule {

    /**
     * A abstract function inject ReportHome Fragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideReportHomeFragment(): ReportHomeFragment
}
