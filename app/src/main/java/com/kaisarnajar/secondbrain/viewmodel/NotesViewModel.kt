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

    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> = _isImporting.asStateFlow()

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

    fun importFile(context: Context, uri: Uri, onResult: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            _isImporting.value = true
            try {
                val parsed = com.kaisarnajar.secondbrain.util.DocumentParser.parseDocument(context, uri)
                if (parsed.content.isNotBlank() || parsed.title.isNotBlank()) {
                    repository.insertNote(
                        NoteEntity(
                            title = parsed.title,
                            content = parsed.content,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    onResult?.invoke(true)
                } else {
                    onResult?.invoke(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult?.invoke(false)
            } finally {
                _isImporting.value = false
            }
        }
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

    fun addMockData() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val mockNotes = listOf(
                NoteEntity(
                    title = "🧠 Second Brain Architecture",
                    content = "Second Brain is built with modern Android development standards (MAD): Jetpack Compose for UI, Hilt for DI, Room DB for local persistence, and Coroutines/Flow for reactive streaming. Everything runs 100% offline & private by design.",
                    timestamp = now - (1000 * 60 * 60 * 24 * 3)
                ),
                NoteEntity(
                    title = "⚡ Local RAG & Vector Search Ideas",
                    content = "Retrieval-Augmented Generation (RAG) works by converting notes into text embeddings. When a user asks a question, we compute the question vector, retrieve top-K relevant chunks via cosine similarity, and synthesize an accurate answer on-device.",
                    timestamp = now - (1000 * 60 * 60 * 24 * 2)
                ),
                NoteEntity(
                    title = "📚 Book Highlights - Deep Work",
                    content = "1. Deep work is essential to wring maximum value out of your intellectual capacity.\n2. High-Quality Work Produced = (Time Spent) x (Intensity of Focus).\n3. Embrace focus and minimize constant digital distractions.",
                    timestamp = now - (1000 * 60 * 60 * 24 * 1)
                ),
                NoteEntity(
                    title = "🛒 Weekend Tech Shopping List",
                    content = "- Ergonomic Mechanical Keyboard\n- High Precision Wireless Mouse\n- 4K External Monitor Stand\n- USB-C Multi-port Hub",
                    timestamp = now - (1000 * 60 * 60 * 5)
                ),
                NoteEntity(
                    title = "💡 Product Ideas & Roadmap",
                    content = "- Support Markdown preview and syntax highlighting in note editor.\n- Integrate local ML Kit text recognition for photo imports.\n- Export notes as JSON / Markdown backup archive.",
                    timestamp = now
                )
            )
            mockNotes.forEach { note ->
                repository.insertNote(note)
            }
        }
    }
}

