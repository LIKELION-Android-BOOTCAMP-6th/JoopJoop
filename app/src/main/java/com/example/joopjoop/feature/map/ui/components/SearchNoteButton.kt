package com.example.joopjoop.feature.map.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.joopjoop.ui.theme.BgDarkest
import com.example.joopjoop.ui.theme.TextPrimary

/**
 * 지도 상단 "쪽지 탐색" 버튼 (재조회 기능)
 */
@Composable
fun SearchNoteButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        color = BgDarkest.copy(alpha = 0.7f), // 테마의 가장 어두운 배경색 적용
        contentColor = TextPrimary
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("쪽지 탐색", style = MaterialTheme.typography.labelLarge)
        }
    }
}