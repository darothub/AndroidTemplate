package com.anapfoundation.covid_19volunteerapp.utils.extensions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.anapfoundation.covid_19volunteerapp.model.ArrayObjOfStates
import com.anapfoundation.covid_19volunteerapp.model.CityClass
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.IOException
import java.io.InputStream

inline fun Activity.getName():String{
    return this::class.qualifiedName!!
}

inline fun Activity.readCitiesAndLgaData():HashMap<String, List<CityClass>>{
    val stateLgaMap: HashMap<String, List<CityClass>> by lazy {
        HashMap<String, List<CityClass>>()
    }
    val gson: Gson by lazy {
        Gson()
    }
    try {
        val inputStream: InputStream = this.assets.open("stateslga.json")
        val json = inputStream.bufferedReader().readText()
        val stateType = object : TypeToken<ArrayList<ArrayObjOfStates?>?>() {}.type
        val stateListObject: ArrayList<ArrayObjOfStates> = gson.fromJson(json, stateType)
        stateLgaMap.put("States", listOf(CityClass("LGA", 0)))
        stateListObject.associateByTo(stateLgaMap, {
            it.state.name
        }, {
            it.state.locals.toList()
        })

//            requireContext().toast("state ${stateListObject}")

    } catch (e: IOException) {
        Log.e("$this", "$e")
    }
    return stateLgaMap
}

fun Context.permissionRequest() {
    Dexter.withContext(this)
        .withPermission(Manifest.permission.CAMERA)
        .withListener(object : PermissionListener {
            override fun onPermissionGranted(response: PermissionGrantedResponse?) {

            }
            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                val dialogPermissionListener: PermissionListener =
                    DialogOnDeniedPermissionListener.Builder
                        .withContext(this@permissionRequest)
                        .withTitle("Camera permission")
                        .withMessage("Camera permission is needed to take picture for your report")
                        .withButtonText(android.R.string.ok)
                        .withIcon(android.R.drawable.ic_menu_camera)
                        .build()
                dialogPermissionListener.onPermissionDenied(response)
            }

            override fun onPermissionRationaleShouldBeShown(
                permission: PermissionRequest?,
                token: PermissionToken?
            ) {

            }
        }).check()
}


fun Fragment.dispatchTakePictureIntent(createImageFile:()->File?, REQUEST_TAKE_PHOTO:Int) {
    //Class title
    val title: String by lazy {
        getName()
    }
    var photoString = ""
    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
        // Ensure that there's a camera activity to handle the intent
        takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
            // Create the File where the photo should go
            val photoFile: File? = try {
                createImageFile()

            } catch (ex: IOException) {
                // Error occurred while creating the File
                Log.i(title, ex.localizedMessage)
                null
            }
            // Continue only if the File was successfully created
//                Log.i(title, "File ${createImageFile()}")
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.anapfoundation.android.fileprovider",
                    it
                )

                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            }



        }
    }
}