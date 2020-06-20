package com.anapfoundation.volunteerapp.helpers

import android.content.SharedPreferences
import com.anapfoundation.volunteerapp.model.User
import com.anapfoundation.volunteerapp.network.storage.StorageRequest
import com.google.gson.Gson
import javax.inject.Inject

class SharedPrefManager @Inject constructor(val sharedPrefs:SharedPreferences, val gson: Gson):StorageRequest{
    override fun clearData() {
        sharedPrefs.edit()
            .apply {
                clear()
                apply()
            }
    }

    override fun <T> saveData(user: T?, key: String): ArrayList<String> {
        val result:ArrayList<String>
        val userJson = gson.toJson(user)
        result = arrayListOf(userJson)
        sharedPrefs.edit()
            .apply {
                putString(key, userJson)
                apply()
            }
        return result
    }


    override fun getUserData(user:String):User? {
        return gson.fromJson(user, User::class.java)
    }

    override fun checkUser(key: String): User? {
        when {
            sharedPrefs.contains(key) ->{
                val result = sharedPrefs.getString(key, "")
                return result?.let { getUserData(it) }
            }
        }
        return null
    }


}