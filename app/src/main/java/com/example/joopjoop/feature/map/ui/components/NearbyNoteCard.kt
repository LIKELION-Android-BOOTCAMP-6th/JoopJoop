package com.example.joopjoop.feature.map.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.joopjoop.ui.theme.*
/**
 * 하단 주변 쪽지 정보 카드 (F-MAP-08)
 */
@Composable
fun NearbyNoteCard(noteCountText: String, onViewListClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BgDark), // 카드 배경색
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            // 왼쪽 아이콘 영역
            Box(
                modifier = Modifier.size(48.dp).background(OrangePrimary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Email, null, tint = OrangePrimary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            // 텍스트 및 버튼 영역
            Column(modifier = Modifier.weight(1f)) {
                Text(noteCountText, color = TextPrimary, fontWeight = FontWeight.Bold) //
                Text("Someone left a message here", color = TextSecondary, style = MaterialTheme.typography.bodySmall) //
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onViewListClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("View nearby notes", color = TextPrimary)
                }
            }
        }
    }
}