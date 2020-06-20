package com.anapfoundation.volunteerapp.helpers

import android.widget.EditText

class IsEmptyCheck {

    companion object {
        operator fun invoke(vararg edits: EditText): EditText? {
            for (edit in edits) {
                if (edit.text.isEmpty()) {
                    return edit
                }
            }
            return null

        }

        /**
         * A function to validate email and password fields
         * @param email is a type of string
         * @param password is a type of string
         */
        fun fieldsValidation(email: String?, password: String?): String? {
            val emailPattern = Regex("""^\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*${'$'}""")
            val passwordPattern = Regex("""^[a-zA-Z0-9@$!.%*#?&]{6,}$""")
            var message:String? = ""
            when {
                email != null && password != null -> {
                    val matchedEmail = emailPattern.matches(email)
                    val matchedPassword = passwordPattern.matches(password)
                    message = when {
                        !matchedEmail -> email
                        !matchedPassword -> password
                        else -> null
                    }
                }
                password == null ->{
                    val matchedEmail = emailPattern.matches(email.toString())
                    message = when {
                        !matchedEmail -> email
                        else -> null
                    }
                }
                email == null ->{
                    val matchedPassword = passwordPattern.matches(password)
                    message = when {
                        !matchedPassword -> password
                        else -> null
                    }
                }
            }

            return message
        }
    }
}