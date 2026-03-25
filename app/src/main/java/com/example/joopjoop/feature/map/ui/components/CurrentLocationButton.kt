package com.example.joopjoop.feature.map.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.joopjoop.ui.theme.BgDarkest

/**
 * 카메라를 현재 위치로 복귀시키는 버튼
 */
@Composable
fun CurrentLocationButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color.White,
        contentColor = BgDarkest, //
        shape = CircleShape,
        modifier = Modifier.size(54.dp)
    ) {
        Icon(Icons.Default.LocationOn, "내 위치")
    }
}