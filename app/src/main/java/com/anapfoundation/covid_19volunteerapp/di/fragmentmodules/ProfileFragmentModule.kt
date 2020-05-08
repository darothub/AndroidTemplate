package com.anapfoundation.covid_19volunteerapp.di.fragmentmodules

import com.anapfoundation.covid_19volunteerapp.ui.ProfileFragment
import com.anapfoundation.covid_19volunteerapp.ui.ReportHomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ProfileFragmentModule {
    /**
     * A abstract function inject Profile Fragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideProfileFragment(): ProfileFragment
}