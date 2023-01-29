package com.example.nextcloudmemories.dto

data class RemoteImage (
    val fileId: Int,
    val eTag: String,
    val fileName: String,
)