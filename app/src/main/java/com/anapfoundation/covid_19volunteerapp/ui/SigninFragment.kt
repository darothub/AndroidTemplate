package com.anapfoundation.covid_19volunteerapp.ui

import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
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
        signupText.setAsSpannable()
    }
    val color:Int by lazy {
        resources.getColor(R.color.colorPrimary)
    }

    private val textLen:Int by lazy {
        signupText.length
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        showStatusBar()
        return inflater.inflate(R.layout.fragment_signin, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupSignUpLink()

        passwordEditField.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                hideKeyboard()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

    }

    fun setupSignUpLink(){
        val start = 23
        val end = textLen
        spannableString.enableClickOnSubstring(start, end){
            findNavController().navigate(R.id.signupFragment)
        }
        spannableString.setColorToSubstring(color, start, end)
        spannableString.removeUnderLine(start, end)
        signupLink.text = spannableString
        signupLink.movementMethod = LinkMovementMethod.getInstance()
    }


}
