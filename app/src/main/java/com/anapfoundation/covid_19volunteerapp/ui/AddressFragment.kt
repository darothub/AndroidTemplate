package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import android.text.SpannableString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.model.CityClass
import com.anapfoundation.covid_19volunteerapp.model.ArrayObjOfStates
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_address.*
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream


/**
 * A simple [Fragment] subclass.
 */
class AddressFragment : Fragment() {

    val title: String by lazy {
        getName()
    }
    val stateLgaMap: HashMap<String, List<CityClass>> by lazy {
        HashMap<String, List<CityClass>>()
    }
    val gson: Gson by lazy {
        Gson()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_address, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        readCitiesAndLgaData()
        setAdapterData()


        saveBtn.setOnClickListener {
            findNavController().navigate(R.id.reportFragment)
        }

    }

    private fun setAdapterData() {
        val newList = arrayListOf<String>()
        newList.add("States")

        newList.addAll(stateLgaMap.keys.toSortedSet())

        val adapterState =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, newList)
        spinnerState.adapter = adapterState
        spinnerState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val lga = ArrayList<String>()
                stateLgaMap.get(newList[position])!!.toList().mapTo(lga, {
                    it.name
                })

                val v = parent?.getChildAt(0) as TextView
//                v.setTextColor(resources.getColor(R.color.colorTextHint))

                val adapterLga = ArrayAdapter(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    lga
                )
                spinnerLGA.adapter = adapterLga
            }

        }
    }

    private fun readCitiesAndLgaData(){
        try {
            val inputStream: InputStream = requireActivity().assets.open("stateslga.json")
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
            Log.e(title, "$e")
        }
    }


}
