package com.anapfoundation.volunteerapp.di.fragmentmodules

import com.anapfoundation.volunteerapp.ui.ReportUploadFragment
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