package com.anapfoundation.covid_19volunteerapp.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelModules
import com.anapfoundation.covid_19volunteerapp.di.datasourcefactory.DataSourceFactoryModule
import com.anapfoundation.covid_19volunteerapp.di.fragmentmodules.*
import com.anapfoundation.covid_19volunteerapp.di.networkmodules.auth.AuthRequestModule
import com.anapfoundation.covid_19volunteerapp.di.networkmodules.user.UserRequestModule
import com.anapfoundation.covid_19volunteerapp.helpers.SharedPrefManager
import com.anapfoundation.covid_19volunteerapp.model.User
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.ui.MainActivity
import com.anapfoundation.covid_19volunteerapp.utils.constant.BASE_URL
import com.google.gson.Gson
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
            UserRequestModule::class,
            SigninFragmentModule::class,
            ReportHomeFragmentModule::class,
            ReportParentFragmentModule::class,
            ReportUploadFragmentModule::class,
            AuthRequestModule::class,
            CreateReportFragmentModule::class,
            ProfileFragmentModule::class,
            AddressFragmentModule::class,
            EditProfileFragmentModule::class,
            ForgotPasswordFragmentModule::class,
            ResetPasswordFragmentModule::class,
            ReviewerScreenModule::class,
            ReportApprovalFragmentModule::class,
            DataSourceFactoryModule::class,
            UnapprovedReportFragmentModule::class,
            ApprovedReportFragmentModule::class

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
    open fun provideRetrofitInstance(
        gson: GsonConverterFactory,
        callAdapter: RxJava2CallAdapterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(callAdapter)
            .addConverterFactory(gson)
            .build()
    }

    @Singleton
    @Provides
    fun getContexts(application: Application): Context {
        return application.applicationContext
    }

    @Singleton
    @Provides
    fun provideSharedPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences("anapcovidapp", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Singleton
    @Provides
    fun provideStorage(sharedPrefs: SharedPreferences, gson: Gson): StorageRequest {
        return SharedPrefManager(sharedPrefs, gson)
    }

}