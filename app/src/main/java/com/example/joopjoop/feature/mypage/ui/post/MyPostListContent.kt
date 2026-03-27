package com.example.joopjoop.feature.mypage.ui.post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.example.joopjoop.feature.mypage.ui.components.MyNoteGridItem
import com.example.joopjoop.feature.mypage.viewmodel.MyPageViewModel
import com.example.joopjoop.ui.theme.OrangePrimary

@Composable
fun MyPostListContent(
    viewModel: MyPageViewModel,
    onNoteClick: (String) -> Unit
) {
    val postState by viewModel.postUiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            postState.isLoading -> {
                CircularProgressIndicator(color = OrangePrimary)
            }

            postState.errorMessage != null -> {
                // 에러 발생 시 안내 및 재시도 버튼
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = postState.errorMessage!!, color = Color.White)
                    Button(onClick = { /* 재시도 로직 */ }) { Text("재시도") }
                }
            }

            postState.posts.isEmpty() -> {
                // [핵심] 빈 화면 상태 (Empty State)
                Text(
                    text = "아직 작성한 쪽지가 없어요.\n주변에 첫 쪽지를 남겨보세요!",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3), modifier = Modifier.fillMaxSize()
                ) {
                    items(postState.posts) { note ->
                        MyNoteGridItem(
                            note = note,
                            onNoteClick = { noteId ->
                                // TODO: NavGraph에서 전달받은 상세화면 이동 로직 호출
                                println("클릭된 쪽지 ID: $noteId")
                            }
                        )
                    }
                }
            }
        }
    }
}