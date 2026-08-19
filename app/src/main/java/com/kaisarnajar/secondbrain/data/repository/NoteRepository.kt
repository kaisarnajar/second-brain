package com.kaisarnajar.secondbrain.data.repository

import com.kaisarnajar.secondbrain.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<NoteEntity>>
    fun searchNotes(query: String): Flow<List<NoteEntity>>
    suspend fun getNoteById(id: Long): NoteEntity?
    suspend fun insertNote(note: NoteEntity): Long
    suspend fun updateNote(note: NoteEntity)
    suspend fun deleteNoteById(id: Long)
}
