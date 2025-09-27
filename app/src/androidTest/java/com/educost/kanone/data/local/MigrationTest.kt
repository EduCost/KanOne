package com.educost.kanone.data.local

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val DB_NAME = "test_db"

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        instrumentation = InstrumentationRegistry.getInstrumentation(),
        databaseClass = KanbanDatabase::class.java,
    )

    @Test
    fun migration1To2() {
        var db = helper.createDatabase(DB_NAME, 1).apply {
            execSQL("INSERT INTO boards VALUES (1, 'Board 1')")
            close()
        }

        db = helper.runMigrationsAndValidate(
            name = DB_NAME,
            version = 2,
            migrations = arrayOf(KanbanDatabase.MIGRATION_1_2),
            validateDroppedTables = true
        )

        db.query("SELECT * FROM boards").apply {
            assertThat(moveToFirst()).isTrue()
            assertThat(getFloat(getColumnIndex("zoom_percentage"))).isEqualTo(100f)
            assertThat(getInt(getColumnIndex("show_images"))).isEqualTo(1)
        }
    }

    @Test
    fun testAllMigrations() {
        helper.createDatabase(DB_NAME, 1).apply { close() }
        Room
            .databaseBuilder(
                context = InstrumentationRegistry.getInstrumentation().targetContext,
                klass = KanbanDatabase::class.java,
                name = DB_NAME
            )
            .addMigrations(KanbanDatabase.MIGRATION_1_2)
            .build()
            .apply {
                openHelper.writableDatabase.close()
            }
    }

}