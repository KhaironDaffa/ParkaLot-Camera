package com.dicoding.parkalotcamera

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.parkalotcamera.databinding.ActivityMainBinding
import com.dicoding.parkalotcamera.utils.snackbar
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedImageUri: Uri? = null
    private var nameFile = "ParkaLot "

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

        val timeStamp = SimpleDateFormat("EEE, d MMM yyyy - HH:mm:ss", Locale.getDefault()).format(Date())
        var imgRef = FirebaseStorage.getInstance().reference.child("images/$nameFile$timeStamp")
        imgRef
            .putFile(selectedImageUri!!)
            .addOnSuccessListener {
                binding.mainRoot.snackbar("Image Uploaded")
            }
            .addOnFailureListener {
                binding.mainRoot.snackbar("Image Uploaded")
            }

    }
}