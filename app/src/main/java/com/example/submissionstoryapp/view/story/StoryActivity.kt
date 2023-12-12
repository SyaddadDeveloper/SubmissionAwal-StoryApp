package com.example.submissionstoryapp.view.story

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import com.example.submissionstoryapp.data.Result
import androidx.core.content.ContextCompat
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.databinding.ActivityStoryBinding
import com.example.submissionstoryapp.data.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File


class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentImageUri: Uri? = null
    private val viewModel by viewModels<StoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            showToast(if (isGranted) "Permission granted" else "Permission denied")
        }

    private val requestPermissionLocation = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    showToast("Permission request granted")
                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    showToast("Permission request granted")
                }

                else -> {
                    showToast("Permission request denied")
                }
            }
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { selectedUri: Uri? ->
        selectedUri?.let {
            currentImageUri = it
            showImage()
        } ?: Log.d("Photo Picker", "No media selected")
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }
    private fun setupViews() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.apply {
            galleryButton.setOnClickListener { startGallery() }
            cameraButton.setOnClickListener { startCamera() }
            uploadButton.setOnClickListener { uploadImage() }
            permissionShareStoryLocation.setOnClickListener { requestLocationPermissionOrShowMessage() }
        }

        supportActionBar?.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.custom_actionbar)
        }
    }

    private fun requestLocationPermissionOrShowMessage() {
        if (!isPermissionsGranted(Manifest.permission.ACCESS_FINE_LOCATION) &&
            !isPermissionsGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            requestPermissionLocation.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            binding.permissionShareStoryLocation.isChecked = false
        } else {
            if (!binding.permissionShareStoryLocation.isChecked){
                showToast(getString(R.string.location_unauthorized))
            }else{
                showToast(getString(R.string.location_authorized))
            }
        }
    }

    private fun isPermissionsGranted(permission: String) =
        ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            currentImageUri = getImageUri(this)
            launcherIntentCamera.launch(currentImageUri)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.descEditText.text.toString()

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                uploadStory(imageFile, description)
            } else {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val isChecked = binding.permissionShareStoryLocation.isChecked
                        val lat = if (isChecked) location.latitude else null
                        val lon = if (isChecked) location.longitude else null
                        uploadStory(imageFile, description, lat, lon)
                    } else {
                        // Handle the case where location is null
                        showToast("Unable to retrieve location.")
                        showLoading(false)
                    }
                }
            }
            showLoading(true)
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun uploadStory(
        imageFile: File,
        description: String,
        lat: Double? = null,
        lon: Double? = null,
    ) {
        viewModel.uploadStories(imageFile, description, lat, lon).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showToast(result.data.message)
                        showLoading(false)
                        finish()
                    }
                    is Result.Error -> {
                        showToast(result.error)
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}