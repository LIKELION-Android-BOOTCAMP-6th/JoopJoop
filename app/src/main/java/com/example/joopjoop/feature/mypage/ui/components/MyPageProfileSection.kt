package com.example.joopjoop.feature.mypage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.joopjoop.feature.mypage.ui.MyPageColors

@Composable
fun MyPageProfileSection(
    nickname: String,
    noteCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .border(width = 2.dp, color = MyPageColors.Primary, shape = CircleShape)
                .background(Color(0xFF3B2D20), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "프로필 이미지",
                tint = MyPageColors.OnSurfaceMedium,
                modifier = Modifier.size(56.dp)
            )
        }

        Column(
            modifier = Modifier.padding(start = 14.dp)
        ) {
            Text(
                text = nickname,
                color = MyPageColors.OnSurfaceHigh,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "게시물 $noteCount",
                color = MyPageColors.OnSurfaceMedium,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
