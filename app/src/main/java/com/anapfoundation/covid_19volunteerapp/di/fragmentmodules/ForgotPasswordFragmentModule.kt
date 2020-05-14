package com.anapfoundation.covid_19volunteerapp.di.fragmentmodules

import com.anapfoundation.covid_19volunteerapp.ui.ForgotPasswordFragment
import com.anapfoundation.covid_19volunteerapp.ui.ProfileFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ForgotPasswordFragmentModule {

    /**
     * A abstract function inject Forgot password Fragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideProfileFragment(): ForgotPasswordFragment
}