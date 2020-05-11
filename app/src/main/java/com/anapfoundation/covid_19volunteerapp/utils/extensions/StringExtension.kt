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

inline fun String.setAsSpannable():SpannableString{
    val spannableString: SpannableString by lazy {
        SpannableString(this)
    }
    return  spannableString
}
inline fun SpannableString.setColorToSubstring(color:Int, start:Int, end:Int){

    val color: ForegroundColorSpan by lazy {
        ForegroundColorSpan(color)
    }
    this.setSpan(color, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
}
inline fun SpannableString.enableClickOnSubstring(start:Int, end:Int, crossinline action:() -> Unit){
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

inline fun SpannableString.removeUnderLine(start:Int, end:Int){
    this.setSpan(NounderLine(), start, end, Spanned.SPAN_MARK_MARK)
}