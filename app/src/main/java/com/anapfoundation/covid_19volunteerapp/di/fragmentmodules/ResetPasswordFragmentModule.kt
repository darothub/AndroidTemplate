package com.anapfoundation.covid_19volunteerapp.di.fragmentmodules

import com.anapfoundation.covid_19volunteerapp.ui.ForgotPasswordFragment
import com.anapfoundation.covid_19volunteerapp.ui.ResetPasswordFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ResetPasswordFragmentModule {
    /**
     * A abstract function inject Reset password Fragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideResetPasswordFragment(): ResetPasswordFragment
}