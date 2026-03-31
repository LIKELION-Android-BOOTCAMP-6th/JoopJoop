package com.example.joopjoop.feature.setting.ui

import android.widget.Toast
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.joopjoop.R
import com.example.joopjoop.core.common.util.ImageProcessor
import com.example.joopjoop.core.repository.AuthRepository
import com.example.joopjoop.feature.auth.viewmodel.AuthViewModelFactory
import com.example.joopjoop.feature.notification.viewmodel.NotificationViewModel
import com.example.joopjoop.feature.setting.viewmodel.SettingEvent
import com.example.joopjoop.feature.setting.viewmodel.SettingViewModel
import com.example.joopjoop.ui.theme.BgDark
import com.example.joopjoop.ui.theme.BgDarkest
import com.example.joopjoop.ui.theme.JoopJoopTheme
import com.example.joopjoop.ui.theme.OrangePrimary
import com.example.joopjoop.ui.theme.TextPrimary
import com.example.joopjoop.ui.theme.TextSecondary
import com.example.joopjoop.ui.theme.TextTertiary
import com.example.joopjoop.core.model.User

@Composable
fun SettingRoute(
    authRepository: AuthRepository,
    notificationViewModel: NotificationViewModel,
    onNavigateToLogin: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    val viewModel: SettingViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository, notificationViewModel,
            imageProcessor = ImageProcessor(context)
        )
    )

    val galleryLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        // 사용자가 사진을 선택하면 ViewModel의 함수 호출
        uri?.let { viewModel.updateProfileImage(it) }
    }

    // 상태 수집
    val currentUser by viewModel.currentUser.collectAsState()
    val isNicknameAvailable by viewModel.isNicknameAvailable.collectAsState() // 중복 확인 상태

    LaunchedEffect(Unit) {
        viewModel.settingEvent.collect { event ->
            when (event) {
                is SettingEvent.LogoutSuccess -> {
                    onNavigateToLogin()
                }
                is SettingEvent.UpdateSuccess -> {
                    // 성공 토스트 알림
                    Toast.makeText(context, "프로필이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                }
                is SettingEvent.Error -> {
                    // 에러 메시지 토스트 알림
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    SettingScreen(
        user = currentUser,
        isNicknameAvailable = isNicknameAvailable, // 💡 전달
        onCheckNickname = { viewModel.checkNicknameAvailability(it) }, // 중복 확인 호출
        onNicknameChanged = { viewModel.onNicknameChanged() }, // 타이핑 시 상태 리셋
        onUpdateNickname = { viewModel.updateNickname(it) },
        onProfileEditClick = { galleryLauncher.launch("image/*") },
        onBackClick = onBackClick,
        onLogoutClick = { viewModel.logout() }
    )
}

@Composable
fun SettingScreen(
    user: User?,
    isNicknameAvailable: Boolean?, // 💡 추가
    onCheckNickname: (String) -> Unit, // 💡 추가
    onNicknameChanged: () -> Unit, // 💡 추가
    onUpdateNickname: (String) -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onProfileEditClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var isNotificationEnabled by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var newNickname by remember { mutableStateOf(user?.nickname ?: "") }

    // 닉네임 수정 팝업창
    if (showDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                showDialog = false
                onNicknameChanged()
            },
            title = { Text("닉네임 수정", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.TextField(
                            value = newNickname,
                            onValueChange = {
                                newNickname = it
                                onNicknameChanged() // 글자 바뀔 때마다 "확인" 다시 하게 리셋
                            },
                            placeholder = { Text("새 닉네임 입력") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = androidx.compose.material3.TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = OrangePrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // 💡 중복 확인 버튼
                        androidx.compose.material3.Button(
                            onClick = { onCheckNickname(newNickname) },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = OrangePrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("확인", fontSize = 12.sp)
                        }
                    }

                    // 💡 중복 확인 결과 메시지
                    val message = when (isNicknameAvailable) {
                        true -> "사용 가능한 닉네임입니다."
                        false -> "이미 존재하는 닉네임입니다."
                        else -> ""
                    }
                    val messageColor = if (isNicknameAvailable == true) Color.Green else Color.Red

                    if (message.isNotEmpty()) {
                        Text(
                            text = message,
                            color = messageColor,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Text(
                    "변경",
                    modifier = Modifier.clickable(enabled = isNicknameAvailable == true) {
                        onUpdateNickname(newNickname)
                        showDialog = false
                    },
                    color = if (isNicknameAvailable == true) OrangePrimary else Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            },
            dismissButton = {
                Text(
                    "취소",
                    modifier = Modifier.clickable { showDialog = false },
                    color = TextSecondary
                )
            },
            containerColor = BgDark
        )
    }

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
                            // 이미지 URL이 있으면 사진을 보여주고, 없으면 기본 아이콘을 보여줌
                            if (user?.profileImageUrl.isNullOrEmpty()) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_person),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(70.dp)
                                )
                            } else {
                                coil.compose.AsyncImage(
                                    model = user?.profileImageUrl,
                                    contentDescription = "Profile Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(OrangePrimary, CircleShape)
                                .border(2.dp, BgDark, CircleShape)
                                .clickable { onProfileEditClick() }, // 💡 여기서 콜백 호출!
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
                            text = user?.nickname ?: "사용자",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = "Edit Nickname",
                            tint = OrangePrimary,
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape) // 클릭 영역을 둥글게 (시각적 피드백용)
                                .clickable {
                                    newNickname = user?.nickname ?: "" // 현재 이름 미리 채워두기
                                    onNicknameChanged()              // 중복 확인 상태 초기화
                                    showDialog = true                // 다이얼로그 띄우기!
                                }
                        )
                    }
                    Text(
                        text = user?.email ?: "이메일",
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
        SettingScreen(
            user = null,                // 테스트용 유저 데이터 (null 가능)
            isNicknameAvailable = null, // 중복 확인 전 상태
            onCheckNickname = {},       // 빈 함수
            onNicknameChanged = {},     // 빈 함수
            onUpdateNickname = {},      // 빈 함수
            onBackClick = {},
            onProfileEditClick = {},
            onLogoutClick = {}
        )
    }
}
