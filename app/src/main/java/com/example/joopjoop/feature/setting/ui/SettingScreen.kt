package com.example.joopjoop.feature.setting.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.joopjoop.R
import com.example.joopjoop.core.repository.AuthRepository
import com.example.joopjoop.feature.auth.viewmodel.AuthViewModelFactory
import com.example.joopjoop.feature.notification.viewmodel.NotificationViewModel
import com.example.joopjoop.feature.setting.SettingViewModel
import com.example.joopjoop.ui.theme.BgDark
import com.example.joopjoop.ui.theme.BgDarkest
import com.example.joopjoop.ui.theme.JoopJoopTheme
import com.example.joopjoop.ui.theme.OrangePrimary
import com.example.joopjoop.ui.theme.TextPrimary
import com.example.joopjoop.ui.theme.TextSecondary
import com.example.joopjoop.ui.theme.TextTertiary

@Composable
fun SettingRoute(
    authRepository: AuthRepository,
    notificationViewModel: NotificationViewModel,
    onNavigateToLogin: () -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel: SettingViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository, notificationViewModel)
    )
    // 로그아웃 성공 시 이벤트 처리
    LaunchedEffect(Unit) {
        viewModel.logoutSuccess.collect {
            onNavigateToLogin()
        }
    }

    // UI 레이아웃 호출
    SettingScreen(
        onBackClick = onBackClick,
        onProfileEditClick = { /* 프로필 수정 로직 */ },
        onLogoutClick = { viewModel.logout() } // ViewModel의 로그아웃 함수 호출
    )
}
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onProfileEditClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var isNotificationEnabled by remember { mutableStateOf(true) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BgDarkest,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onBackClick() }
                        .padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        tint = OrangePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = stringResource(R.string.setting_title),
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Profile Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgDark, RoundedCornerShape(24.dp))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Profile Image with Edit Button
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .border(4.dp, OrangePrimary, CircleShape)
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(Color.Gray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_person),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(70.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(OrangePrimary, CircleShape)
                                .border(2.dp, BgDark, CircleShape)
                                .clickable { onProfileEditClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_edit),
                                contentDescription = "Edit Profile",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "라이언 킴",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "ryan.kim@example.com",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.setting_section_title),
                color = OrangePrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Simplified Notification Setting Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgDark, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_notification),
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(R.string.notification_setting),
                        color = TextPrimary,
                        fontSize = 16.sp
                    )
                }
                Switch(
                    checked = isNotificationEnabled,
                    onCheckedChange = { isNotificationEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = OrangePrimary,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFF3E3E42),
                        uncheckedBorderColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout
            SettingActionRow(
                iconId = R.drawable.ic_logout,
                title = stringResource(R.string.logout),
                onClick = onLogoutClick
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SettingActionRow(
    iconId: Int,
    title: String,
    textColor: Color = TextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgDark, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null,
                tint = if (textColor == TextPrimary) OrangePrimary else textColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                color = textColor,
                fontSize = 16.sp
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingScreenPreview() {
    JoopJoopTheme {
        SettingScreen()
    }
}
