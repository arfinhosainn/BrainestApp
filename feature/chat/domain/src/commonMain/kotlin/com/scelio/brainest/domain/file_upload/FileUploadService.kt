package com.scelio.brainest.domain.file_upload

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.Result

interface FileUploadService {

    suspend fun uploadImage(
        imageData: ByteArray,
        mimeType: String
    ): Result<String, DataError.Remote>


    suspend fun uploadImages(images: List<Pair<ByteArray, String>>): Result<List<String>, DataError.Remote>


    suspend fun uploadFile(
        fileData: ByteArray,
        fileName: String,
        mimeType: String
    ): Result<String, DataError.Remote>
}