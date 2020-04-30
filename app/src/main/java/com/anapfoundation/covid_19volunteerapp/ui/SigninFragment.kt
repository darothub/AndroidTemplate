package com.anapfoundation.covid_19volunteerapp.ui

import android.graphics.Paint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

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

        spannableString.enableClickOnSubstring(23, 30){
            Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show()
        }
        spannableString.setColorToSubstring(color, 23, 30)
        spannableString.removeUnderLine(23, 30)
        signup_link.text = spannableString
        signup_link.movementMethod = LinkMovementMethod.getInstance()

//        signup_link.paintFlags = signup_link.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
    }



}
