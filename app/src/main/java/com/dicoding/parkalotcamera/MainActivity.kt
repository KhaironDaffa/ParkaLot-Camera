package com.dicoding.parkalotcamera

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.parkalotcamera.api.ApiConfig
import com.dicoding.parkalotcamera.databinding.ActivityMainBinding
import com.dicoding.parkalotcamera.response.UploadResponse
import com.dicoding.parkalotcamera.utils.getFileName
import com.dicoding.parkalotcamera.utils.snackbar
import com.google.firebase.storage.FirebaseStorage
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedImageUri: Uri? = null

    companion object {
        const val REQUEST_CODE_PICK_IMAGE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectImg.setOnClickListener {
            openImageChooser()
        }

        binding.upload.setOnClickListener {
            uploadImage()
        }
    }

    private fun openImageChooser() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, REQUEST_CODE_PICK_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_PICK_IMAGE -> {
                    selectedImageUri = data?.data
                    binding.imgView.setImageURI(selectedImageUri)
                }
            }
        }
    }

    private fun uploadImage() {
        if (selectedImageUri == null) {
            binding.mainRoot.snackbar("Select an Image First")
            return
        }

        /*
        var imgRef = FirebaseStorage.getInstance().reference.child("images/$nameFile$timeStamp")
        imgRef
            .putFile(selectedImageUri!!)
            .addOnSuccessListener {
                binding.mainRoot.snackbar("Image Uploaded")
            }
            .addOnFailureListener {
                binding.mainRoot.snackbar("Image Uploaded")
            }*/

        val parcelFileDescriptor =
            contentResolver.openFileDescriptor(selectedImageUri!!, "r", null) ?: return

        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(cacheDir, contentResolver.getFileName(selectedImageUri!!))
        val outputStream = FileOutputStream(file)
        val nameFile = "ParkaLot CarPark Image"
        inputStream.copyTo(outputStream)

        val body = UploadRequestBody(file, "image")
        ApiConfig.getApiService().uploadImage(
            MultipartBody.Part.createFormData(
                "image",
                file.name,
                body
            ),
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), nameFile)
        ).enqueue(object : Callback<UploadResponse> {
            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                binding.mainRoot.snackbar(t.message!!)
            }

            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                response.body()?.let {
                    binding.mainRoot.snackbar(it.message)
                }
            }
        })

    }
}