package com.anapfoundation.covid_19volunteerapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.user.UserViewModel
import com.anapfoundation.covid_19volunteerapp.model.response.Data
import com.anapfoundation.covid_19volunteerapp.model.request.Report
import com.anapfoundation.covid_19volunteerapp.model.StatesList
import com.anapfoundation.covid_19volunteerapp.model.request.AddReportResponse
import com.anapfoundation.covid_19volunteerapp.model.user.UserResponse
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.storage.FirebaseStorage
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_report_upload.*
import kotlinx.android.synthetic.main.layout_upload_gallery.view.*
import kotlinx.android.synthetic.main.report_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.*
import java.security.Permission
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
    private val loggedInUser by lazy {
        storageRequest.checkUser("loggedInUser")
    }
    //Get token
    private val token by lazy {
        loggedInUser?.token
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

    private val cameraPermission by lazy {
        net.codecision.startask.permissions.Permission.Builder(Manifest.permission.CAMERA)
            .setRequestCode(REQUEST_TAKE_PHOTO)
            .build()
    }
    var imageUrl:String?=null
    var suggestion:String?=null


    val imageFileAndPath by lazy {
        requireActivity().createImageFile()
    }

    var path =""

    lateinit var photoUri: Uri
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
        imagePreview.clipToOutline = true

        cameraIcon.setOnClickListener {
            checkCameraPermission()
        }
        galleryIcon.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(intent, REQUEST_FROM_GALLERY)
        }

        bottomSheetDialog.setOnDismissListener {
            Log.i(title, "dismissed ${it.dismiss()}")
        }
        bottomSheetDialog.setOnCancelListener {
            Log.i(title, "cancelled ${it.cancel()}")
        }
    }


    private fun addReportRequest() {
        submitBtn.setOnClickListener {

            CoroutineScope(Main).launch {
                progressBar.show()
                submitBtn.hide()
                uploadImage(path, imagePreview, capture, canvas, storageRef, imageUrlField, true)
                delay(8000)

                val story = storyEditText.text
                val stateGUID = loggedInUser?.stateID
                val zoneGUID = loggedInUser?.zoneID
                val lgaGUID = loggedInUser?.lgID
                val district = loggedInUser?.districtID
                suggestion = suggestionEditText.text.toString()
                imageUrl = imageUrlField.text.toString().substring(10)

                Log.i(title, "state $stateGUID, local $lgaGUID, district $district zone $zoneGUID")

                if (suggestion!!.isEmpty()){
                    suggestion = null
                }
                if (imageUrl!!.isEmpty()){
                    imageUrl = null
                }
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
                    zoneGUID,
                    suggestion,
                    header
                )
                Log.i(title, "mediaURL $imageUrl suggestion $suggestion")
                val response = observeRequest(request, progressBar, submitBtn)

                response.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    requestResponseTask(it)
                })
            }




            Log.i(title, "Report $report")

        }
    }

    private fun requestResponseTask(it: Pair<Boolean, Any?>) {
        val (bool, result) = it
        when (bool) {

            true -> {
                requireContext().toast(requireContext().getLocalisedString(R.string.upload_successful))
                loggedInUser?.totalReports = loggedInUser?.totalReports?.plus(1)
                storageRequest.saveData(loggedInUser, "loggedInUser")
                val res = result as AddReportResponse
                Log.i(title, "message ${result.message}")

                findNavController().navigate(R.id.reportHomeFragment)
            }
            false ->{
                val res = result
                Log.i(title, "error $res")
            }

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
        reportUploadState.setText(loggedInUser?.stateName)
        reportUploadLGA.setText(loggedInUser?.lgName)
//        reportUploadState.adapter = adapterState
//        setLGASpinner(reportUploadState, reportUploadLGA, lgaAndDistrict, states, userViewModel)
        Log.i(title, "states $states")
    }

    private fun extractStateList(it: StatesList) {
        it.data.associateByTo(states, {
            it.state /* key */
        }, {
            "${it.id} ${it.zone}" /* value */
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
        uploadPictureText.text = requireContext().getLocalisedString(R.string.upload_picture)
//        val state = reportUploadState.selectedItem




        uploadPictureBtn.text = requireContext().getLocalisedString(R.string.upload_image)

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
//        uploadPictureBtn.setOnClickListener {
//
//            bottomSheetProgressBar.show()
//            uploadPictureBtn.hide()
//            uploadImage(path, imagePreview, capture, canvas, storageRef, imageUrlField)
//            CoroutineScope(Main).launch {
//                delay(3000)
//                bottomSheetProgressBar.hide()
//                uploadPictureBtn.show()
//                bottomSheetDialog.dismiss()
//            }
//
////            findNavController().navigate(R.id.reportUploadFragment)
//        }


    }



    private fun checkCameraPermission() {
        cameraPermission.check(this)
            .onGranted {

                dispatchTakePictureIntent(imageFileAndPath.first, REQUEST_TAKE_PHOTO)

            }.onShowRationale {
                showRationaleDialog()
            }
    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Camera permission")
            .setMessage("Allow app to use your camera to take photos and record videos.")
            .setPositiveButton("Allow") { _, _ ->
                cameraPermission.request(this)
            }
            .setNegativeButton("Deny") { _, _ ->

            }
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == REQUEST_TAKE_PHOTO){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                Log.i(title, "herePermissionResultGranted")
            }
            else{
                if(!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)){
                    Log.i(title, "herePermissionResult")
                    AlertDialog.Builder(requireActivity())
                        .setMessage("Permanently denied request")
                        .setPositiveButton("Go to setting") { dialog, which ->
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", activity?.packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()

    }

    @ExperimentalStdlibApi
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            bottomSheetDialog.show()
            imagePreview.setImageBitmap(imageBitmap)
            imagePreview.show()

            uploadPictureEvent(imageBitmap, null)

        }
        else if (requestCode == REQUEST_FROM_GALLERY && resultCode == RESULT_OK) {
            try {
                val imageUri = data!!.data
                imagePreview.setImageURI(imageUri)
                imagePreview.show()
                if (imageUri != null) {
                    uploadPictureEvent(null, imageUri)
                }

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }
    }

    @ExperimentalStdlibApi
    private fun uploadPictureEvent(imageBitmap: Bitmap?, uri:Uri?) {
        path = "images/report_$timeStamp"
        uploadPictureBtn.setOnClickListener {
            bottomSheetProgressBar.show()
            uploadPictureBtn.hide()

            CoroutineScope(Main).launch {
                delay(1000)
                bottomSheetProgressBar.hide()
                uploadPictureBtn.show()
                if (imageBitmap != null){
                    imageUploadPreview.setImageBitmap(imageBitmap)
                    imageUploadPreview.show()
                }
                else{
                    imageUploadPreview.setImageURI(uri)
                    imageUploadPreview.show()
                }

                uploadPictureText.setText(requireContext().getLocalisedString(R.string.new_picture))
                bottomSheetDialog.dismiss()

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


    // Add taken picture to gallery
    private fun galleryAddPic() {
        currentPhotoPath = imageFileAndPath.second
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            requireActivity().sendBroadcast(mediaScanIntent)
            Log.i(title, "new uri ${Uri.fromFile(f)}")
        }




    }


}
