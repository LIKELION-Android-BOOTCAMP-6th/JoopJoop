package com.example.joopjoop.feature.mypage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.joopjoop.feature.mypage.ui.MyPageColors
import com.example.joopjoop.feature.mypage.ui.MyPageTab

@Composable
fun MyPageTabMenu(
    selectedTab: MyPageTab,
    onSelectTab: (MyPageTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
            .height(48.dp)
    ) {
        TopTab(
            text = "쪽지 목록",
            selected = selectedTab == MyPageTab.NOTE,
            onClick = { onSelectTab(MyPageTab.NOTE) },
            modifier = Modifier.weight(1f)
        )
        TopTab(
            text = "스크랩 목록",
            selected = selectedTab == MyPageTab.SCRAP,
            onClick = { onSelectTab(MyPageTab.SCRAP) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TopTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            color = if (selected) MyPageColors.OnSurfaceHigh else Color(0xFF808080),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .height(2.dp)
                .fillMaxWidth(0.7f)
                .background(if (selected) MyPageColors.Primary else Color.Transparent)
        )
    }
}
