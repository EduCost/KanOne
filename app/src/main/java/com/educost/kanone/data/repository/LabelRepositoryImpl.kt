package com.educost.kanone.data.repository

import com.educost.kanone.data.local.LabelDao
import com.educost.kanone.data.mapper.toLabel
import com.educost.kanone.data.mapper.toLabelEntity
import com.educost.kanone.data.model.entity.LabelCardCrossRef
import com.educost.kanone.domain.error.GenericError
import com.educost.kanone.domain.model.Label
import com.educost.kanone.domain.repository.LabelRepository
import com.educost.kanone.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class LabelRepositoryImpl(val labelDao: LabelDao) : LabelRepository {

    override fun observeLabels(cardId: Long): Flow<Result<List<Label>, GenericError>> {
        return labelDao.observeLabels(cardId).map { labels ->
            Result.Success(labels.map { it.toLabel() })

        }.catch { e ->
            e.printStackTrace()
            Result.Error(GenericError)
        }
    }

    override suspend fun updateLabel(label: Label, boardId: Long): Boolean {
        return try {
            labelDao.updateLabel(label.toLabelEntity(boardId))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun createLabelAndAssociateWithCard(
        label: Label,
        boardId: Long,
        cardId: Long
    ): Boolean {
        return try {
            labelDao.createLabelAndAssociateWithCard(
                label = label.toLabelEntity(boardId),
                cardId = cardId
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    override suspend fun associateLabelWithCard(labelId: Long, cardId: Long): Boolean {
        return try {
            labelDao.associateLabelWithCard(
                LabelCardCrossRef(
                    labelId = labelId,
                    cardId = cardId
                )
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    override suspend fun deleteLabelAssociation(labelId: Long, cardId: Long): Boolean {
        return try {
            labelDao.deleteLabelAssociation(
                LabelCardCrossRef(
                    labelId = labelId,
                    cardId = cardId
                )
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun hasLabelAssociation(labelId: Long, cardId: Long): Boolean {
        return try {
            val labelAssociation = labelDao.getLabelAssociation(
                labelId = labelId,
                cardId = cardId
            )
            labelAssociation != null
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    override suspend fun getBoardId(cardId: Long): Result<Long, GenericError> {
        return try {
            val boardId = labelDao.getCardBoardId(cardId)
            Result.Success(boardId)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(GenericError)
        }
    }
}