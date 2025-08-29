package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.domain.util.ImageCompressor
import com.educost.kanone.domain.util.InternalStorageManager
import com.educost.kanone.utils.Result

class SaveImageUseCase(
    private val internalStorageManager: InternalStorageManager,
    private val imageCompressor: ImageCompressor,
) {

    suspend operator fun invoke(uri: String): Result<String, GenericError> {

        val compressThreshold = 500 * 1024L
        val imageCompressionResult = imageCompressor.compressImage(uri, compressThreshold)
        val imageName = internalStorageManager.generateFileName(uri)

        if (imageCompressionResult is Result.Success) {
            val saveImageResult = internalStorageManager.saveImage(
                imageBytes = imageCompressionResult.data,
                fileName = imageName
            )

            if (saveImageResult is Result.Success) {
                val absolutePath = saveImageResult.data
                return Result.Success(absolutePath)
            }


        } else {
            val saveImageResult = internalStorageManager.saveImage(
                uri = uri,
                fileName = imageName
            )

            if (saveImageResult is Result.Success) {
                val absolutePath = saveImageResult.data
                return Result.Success(absolutePath)
            }

        }

        return Result.Error(GenericError)

    }

}