package test.dapuk.dicodingstory.ui.addstory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import test.dapuk.dicodingstory.R
import test.dapuk.dicodingstory.data.repository.StoryRepository
import test.dapuk.dicodingstory.data.response.RegisterResponse
import test.dapuk.dicodingstory.data.retrofit.ApiConfig
import test.dapuk.dicodingstory.data.sharedpref.SharedPreferenceManager
import test.dapuk.dicodingstory.databinding.ActivityAddStoryBinding
import test.dapuk.dicodingstory.ui.camera.CameraActivity
import test.dapuk.dicodingstory.ui.camera.CameraActivity.Companion.CAMERAX_RESULT
import test.dapuk.dicodingstory.ui.ViewModelFactory
import test.dapuk.dicodingstory.ui.login.LoginActivity
import test.dapuk.dicodingstory.utils.reduceFileImage
import test.dapuk.dicodingstory.utils.uriToFile

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private lateinit var addStoryViewModel: AddStoryViewModel
    private val reqPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_DENIED

    private lateinit var sharedPreferencesManager: SharedPreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("SESSION", Context.MODE_PRIVATE)
        val apiService = ApiConfig.getApiService()
        val storyRepository = StoryRepository(apiService, sharedPreferences)
        val viewModelFactory = ViewModelFactory(storyRepository)
        addStoryViewModel = ViewModelProvider(this).get(AddStoryViewModel::class.java)
        setContentView(R.layout.activity_add_story)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (!allPermissionGranted()) {
            reqPermission.launch(REQUIRED_PERMISSION)
        }

        addStoryViewModel.currentImageUri.observe(this, { uri ->
            if (uri != null) {
                showImage(uri)
            }
        })

        binding.ivStoryimage.setImageResource(R.drawable.failed_load_img)
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.buttonAdd.setOnClickListener { uploadImage() }
        setContentView(binding.root)
    }


    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                addStoryViewModel.setCurrentImageUri(uri)
                showImage(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    private fun startGallery() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage(uri: Uri) {
        Log.d("Image URI", "showImage: $uri")
        binding.ivStoryimage.setImageURI(uri)
    }

    private fun startCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            addStoryViewModel.setCurrentImageUri(currentImageUri)
            currentImageUri?.let { it1 -> showImage(it1) }
        }
    }

    private fun uploadImage() {
        if (addStoryViewModel.currentImageUri.value == null) {
            Toast.makeText(this, "Gambar belum dipilih", Toast.LENGTH_SHORT).show()
            return
        }
        addStoryViewModel.currentImageUri.observe(this, { uri ->
            val imageFile = uriToFile(uri!!, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString()
            Log.d("image file", "showImage: ${imageFile.path}")
            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            if (description.isEmpty()) {
                Toast.makeText(this, "Deskripsi belum diisi", Toast.LENGTH_SHORT).show()
            } else lifecycleScope.launch {
                try {
                    binding.progressBar3.visibility = View.VISIBLE
                    val apiService = ApiConfig.getApiService()
                    val sharedPreferences: SharedPreferences =
                        getSharedPreferences("SESSION", Context.MODE_PRIVATE)
                    val storyRepository = StoryRepository(apiService, sharedPreferences)
                    val response = storyRepository.addStory(multipartBody, requestBody)
                    if (response?.error == false) {
                        Log.d("Upload Success", response.message)
                        Toast.makeText(this@AddStoryActivity, "Upload Success", Toast.LENGTH_SHORT)
                            .show()
                        lifecycleScope.launch {
                            delay(500L)
                            finish()
                        }
                    } else {
                        Log.e("Upload Failed", response?.message ?: "Unknown error")
                        Toast.makeText(this@AddStoryActivity, "Upload Failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
                    Toast.makeText(this@AddStoryActivity, "Upload Error", Toast.LENGTH_SHORT).show()
                    Log.e("Upload Error", errorResponse.message)
                } catch (e: Exception) {
                    Toast.makeText(this@AddStoryActivity, "No Internet", Toast.LENGTH_SHORT).show()
                    Log.e("Upload Error", "${e.message}")
                } finally {
                    binding.progressBar3.visibility = View.GONE
                }
            }
        })

    }

    override fun onStart() {
        super.onStart()
        sharedPreferencesManager = SharedPreferenceManager(this)
        val session = sharedPreferencesManager.getSession()
        if (session != null) {
            val userId = session.userId
            val name = session.name
            val token = session.token
            Log.d("viewmodel response", "UserId: $userId, Name: $name, Token: $token")
        } else {
            Log.d("session:", "null")
            startActivity(Intent(this@AddStoryActivity, LoginActivity::class.java))
            finish()
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}