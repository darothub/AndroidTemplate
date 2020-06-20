package com.anapfoundation.volunteerapp.helpers

import org.hamcrest.CoreMatchers
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class IsEmptyCheckTest {

    @Before
    fun setUp() {
    }

    @Test
    fun emailAndPasswordValidator_CorrectEmailAndPassword_ReturnsNull() {
        assertNull(IsEmptyCheck.fieldsValidation("anap@gmail.com", "Password"))
    }

    @Test
    fun emailAndPasswordValidator_IncorrectPasswordOnly_ReturnsTheInvalidString() {
        assertThat(IsEmptyCheck.fieldsValidation("anap@gmail.com", "Pass"), CoreMatchers.`is`("Pass"))
    }

    @Test
    fun emailAndPasswordValidator_IncorrectEmailOnly_ReturnsTheInvalidString() {
        assertThat(IsEmptyCheck.fieldsValidation("anapgmail.com", "Pass"), CoreMatchers.`is`("anapgmail.com"))
    }
}