package com.anapfoundation.volunteerapp.network.storage

import com.anapfoundation.volunteerapp.model.User

interface StorageRequest {
    fun clearData()
    fun <T>saveData(user: T?, key:String):ArrayList<String>
    fun <T>keepData(user: T?, key:String)
    fun getUserData(user:String):User?
    fun checkUser(key: String): User?
    fun <T>clearByKey(key: String):Boolean
}



