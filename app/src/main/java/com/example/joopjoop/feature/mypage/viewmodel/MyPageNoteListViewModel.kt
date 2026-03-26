package com.example.joopjoop.feature.mypage.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.joopjoop.feature.mypage.data.repository.MyPageRepository
import com.example.joopjoop.feature.mypage.data.repository.MyPageRepositoryImpl
import com.example.joopjoop.feature.mypage.ui.notes.MyNoteCardUiModel
import com.example.joopjoop.feature.mypage.ui.notes.MyPageNoteListUiState

class MyPageNoteListViewModel(
    private val repository: MyPageRepository = MyPageRepositoryImpl()
) : ViewModel() {

    var uiState: MyPageNoteListUiState by mutableStateOf(MyPageNoteListUiState(isLoading = true))
        private set

    fun loadNotes() {
        runCatching {
            repository.getMyNotes()
        }.onSuccess { notes ->
            uiState = MyPageNoteListUiState(
                isLoading = false,
                notes = notes.map {
                    MyNoteCardUiModel(
                        id = it.noteId,
                        previewText = it.previewText,
                        createdAt = it.createdAt,
                        imageUrl = it.imageUrl
                    )
                },
                errorMessage = null
            )
        }.onFailure {
            uiState = MyPageNoteListUiState(
                isLoading = false,
                errorMessage = "쪽지 목록을 불러오지 못했습니다."
            )
        }
    }
}
