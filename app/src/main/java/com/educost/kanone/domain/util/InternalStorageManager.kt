package com.educost.kanone.domain.util

import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.utils.Result

interface InternalStorageManager {

    suspend fun saveImage(uri: String, fileName: String) : Result<String, GenericError>

    suspend fun saveImage(imageBytes: ByteArray, fileName: String): Result<String, GenericError>
    
    suspend fun deleteFile(absolutePath: String): Boolean


    suspend fun generateFileName(uri: String): String
}