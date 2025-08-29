package com.educost.kanone.data.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.net.toUri
import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.domain.util.ImageCompressor
import com.educost.kanone.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

class DefaultImageCompressor(private val context: Context) : ImageCompressor {

    override suspend fun compressImage(uri: String, compressThreshold: Long): Result<ByteArray, GenericError> {
        return withContext(Dispatchers.IO) {
            try {
                val uri = uri.toUri()
                val mimeType = context.contentResolver.getType(uri)
                val inputBytes = context
                    .contentResolver
                    .openInputStream(uri)
                    ?.use { inputStream ->
                        inputStream.readBytes()
                    } ?: return@withContext Result.Error(GenericError)

                ensureActive()

                withContext(Dispatchers.Default) {

                    val bitmap = BitmapFactory.decodeByteArray(inputBytes, 0, inputBytes.size)

                    ensureActive()

                    val compressFormat = when (mimeType) {

                        "image/png" -> Bitmap.CompressFormat.PNG
                        "image/jpeg" -> Bitmap.CompressFormat.JPEG
                        "image/webp" -> if (Build.VERSION.SDK_INT >= 30) {
                            Bitmap.CompressFormat.WEBP_LOSSLESS
                        } else Bitmap.CompressFormat.WEBP

                        else -> Bitmap.CompressFormat.JPEG
                    }

                    var outputBytes: ByteArray
                    var quality = 90

                    do {
                        ByteArrayOutputStream().use { outputStream ->
                            bitmap.compress(compressFormat, quality, outputStream)
                            outputBytes = outputStream.toByteArray()
                            quality -= (quality * 0.1).roundToInt()
                        }
                    } while (
                        isActive &&
                        outputBytes.size > compressThreshold &&
                        quality > 5 &&
                        compressFormat != Bitmap.CompressFormat.PNG
                    )

                    Result.Success(outputBytes)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Error(GenericError)
            }
        }
    }

}