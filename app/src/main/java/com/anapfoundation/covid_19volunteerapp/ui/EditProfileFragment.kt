package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.model.CityClass
import com.anapfoundation.covid_19volunteerapp.utils.extensions.getName
import com.anapfoundation.covid_19volunteerapp.utils.extensions.readCitiesAndLgaData
import com.anapfoundation.covid_19volunteerapp.utils.extensions.setSpinnerAdapterData
import kotlinx.android.synthetic.main.fragment_address.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*

/**
 * A simple [Fragment] subclass.
 */
class EditProfileFragment : Fragment() {

    val title: String by lazy {
        getName()
    }
    val stateLgaMap: HashMap<String, List<CityClass>> by lazy {
        requireActivity().readCitiesAndLgaData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requireContext().setSpinnerAdapterData(editInfoStateSpinner, editInfoLGASpinner, stateLgaMap)

        editInfoBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}
