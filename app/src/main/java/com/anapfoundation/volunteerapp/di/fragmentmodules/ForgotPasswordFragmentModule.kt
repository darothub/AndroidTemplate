package com.anapfoundation.volunteerapp.di.fragmentmodules

import com.anapfoundation.volunteerapp.ui.ForgotPasswordFragment
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