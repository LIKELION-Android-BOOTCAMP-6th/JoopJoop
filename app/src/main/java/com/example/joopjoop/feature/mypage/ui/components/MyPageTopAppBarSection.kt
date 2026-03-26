package com.example.joopjoop.feature.mypage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
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
fun MyPageTopAppBarSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .height(56.dp)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "뒤로가기",
            tint = MyPageColors.OnSurfaceHigh,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = "마이페이지",
            color = MyPageColors.OnSurfaceHigh,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "설정",
            tint = MyPageColors.OnSurfaceHigh,
            modifier = Modifier.size(24.dp)
        )
    }
}
