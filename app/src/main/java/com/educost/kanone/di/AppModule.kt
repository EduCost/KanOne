package com.educost.kanone.di

import android.content.Context
import androidx.room.Room
import com.educost.kanone.data.local.AttachmentDao
import com.educost.kanone.data.local.BoardDao
import com.educost.kanone.data.local.CardDao
import com.educost.kanone.data.local.ChecklistDao
import com.educost.kanone.data.local.ColumnDao
import com.educost.kanone.data.local.KanbanDatabase
import com.educost.kanone.data.local.LabelDao
import com.educost.kanone.data.repository.AttachmentRepositoryImpl
import com.educost.kanone.data.repository.BoardRepositoryImpl
import com.educost.kanone.data.repository.CardRepositoryImpl
import com.educost.kanone.data.repository.ChecklistRepositoryImpl
import com.educost.kanone.data.repository.ColumnRepositoryImpl
import com.educost.kanone.data.repository.LabelRepositoryImpl
import com.educost.kanone.domain.repository.AttachmentRepository
import com.educost.kanone.domain.repository.BoardRepository
import com.educost.kanone.domain.repository.CardRepository
import com.educost.kanone.domain.repository.ChecklistRepository
import com.educost.kanone.domain.repository.ColumnRepository
import com.educost.kanone.domain.repository.LabelRepository
import com.educost.kanone.domain.usecase.CreateBoardUseCase
import com.educost.kanone.domain.usecase.ObserveAllBoardsUseCase
import com.educost.kanone.domain.usecase.ObserveCompleteBoardUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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
    fun provideChecklistDao(database: KanbanDatabase) = database.checklistDao()

    @Provides
    @Singleton
    fun provideLabelDao(database: KanbanDatabase) = database.labelDao()


    // Repositories
    @Provides
    @Singleton
    fun provideBoardRepository(boardDao: BoardDao): BoardRepository = BoardRepositoryImpl(boardDao)


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
}