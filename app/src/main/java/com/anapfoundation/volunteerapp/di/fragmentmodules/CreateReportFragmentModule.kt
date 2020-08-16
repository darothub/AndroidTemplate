package com.anapfoundation.volunteerapp.di.fragmentmodules

import com.anapfoundation.volunteerapp.ui.CreateReportFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class CreateReportFragmentModule {
    /**
     * A abstract function inject CreateReport Fragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideCreateReportFragment(): CreateReportFragment
}