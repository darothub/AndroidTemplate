package com.anapfoundation.volunteerapp.di.fragmentmodules

import com.anapfoundation.volunteerapp.ui.HomeFragment
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