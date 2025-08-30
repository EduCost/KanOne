package com.educost.kanone.di

import android.content.Context
import androidx.room.Room
import com.educost.kanone.data.local.AttachmentDao
import com.educost.kanone.data.local.BoardDao
import com.educost.kanone.data.local.CardDao
import com.educost.kanone.data.local.ColumnDao
import com.educost.kanone.data.local.KanbanDatabase
import com.educost.kanone.data.local.TaskDao
import com.educost.kanone.data.repository.AttachmentRepositoryImpl
import com.educost.kanone.data.repository.BoardRepositoryImpl
import com.educost.kanone.data.repository.CardRepositoryImpl
import com.educost.kanone.data.repository.ColumnRepositoryImpl
import com.educost.kanone.data.repository.TaskRepositoryImpl
import com.educost.kanone.data.util.DefaultImageCompressor
import com.educost.kanone.data.util.DefaultInternalStorageManager
import com.educost.kanone.dispatchers.DefaultDispatcherProvider
import com.educost.kanone.dispatchers.DispatcherProvider
import com.educost.kanone.domain.repository.AttachmentRepository
import com.educost.kanone.domain.repository.BoardRepository
import com.educost.kanone.domain.repository.CardRepository
import com.educost.kanone.domain.repository.ColumnRepository
import com.educost.kanone.domain.repository.TaskRepository
import com.educost.kanone.domain.usecase.CreateAttachmentUseCase
import com.educost.kanone.domain.usecase.CreateBoardUseCase
import com.educost.kanone.domain.usecase.CreateCardUseCase
import com.educost.kanone.domain.usecase.CreateColumnUseCase
import com.educost.kanone.domain.usecase.CreateTaskUseCase
import com.educost.kanone.domain.usecase.DeleteAttachmentUseCase
import com.educost.kanone.domain.usecase.DeleteColumnUseCase
import com.educost.kanone.domain.usecase.DeleteImageUseCase
import com.educost.kanone.domain.usecase.DeleteTaskUseCase
import com.educost.kanone.domain.usecase.GetCardColumnIdUseCase
import com.educost.kanone.domain.usecase.ObserveAllBoardsUseCase
import com.educost.kanone.domain.usecase.ObserveCardUseCase
import com.educost.kanone.domain.usecase.ObserveCompleteBoardUseCase
import com.educost.kanone.domain.usecase.PersistBoardPositionsUseCase
import com.educost.kanone.domain.usecase.ReorderCardsUseCase
import com.educost.kanone.domain.usecase.RestoreColumnUseCase
import com.educost.kanone.domain.usecase.SaveImageUseCase
import com.educost.kanone.domain.usecase.UpdateCardUseCase
import com.educost.kanone.domain.usecase.UpdateColumnUseCase
import com.educost.kanone.domain.usecase.UpdateTaskUseCase
import com.educost.kanone.domain.util.ImageCompressor
import com.educost.kanone.domain.util.InternalStorageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider {
        return DefaultDispatcherProvider()
    }

    @Provides
    @Singleton
    fun provideImageCompressor(@ApplicationContext context: Context): ImageCompressor {
        return DefaultImageCompressor(context)

    }

    @Provides
    @Singleton
    fun provideInternalStorageManager(@ApplicationContext context: Context): InternalStorageManager {
        return DefaultInternalStorageManager(context)
    }


    // Database
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KanbanDatabase {
        return Room.databaseBuilder(
            context,
            KanbanDatabase::class.java,
            KanbanDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideBoardDao(database: KanbanDatabase) = database.boardDao()

    @Provides
    @Singleton
    fun provideColumnDao(database: KanbanDatabase) = database.columnDao()

    @Provides
    @Singleton
    fun provideCardDao(database: KanbanDatabase) = database.cardDao()

    @Provides
    @Singleton
    fun provideAttachmentDao(database: KanbanDatabase) = database.attachmentDao()

    @Provides
    @Singleton
    fun provideTaskDao(database: KanbanDatabase) = database.taskDao()

    @Provides
    @Singleton
    fun provideLabelDao(database: KanbanDatabase) = database.labelDao()


    // Repositories
    @Provides
    @Singleton
    fun provideBoardRepository(boardDao: BoardDao): BoardRepository = BoardRepositoryImpl(boardDao)

    @Provides
    @Singleton
    fun provideColumnRepository(columnDao: ColumnDao): ColumnRepository =
        ColumnRepositoryImpl(columnDao)

    @Provides
    @Singleton
    fun provideCardRepository(cardDao: CardDao): CardRepository = CardRepositoryImpl(cardDao)

    @Provides
    @Singleton
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository = TaskRepositoryImpl(taskDao)

    @Provides
    @Singleton
    fun provideAttachmentRepository(attachmentDao: AttachmentDao): AttachmentRepository {
        return AttachmentRepositoryImpl(attachmentDao)
    }


    // Use cases
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
    fun provideGetCardColumnIdUseCase(cardRepository: CardRepository): GetCardColumnIdUseCase {
        return GetCardColumnIdUseCase(cardRepository)
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

}