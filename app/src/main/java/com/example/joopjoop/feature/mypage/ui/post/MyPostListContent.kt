package com.example.joopjoop.feature.mypage.ui.post

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.joopjoop.feature.mypage.viewmodel.MyPageViewModel
import com.example.joopjoop.ui.theme.OrangePrimary

@Composable
fun MyPostListContent(
    viewModel: MyPageViewModel // 동일 뷰모델 혹은 별도 분리된 뷰모델 사용 가능
) {
    val postState by viewModel.postUiState.collectAsState()

    if (postState.isLoading) {
        CircularProgressIndicator(color = OrangePrimary)
    } else {
        // 이미지 예시의 3열 그리드
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize()
        ) {
            items(postState.posts) { note ->
                // 개별 격자 디자인
            }
        }
    }
}