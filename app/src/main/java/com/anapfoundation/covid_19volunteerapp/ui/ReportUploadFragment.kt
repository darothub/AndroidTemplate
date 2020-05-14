package com.anapfoundation.covid_19volunteerapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.user.UserViewModel
import com.anapfoundation.covid_19volunteerapp.model.response.Data
import com.anapfoundation.covid_19volunteerapp.model.request.Report
import com.anapfoundation.covid_19volunteerapp.model.StatesList
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener
import com.karumi.dexter.listener.single.PermissionListener
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_report_upload.*
import kotlinx.android.synthetic.main.layout_upload_gallery.view.*
import kotlinx.android.synthetic.main.report_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ReportUploadFragment : DaggerFragment() {

    //Class title
    val title: String by lazy {
        getName()
    }

    lateinit var report: Report

    val REQUEST_TAKE_PHOTO = 1
    val REQUEST_FROM_GALLERY = 0
    lateinit var currentPhotoPath: String

    val bottomSheetDialog by lazy {
        BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
    //inflate bottomSheetView
    val bottomSheetView by lazy {
        LayoutInflater.from(requireContext()).inflate(
            R.layout.layout_upload_gallery,
            requireActivity().findViewById(R.id.uploadBottomSheetContainer)
        )
    }
    //Get included layout in the parent layout/* layout.fragment_report_upload */
    val bottomSheetIncludeLayout by lazy {
        bottomSheetView.findViewById<View>(R.id.galleryBottomSheet)
    }

    val cameraIcon by lazy {
        bottomSheetView.cameraIcon
    }

    val galleryIcon by lazy {
        bottomSheetView.galleryIcon
    }

    val imagePreview by lazy {
        bottomSheetView.imagePreview
    }
    //Get upload button from the included layout
    lateinit var uploadPictureBtn:Button

    val bottomSheetProgressBar by lazy {
        bottomSheetIncludeLayout.findViewById<ProgressBar>(R.id.includedProgressBar)
    }

    //Get submit button from the included layout
    lateinit var submitBtn:Button

    //Get progress bar button from the included layout
    val progressBar by lazy{
        reportUploadBottomLayout.findViewById<ProgressBar>(R.id.includedProgressBar)
    }

    //Get logged-in user
    private val getUser by lazy {
        storageRequest.checkUser("loggedInUser")
    }
    //Get token
    private val token by lazy {
        getUser?.token
    }
    //Set header
    private val header by lazy {
        "Bearer $token"
    }

    val capture by lazy {
        Bitmap.createBitmap(cameraIcon.width, cameraIcon.height, Bitmap.Config.ARGB_8888)
    }
    val canvas by lazy {
        Canvas(capture)
    }

    val firebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    val storageRef by lazy {
        firebaseStorage.reference
    }

    val timeStamp by lazy {
        SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    }
    var imageUrl = ""

    var imagePicker = "false"


    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    private val userViewModel: UserViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(UserViewModel::class.java)
    }
    private val authViewModel: AuthViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel::class.java)
    }

    @Inject
    lateinit var storageRequest: StorageRequest

    private val states = hashMapOf<String, String>()
    private val lgaAndDistrict by lazy {
        hashMapOf<String, String>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_upload, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.i(title, header)


    }

    override fun onResume() {
        super.onResume()
        setButtonText()
        uploadPictureBtn =  bottomSheetIncludeLayout.findViewById<Button>(R.id.includeBtn)
        reportUploadBackButton.setBackButtonNavigation()
        receiveReportFromPreviousScreen()
        getStateAndSendToSpinner()
        addReportRequest()
        initEnterKeyToSubmitForm(reportUploadTownEditText) {  addReportRequest() }

        setOnClickEvent(uploadCard, uploadIcon) { showBottomSheet() }
        permissionRequest()
        imagePreview.clipToOutline = true


    }


    private fun addReportRequest() {
        submitBtn.setOnClickListener {
            val story = storyEditText.text
            val stateSelected = reportUploadState.selectedItem
            val stateGUID = states.get(stateSelected)
            val selectedLGA = reportUploadLGA.selectedItem
            val lgaAndDistrictArray = lgaAndDistrict.get(selectedLGA)?.split(" ")
            val lgaGUID = lgaAndDistrictArray?.get(0).toString()
            val district = lgaAndDistrictArray?.get(1).toString()
            Log.i(title, "lgaAndDistrict $lgaAndDistrictArray lga $lgaGUID district $district")
            report.story = story.toString()
            report.state = "$stateGUID"
            report.mediaURL = imageUrl
            report.town = reportUploadTownEditText.text.toString()
            report.localGovernment = lgaGUID
            report.district = district

            val request = authViewModel.addReport(
                report.topic,
                report.rating,
                report.story,
                report.state,
                report.mediaURL,
                report.localGovernment,
                report.district,
                report.town,
                header
            )

            val response = observeRequest(request, progressBar, submitBtn)

            response.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                requestResponseTask(it)
            })

            Log.i(title, "Report $report")

        }
    }

    private fun requestResponseTask(it: Pair<Boolean, Any?>) {
        val (bool, result) = it
        when (bool) {

            true -> {
                requireContext().toast(requireContext().getLocalisedString(R.string.upload_successful))
                val res = result as Data
                Log.i(title, "message ${result.message}")
                findNavController().navigate(R.id.reportHomeFragment)
            }
            else -> Log.i(title, "error $result")
        }
    }

    private fun getStateAndSendToSpinner() {
        val stateData = getStates(header)
        stateData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            extractStateList(it)
            setupSpinner()
        })
    }

    private fun receiveReportFromPreviousScreen() {
        arguments?.let {
            report = ReportUploadFragmentArgs.fromBundle(it).report!!
        }
    }

    private fun setButtonText() {
        submitBtn = reportUploadBottomLayout.findViewById<Button>(R.id.includeBtn)
        submitBtn.setButtonText(requireContext().getLocalisedString(R.string.submit_text))
    }

    private fun setupSpinner() {
        val adapterState =
            ArrayAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                states.keys.sorted()
            )
        reportUploadState.adapter = adapterState
        setLGASpinner(reportUploadState, reportUploadLGA, lgaAndDistrict, states, userViewModel)
        Log.i(title, "states $states")
    }

    private fun extractStateList(it: StatesList) {
        it.data.associateByTo(states, {
            it.state /* key */
        }, {
            it.id /* value */
        })
    }


    private fun getStates(header:String): MediatorLiveData<StatesList> {
        val data = MediatorLiveData<StatesList>()
        val request = userViewModel.getStates("37")
        val response = observeRequest(request, null, null)
        data.addSource(response) {
            data.value = it.second as StatesList
        }
        return data
    }



    private fun showBottomSheet() {

        uploadPictureBtn.text = requireContext().getLocalisedString(R.string.done)

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
        uploadPictureBtn.setOnClickListener {

            bottomSheetProgressBar.show()
            uploadPictureBtn.hide()
            uploadReportImage()
            CoroutineScope(Main).launch {
                delay(3000)
                bottomSheetProgressBar.hide()
                uploadPictureBtn.show()
                bottomSheetDialog.dismiss()
            }

//            findNavController().navigate(R.id.reportUploadFragment)
        }
        cameraIcon.setOnClickListener {
            imagePicker = "camera"
            dispatchTakePictureIntent()
        }
        galleryIcon.setOnClickListener {

            imagePicker = "gallery"
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(intent, REQUEST_FROM_GALLERY)
        }

    }

    private fun permissionRequest() {
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {

                }
                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    val dialogPermissionListener: PermissionListener =
                        DialogOnDeniedPermissionListener.Builder
                            .withContext(context)
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

    override fun onPause() {
        super.onPause()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            bottomSheetDialog.show()
            imagePreview.setImageBitmap(imageBitmap)
            imagePreview.show()

        }
        else if (requestCode == REQUEST_FROM_GALLERY && resultCode == RESULT_OK) {
            try {
                val imageUri = data!!.data
                imagePreview.setImageURI(imageUri)
                imagePreview.show()

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }
    }

    //Event listener for upload card or upload forward icon
    fun setOnClickEvent(vararg views: View, action: () -> Unit) {
        for (view in views) {
            view.setOnClickListener {
                action()
            }
        }

    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name

        val storageDir: File? =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }


    private fun dispatchTakePictureIntent() {
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
        galleryAddPic()
    }

    private fun uploadReportImage() {
        val state = reportUploadState.selectedItem
        val path = "images/report_$state _$timeStamp" + "_.jpg"

        Log.i(title, "ImagePicker $imagePicker")


        imagePreview.draw(canvas)


        val outputStream = ByteArrayOutputStream()
        capture.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val data = outputStream.toByteArray()
        val imageRef = storageRef.child(path)

        val uploadTask = imageRef.putBytes(data)

        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Log.i(title, "Upload not successful")
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            Log.i(title, "Upload successful")
        }
        uploadTask.continueWith { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                downloadUri?.addOnSuccessListener {url ->
                    imageUrl = url.toString()
                    Log.i(title, "url $url")
                    imageUrlField.append(imageUrl)
                    imageUrlField.show()

                }

            } else {
                Log.i(title, "uri: No url")
            }
        }

    }

    // Add taken picture to gallery
    private fun galleryAddPic() {
//        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
//            val f = File(currentPhotoPath)
//            mediaScanIntent.data = Uri.fromFile(f)
//            requireActivity().sendBroadcast(mediaScanIntent)
//            Log.i(title, "new uri ${Uri.fromFile(f)}")
//        }
        MediaScannerConnection.scanFile(
            requireContext(), arrayOf<String>(currentPhotoPath),
            null
        ) { path, uri ->
            Log.i(
                title,
                "file $path was scanned seccessfully: $uri"
            )
        }
    }


}
