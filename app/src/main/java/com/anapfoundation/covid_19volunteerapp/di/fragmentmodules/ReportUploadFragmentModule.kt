package com.anapfoundation.covid_19volunteerapp.di.fragmentmodules

import com.anapfoundation.covid_19volunteerapp.ui.ReportHomeFragment
import com.anapfoundation.covid_19volunteerapp.ui.ReportUploadFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ReportUploadFragmentModule {
    /**
     * A abstract function inject ReportUpload Fragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideReportUploadFragment(): ReportUploadFragment
}