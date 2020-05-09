package com.anapfoundation.covid_19volunteerapp.di.fragmentmodules

import com.anapfoundation.covid_19volunteerapp.ui.AddressFragment
import com.anapfoundation.covid_19volunteerapp.ui.ProfileFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AddressFragmentModule {
    /**
     * A abstract function inject Profile Fragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideAddressFragment(): AddressFragment
}