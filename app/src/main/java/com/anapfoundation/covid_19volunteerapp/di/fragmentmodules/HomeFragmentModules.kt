package com.anapfoundation.covid_19volunteerapp.di.fragmentmodules

import com.anapfoundation.covid_19volunteerapp.ui.ForgotPasswordFragment
import com.anapfoundation.covid_19volunteerapp.ui.HomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class HomeFragmentModules {
    /**
     * A abstract function inject Home Fragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideHomeFragment(): HomeFragment
}