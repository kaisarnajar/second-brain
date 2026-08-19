package com.kaisarnajar.secondbrain.data.repository

import com.kaisarnajar.secondbrain.data.local.NoteDao
import com.kaisarnajar.secondbrain.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class NoteRepositoryTest {

    private lateinit var repository: NoteRepository
    private lateinit var fakeDao: FakeNoteDao

    @Before
    fun setUp() {
        fakeDao = FakeNoteDao()
        repository = NoteRepositoryImpl(fakeDao)
    }

    @Test
    fun insertAndGetNoteById(): Unit = runBlocking {
        val note = NoteEntity(id = 1L, title = "Test Note", content = "Test Content")
        repository.insertNote(note)

        val retrieved = repository.getNoteById(1L)
        assertNotNull(retrieved)
        assertEquals("Test Note", retrieved?.title)
        assertEquals("Test Content", retrieved?.content)
    }

    @Test
    fun deleteNoteById(): Unit = runBlocking {
        val note = NoteEntity(id = 1L, title = "To Delete", content = "Delete me")
        repository.insertNote(note)

        repository.deleteNoteById(1L)
        val retrieved = repository.getNoteById(1L)
        assertNull(retrieved)
    }

    @Test
    fun getAllNotes(): Unit = runBlocking {
        repository.insertNote(NoteEntity(id = 1L, title = "Note 1", content = "Content 1"))
        repository.insertNote(NoteEntity(id = 2L, title = "Note 2", content = "Content 2"))

        val notes = repository.getAllNotes().first()
        assertEquals(2, notes.size)
    }

    @Test
    fun searchNotes(): Unit = runBlocking {
        repository.insertNote(NoteEntity(id = 1L, title = "Kotlin Coroutines", content = "Flow and StateFlow"))
        repository.insertNote(NoteEntity(id = 2L, title = "Room Database", content = "SQLite local storage"))

        val searchResults = repository.searchNotes("Coroutines").first()
        assertEquals(1, searchResults.size)
        assertEquals("Kotlin Coroutines", searchResults[0].title)
    }
}


private class FakeNoteDao : NoteDao {
    private val notesMap = mutableMapOf<Long, NoteEntity>()

    override fun getAllNotes(): Flow<List<NoteEntity>> {
        return flowOf(notesMap.values.sortedByDescending { it.timestamp })
    }

    override suspend fun getNoteById(id: Long): NoteEntity? {
        return notesMap[id]
    }

    override fun searchNotes(query: String): Flow<List<NoteEntity>> {
        return flowOf(notesMap.values.filter {
            it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
        })
    }

    override suspend fun insertNote(note: NoteEntity): Long {
        val id = if (note.id == 0L) (notesMap.size + 1).toLong() else note.id
        val entity = note.copy(id = id)
        notesMap[id] = entity
        return id
    }

    override suspend fun updateNote(note: NoteEntity) {
        notesMap[note.id] = note
    }

    override suspend fun deleteNoteById(id: Long) {
        notesMap.remove(id)
    }
}
