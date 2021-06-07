package com.dicoding.parkalotcamera.response

data class UploadResponse(
    val error: Boolean,
    val message: String,
    val image: String
)