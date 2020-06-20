package com.anapfoundation.volunteerapp.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * An interface to build the application instance and all dependencies
 *
 */
@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, ActivityBuilderModule::class])
interface AppComponent:AndroidInjector<BaseApplication> {

    /**
     * An interface to build the application
     *
     */
    @Component.Builder
    interface Builder{

        @BindsInstance
        fun application(application: Application):Builder

        /**
         * Function to return app component
         */
        fun build():AppComponent
    }
}