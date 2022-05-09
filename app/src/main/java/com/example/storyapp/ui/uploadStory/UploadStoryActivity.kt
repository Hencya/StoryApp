package com.example.storyapp.ui.uploadStory

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.example.storyapp.data.ResultResponse
import com.example.storyapp.data.model.UserModel
import com.example.storyapp.databinding.ActivityUploadStoryBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.camera.CameraActivity
import com.example.storyapp.ui.setting.SettingActivity
import com.example.storyapp.utils.reduceFileImage
import com.example.storyapp.utils.rotateBitmap
import com.example.storyapp.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UploadStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadStoryBinding

    private var getFile: File? = null
    private var location: Location? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var user: UserModel

    private val uploadStoryViewModel: UploadStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.camera_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = intent.getParcelableExtra(DATA_USER)!!
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        supportActionBar?.title = getString(R.string.upload_story_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.cameraXButton.setOnClickListener { startCameraX() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { uploadImage() }
        binding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getMyLocation()
            } else {
                location = null
            }
        }

    }


    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            binding.previewImageView.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this@UploadStoryActivity)

            getFile = myFile

            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private fun uploadImage() {

        val description = binding.descriptionEditText.text.toString()
            .toRequestBody("application/json;charset=utf-8".toMediaType())

        if (binding.descriptionEditText.text.toString().isEmpty()) {
            binding.descriptionEditText.error = getString(R.string.empty_description)
            return
        }

        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            var lat: RequestBody? = null
            var lon: RequestBody? = null
            if (location != null) {
                lat = location?.latitude.toString().toRequestBody("text/plain".toMediaType())
                lon = location?.longitude.toString().toRequestBody("text/plain".toMediaType())
            }

            uploadStoryViewModel.uploadStory(user.token, description, imageMultipart, lat, lon)
                .observe(this) {
                    if (it != null) {
                        when (it) {
                            is ResultResponse.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is ResultResponse.Success -> {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@UploadStoryActivity,
                                    getString(R.string.upload_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                            is ResultResponse.Error -> {
                                binding.progressBar.visibility = View.GONE
                                AlertDialog.Builder(this).apply {
                                    setTitle(getString(R.string.upload_failed))
                                    setMessage(getString(R.string.message_upload_failed_alert) + ", ${it.error}")
                                    setPositiveButton(getString(R.string.next_alert)) { _, _ ->
                                        binding.progressBar.visibility = View.GONE
                                    }
                                    create()
                                    show()
                                }
                            }
                        }
                    }
                }
        } else {
            Toast.makeText(
                this@UploadStoryActivity,
                getString(R.string.attach_file_warning),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.menu_setting -> {
                val settingActivityIntent = Intent(this, SettingActivity::class.java)
                startActivity(settingActivityIntent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    location = it
                    Log.d(TAG, "Lat : ${it.latitude}, Lon : ${it.longitude}")
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.enable_gps_permission),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.switchCompat.isChecked = false
                }
            }
        } else {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        Log.d(TAG, "$it")
        if (it[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            getMyLocation()
        } else binding.switchCompat.isChecked = false
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private const val TAG = "UploadStoryActivity"
        const val DATA_USER = "data_user"

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}