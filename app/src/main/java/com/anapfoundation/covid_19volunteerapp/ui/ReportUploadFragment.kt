package com.anapfoundation.covid_19volunteerapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
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
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModelProvider
import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.model.*
import com.anapfoundation.covid_19volunteerapp.model.servicesmodel.ServiceResult
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import com.cloudinary.android.MediaManager
import com.google.android.material.bottomsheet.BottomSheetDialog
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
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ReportUploadFragment : DaggerFragment() {

    val title: String by lazy {
        getName()
    }
    val stateLgaMap: HashMap<String, List<CityClass>> by lazy {
        requireActivity().readCitiesAndLgaData()
    }

    lateinit var report: Report

    val REQUEST_TAKE_PHOTO = 1
    lateinit var currentPhotoPath: String

    val bottomSheetDialog by lazy {
        BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
    val bottomSheetView by lazy {
        LayoutInflater.from(requireContext()).inflate(
            R.layout.layout_upload_gallery,
            requireActivity().findViewById(R.id.uploadBottomSheetContainer)
        )
    }
    val bottomSheetIncludeLayout by lazy {
        bottomSheetView.findViewById<View>(R.id.galleryBottomSheet)
    }
    val uploadPictureBtn by lazy {
        bottomSheetIncludeLayout.findViewById<Button>(R.id.includeBtn)
    }

    val submitBtn by lazy {
        reportUploadBottomLayout.findViewById<Button>(R.id.includeBtn)
    }
    val progressBar by lazy{
        reportUploadBottomLayout.findViewById<ProgressBar>(R.id.includedProgressBar)
    }

    val getUser by lazy {
        storageRequest.checkUser("loggedInUser")
    }
    val token by lazy {
        getUser?.token
    }
    val header by lazy {
        "Bearer $token"
    }
    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    val authViewModel: AuthViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel::class.java)
    }

    @Inject
    lateinit var storageRequest: StorageRequest

    val states = hashMapOf<String, String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_upload, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        reportUploadBackButton.setBackButtonNavigation()



        arguments?.let {
            report = ReportUploadFragmentArgs.fromBundle(it).report!!
        }

        val stateData = getStates(header)


        stateData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            it.data.associateByTo(states, {
                it.state
            },{
                it.id
            })

            val adapterState =
                ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, states.keys.sorted())
            reportUploadState.adapter = adapterState
            Log.i(title, "states $states")
        })



//        requireContext().setSpinnerAdapterData(reportUploadState, reportUploadLGA, stateLgaMap)
        submitBtn.text = requireContext().localized(R.string.submit_text)
//        submitReportBtn.setOnClickListener {
//            Toast.makeText(requireContext(), "hey", Toast.LENGTH_SHORT).show()
//            findNavController().navigate(R.id.reportReviewFragment)
//        }





        requireContext().toast("${report}")

        Log.i(title, header)
        submitBtn.setOnClickListener {
            val story = storyEditText.text
            val stateSelected = reportUploadState.selectedItem
            val stateGUID = states.get(stateSelected)
            report.story = story.toString()

            report.state = "$stateGUID"

            requireContext().toast("${stateGUID}")

            val request = authViewModel.addReport(report.topic, report.rating, report.story, report.state , header)
            val response = observeRequest(request, progressBar, submitBtn)

            response.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                val (bool, result) = it
                when (bool) {
                    true -> {
                        val res = result as Data
                        Log.i(title, "message ${result.message}")
//                        findNavController().navigate(R.id.reportFragment)
                    }
                    else -> Log.i(title, "error $result")
                }
            })

        }

    }


    private fun getStates(header:String): MediatorLiveData<StatesList> {
        val data = MediatorLiveData<StatesList>()
        val request = authViewModel.getStates(header)
        val response = observeRequest(request, null, null)
        data.addSource(response) {
            data.value = it.second as StatesList
        }
        return data
    }
    override fun onResume() {
        super.onResume()

        setOnClickEvent(uploadCard, uploadIcon) { showBottomSheet() }


    }


    private fun showBottomSheet() {

        uploadPictureBtn.text = requireContext().localized(R.string.done)

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
        uploadPictureBtn.setOnClickListener {

//            findNavController().navigate(R.id.reportUploadFragment)
        }
        bottomSheetView.cameraIcon.setOnClickListener {


            Dexter.withContext(requireContext())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
//                        requireContext().toast("thanks")
                        dispatchTakePictureIntent()

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

//
        }

    }

    override fun onPause() {
        super.onPause()
        bottomSheetDialog.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            cameraImage.setImageBitmap(imageBitmap)
//            MediaManager.init(requireContext())
//            val url = MediaManager.get().url().generate(imageBitmap.toString())


//            Log.i(title, "url ${url}")
        }
    }

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
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
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


//    private fun dispatchTakePictureIntent() {
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
//                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
//            }
//        }
//    }

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
                Log.i(title, "File ${createImageFile()}")
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.anapfoundation.covid_19volunteerapp.android.fileprovider",
                        it
                    )



//
//                    val requestId = MediaManager.get().upload("dog.mp4")
//                        .unsigned("preset1")
//                        .option("resource_type", "video")
//                        .option("folder", "my_folder/my_sub_folder/")
//                        .option("public_id", "my_dog")
//                        .option("overwrite", true)
//                        .option("notification_url", "https://mysite.example.com/notify_endpoint")
//                        .dispatch()

                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                }

            }
        }
    }

    // Add taken picture to gallery
    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            requireActivity().sendBroadcast(mediaScanIntent)
        }
    }


}
