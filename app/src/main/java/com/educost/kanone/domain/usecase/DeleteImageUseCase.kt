package com.educost.kanone.domain.usecase

import com.educost.kanone.domain.util.InternalStorageManager

class DeleteImageUseCase(val internalStorageManager: InternalStorageManager) {

    suspend operator fun invoke(absolutePath: String): Boolean {
        return internalStorageManager.deleteFile(absolutePath)
    }

}