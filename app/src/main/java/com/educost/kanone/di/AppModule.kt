package com.educost.kanone.di

import android.content.Context
import androidx.room.Room
import com.educost.kanone.data.local.BoardDao
import com.educost.kanone.data.local.KanbanDatabase
import com.educost.kanone.data.repository.BoardRepositoryImpl
import com.educost.kanone.domain.repository.BoardRepository
import com.educost.kanone.domain.usecase.CreateBoardUseCase
import com.educost.kanone.domain.usecase.ObserveAllBoardsUseCase
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

    @Provides
    @Singleton
    fun provideBoardRepository(boardDao: BoardDao): BoardRepository = BoardRepositoryImpl(boardDao)

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




}