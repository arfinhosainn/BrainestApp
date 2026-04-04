package com.scelio.brainest.flashcards.domain

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.Result

interface OpenAiFileService {
    suspend fun uploadDocument(
        fileData: ByteArray,
        fileName: String,
        mimeType: String
    ): Result<String, DataError.Remote>

    suspend fun deleteFile(fileId: String): Result<Unit, DataError.Remote>
}
