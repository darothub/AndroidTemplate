package com.anapfoundation.volunteerapp.utils.extensions

import android.os.Build
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import com.anapfoundation.volunteerapp.R
import org.hamcrest.CoreMatchers
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class LocalizationKtTest{

    @Test fun readStringFromContext_LocalizedString() {
        // Given a Fragment object is launched
        val scenario = launchFragmentInContainer<Fragment>()

        // ...when the string is returned from the object under test...
        var res = ""
        scenario.onFragment {
            res = it.getLocalisedString(R.string.dash)
        }

        // ...then the result should be the expected one.
        assertThat(res, CoreMatchers.`is`("---------"))
    }
}