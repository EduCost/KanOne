package com.educost.kanone.data.local

import androidx.room.Delete
import androidx.room.Insert
import com.educost.kanone.data.model.entity.AttachmentEntity

interface AttachmentDao {

    @Insert
    suspend fun insertAttachment(attachment: AttachmentEntity)

    @Delete
    suspend fun deleteAttachment(attachment: AttachmentEntity)

}