package com.example.joopjoop.feature.note.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.joopjoop.core.repository.NoteRepository

// NoteDetailViewModel 인스턴스 생성을 담당하며, 필요한 Repository를 주입하는 팩토리 클래스
class NoteViewModelFactory(
    private val repository: NoteRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // NoteDetailViewModel 생성
            modelClass.isAssignableFrom(NoteDetailViewModel::class.java) -> {
                NoteDetailViewModel(repository) as T
            }
            // NoteListViewModel 생성
            modelClass.isAssignableFrom(NoteListViewModel::class.java) -> {
                NoteListViewModel(repository) as T
            }
            // WriteNoteViewModel 생성
//            modelClass.isAssignableFrom(WriteNoteViewModel::class.java) -> {
//                WriteNoteViewModel(repository) as T
//            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}