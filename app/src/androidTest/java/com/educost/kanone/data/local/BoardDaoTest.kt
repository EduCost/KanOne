package com.educost.kanone.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.educost.kanone.data.model.entity.BoardEntity
import com.educost.kanone.data.model.entity.CardEntity
import com.educost.kanone.data.model.entity.ColumnEntity
import com.educost.kanone.data.model.entity.LabelEntity
import com.educost.kanone.data.model.relation.BoardWithColumns
import com.educost.kanone.data.model.relation.CardWithRelations
import com.educost.kanone.data.model.relation.ColumnsWithCards
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BoardDaoTest {

    private lateinit var db: KanbanDatabase
    private lateinit var boardDao: BoardDao
    private lateinit var columnDao: ColumnDao
    private lateinit var cardDao: CardDao
    private lateinit var attachmentDao: AttachmentDao
    private lateinit var taskDao: TaskDao
    private lateinit var labelDao: LabelDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            KanbanDatabase::class.java
        ).allowMainThreadQueries().build()
        boardDao = db.boardDao()
        columnDao = db.columnDao()
        cardDao = db.cardDao()
        attachmentDao = db.attachmentDao()
        taskDao = db.taskDao()
        labelDao = db.labelDao()
    }

    @After
    fun closeDb() {
        db.close()
    }


    @Test
    fun observeCompleteBoard_reflectsInsertedColumn() = runTest {
        val initialBoard = getInitialBoard()
        val insertedBoardId = boardDao.createBoard(initialBoard)
        val expectedBoard = initialBoard.copy(id = insertedBoardId)

        boardDao.observeCompleteBoard(insertedBoardId).test {

            val boardWithoutColumns = awaitItem()
            assertThat(boardWithoutColumns.board).isEqualTo(expectedBoard)
            assertThat(boardWithoutColumns.columns).isEmpty()

            val columnToInsert = getTestColumn(insertedBoardId)
            val insertedColumnId = columnDao.createColumn(columnToInsert)
            val expectedColumn = columnToInsert.copy(id = insertedColumnId)

            val expected = BoardWithColumns(
                board = expectedBoard,
                columns = listOf(
                    ColumnsWithCards(
                        expectedColumn,
                        cards = emptyList()
                    )
                )
            )

            assertThat(awaitItem()).isEqualTo(expected)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun observeCompleteBoard_reflectsDeletedColumn() = runTest {
        val initialBoard = getInitialBoard()
        val insertedBoardId = boardDao.createBoard(initialBoard)
        val expectedBoard = initialBoard.copy(id = insertedBoardId)

        boardDao.observeCompleteBoard(insertedBoardId).test {

            val boardWithoutColumns = awaitItem()
            assertThat(boardWithoutColumns.board).isEqualTo(expectedBoard)
            assertThat(boardWithoutColumns.columns).isEmpty()

            val columnToInsert = getTestColumn(insertedBoardId)
            val insertedColumnId = columnDao.createColumn(columnToInsert)
            val expectedColumn = columnToInsert.copy(id = insertedColumnId)
            assertThat(awaitItem().columns[0].column).isEqualTo(expectedColumn)

            columnDao.deleteColumn(expectedColumn)

            val expected = BoardWithColumns(
                board = expectedBoard,
                columns = emptyList()
            )

            assertThat(awaitItem()).isEqualTo(expected)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun observeCompleteBoard_reflectsInsertedCard() = runTest {

        val initialBoard = getInitialBoard()
        val insertedBoardId = boardDao.createBoard(initialBoard)
        val expectedBoard = initialBoard.copy(id = insertedBoardId)

        boardDao.observeCompleteBoard(insertedBoardId).test {

            val boardWithoutColumns = awaitItem()
            assertThat(boardWithoutColumns.board).isEqualTo(expectedBoard)
            assertThat(boardWithoutColumns.columns).isEmpty()

            val columnToInsert = getTestColumn(insertedBoardId)
            val insertedColumnId = columnDao.createColumn(columnToInsert)
            val expectedColumn = columnToInsert.copy(id = insertedColumnId)
            assertThat(awaitItem().columns[0].column).isEqualTo(expectedColumn)

            val cardToInsert = getTestCard(insertedColumnId)
            val insertedCardId = cardDao.createCard(cardToInsert)
            val expectedCard = cardToInsert.copy(id = insertedCardId)

            val expected = BoardWithColumns(
                board = expectedBoard,
                columns = listOf(
                    ColumnsWithCards(
                        expectedColumn,
                        listOf(
                            CardWithRelations(
                                expectedCard,
                                attachments = emptyList(),
                                tasks = emptyList(),
                                labels = emptyList()
                            )
                        )
                    )
                )
            )

            assertThat(awaitItem()).isEqualTo(expected)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun observeCompleteBoard_reflectsInsertedLabel() = runTest {

        val initialBoard = getInitialBoard()
        val insertedBoardId = boardDao.createBoard(initialBoard)
        val expectedBoard = initialBoard.copy(id = insertedBoardId)

        boardDao.observeCompleteBoard(insertedBoardId).test {

            val boardWithoutColumns = awaitItem()
            assertThat(boardWithoutColumns.board).isEqualTo(expectedBoard)
            assertThat(boardWithoutColumns.columns).isEmpty()

            val columnToInsert = getTestColumn(insertedBoardId)
            val insertedColumnId = columnDao.createColumn(columnToInsert)
            val expectedColumn = columnToInsert.copy(id = insertedColumnId)
            assertThat(awaitItem().columns[0].column).isEqualTo(expectedColumn)

            val cardToInsert = getTestCard(insertedColumnId)
            val insertedCardId = cardDao.createCard(cardToInsert)
            val expectedCard = cardToInsert.copy(id = insertedCardId)
            assertThat(awaitItem().columns[0].cards[0].card).isEqualTo(expectedCard)

            val labelToInsert = getTestLabel(insertedBoardId)
            val insertedLabelId = labelDao.createLabelAndAssociateWithCard(labelToInsert, insertedCardId)
            val expectedLabel = labelToInsert.copy(id = insertedLabelId)

            val expected = BoardWithColumns(
                board = expectedBoard,
                columns = listOf(
                    ColumnsWithCards(
                        expectedColumn,
                        listOf(
                            CardWithRelations(
                                expectedCard,
                                attachments = emptyList(),
                                tasks = emptyList(),
                                labels = listOf(expectedLabel)
                            )
                        )
                    )
                )
            )

            assertThat(awaitItem()).isEqualTo(expected)
            cancelAndConsumeRemainingEvents()
        }
    }


    private fun getInitialBoard(): BoardEntity = BoardEntity(
        name = "Test Board",
    )

    private fun getTestColumn(boardId: Long): ColumnEntity = ColumnEntity(
        name = "Test Column",
        position = 0,
        color = -1,
        boardId = boardId
    )

    private fun getTestCard(columnId: Long): CardEntity = CardEntity(
        title = "Test Card",
        position = 0,
        columnId = columnId
    )

    private fun getTestLabel(boardId: Long): LabelEntity = LabelEntity(
        name = "Test Label",
        boardId = boardId,
    )
}

