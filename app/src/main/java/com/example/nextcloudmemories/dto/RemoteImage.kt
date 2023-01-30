package com.example.nextcloudmemories.dto

import android.graphics.Bitmap

data class RemoteImage (
    val fileId: Int,
    val eTag: String,
    val fileName: String,
    val bitmap: Bitmap,
    val taken: Int,
)