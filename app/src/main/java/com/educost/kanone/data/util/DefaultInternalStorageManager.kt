package com.educost.kanone.data.util

import android.content.Context
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.domain.logs.LogHandler
import com.educost.kanone.domain.logs.LogLevel
import com.educost.kanone.domain.logs.LogLocation
import com.educost.kanone.domain.util.InternalStorageManager
import com.educost.kanone.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class DefaultInternalStorageManager(
    private val context: Context,
    private val logHandler: LogHandler
) : InternalStorageManager {

    val imageDirectory by lazy {
        File(context.filesDir, "images").apply {
            if (!exists()) mkdirs()
        }
    }

    override suspend fun saveImage(uri: String, fileName: String): Result<String, GenericError> {
        return withContext(Dispatchers.IO) {
            try {
                val uri = uri.toUri()
                val file = File(imageDirectory, fileName)

                context
                    .contentResolver
                    .openInputStream(uri)
                    ?.use { inputStream ->
                        file.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                Result.Success(file.absolutePath)

            } catch (e: Exception) {
                logHandler.log(
                    throwable = e,
                    message = "Error saving image",
                    from = LogLocation.INTERNAL_STORAGE_MANAGER,
                    level = LogLevel.ERROR
                )

                Result.Error(GenericError)
            }
        }
    }

    override suspend fun saveImage(
        imageBytes: ByteArray,
        fileName: String
    ): Result<String, GenericError> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(imageDirectory, fileName)
                file.outputStream().use { outputStream ->
                    outputStream.write(imageBytes)
                }
                Result.Success(file.absolutePath)

            } catch (e: Exception) {
                logHandler.log(
                    throwable = e,
                    message = "Error saving image",
                    from = LogLocation.INTERNAL_STORAGE_MANAGER,
                    level = LogLevel.ERROR
                )

                Result.Error(GenericError)
            }
        }
    }

    override suspend fun generateFileName(uri: String): String {
        val uri = uri.toUri()
        val mimeType = context.contentResolver.getType(uri)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)

        val fileName = UUID.randomUUID().toString() + "." + extension
        return fileName
    }

    override suspend fun deleteFile(absolutePath: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(absolutePath)
                file.delete()
            } catch (e: Exception) {
                logHandler.log(
                    throwable = e,
                    message = "Error deleting file",
                    from = LogLocation.INTERNAL_STORAGE_MANAGER,
                    level = LogLevel.ERROR
                )

                false
            }
        }
    }
}