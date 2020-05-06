package com.anapfoundation.covid_19volunteerapp.di.fragmentmodules

import com.anapfoundation.covid_19volunteerapp.ui.SignupFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SignupFragmentModule {
    /**
     * A abstract function inject RegisterFragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideSignupFragment(): SignupFragment
}
