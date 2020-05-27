package com.anapfoundation.covid_19volunteerapp.utils.extensions

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.anapfoundation.covid_19volunteerapp.model.ArrayObjOfStates
import com.anapfoundation.covid_19volunteerapp.model.CityClass
import com.cloudinary.Cloudinary
import com.cloudinary.Transformation
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.transformation.Layer
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

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


fun Activity.createImageFile(): Pair<File?, String> {
    val timeStamp by lazy {
        SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    }
    lateinit var currentPhotoPath: String
    // Create an image file name

    val storageDir: File? =
        this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val newFile = File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    ).apply {
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = absolutePath
    }
    return Pair(newFile, currentPhotoPath)
}
fun Fragment.dispatchTakePictureIntent(file:File?, REQUEST_TAKE_PHOTO:Int) {
    //Class title
    val title: String by lazy {
        getName()
    }

    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
        // Ensure that there's a camera activity to handle the intent
        takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
            // Create the File where the photo should go
            val photoFile: File? = try {
                file
            } catch (ex: IOException) {
                // Error occurred while creating the File
                Log.i(title, ex.localizedMessage)
                null
            }
            // Continue only if the File was successfully created
                Log.i(title, "File $file")
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


fun Fragment.uploadImage(path:String, imagePreview:ImageView, capture:Bitmap, canvas:Canvas, storageRef:StorageReference, imageUrlText:TextView, report:Boolean=false){

    val title:String by lazy{
        this.getName()
    }
    var imageUrl = ""

    val path = path
    val imageRef = storageRef.child(path)

    imagePreview.draw(canvas)

    val outputStream = ByteArrayOutputStream()
    capture.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    val data = outputStream.toByteArray()

    try {
        val uploadRequest = MediaManager.get().upload(data)
        val cloudinary = MediaManager.get().cloudinary.uploader()

        if (report) {
            uploadRequest
                .option("folder", "Reports")
                .option("resource_type", "image")
                .option("use_filename", true)
                .option("public_id", path)
                .maxFileSize(5 * 1024 * 1024)
        } else {
            uploadRequest
                .option("folder", "Profile")
                .option("resource_type", "image")
                .option("use_filename", true)
                .option("public_id", path)
                .maxFileSize(5 * 1024 * 1024)
        }
        uploadRequest.callback(object :UploadCallback{
                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
//                    cloudinary.destroy()
                    imageUrl = resultData?.get("secure_url").toString()
                    imageUrlText.text = ""
                    imageUrlText.text = "imageUrl: "
                    imageUrlText.append(imageUrl)
                    Log.i(title, "url ${imageUrlText.text}")
                    Log.i(title, "data $resultData")
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    Log.i(title, "$totalBytes")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    Log.i(title, "${error?.description}")
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Log.i(title, "${error?.description}")
                }

                override fun onStart(requestId: String?) {
                    Log.i(title, "Upload started")
                }

            })
        val req = uploadRequest.dispatch(requireContext())


    }
    catch (e:Exception){
        Log.e(title, e.localizedMessage)
    }

}