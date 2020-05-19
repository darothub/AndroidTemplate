package com.anapfoundation.covid_19volunteerapp.di.fragmentmodules

import com.anapfoundation.covid_19volunteerapp.ui.ApprovedReportFragment
import com.anapfoundation.covid_19volunteerapp.ui.UnapprovedReportFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector
@Module
abstract class UnapprovedReportFragmentModule {
    /**
     * A abstract function inject Unapproved Reports Fragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideUnapprovedReportFragment(): UnapprovedReportFragment
}