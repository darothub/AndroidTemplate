package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_signin.*
import kotlinx.android.synthetic.main.fragment_signup.*

/**
 * A simple [Fragment] subclass.
 */
class SignupFragment : Fragment() {

    val title:String by lazy {
        getName()
    }
    val signinText:String by lazy {
        requireContext().localized(R.string.signin_link)
    }
    val spannableString: SpannableString by lazy {
        signinText.setAsSpannable()
    }
    val color:Int by lazy {
        resources.getColor(R.color.colorPrimary)
    }
    val textLen:Int by lazy {
        signinText.length
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        showStatusBar()
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupSignInLink()
        cpasswordEditTextField.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                hideKeyboard()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        val signupBtn = signupIndicator.findViewById<Button>(R.id.includeBtn)
        signupBtn.text = requireContext().localized(R.string.signup_text)
        signupBtn.setOnClickListener {
            findNavController().navigate(R.id.addressFragment)
        }
    }

    private fun setupSignInLink() {
        val start = 25
        val end = textLen
        spannableString.enableClickOnSubstring(start, end){
            findNavController().navigate(R.id.signinFragment)
        }
        spannableString.setColorToSubstring(color, start, end)
        spannableString.removeUnderLine(start, end)
        signinLink.text = spannableString
        signinLink.movementMethod = LinkMovementMethod.getInstance()
    }
}
