package com.anapfoundation.covid_19volunteerapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anapfoundation.covid_19volunteerapp.R
import java.lang.Thread.sleep

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val myThread = Thread(){
            try {
                kotlin.run {
                    sleep(2000)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
            catch(e: Exception){
                e.printStackTrace()
            }

        }
        myThread.start()
    }
}
