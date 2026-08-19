package com.kaisarnajar.secondbrain.data.repository

import com.kaisarnajar.secondbrain.data.local.NoteDao
import com.kaisarnajar.secondbrain.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {

    override fun getAllNotes(): Flow<List<NoteEntity>> {
        return noteDao.getAllNotes()
    }

    override fun searchNotes(query: String): Flow<List<NoteEntity>> {
        return noteDao.searchNotes(query)
    }

    override suspend fun getNoteById(id: Long): NoteEntity? {
        return noteDao.getNoteById(id)
    }

    override suspend fun insertNote(note: NoteEntity): Long {
        return noteDao.insertNote(note)
    }

    override suspend fun updateNote(note: NoteEntity) {
        noteDao.updateNote(note)
    }

    override suspend fun deleteNoteById(id: Long) {
        noteDao.deleteNoteById(id)
    }
}
