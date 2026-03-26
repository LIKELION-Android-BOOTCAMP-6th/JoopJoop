package com.example.joopjoop.feature.mypage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.joopjoop.feature.mypage.ui.CardImageRatio
import com.example.joopjoop.feature.mypage.ui.MyPageColors

@Composable
fun MyPageContentCard(
    previewText: String,
    createdAt: String,
    imageRatio: CardImageRatio
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MyPageColors.Card, RoundedCornerShape(4.dp))
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(if (imageRatio == CardImageRatio.OneToOne) 1f else 4f / 5f)
                .background(Color(0xFF3A332F), RoundedCornerShape(4.dp))
        )

        Text(
            text = previewText,
            color = MyPageColors.OnSurfaceHigh,
            fontSize = 13.sp,
            lineHeight = (13 * 1.4f).sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = createdAt,
            color = Color(0xFF666666),
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}
