package com.anapfoundation.volunteerapp.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.addCallback
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.anapfoundation.volunteerapp.R
import com.anapfoundation.volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.volunteerapp.data.viewmodel.auth.resetPassword
import com.anapfoundation.volunteerapp.helpers.IsEmptyCheck
import com.anapfoundation.volunteerapp.model.user.UserResponse
import com.anapfoundation.volunteerapp.network.storage.StorageRequest
import com.anapfoundation.volunteerapp.utils.extensions.*
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_reset_password.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ResetPasswordFragment : DaggerFragment() {
    val title: String by lazy {
        getName()
    }

    val CHANNEL_ID = "reset"


    private val progressBar by lazy {
        resetPasswordBottomLayout.findViewById<ProgressBar>(R.id.includedProgressBar)
    }
    lateinit var resetButton: Button
    lateinit var logoDrawable: Drawable

    var signupFragment = SignupFragment()

    @Inject
    lateinit var storageRequest: StorageRequest
    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    val authViewModel: AuthViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel::class.java)
    }


    lateinit var token: String
    var header = "Bearer "
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password, container, false)
    }

    override fun onStart() {
        super.onStart()

//        Log.i(title, "onStart")
        arguments?.let {
            token = ResetPasswordFragmentArgs.fromBundle(it).token!!
        }

        header += token
        requireActivity().onBackPressedDispatcher.addCallback {

            goto(R.id.signinFragment)
        }
       createNotificationChannel()


//        Log.i(title, "token $token")
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(true)
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = requireContext().getSystemService(
                NOTIFICATION_SERVICE) as NotificationManager

            if(channel.sound == null){
                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, "com.anapfoundation.covid_19volunteerapp")
                    putExtra(Settings.EXTRA_CHANNEL_ID, channel.id)
                }
                startActivity(intent)
            }
            notificationManager.createNotificationChannel(channel)




        }
    }

    override fun onResume() {
        super.onResume()
        Log.i(title, "onResume")
        resetButton = resetPasswordBottomLayout.findViewById<Button>(R.id.includeBtn)
        resetButton.setButtonText(getLocalisedString(R.string.submit_text))

        initEnterKeyToSubmitForm(resetPasswordPasswordEdit) { resetPasswordRequest() }
        resetButton.setOnClickListener {
            resetPasswordRequest()
        }

        resetPasswordPasswordEdit.doOnTextChanged { text, start, count, after ->
            if (text != null) {
                signupFragment.validateEmailAndPassword(text, resetPasswordStandard)
            }
            return@doOnTextChanged
        }

        val user = storageRequest.checkUser("loggedInUser")

    }

    /**
     * Reset password request
     *
     */
    private fun resetPasswordRequest() {
        val password = resetPasswordPasswordEdit.text.toString().trim()
        val checkForEmpty =
            IsEmptyCheck(resetPasswordPasswordEdit)
        val validation = IsEmptyCheck.fieldsValidation(null, password)

        when {
            checkForEmpty != null -> {
                checkForEmpty.error = getLocalisedString(R.string.field_required)
                toast("${checkForEmpty.hint} is empty")
            }
            validation != null -> toast(getLocalisedString(R.string.password_invalid))
            else -> {
                val request = authViewModel.resetPassword(password, token)
                val response = observeRequest(request, progressBar, resetButton)

                Log.i(title, "token $token")
                response.observe(viewLifecycleOwner, Observer {
                    val (bool, result) = it
                    onRequestResponseTask(bool, result)
                })
            }
        }
    }

    /**
     * Handles request live response
     *
     * @param bool
     * @param result
     */
    private fun onRequestResponseTask(
        bool: Boolean,
        result: Any?
    ) {
//        resources.getDrawable(R.drawable.applogo, requireContext().theme)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            logoDrawable = resources.getDrawable(R.drawable.logo_black, requireContext().theme)
        } else {
            logoDrawable = resources.getDrawable(R.drawable.logo_black)
        }
        val icon = BitmapFactory.decodeResource(resources, R.drawable.logo_black)
        when (bool) {
            true -> {
                val res = result as UserResponse
                toast(res.data.toString())

                // Create an explicit intent for an Activity in your app
                val intent = Intent(requireContext(), MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent: PendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, 0)

                val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(requireContext().getString(R.string.password_reset))
                    .setContentText(getLocalisedString(R.string.reset_success))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setColorized(true)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setStyle(NotificationCompat.BigPictureStyle()
                        .bigPicture(icon))
                    .setAutoCancel(true)
                with(NotificationManagerCompat.from(requireContext())) {
                    // notificationId is a unique int for each notification that you must define
                    notify(0, builder.build())
                }
                activity?.finish()
//                Log.i(title, "result of reset ${res.data}")
            }
            else -> Log.i(title, "error $result")
        }
    }



    override fun onPause() {
        super.onPause()

//        Log.i(title, "onPause")
    }

}
