package com.anapfoundation.covid_19volunteerapp.di

import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelModules
import com.anapfoundation.covid_19volunteerapp.di.fragmentmodules.SignupFragmentModule
import com.anapfoundation.covid_19volunteerapp.di.networkmodules.UserRequestModule
import com.anapfoundation.covid_19volunteerapp.ui.MainActivity
import com.anapfoundation.covid_19volunteerapp.utils.constant.BASE_URL
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [ActivityStaticModule::class])
abstract class ActivityBuilderModule {
    @ContributesAndroidInjector(
        modules = [
            SignupFragmentModule::class,
            ViewModelModules::class,
            UserRequestModule::class
        ]
    )
    abstract fun mainActivity(): MainActivity
}

@Module
open class ActivityStaticModule {
    /**
     * A function to provide context
     *
     */

    @Singleton
    @Provides
    fun provideCallAdapterFactory(): RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()

    @Singleton
    @Provides
    fun provideGsonConverterFcatory(): GsonConverterFactory = GsonConverterFactory.create()
    /**
     * A function to provide retrofit instance
     *
     */
    @Singleton
    @Provides
    open fun provideRetrofitInstance(gson: GsonConverterFactory, callAdapter: RxJava2CallAdapterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(callAdapter)
            .addConverterFactory(gson)
            .build()
    }


}