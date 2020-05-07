package com.anapfoundation.covid_19volunteerapp.services

/**
 * A sealed class to control response from API
 */
sealed class ServicesResponseWrapper<T>(
    val data: T? = null,
    val message: String? = null
) {
    /**
     * A success class wrapper
     */
    class Success<T>(data: T?) : ServicesResponseWrapper<T>(data)
    /**
     * A loading class wrapper
     */
    class Loading<T>(data: T? = null, message: String) : ServicesResponseWrapper<T>(data, message)
    class Logout<T>(message: String, data: T? = null) : ServicesResponseWrapper<T>(data, message)
    /**
     * An error class wrapper
     */
    class Error<T>(message: String?, data: T? = null) : ServicesResponseWrapper<T>(data, message)
}