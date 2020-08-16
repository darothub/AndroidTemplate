package com.anapfoundation.volunteerapp.di.fragmentmodules

import com.anapfoundation.volunteerapp.ui.UnapprovedReportFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector
@Module
abstract class UnapprovedReportFragmentModule {
    /**
     * A abstract function inject Unapproved Reports Fragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideUnapprovedReportFragment(): UnapprovedReportFragment
}