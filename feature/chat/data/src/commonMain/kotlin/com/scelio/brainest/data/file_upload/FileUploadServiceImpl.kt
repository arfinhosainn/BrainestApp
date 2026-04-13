package com.scelio.brainest.data.file_upload

import com.scelio.brainest.domain.file_upload.FileUploadService
import com.scelio.brainest.domain.logging.BrainestLogger
import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.Result
import io.github.jan.supabase.storage.Storage
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class FileUploadServiceImpl(
    private val storage: Storage,
    private val userId: String,
    private val logger: BrainestLogger
) : FileUploadService {

    companion object {
        private const val CHAT_IMAGES_BUCKET = "chat-images"
        private const val CHAT_FILES_BUCKET = "chat-files"
    }

    override suspend fun uploadImage(
        imageData: ByteArray,
        mimeType: String
    ): Result<String, DataError.Remote> {
        return try {
            val fileExtension = getExtensionFromMimeType(mimeType)
            val fileName = "${Uuid.random()}.$fileExtension"
            val path = "$userId/$fileName"

            val bucket = storage.from(CHAT_IMAGES_BUCKET)
            bucket.upload(
                path = path,
                data = imageData,
            )
            val publicUrl = bucket.publicUrl(path)

            Result.Success(publicUrl)
        } catch (e: Exception) {
            logger.error("Image upload error", e)
            Result.Failure(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun uploadImages(
        images: List<Pair<ByteArray, String>>
    ): Result<List<String>, DataError.Remote> {
        return try {
            val bucket = storage.from(CHAT_IMAGES_BUCKET)

            val urls = images.map { (data, mimeType) ->
                val fileExtension = getExtensionFromMimeType(mimeType)
                val fileName = "${Uuid.random()}.$fileExtension"
                val path = "$userId/$fileName"

                bucket.upload(
                    path = path,
                    data = data,
                )

                bucket.publicUrl(path)
            }

            Result.Success(urls)
        } catch (e: Exception) {
            logger.error("Images upload error", e)
            Result.Failure(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun uploadFile(
        fileData: ByteArray,
        fileName: String,
        mimeType: String
    ): Result<String, DataError.Remote> {
        return try {
            val uniqueFileName = "${Uuid.random()}_$fileName"
            val path = "$userId/$uniqueFileName"

            val bucket = storage.from(CHAT_FILES_BUCKET)
            bucket.upload(
                path = path,
                data = fileData,
            )

            val publicUrl = bucket.publicUrl(path)

            Result.Success(publicUrl)
        } catch (e: Exception) {
            logger.error("File upload error", e)
            Result.Failure(DataError.Remote.UNKNOWN)
        }
    }

    suspend fun deleteImage(imageUrl: String): Result<Unit, DataError.Remote> {
        return try {
            val path = extractPathFromUrl(imageUrl, CHAT_IMAGES_BUCKET)

            val bucket = storage.from(CHAT_IMAGES_BUCKET)
            bucket.delete(listOf(path))

            Result.Success(Unit)
        } catch (e: Exception) {
            logger.error("Image delete error", e)
            Result.Failure(DataError.Remote.UNKNOWN)
        }
    }


    suspend fun deleteFile(fileUrl: String): Result<Unit, DataError.Remote> {
        return try {
            val path = extractPathFromUrl(fileUrl, CHAT_FILES_BUCKET)

            val bucket = storage.from(CHAT_FILES_BUCKET)
            bucket.delete(listOf(path))

            Result.Success(Unit)
        } catch (e: Exception) {
            logger.error("File delete error", e)
            Result.Failure(DataError.Remote.UNKNOWN)
        }
    }

    private fun getExtensionFromMimeType(mimeType: String): String {
        return when (mimeType.lowercase()) {
            "image/jpeg", "image/jpg" -> "jpg"
            "image/png" -> "png"
            "image/gif" -> "gif"
            "image/webp" -> "webp"
            "image/heic" -> "heic"
            "image/heif" -> "heif"
            "image/svg+xml" -> "svg"
            "image/bmp" -> "bmp"
            else -> "jpg"
        }
    }


    private fun extractPathFromUrl(url: String, bucketName: String): String {
        val parts = url.split("/storage/v1/object/public/$bucketName/")
        return if (parts.size > 1) parts[1] else throw IllegalArgumentException("Invalid URL format")
    }
}