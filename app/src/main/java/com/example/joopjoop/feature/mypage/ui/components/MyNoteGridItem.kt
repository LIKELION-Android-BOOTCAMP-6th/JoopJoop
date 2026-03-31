package com.example.joopjoop.feature.mypage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.joopjoop.core.common.util.JoopJoopImage
import com.example.joopjoop.core.model.Note
import com.example.joopjoop.ui.theme.BgDark
import com.example.joopjoop.ui.theme.TextPrimary

@Composable
fun MyNoteGridItem(
    note: Note,
    onNoteClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(1.dp) // 그리드 사이의 간격
            .aspectRatio(1f) // 1:1 정사각형 비율 유지
            .clickable { onNoteClick(note.id) },
        shape = RectangleShape
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDark), // 기본 배경색
            contentAlignment = Alignment.Center
        ) {
            if (!note.imageUrl.isNullOrEmpty()) {
                // [Case A] 이미지가 있는 경우
                JoopJoopImage(
                    model = note.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // [Case B] 이미지가 없는 경우 -> 텍스트 미리보기
                Text(
                    text = note.contentText,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    maxLines = 4, // 너무 길지 않게 제한
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}