package com.anapfoundation.volunteerapp.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anapfoundation.volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.volunteerapp.data.viewmodel.user.UserViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Provider

@Module(includes = [ProviderModule::class])
abstract class ViewModelModules {
    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel::class)
    abstract fun bindUserViewModel(registerUserViewModel: UserViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel
}

@Module
internal object ProviderModule{
    @Provides
    fun viewModelFactory(creators: MutableMap<Class<out ViewModel>, Provider<ViewModel>>): ViewModelProvider.Factory{
        return ViewModelProviderFactory(creators)
    }
}