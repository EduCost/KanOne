package com.educost.kanone.domain.util

import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.utils.Result

interface ImageCompressor {

    suspend fun compressImage(uri: String, compressThreshold: Long): Result<ByteArray, GenericError>

}