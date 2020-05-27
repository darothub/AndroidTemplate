package com.anapfoundation.covid_19volunteerapp.data.viewmodel.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.anapfoundation.covid_19volunteerapp.model.response.Data
import com.anapfoundation.covid_19volunteerapp.model.DefaultResponse
import com.anapfoundation.covid_19volunteerapp.model.LGA
import com.anapfoundation.covid_19volunteerapp.model.StatesList
import com.anapfoundation.covid_19volunteerapp.model.User
import com.anapfoundation.covid_19volunteerapp.model.user.UserResponse
import com.anapfoundation.covid_19volunteerapp.network.user.UserRequestInterface
import com.anapfoundation.covid_19volunteerapp.services.ServicesResponseWrapper
import com.anapfoundation.covid_19volunteerapp.utils.extensions.getName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject


class UserViewModel @Inject constructor(
    val userRequestInterface: UserRequestInterface,
    val retrofit: Retrofit,
    val state: SavedStateHandle
) : ViewModel() {

    val title: String by lazy {
        this.getName()

    }
    val registeringUser:String by lazy {
        "registeringUser"
    }

    fun getSavedUserForm(): User?{
        return state.get<User>(registeringUser)
    }
    fun saveRegisteringUser(user: User){
        state.set(registeringUser, user)
    }

    fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        password: String,
        houseNumber: String,
        street: String,
        state: String,
        localGovernment: String,
        zone:String,
        district:String

    ): LiveData<ServicesResponseWrapper<Data>> {

        val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
        responseLiveData.value = ServicesResponseWrapper.Loading(
            null,
            "Loading..."
        )
        val request = userRequestInterface.registerUser(
            firstName,
            lastName,
            email,
            password,
            phone,
            houseNumber,
            street,
            state,
            localGovernment,
            zone,
            district
        )
        request.enqueue(object : Callback<UserResponse> {
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                onFailureResponse(responseLiveData, t)
            }

            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                onResponseTask(response as Response<Data>, responseLiveData)
            }

        })
        return responseLiveData
    }

    fun loginUserRequest(
        username: String,
        password: String
    ): LiveData<ServicesResponseWrapper<Data>> {

        val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
        responseLiveData.value = ServicesResponseWrapper.Loading(
            null,
            "Loading..."
        )
        val request = userRequestInterface.loginRequest(username, password)
        request.enqueue(object : Callback<UserResponse> {
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                onFailureResponse(responseLiveData, t)
                Log.i(title, "Error $t")
            }

            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                onResponseTask(response as Response<Data>, responseLiveData)
            }

        })
        return responseLiveData
    }
    fun getStates(first: String, after:String?=""): LiveData<ServicesResponseWrapper<Data>>{

        val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
        responseLiveData.value = ServicesResponseWrapper.Loading(
            null,
            "Loading..."
        )
        val request = userRequestInterface.getStates(first, after)

        request.enqueue(object :Callback<StatesList>{
            override fun onFailure(call: Call<StatesList>, t: Throwable) {
                onFailureResponse(responseLiveData, t)
            }

            override fun onResponse(call: Call<StatesList>, response: Response<StatesList>) {
                onResponseTask(response as Response<Data>, responseLiveData)
            }

        })
        return responseLiveData

    }

    fun getLocal(stateID:String,first: String, after: String?): LiveData<ServicesResponseWrapper<Data>>{
        val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
        responseLiveData.value = ServicesResponseWrapper.Loading(
            null,
            "Loading..."
        )
        val request = userRequestInterface.getLocal(stateID, first, after)
        request.enqueue(object :Callback<LGA>{
            override fun onFailure(call: Call<LGA>, t: Throwable) {
                onFailureResponse(responseLiveData, t)
            }

            override fun onResponse(call: Call<LGA>, response: Response<LGA>) {
                onResponseTask(response as Response<Data>, responseLiveData)
            }

        })
        return responseLiveData
    }
    fun forgotPasswordRequest(email: String): LiveData<ServicesResponseWrapper<Data>>{
        val responseLiveData = MutableLiveData<ServicesResponseWrapper<Data>>()
        responseLiveData.value = ServicesResponseWrapper.Loading(
            null,
            "Loading..."
        )
        val request = userRequestInterface.forgotPasswordRequest(email)
        request.enqueue(object :Callback<DefaultResponse>{
            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                onFailureResponse(responseLiveData, t)
            }

            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                onResponseTask(response as Response<Data>, responseLiveData)
            }

        })
        return responseLiveData
    }

    internal fun onFailureResponse(
        responseLiveData: MutableLiveData<ServicesResponseWrapper<Data>>,
        t: Throwable
    ) {
        responseLiveData.postValue(ServicesResponseWrapper.Error(t.localizedMessage, 502, null))
    }
    internal fun onResponseTask(response: Response<Data>, responseLiveData: MutableLiveData<ServicesResponseWrapper<Data>>){
        val res = response.body()
        val statusCode = response.code()
        Log.i(title, "${response.code()}")
        when(statusCode) {

            401 -> {
                try {
                    Log.i(title, "errorbody ${response.raw()}")
                    val a = object : Annotation{}
                    val converter = retrofit.responseBodyConverter<DefaultResponse>(
                        DefaultResponse::class.java, arrayOf(a))
                    val error = converter.convert(response.errorBody())
                    Log.i(title, "message ${error?.message}")
                    responseLiveData.postValue(ServicesResponseWrapper.Logout(error?.message.toString()))
                }
                catch (e:Exception){
                    Log.i(title, e.message)
                }

            }
            in 400..501 ->{
                try {
                    Log.i(title, "errorbody ${response.raw()}")
                    val a = object : Annotation{}
                    val converter = retrofit.responseBodyConverter<DefaultResponse>(
                        DefaultResponse::class.java, arrayOf(a))
                    val error = converter.convert(response.errorBody())
                    Log.i(title, "message ${error?.message}")
                    responseLiveData.postValue(ServicesResponseWrapper.Error(error?.message))
                }
                catch (e:java.lang.Exception){
                    Log.i(title, e.message)
                }

            }
            else -> {
                try {
                    Log.i(title, "errors ${response.errorBody()}")
                    responseLiveData.postValue(ServicesResponseWrapper.Success(res))
                }catch (e:java.lang.Exception){
                    Log.i(title, e.message)
                }


            }
        }

    }
}