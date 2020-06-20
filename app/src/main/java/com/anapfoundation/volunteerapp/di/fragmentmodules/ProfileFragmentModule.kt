package com.anapfoundation.volunteerapp.di.fragmentmodules

import com.anapfoundation.volunteerapp.ui.ProfileFragment
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