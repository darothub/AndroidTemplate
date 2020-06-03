package com.anapfoundation.covid_19volunteerapp.di.fragmentmodules

import com.anapfoundation.covid_19volunteerapp.ui.HomeFragment
import com.anapfoundation.covid_19volunteerapp.ui.SingleReportFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SingleReportFragmentModule {
    /**
     * A abstract function inject Single Report Fragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideSingleReportFragment(): SingleReportFragment
}