package com.anapfoundation.volunteerapp.di.fragmentmodules

import com.anapfoundation.volunteerapp.ui.ReviewerScreenFragment
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