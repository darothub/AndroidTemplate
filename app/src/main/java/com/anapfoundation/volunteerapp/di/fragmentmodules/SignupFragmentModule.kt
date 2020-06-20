package com.anapfoundation.volunteerapp.di.fragmentmodules

import com.anapfoundation.volunteerapp.ui.SignupFragment
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
