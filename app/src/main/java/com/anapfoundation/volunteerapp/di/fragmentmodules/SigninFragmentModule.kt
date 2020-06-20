package com.anapfoundation.volunteerapp.di.fragmentmodules

import com.anapfoundation.volunteerapp.ui.SigninFragment
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