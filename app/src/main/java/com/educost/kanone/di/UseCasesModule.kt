package com.educost.kanone.di

import com.educost.kanone.domain.repository.AttachmentRepository
import com.educost.kanone.domain.repository.BoardRepository
import com.educost.kanone.domain.repository.CardRepository
import com.educost.kanone.domain.repository.ColumnRepository
import com.educost.kanone.domain.repository.LabelRepository
import com.educost.kanone.domain.repository.TaskRepository
import com.educost.kanone.domain.usecase.CreateAttachmentUseCase
import com.educost.kanone.domain.usecase.CreateBoardUseCase
import com.educost.kanone.domain.usecase.CreateCardUseCase
import com.educost.kanone.domain.usecase.CreateColumnUseCase
import com.educost.kanone.domain.usecase.CreateLabelForCardUseCase
import com.educost.kanone.domain.usecase.CreateTaskUseCase
import com.educost.kanone.domain.usecase.DeleteAttachmentUseCase
import com.educost.kanone.domain.usecase.DeleteBoardUseCase
import com.educost.kanone.domain.usecase.DeleteCardUseCase
import com.educost.kanone.domain.usecase.DeleteColumnUseCase
import com.educost.kanone.domain.usecase.DeleteImageUseCase
import com.educost.kanone.domain.usecase.DeleteTaskUseCase
import com.educost.kanone.domain.usecase.ObserveAllBoardsUseCase
import com.educost.kanone.domain.usecase.ObserveCardUseCase
import com.educost.kanone.domain.usecase.ObserveCompleteBoardUseCase
import com.educost.kanone.domain.usecase.ObserveLabelsUseCase
import com.educost.kanone.domain.usecase.PersistBoardPositionsUseCase
import com.educost.kanone.domain.usecase.ReorderCardsUseCase
import com.educost.kanone.domain.usecase.RestoreColumnUseCase
import com.educost.kanone.domain.usecase.SaveImageUseCase
import com.educost.kanone.domain.usecase.UpdateBoardUseCase
import com.educost.kanone.domain.usecase.UpdateCardUseCase
import com.educost.kanone.domain.usecase.UpdateColumnUseCase
import com.educost.kanone.domain.usecase.UpdateLabelAssociationUseCase
import com.educost.kanone.domain.usecase.UpdateLabelUseCase
import com.educost.kanone.domain.usecase.UpdateTaskUseCase
import com.educost.kanone.domain.util.ImageCompressor
import com.educost.kanone.domain.util.InternalStorageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCasesModule {

    @Provides
    @Singleton
    fun provideObserveAllBoardsUseCase(boardRepository: BoardRepository): ObserveAllBoardsUseCase {
        return ObserveAllBoardsUseCase(boardRepository)
    }

    @Provides
    @Singleton
    fun provideCreateBoardUseCase(boardRepository: BoardRepository): CreateBoardUseCase {
        return CreateBoardUseCase(boardRepository)
    }

    @Provides
    @Singleton
    fun provideObserveCompleteBoardUseCase(boardRepository: BoardRepository): ObserveCompleteBoardUseCase {
        return ObserveCompleteBoardUseCase(boardRepository)
    }

    @Provides
    @Singleton
    fun provideCreateColumnUseCase(columnRepository: ColumnRepository): CreateColumnUseCase {
        return CreateColumnUseCase(columnRepository)
    }

    @Provides
    @Singleton
    fun provideCreateCardUseCase(cardRepository: CardRepository): CreateCardUseCase {
        return CreateCardUseCase(cardRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateColumnRepository(columnRepository: ColumnRepository): UpdateColumnUseCase {
        return UpdateColumnUseCase(columnRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteColumnUseCase(columnRepository: ColumnRepository): DeleteColumnUseCase {
        return DeleteColumnUseCase(columnRepository)
    }

    @Provides
    @Singleton
    fun provideRestoreColumnUseCase(columnRepository: ColumnRepository): RestoreColumnUseCase {
        return RestoreColumnUseCase(columnRepository)
    }

    @Provides
    @Singleton
    fun providePersistBoardPositionsUseCase(boardRepository: BoardRepository): PersistBoardPositionsUseCase {
        return PersistBoardPositionsUseCase(boardRepository)
    }

    @Provides
    @Singleton
    fun provideReorderCardsUseCase(cardRepository: CardRepository): ReorderCardsUseCase {
        return ReorderCardsUseCase(cardRepository)
    }

    @Provides
    @Singleton
    fun provideObserveCardUseCase(cardRepository: CardRepository): ObserveCardUseCase {
        return ObserveCardUseCase(cardRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateCardUseCase(cardRepository: CardRepository): UpdateCardUseCase {
        return UpdateCardUseCase(cardRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateTaskUseCase(taskRepository: TaskRepository): UpdateTaskUseCase {
        return UpdateTaskUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideCreateTaskUseCase(taskRepository: TaskRepository): CreateTaskUseCase {
        return CreateTaskUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteTaskUseCase(taskRepository: TaskRepository): DeleteTaskUseCase {
        return DeleteTaskUseCase(taskRepository)
    }

    @Provides
    @Singleton
    fun provideSaveImageUseCase(
        internalStorageManager: InternalStorageManager,
        imageCompressor: ImageCompressor
    ): SaveImageUseCase {
        return SaveImageUseCase(internalStorageManager, imageCompressor)
    }

    @Provides
    @Singleton
    fun provideCreateAttachmentUseCase(attachmentRepository: AttachmentRepository): CreateAttachmentUseCase {
        return CreateAttachmentUseCase(attachmentRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteImageUseCase(internalStorageManager: InternalStorageManager): DeleteImageUseCase {
        return DeleteImageUseCase(internalStorageManager)
    }

    @Provides
    @Singleton
    fun provideDeleteAttachmentUseCase(attachmentRepository: AttachmentRepository): DeleteAttachmentUseCase {
        return DeleteAttachmentUseCase(attachmentRepository)
    }

    @Provides
    @Singleton
    fun provideObserveLabelsUseCase(labelRepository: LabelRepository): ObserveLabelsUseCase {
        return ObserveLabelsUseCase(labelRepository)
    }

    @Provides
    @Singleton
    fun provideCreateLabelForCardUseCase(labelRepository: LabelRepository): CreateLabelForCardUseCase {
        return CreateLabelForCardUseCase(labelRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateLabelAssociationUseCase(labelRepository: LabelRepository): UpdateLabelAssociationUseCase {
        return UpdateLabelAssociationUseCase(labelRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateLabelUseCase(labelRepository: LabelRepository): UpdateLabelUseCase {
        return UpdateLabelUseCase(labelRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteCardUseCase(cardRepository: CardRepository): DeleteCardUseCase {
        return DeleteCardUseCase(cardRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateBoardUseCase(boardRepository: BoardRepository): UpdateBoardUseCase {
        return UpdateBoardUseCase(boardRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteBoardUseCase(boardRepository: BoardRepository): DeleteBoardUseCase {
        return DeleteBoardUseCase(boardRepository)
    }
}