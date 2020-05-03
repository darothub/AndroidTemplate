package com.anapfoundation.covid_19volunteerapp.model

data class CityClass(val name:String, val id:Int)

data class ArrayObjOfStates(val state:States)

data class States(val name: String, val id:Int, val locals:Array<CityClass>)