package com.anapfoundation.covid_19volunteerapp.utils.extensions

import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.ui.SigninFragment
import com.anapfoundation.covid_19volunteerapp.ui.SignupFragment
import org.hamcrest.CoreMatchers
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class LocalizationKtTest{

    val context = ApplicationProvider.getApplicationContext<Context>()
    @Inject
    lateinit var signupFragment: SignupFragment

    @Test fun readStringFromContext_LocalizedString() {
        // Given a Context object retrieved from Robolectric...
//        val factory = FragmentFactory()
//        val scenario = launchFragmentInContainer<SignupFragment>()
//        var dash = ""
//        with(launchFragmentInContainer<SignupFragment>()){
//            onFragment {
//                dash = it.getLocalisedString(R.string.dash)
//            }
//        }


        // ...when the string is returned from the object under test...
        val result: String = context.resources.getString(R.string.dash)

        // ...then the result should be the expected one.
        assertThat(result, CoreMatchers.`is`("---------"))
    }
}