package com.kaisarnajar.secondbrain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaisarnajar.secondbrain.data.local.NoteEntity
import com.kaisarnajar.secondbrain.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val notes: StateFlow<List<NoteEntity>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllNotes()
            } else {
                repository.searchNotes(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            repository.deleteNoteById(id)
        }
    }

    fun saveNote(
        id: Long = 0,
        title: String,
        content: String,
        onComplete: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            if (title.isBlank() && content.isBlank()) {
                onComplete?.invoke()
                return@launch
            }
            if (id == 0L) {
                repository.insertNote(
                    NoteEntity(
                        title = title.ifBlank { "Untitled Note" },
                        content = content,
                        timestamp = System.currentTimeMillis()
                    )
                )
            } else {
                repository.updateNote(
                    NoteEntity(
                        id = id,
                        title = title.ifBlank { "Untitled Note" },
                        content = content,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
            onComplete?.invoke()
        }
    }

    suspend fun getNoteById(id: Long): NoteEntity? {
        return repository.getNoteById(id)
    }
}
