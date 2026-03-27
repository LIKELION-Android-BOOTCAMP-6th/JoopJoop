package com.example.joopjoop.feature.note.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.joopjoop.core.repository.NoteRepository
import com.google.android.gms.location.FusedLocationProviderClient

// WriteNoteViewModelFactory만 개별로 작성 (파라미터가 달라서 개별로 파일을 만드는게 더 나을 것 같음)
class WriteNoteViewModelFactory(
    private val repository: NoteRepository,
    private val fusedLocationClient: FusedLocationProviderClient // 위치 클라이언트 추가
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WriteNoteViewModel::class.java)) {
            return WriteNoteViewModel(repository, fusedLocationClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}