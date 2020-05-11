package com.anapfoundation.covid_19volunteerapp.utils.extensions

import android.app.Activity
import android.util.Log
import com.anapfoundation.covid_19volunteerapp.model.ArrayObjOfStates
import com.anapfoundation.covid_19volunteerapp.model.CityClass
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream

inline fun Activity.getName():String{
    return this::class.qualifiedName!!
}

inline fun Activity.readCitiesAndLgaData():HashMap<String, List<CityClass>>{
    val stateLgaMap: HashMap<String, List<CityClass>> by lazy {
        HashMap<String, List<CityClass>>()
    }
    val gson: Gson by lazy {
        Gson()
    }
    try {
        val inputStream: InputStream = this.assets.open("stateslga.json")
        val json = inputStream.bufferedReader().readText()
        val stateType = object : TypeToken<ArrayList<ArrayObjOfStates?>?>() {}.type
        val stateListObject: ArrayList<ArrayObjOfStates> = gson.fromJson(json, stateType)
        stateLgaMap.put("States", listOf(CityClass("LGA", 0)))
        stateListObject.associateByTo(stateLgaMap, {
            it.state.name
        }, {
            it.state.locals.toList()
        })

//            requireContext().toast("state ${stateListObject}")

    } catch (e: IOException) {
        Log.e("$this", "$e")
    }
    return stateLgaMap
}