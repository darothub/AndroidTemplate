package com.anapfoundation.covid_19volunteerapp.network.storage

import com.anapfoundation.covid_19volunteerapp.model.User

interface StorageRequest {
    fun clearData()
    fun saveData(user: User?, key:String):ArrayList<String>
    fun getUserData(user:String):User?
    fun checkUser(key:String):User?
}