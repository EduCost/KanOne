package com.educost.kanone.di

import android.content.Context
import androidx.room.Room
import com.educost.kanone.data.local.AttachmentDao
import com.educost.kanone.data.local.BoardDao
import com.educost.kanone.data.local.CardDao
import com.educost.kanone.data.local.ColumnDao
import com.educost.kanone.data.local.KanbanDatabase
import com.educost.kanone.data.local.LabelDao
import com.educost.kanone.data.local.TaskDao
import com.educost.kanone.data.repository.AttachmentRepositoryImpl
import com.educost.kanone.data.repository.BoardRepositoryImpl
import com.educost.kanone.data.repository.CardRepositoryImpl
import com.educost.kanone.data.repository.ColumnRepositoryImpl
import com.educost.kanone.data.repository.LabelRepositoryImpl
import com.educost.kanone.data.repository.TaskRepositoryImpl
import com.educost.kanone.data.repository.UserPreferencesRepositoryImpl
import com.educost.kanone.data.util.DefaultImageCompressor
import com.educost.kanone.data.util.DefaultInternalStorageManager
import com.educost.kanone.data.util.JsonConverterImpl
import com.educost.kanone.dispatchers.DefaultDispatcherProvider
import com.educost.kanone.dispatchers.DispatcherProvider
import com.educost.kanone.domain.repository.AttachmentRepository
import com.educost.kanone.domain.repository.BoardRepository
import com.educost.kanone.domain.repository.CardRepository
import com.educost.kanone.domain.repository.ColumnRepository
import com.educost.kanone.domain.repository.LabelRepository
import com.educost.kanone.domain.repository.TaskRepository
import com.educost.kanone.domain.repository.UserPreferencesRepository
import com.educost.kanone.domain.util.ImageCompressor
import com.educost.kanone.domain.util.InternalStorageManager
import com.educost.kanone.domain.util.JsonConverter
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

    @Provides
    @Singleton
    fun provideJsonConverter(): JsonConverter {
        return JsonConverterImpl()
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

    @Provides
    @Singleton
    fun provideLabelRepository(labelDao: LabelDao): LabelRepository {
        return LabelRepositoryImpl(labelDao)
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository {
        return UserPreferencesRepositoryImpl(context)
    }

}