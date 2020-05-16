package com.anapfoundation.covid_19volunteerapp.di.fragmentmodules

import com.anapfoundation.covid_19volunteerapp.ui.ReviewerScreenFragment
import com.anapfoundation.covid_19volunteerapp.ui.SigninFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ReviewerScreenModule {

    /**
     * A abstract function injects ReviewerScreenFragment from DaggerGraph
     */
    @ContributesAndroidInjector()
    abstract fun provideReviewerScreen(): ReviewerScreenFragment
}