package com.anapfoundation.covid_19volunteerapp.utils.extensions

import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import com.anapfoundation.covid_19volunteerapp.R

fun String.setAsSpannable():SpannableString{
    val spannableString: SpannableString by lazy {
        SpannableString(this)
    }
    return  spannableString
}
fun SpannableString.setColorToSubstring(color:Int, start:Int, end:Int){

    val color: ForegroundColorSpan by lazy {
        ForegroundColorSpan(color)
    }
    this.setSpan(color, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
}
fun SpannableString.enableClickOnSubstring(start:Int, end:Int, action:() -> Unit){
    var clickableSpan = object : ClickableSpan(){
        override fun onClick(widget: View) {
            action.invoke()

        }

    }
    this.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
}

class NounderLine : UnderlineSpan(){
    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = false
    }
}

fun SpannableString.removeUnderLine(start:Int, end:Int){
    this.setSpan(NounderLine(), start, end, Spanned.SPAN_MARK_MARK)
}