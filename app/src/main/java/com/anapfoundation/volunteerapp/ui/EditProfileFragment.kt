package com.anapfoundation.volunteerapp.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import com.anapfoundation.volunteerapp.R
import com.anapfoundation.volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.volunteerapp.data.viewmodel.auth.updateProfile
import com.anapfoundation.volunteerapp.data.viewmodel.user.UserViewModel
import com.anapfoundation.volunteerapp.helpers.IsEmptyCheck
import com.anapfoundation.volunteerapp.model.ProfileData
import com.anapfoundation.volunteerapp.model.StatesList
import com.anapfoundation.volunteerapp.network.storage.StorageRequest
import com.anapfoundation.volunteerapp.utils.extensions.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.layout_upload_gallery.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.lang.Exception
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class EditProfileFragment : DaggerFragment() {

    val title: String by lazy {
        getName()
    }

    val REQUEST_TAKE_PHOTO = 1
    val REQUEST_FROM_GALLERY = 0

    val bottomSheetDialog by lazy {
        BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
    //Inflate bottomSheetView
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

    //Set camera icon
    val cameraIcon by lazy {
        bottomSheetView.cameraIcon
    }
    //Set gallery icon
    val galleryIcon by lazy {
        bottomSheetView.galleryIcon
    }

    //Set image preview
    val imagePreview by lazy {
        bottomSheetView.imagePreview
    }
    //Set capture params
    val capture by lazy {
        Bitmap.createBitmap(cameraIcon.width, cameraIcon.height, Bitmap.Config.ARGB_8888)
    }
    //Set image canvas
    val canvas by lazy {
        Canvas(capture)
    }
    //Set states hashmap
    val states = hashMapOf<String, String>()
    //Set lga and district hashmap
    val lgaAndDistrict = hashMapOf<String, String>()

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory
    val authViewModel: AuthViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel::class.java)
    }
    val userViewModel: UserViewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(UserViewModel::class.java)
    }
    @Inject
    lateinit var storageRequest: StorageRequest

    //Get logged-in user
    val loggedInUser by lazy {
        storageRequest.checkUser("loggedInUser")
    }

    //Get token
    val token by lazy {
        loggedInUser?.token
    }

    //Set header
    val header by lazy {
        "Bearer $token"
    }
    //Set progress bar
    val progressBar by lazy {
        editProfileBottomLayout.findViewById<ProgressBar>(R.id.includedProgressBar)
    }

    //Set bottom progress bar
    val bottomSheetProgressBar by lazy {
        bottomSheetIncludeLayout.findViewById<ProgressBar>(R.id.includedProgressBar)
    }

    //Get image file and path
    val imageFileAndPath by lazy {
        requireActivity().createImageFile()
    }
    //Set camera permission
    private val cameraPermission by lazy {
        net.codecision.startask.permissions.Permission.Builder(Manifest.permission.CAMERA)
            .setRequestCode(REQUEST_TAKE_PHOTO)
            .build()
    }
    //Get upload button from the included layout
    lateinit var uploadPictureBtn:Button

    //Set update button
    lateinit var updateBtn:Button
    var imageText = ""

    val args: EditProfileFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        editInfoBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i(title, "onStart")

        val user = args.profileData
        if(user?.imageUrl != null){
            val profileImageUrl = "imageUrl: ${user.imageUrl}"
            imageUrlText.text = profileImageUrl

        }
        editInfoFNameEditText.setText(user?.firstName)
        editInfoLNameEditText.setText(user?.lastName)
        editInfoEmailEditText.setText(user?.email)
        editInfoPhoneEditText.setText(user?.phone)
        editInfoHouseNoEditText.setText(user?.houseNumber)
        editInfoStateSpinner.prompt = user?.stateName
        editInfoStreetEditText.setText(user?.street)
    }
    @ExperimentalStdlibApi
    override fun onResume() {
        super.onResume()

        getStateAndSendToSpinner()
        updateBtn = editProfileBottomLayout.findViewById<Button>(R.id.includeBtn)
        updateBtn.setButtonText(getLocalisedString(R.string.update_profile))
        updateBtn.setOnClickListener {
            updateProfileRequest()
        }
        camerPermissionRequest()

        initEnterKeyToSubmitForm(editInfoStreetEditText) { updateProfileRequest()}

        uploadPictureBtn =  bottomSheetIncludeLayout.findViewById<Button>(R.id.includeBtn)


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
        imagePreview.clipToOutline = true
        crashReportByUser(loggedInUser)
    }

    @ExperimentalStdlibApi
    private fun camerPermissionRequest(){
        setOnClickEventForPicture(editInfoUploadCard, moreIcon){ showBottomSheet() }
    }

    /**
     * Show bottom sheet
     *
     */
    @ExperimentalStdlibApi
    private fun showBottomSheet() {
        val firstName = "${editInfoFNameEditText.text}"
        val id = loggedInUser?.id
        val path = "images/profile_$firstName _$id" + "_.jpg"

        uploadPictureBtn.text = getLocalisedString(R.string.done)

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

        uploadPictureBtn.setOnClickListener {

            bottomSheetProgressBar.show()
            uploadPictureBtn.hide()
            uploadImage(path, imagePreview, capture, canvas, imageUrlText)
            CoroutineScope(Dispatchers.Main).launch {
                delay(3000)
                bottomSheetProgressBar.hide()
                uploadPictureBtn.show()
                bottomSheetDialog.dismiss()
            }
        }
    }

    /**
     * Check camera permission
     *
     */
    private fun checkCameraPermission() {
        cameraPermission.check(this)
            .onGranted {
                dispatchTakePictureIntent(imageFileAndPath.first, REQUEST_TAKE_PHOTO)
            }.onShowRationale {
                showRationaleDialog()
            }
    }

    /**
     * Show permission rationale
     *
     */
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            Log.i(title, "data $data")
            bottomSheetDialog.show()
            imagePreview.setImageBitmap(imageBitmap)
            imagePreview.show()

        }
        else if (requestCode == REQUEST_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
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


    /**
     * Get the state-list and inflate state spinner
     *
     */
    private fun getStateAndSendToSpinner() {
        val stateData = getStates("37", "")
        stateData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            extractStateList(it)
            setupSpinner()
        })
    }

    /**
     * Get states
     *
     * @param first
     * @param after
     * @return
     */
    private fun getStates(first:String, after:String): MediatorLiveData<StatesList> {
        val data = MediatorLiveData<StatesList>()
        val request = userViewModel.getStates(first, after)
        val response = observeRequest(request, null, null)
        data.addSource(response) {
            try {
                data.value = it.second as StatesList
            }
            catch (e: Exception){
                Log.i(title, e.localizedMessage)
            }

        }
        return data
    }

    /**
     * Extract state-list from livedata
     * and put inside hashmap(states)
     * @param it
     */
    private fun extractStateList(it: StatesList) {
        it.data.associateByTo(states, {
            it.state /* key */
        }, {
            "${it.id} ${it.zone}" /* value */
        })


    }

    /**
     * Set up spinner
     *
     */
    private fun setupSpinner() {
        val stateArray = states.keys.sorted().toMutableList()
        stateArray.add(0, loggedInUser?.stateName.toString())
        val adapterState =
            ArrayAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                stateArray
            )
        editInfoStateSpinner.adapter = adapterState

        Log.i("SpinnerItem", "State ${states}")
        setLGASpinner(editInfoStateSpinner, editInfoLGASpinner, lgaAndDistrict, states, userViewModel, loggedInUser)
//        Log.i(title, "states $states")
    }
    private fun updateProfileRequest() {
        var profileImageUrl: String?
        var validation:String?=""
        val firstName = editInfoFNameEditText.text.toString().trim()
        val lastName = editInfoLNameEditText.text.toString().trim()
        val emailAddress = editInfoEmailEditText.text.toString().trim().toLowerCase()
        val phoneNumber = editInfoPhoneEditText.text.toString().trim()
        val houseNumber = editInfoHouseNoEditText.text.toString().trim()
        val selectedState = editInfoStateSpinner.selectedItem
        val valueOfStateSelected = states.get(selectedState)?.split(" ")
        val state = valueOfStateSelected?.get(0).toString()
        val selectedLga = editInfoLGASpinner.selectedItem
        val valueOfSelectedLga =  lgaAndDistrict.get(selectedLga)?.split(" ")
        val lgaGUID = valueOfSelectedLga?.get(0).toString()
        val zoneGUID = valueOfStateSelected?.get(1).toString()
        val street = editInfoStreetEditText.text.toString().trim()
         imageText = imageUrlText.text.toString()
        profileImageUrl = imageText.subSequence(10, imageText.length).toString()


        if(profileImageUrl == "null" || profileImageUrl.isNullOrEmpty()){
            profileImageUrl = null
            Log.i(title, "imageUrl $profileImageUrl")
        }
        val checkForEmpty =
            IsEmptyCheck(editInfoFNameEditText, editInfoLNameEditText)
        if(emailAddress.isNotEmpty()){
            validation = IsEmptyCheck.fieldsValidation(emailAddress, null)
        }
        else{
            validation = null
        }

        when {
            checkForEmpty != null -> {
                checkForEmpty.error = getLocalisedString(R.string.field_required)
                toast("${checkForEmpty.hint} is empty")
            }
            validation != null -> toast("email is invalid")
            else -> {
                val updateProfileRequest = authViewModel.updateProfile(
                    firstName,
                    lastName,
                    emailAddress,
                    phoneNumber,
                    houseNumber,
                    state as String,
                    lgaGUID,
                    street,
                    zoneGUID,
                    profileImageUrl,
                    header
                )
                val response = observeRequest(updateProfileRequest, progressBar, updateBtn)
                response.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    val (bool, result) = it
                    onRequestResponseTask(
                        bool,
                        result
                    )
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
        when (bool) {
            true -> {
                val res = result as ProfileData
                toast(getLocalisedString(R.string.profile_updated))
                Log.i(title, "result of registration ${res.data.firstName}")
                var user = storageRequest.checkUser("loggedInUser")
                user?.imageUrl = imageText.subSequence(10, imageText.length).toString()
                val data = result.data
                user?.stateName = data.stateName
                user?.houseNumber = data.houseNumber
                user?.stateID = data.stateID
                user?.lgName = data.lgName
                user?.lgID = data.lgID
                user?.zoneID = data.zoneID
                user?.districtID = data.districtID

                Log.i(title, "local ${result.data.lgName}")
                storageRequest.saveData(user, "loggedInUser")
                findNavController().navigate(R.id.profileFragment)
            }
            else -> Log.i(title, "error $result")
        }
    }

}
