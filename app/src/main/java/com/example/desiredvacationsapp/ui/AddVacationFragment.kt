package com.example.desiredvacationsapp.ui

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.desiredvacationsapp.ARG_VACATION_ID
import com.example.desiredvacationsapp.BuildConfig
import com.example.desiredvacationsapp.R
import com.example.desiredvacationsapp.Utils
import com.example.desiredvacationsapp.appDatabase.VacationDatabase
import com.example.desiredvacationsapp.databinding.FragmentAddVacationBinding
import com.example.desiredvacationsapp.viewmodel.VacationViewModel
import com.example.desiredvacationsapp.viewmodel.VacationViewModelFactory
import com.example.desiredvacationsapp.widgets.CustomEditText
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.*
import java.util.*

class AddVacationFragment : Fragment() {

    private lateinit var viewModel: VacationViewModel

    // Constants for permission and activity request codes.
    private val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1001
    private val PERMISSION_REQUEST_CAMERA = 1002
    private val PICK_IMAGE_REQUEST = 2001
    private val TAKE_PHOTO_REQUEST = 2002

    private var photoFile: File? = null

    // Selected image path.
    private var selectedImage: String? = null

    private var _binding: FragmentAddVacationBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddVacationBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Initialize ViewModel after activity creation.
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val db = VacationDatabase.getDatabase(requireActivity().applicationContext)
        viewModel = ViewModelProvider(
            requireActivity(),
            VacationViewModelFactory(db.vacationDao())
        )[VacationViewModel::class.java]
    }

    // Set up UI interactions.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.findViewById<Button>(R.id.save_action).setOnClickListener {
            handleSaveAction()
        }

        view.findViewById<FloatingActionButton>(R.id.add_image_button).setOnClickListener {
            handleAddImageButtonClick()
        }
    }

    // Handle permission request results.
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // Simplified the permission check.
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                PERMISSION_REQUEST_READ_EXTERNAL_STORAGE -> openGallery()
                PERMISSION_REQUEST_CAMERA -> takePhoto()
            }
        } else {
            val message = when (requestCode) {
                PERMISSION_REQUEST_READ_EXTERNAL_STORAGE -> "Permission denied to read your External storage"
                PERMISSION_REQUEST_CAMERA -> "Permission denied to access Camera"
                else -> return
            }
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Handle activity result.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    data?.data?.let { uri ->
                        val inputStream = requireActivity().contentResolver.openInputStream(uri)
                        val picturePath = saveImageToInternalStorage(inputStream)
                        selectedImage = picturePath
                        updateImageViewWithSelectedImage() // Update ImageView here
                    }
                }
                TAKE_PHOTO_REQUEST -> {
                    // Handle the result from taking a photo here, save it to the internal storage and assign the path to selectedImage
                    selectedImage = photoFile?.path
                    updateImageViewWithSelectedImage() // Update ImageView here
                }
            }
        }
    }


    // Clean up when the view is destroyed.
    override fun onDestroyView() {
        super.onDestroyView()
        val inputMethodManager =
            requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    // Handle the saving of a new or updated vacation.
    private fun handleSaveAction() {
        // Validate individual CustomEditText fields.
        val isVacationNameValid = validateField(binding.vacationName)
        val isHotelNameValid = validateField(binding.hotelName)
        val isLocationValid = validateField(binding.vacationLocation)
        val isMoneyNecessaryValid = validateField(binding.moneyNecessary)
        val isDescriptionValid = validateField(binding.vacationDescription)

        // If all fields are valid, then proceed with saving.
        if (isVacationNameValid && isHotelNameValid && isLocationValid && isMoneyNecessaryValid && isDescriptionValid) {

            val vacationId = arguments?.getInt(ARG_VACATION_ID) ?: -1
            viewModel.setSelectedImageName(selectedImage)

            val vacationName = binding.vacationName.text.toString()
            val hotelName = binding.hotelName.text.toString()
            val location = binding.vacationLocation.text.toString()
            val moneyNecessary = binding.moneyNecessary.text.toString()
            val description = binding.vacationDescription.text.toString()

            if (vacationId > 0) {
                viewModel.updateVacation(vacationId, vacationName, hotelName, location, moneyNecessary, description)
                val vacationDetailFragment = VacationDetailFragment()
                val bundle = Bundle()
                bundle.putInt(ARG_VACATION_ID, vacationId)
                vacationDetailFragment.arguments = bundle
                Utils.commitFragment(parentFragmentManager, R.id.fragment_container, vacationDetailFragment, true)
            } else {
                viewModel.addNewVacation(vacationName, hotelName, location, moneyNecessary, description, null)
                val vacationFragmentList = VacationFragmentList()
                Utils.commitFragment(parentFragmentManager, R.id.fragment_container, vacationFragmentList, true)
            }
        }
    }

    private fun validateField(customEditText: CustomEditText): Boolean {
        val text = customEditText.text.toString()
        val isValid = text.isNotBlank()
        if (!isValid) {
            customEditText.markAsError()
        }
        return isValid
    }


    // Check permissions and open gallery to choose an image.
    private fun handleAddImageButtonClick() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Photo!")

        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    if (ContextCompat.checkSelfPermission(requireContext(), CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(CAMERA), PERMISSION_REQUEST_CAMERA)
                    } else {
                        takePhoto()
                    }
                }
                options[item] == "Choose from Gallery" -> {
                    if (ContextCompat.checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_READ_EXTERNAL_STORAGE)
                    } else {
                        openGallery()
                    }
                }
                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    // Open gallery to choose an image.
    @SuppressLint("QueryPermissionsNeeded")
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }

    // Open camera to take a photo.
    @SuppressLint("QueryPermissionsNeeded")
    private fun takePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(requireContext().packageManager) != null) {
            photoFile = try {
                createImageFile()
            } catch (ex: IOException) {
                // Handle the error
                null
            }

            photoFile?.let { file ->
                val photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    file
                )
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(cameraIntent, TAKE_PHOTO_REQUEST)
            }
        }
    }


    private fun updateImageViewWithSelectedImage() {
        if (selectedImage != null) {
            val bitmap = BitmapFactory.decodeFile(selectedImage)
            binding.hotelImage2.setImageBitmap(bitmap)
        } else {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    // Save the image to internal storage.
    private fun saveImageToInternalStorage(inputStream: InputStream?): String {
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val file = File(requireActivity().filesDir, "myImage.jpg")
        val outStream: OutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outStream)
        outStream.flush()
        outStream.close()

        return file.absolutePath
    }

    // Create an image file in the external directory.
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

}