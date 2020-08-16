package com.anapfoundation.volunteerapp.di.fragmentmodules

import com.anapfoundation.volunteerapp.ui.ReportHomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ReportHomeFragmentModule {

    /**
     * A abstract function inject ReportHome Fragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideReportHomeFragment(): ReportHomeFragment
}
