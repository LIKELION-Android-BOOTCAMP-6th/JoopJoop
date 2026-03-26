package com.example.joopjoop.feature.mypage.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.joopjoop.feature.mypage.data.repository.MyPageRepository
import com.example.joopjoop.feature.mypage.data.repository.MyPageRepositoryImpl
import com.example.joopjoop.feature.mypage.ui.scraps.ScrapCardUiModel
import com.example.joopjoop.feature.mypage.ui.scraps.MyPageScrapListUiState

class MyPageScrapListViewModel(
    private val repository: MyPageRepository = MyPageRepositoryImpl()
) : ViewModel() {

    var uiState: MyPageScrapListUiState by mutableStateOf(MyPageScrapListUiState(isLoading = true))
        private set

    fun loadScraps() {
        runCatching {
            repository.getScraps()
        }.onSuccess { scraps ->
            uiState = MyPageScrapListUiState(
                isLoading = false,
                scraps = scraps.map {
                    ScrapCardUiModel(
                        id = it.scrapId,
                        sourceNoteId = it.sourceNoteId,
                        previewText = it.previewText,
                        createdAt = it.createdAt,
                        imageUrl = it.imageUrl
                    )
                },
                errorMessage = null
            )
        }.onFailure {
            uiState = MyPageScrapListUiState(
                isLoading = false,
                errorMessage = "스크랩 목록을 불러오지 못했습니다."
            )
        }
    }
}
