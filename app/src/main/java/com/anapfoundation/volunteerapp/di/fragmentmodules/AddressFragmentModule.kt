package com.anapfoundation.volunteerapp.di.fragmentmodules

import com.anapfoundation.volunteerapp.ui.AddressFragment
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