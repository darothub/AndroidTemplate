package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.model.CityClass
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_address.*


/**
 * A simple [Fragment] subclass.
 */
class AddressFragment : Fragment() {

    val title: String by lazy {
        getName()
    }
    val stateLgaMap: HashMap<String, List<CityClass>> by lazy {
        requireActivity().readCitiesAndLgaData()
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


        requireContext().setSpinnerAdapterData(spinnerState, spinnerLGA, stateLgaMap)

        val saveBtn = addressbottomIndicator.findViewById<Button>(R.id.includeBtn)
        saveBtn.text = requireContext().localized(R.string.save_text)
        saveBtn.setOnClickListener {
            findNavController().navigate(R.id.reportFragment)
        }

    }





}
