package com.anapfoundation.volunteerapp.di.fragmentmodules

import com.anapfoundation.volunteerapp.ui.SingleReportFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SingleReportFragmentModule {
    /**
     * A abstract function inject Single Report Fragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideSingleReportFragment(): SingleReportFragment
}