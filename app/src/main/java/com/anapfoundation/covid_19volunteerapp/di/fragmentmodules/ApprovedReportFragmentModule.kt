package com.anapfoundation.covid_19volunteerapp.di.fragmentmodules

import com.anapfoundation.covid_19volunteerapp.ui.ApprovedReportFragment
import com.anapfoundation.covid_19volunteerapp.ui.CreateReportFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector
@Module
abstract class ApprovedReportFragmentModule {

    /**
     * A abstract function inject Approved Reports Fragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideApprovedReportFragment(): ApprovedReportFragment
}