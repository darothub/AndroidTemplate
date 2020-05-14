package com.anapfoundation.covid_19volunteerapp.di.fragmentmodules

import com.anapfoundation.covid_19volunteerapp.ui.CreateReportFragment
import com.anapfoundation.covid_19volunteerapp.ui.EditProfileFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class EditProfileFragmentModule {
    /**
     * A abstract function inject EditProfile Fragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideEditProfileFragment(): EditProfileFragment
}