package com.example.joopjoop.feature.mypage.ui.scrap

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.joopjoop.feature.mypage.ui.components.MyNoteGridItem
import com.example.joopjoop.feature.mypage.viewmodel.MyPageViewModel
import com.example.joopjoop.ui.theme.OrangePrimary

@Composable
fun MyScrapListContent(
    viewModel: MyPageViewModel,
    onNoteClick: (String) -> Unit
) {
    val scrapState by viewModel.scrapUiState.collectAsState()

    // 현재 로그인된 유저 ID 가져오기
    val currentUserId = viewModel.getCurrentUserId()

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        when {
            // 1. 로딩 중
            scrapState.isLoading -> {
                CircularProgressIndicator(color = OrangePrimary)
            }

            // 2. 에러 발생
            scrapState.errorMessage != null -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = scrapState.errorMessage!!, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.loadMyScraps(currentUserId) }) {
                        Text("다시 시도")
                    }
                }
            }

            // 3. 데이터가 비어있음 (Empty State)
            scrapState.scraps.isEmpty() -> {
                Text(
                    text = "아직 스크랩한 쪽지가 없어요.\n마음에 드는 쪽지를 보관해보세요!",
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            // 4. 리스트 표시
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(scrapState.scraps) { note ->
                        MyNoteGridItem(
                            note = note,
                            onNoteClick = { noteId ->
                                // TODO: NavGraph에서 전달받은 상세화면 이동 로직 호출
                                onNoteClick(noteId)
                            })
                    }
                }
            }
        }
    }
}