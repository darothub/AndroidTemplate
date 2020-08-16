package com.anapfoundation.volunteerapp.di.fragmentmodules

import com.anapfoundation.volunteerapp.ui.EditProfileFragment
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