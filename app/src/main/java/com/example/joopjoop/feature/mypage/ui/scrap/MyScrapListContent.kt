package com.example.joopjoop.feature.mypage.ui.scrap

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.joopjoop.feature.mypage.viewmodel.MyPageViewModel

@Composable
fun MyScrapListContent(
    viewModel: MyPageViewModel
) {
    val scrapState by viewModel.scrapUiState.collectAsState()

    LazyVerticalGrid(columns = GridCells.Fixed(3)) {
        items(scrapState.scraps) { scrap ->
            // 스크랩 그리드 아이템 디자인
        }
    }
}