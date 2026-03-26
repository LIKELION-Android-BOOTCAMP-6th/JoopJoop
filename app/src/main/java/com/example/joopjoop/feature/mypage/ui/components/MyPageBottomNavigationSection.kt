package com.example.joopjoop.feature.mypage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.joopjoop.feature.mypage.ui.MyPageColors

@Composable
fun MyPageBottomNavigationSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(MyPageColors.BottomBar)
            .border(width = 0.dp, color = MyPageColors.Card)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BottomNavItem(
                icon = Icons.Default.LocationOn,
                label = "MAP",
                tint = MyPageColors.Primary
            )

            Spacer(modifier = Modifier.width(62.dp))

            BottomNavItem(
                icon = Icons.Default.Person,
                label = "MY PAGE",
                tint = MyPageColors.OnSurfaceDisabled
            )
        }

        Box(
            modifier = Modifier
                .size(56.dp)
                .background(MyPageColors.Primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Create,
                contentDescription = "WRITE",
                tint = MyPageColors.OnSurfaceHigh,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    tint: Color
) {
    Column(
        modifier = Modifier
            .width(64.dp)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            color = tint,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
