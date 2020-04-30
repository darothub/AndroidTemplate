package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.utils.extensions.getName
import com.anapfoundation.covid_19volunteerapp.utils.extensions.localized
import kotlinx.android.synthetic.main.fragment_signin.*

/**
 * A simple [Fragment] subclass.
 */
class SigninFragment : Fragment() {

    val title:String by lazy {
        getName()
    }
    val signupText:String by lazy {
        requireContext().localized(R.string.signup_link)
    }
    val spannableString:SpannableString by lazy {
        SpannableString(signupText)
    }
    val color:ForegroundColorSpan by lazy {
        ForegroundColorSpan(resources.getColor(R.color.colorPrimary))
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        spannableString.setSpan(color, 23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        Toast.makeText(context, signupText, Toast.LENGTH_SHORT).show()

        signup_link.text = spannableString
    }
}
