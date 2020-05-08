package com.anapfoundation.covid_19volunteerapp.data.viewmodel.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anapfoundation.covid_19volunteerapp.model.servicesmodel.ServiceResult
import com.anapfoundation.covid_19volunteerapp.network.user.UserRequestInterface
import com.anapfoundation.covid_19volunteerapp.services.ServicesResponseWrapper
import com.anapfoundation.covid_19volunteerapp.utils.extensions.getName
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.Type
import javax.inject.Inject


class UserViewModel @Inject constructor (val userRequestInterface: UserRequestInterface, val retrofit: Retrofit):ViewModel() {

    val title:String by lazy{
        this.getName()

    }

    fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        password: String

    ): LiveData<ServicesResponseWrapper<ServiceResult>>{

        val responseLiveData = MutableLiveData<ServicesResponseWrapper<ServiceResult>>()
        responseLiveData.value = ServicesResponseWrapper.Loading(
            null,
            "Loading..."
        )
        val request = userRequestInterface.registerUser(firstName, lastName, email, password, phone)
        request.enqueue(object : Callback<ServiceResult>{
            override fun onFailure(call: Call<ServiceResult>, t: Throwable) {
                responseLiveData.postValue(ServicesResponseWrapper.Error("${t.message}", null))
            }

            override fun onResponse(call: Call<ServiceResult>, response: Response<ServiceResult>) {
                val res = response.body()
                Log.i(title, "${response.code()}")
                when {
                    response.code() != 200 || response.code() != 201 ->{
                        Log.i(title, "errorbody ${response.raw()}")
                        val a = object : Annotation{}
                        val converter = retrofit.responseBodyConverter<ServiceResult>(ServiceResult::class.java, arrayOf(a))
                        val error = converter.convert(response.errorBody())
                        Log.i(title, "message ${error?.message}")
                        responseLiveData.postValue(ServicesResponseWrapper.Error(error?.message))
                    }
                    res?.token != null -> {
                        Log.i(title, "token ${res.token}")
                        responseLiveData.postValue(ServicesResponseWrapper.Success(res))
                    }
                    else -> {
                        Log.i(title, "errors ${response.errorBody()}")
                        responseLiveData.postValue(ServicesResponseWrapper.Error(res?.message))
                    }
                }
            }

        })
        return responseLiveData
    }

    fun loginUserRequest(username: String, password: String): LiveData<ServicesResponseWrapper<ServiceResult>>{

        val responseLiveData = MutableLiveData<ServicesResponseWrapper<ServiceResult>>()
        responseLiveData.value = ServicesResponseWrapper.Loading(
            null,
            "Loading..."
        )
        val request = userRequestInterface.loginRequest(username, password)
        request.enqueue(object : Callback<ServiceResult>{
            override fun onFailure(call: Call<ServiceResult>, t: Throwable) {
                responseLiveData.postValue(ServicesResponseWrapper.Error("${t.message}", null))
            }

            override fun onResponse(call: Call<ServiceResult>, response: Response<ServiceResult>) {
                val res = response.body()
                Log.i(title, "${response.code()}")
                when {
                    response.code() != 200  ->{
                        Log.i(title, "errorbody ${response.raw()}")
                        val a = object : Annotation{}
                        val converter = retrofit.responseBodyConverter<ServiceResult>(ServiceResult::class.java, arrayOf(a))
                        val error = converter.convert(response.errorBody()!!)
                        Log.i(title, "message ${error?.message}")
                        responseLiveData.postValue(ServicesResponseWrapper.Error(error?.message))
                    }
                    res?.token != null -> {
                        Log.i(title, "token ${res.token}")
                        responseLiveData.postValue(ServicesResponseWrapper.Success(res))
                    }
                    else -> {
                        Log.i(title, "errors ${response.errorBody()}")
                        responseLiveData.postValue(ServicesResponseWrapper.Error(res?.message))
                    }
                }
            }

        })
        return responseLiveData
    }
}