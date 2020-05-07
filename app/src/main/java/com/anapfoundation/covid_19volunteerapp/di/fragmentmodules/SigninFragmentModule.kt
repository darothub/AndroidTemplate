package com.anapfoundation.covid_19volunteerapp.di.fragmentmodules

import com.anapfoundation.covid_19volunteerapp.ui.SigninFragment
import com.anapfoundation.covid_19volunteerapp.ui.SignupFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SigninFragmentModule {

    /**
     * A abstract function inject SigninFragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideSigninFragment(): SigninFragment
}