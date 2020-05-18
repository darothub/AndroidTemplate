package com.anapfoundation.covid_19volunteerapp.ui

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
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.anapfoundation.covid_19volunteerapp.R
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.ViewModelProviderFactory
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.AuthViewModel
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.auth.updateProfile
import com.anapfoundation.covid_19volunteerapp.data.viewmodel.user.UserViewModel
import com.anapfoundation.covid_19volunteerapp.helpers.IsEmptyCheck
import com.anapfoundation.covid_19volunteerapp.model.CityClass
import com.anapfoundation.covid_19volunteerapp.model.ProfileData
import com.anapfoundation.covid_19volunteerapp.model.StatesList
import com.anapfoundation.covid_19volunteerapp.model.response.Data
import com.anapfoundation.covid_19volunteerapp.network.storage.StorageRequest
import com.anapfoundation.covid_19volunteerapp.utils.extensions.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.storage.FirebaseStorage
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_address.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_report_upload.*
import kotlinx.android.synthetic.main.fragment_signin.*
import kotlinx.android.synthetic.main.layout_upload_gallery.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
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
    val states = hashMapOf<String, String>()
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
    val getUser by lazy {

        storageRequest.checkUser("loggedInUser")
    }

    //Get token
    val token by lazy {
        getUser?.token
    }

    //Set header
    val header by lazy {
        "Bearer $token"
    }
    val progressBar by lazy {
        editProfileBottomLayout.findViewById<ProgressBar>(R.id.includedProgressBar)
    }
    val bottomSheetProgressBar by lazy {
        bottomSheetIncludeLayout.findViewById<ProgressBar>(R.id.includedProgressBar)
    }

    val imageFileAndPath by lazy {
        requireActivity().createImageFile()
    }

    val timeStamp by lazy {
        SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    }
    //Get upload button from the included layout
    lateinit var uploadPictureBtn:Button

    lateinit var updateBtn:Button
    var imageText = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        requireContext().setSpinnerAdapterData(editInfoStateSpinner, editInfoLGASpinner, stateLgaMap)

        editInfoBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onStart() {
        super.onStart()
        val request = authViewModel.getProfileData(header)
        val response = observeRequest(request, null, null)
        response.observe(viewLifecycleOwner, Observer {
            val (bool, result) = it
            try {
                when(bool){
                    true ->{
                        val res = result as ProfileData
                        val user = res.data
                        val loggedInUser = storageRequest.checkUser("loggedInUser")
                        imageUrlText.append(user.profileImageURL)
                        imageUrlText.show()
                        editInfoFNameEditText.setText(user.firstName)
                        editInfoLNameEditText.setText(user.lastName)
                        editInfoEmailEditText.setText(user.email)
                        editInfoPhoneEditText.setText(user.phone)
                        editInfoHouseNoEditText.setText(user.houseNumber)
                        editInfoStateSpinner.prompt = user.state
                        editInfoStreetEditText.setText(user.street)

                        Log.i(title, "name ${user.firstName}")
                    }
                    false ->{
                        Log.i(title, "false")
                    }
                }
            }
            catch (e:Exception){
                Log.i(title, "error ${e.localizedMessage}")
            }

        })
    }
    override fun onResume() {
        super.onResume()
        getStateAndSendToSpinner()
        updateBtn = editProfileBottomLayout.findViewById<Button>(R.id.includeBtn)
        updateBtn.setButtonText(requireContext().getLocalisedString(R.string.update_profile))
        updateBtn.setOnClickListener {
            updateProfileRequest()
        }
        camerPermissionRequest()

        initEnterKeyToSubmitForm(editInfoStreetEditText) { updateProfileRequest()}

        uploadPictureBtn =  bottomSheetIncludeLayout.findViewById<Button>(R.id.includeBtn)

        cameraIcon.setOnClickListener {
            dispatchTakePictureIntent(imageFileAndPath.first, REQUEST_TAKE_PHOTO)
        }
        galleryIcon.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(intent, REQUEST_FROM_GALLERY)
        }
        imagePreview.clipToOutline = true
    }

    private fun camerPermissionRequest(){
        requireContext().permissionRequest()
        setOnClickEventForPicture(editInfoUploadCard, moreIcon){ showBottomSheet() }

    }

    private fun showBottomSheet() {
        val fullName = "${editInfoFNameEditText.text}_${editInfoLNameEditText.text}"
        val path = "images/profile_$fullName _$timeStamp" + "_.jpg"

        uploadPictureBtn.text = requireContext().getLocalisedString(R.string.done)

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

        uploadPictureBtn.setOnClickListener {

            bottomSheetProgressBar.show()
            uploadPictureBtn.hide()
            uploadImage(path, imagePreview, capture, canvas, storageRef, imageUrlText)
            CoroutineScope(Dispatchers.Main).launch {
                delay(3000)
                bottomSheetProgressBar.hide()
                uploadPictureBtn.show()
                bottomSheetDialog.dismiss()
            }

//            findNavController().navigate(R.id.reportUploadFragment)
        }
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




    private fun getStateAndSendToSpinner() {
        val stateData = getStates("37", "")
        stateData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            extractStateList(it)
            setupSpinner()
        })
    }

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

    private fun extractStateList(it: StatesList) {
        it.data.associateByTo(states, {
            it.state /* key */
        }, {
            "${it.id} ${it.zone}" /* value */
        })
    }

    private fun setupSpinner() {
        val stateArray = states.keys.sorted().toMutableList()
        val adapterState =
            ArrayAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                stateArray
            )
        editInfoStateSpinner.adapter = adapterState

        setLGASpinner(editInfoStateSpinner, editInfoLGASpinner, lgaAndDistrict, states, userViewModel)
//        Log.i(title, "states $states")
    }
    private fun updateProfileRequest() {

        var validation:String?=""
        val firstName = editInfoFNameEditText.text.toString().trim()
        val lastName = editInfoLNameEditText.text.toString().trim()
        val emailAddress = editInfoEmailEditText.text.toString().trim().toLowerCase()
        val phoneNumber = editInfoPhoneEditText.text.toString().trim()
        val houseNumber = editInfoHouseNoEditText.text.toString().trim()
        val selectedState = editInfoStateSpinner.selectedItem
        val valueOfStateSelected = states.get(selectedState)?.split(" ")
        val state = valueOfStateSelected?.get(0).toString()
        val zoneGUID = valueOfStateSelected?.get(1).toString()
        val street = editInfoStreetEditText.text.toString().trim()
         imageText = imageUrlText.text.toString()
        val profileImageUrl = imageText.subSequence(10, imageText.length).toString()


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
                checkForEmpty.error = requireContext().getLocalisedString(R.string.field_required)
                requireActivity().toast("${checkForEmpty.hint} is empty")
            }
            validation != null -> requireActivity().toast("email is invalid")
            else -> {
                val updateProfileRequest = authViewModel.updateProfile(
                    firstName,
                    lastName,
                    emailAddress,
                    phoneNumber,
                    houseNumber,
                    state as String,
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

    private fun onRequestResponseTask(
        bool: Boolean,
        result: Any?
    ) {
        when (bool) {
            true -> {
                val res = result as ProfileData
                requireContext().toast(requireContext().getLocalisedString(R.string.profile_updated))
                Log.i(title, "result of registration ${res.data.firstName}")
                var user = storageRequest.checkUser("loggedInUser")
                user?.imageUrl = imageText.subSequence(10, imageText.length).toString()
                storageRequest.saveData(user, "loggedInUser")
                findNavController().navigate(R.id.profileFragment)
            }
            else -> Log.i(title, "error $result")
        }
    }

}
