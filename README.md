# ParkaLot-Camera Documentation
1. Make a main acitvity with an ImageView to preview a chosen image, an image picker button to choose an image that you want, and an upload button to upload image to server.
2. To make those functions, you can see it https://github.com/KhaironDaffa/ParkaLot-Camera/tree/master/app/src/main/java/com/dicoding/parkalotcamera
3. You will need internet permission for this so kindly add them in Manifest file and also dont forgot to add implementation 'com.squareup.retrofit2:retrofit:2.9.0', implementation "com.squareup.retrofit2:converter-gson:2.9.0", and implementation 'com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2' because we will use retrofit to POST image to server
4. first add function to choose image button with

```
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

```

  then add function to upload button with
```
private fun uploadImage() {
        if (selectedImageUri == null) {
            binding.mainRoot.snackbar("Select an Image First")
            return
        }

        val parcelFileDescriptor =
            contentResolver.openFileDescriptor(selectedImageUri!!, "r", null) ?: return

        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(cacheDir, contentResolver.getFileName(selectedImageUri!!))
        val outputStream = FileOutputStream(file)
        val timeStamp = SimpleDateFormat("EEE, d MMM yyyy - HH:mm:ss", Locale.getDefault()).format(Date())
        val nameFile = "ParkaLot - $timeStamp"
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

```

but to make the upload function work, you need to set up the retrofit first and connect it with the server, make an ImageRespond.kt file as well, UploadRequestBody.kt to create the correct information for your image file and a Utils.kt that contain snackbar to preview information and filename to give the image file a name in the server.

5. after following those steps, it is good to go and you can try it on your own.
