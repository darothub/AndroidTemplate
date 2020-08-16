package com.anapfoundation.volunteerapp.di.fragmentmodules

import com.anapfoundation.volunteerapp.ui.ApprovedReportFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector
@Module
abstract class ApprovedReportFragmentModule {

    /**
     * A abstract function inject Approved Reports Fragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideApprovedReportFragment(): ApprovedReportFragment
}