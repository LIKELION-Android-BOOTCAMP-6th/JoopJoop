package com.example.joopjoop.feature.mypage.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.joopjoop.core.common.util.JoopJoopImage
import com.example.joopjoop.core.model.User
import com.example.joopjoop.feature.mypage.viewmodel.MyPageViewModel
import com.example.joopjoop.ui.theme.BgDark
import com.example.joopjoop.ui.theme.BgDarkest
import com.example.joopjoop.ui.theme.DividerColor
import com.example.joopjoop.ui.theme.JoopJoopTheme
import com.example.joopjoop.ui.theme.OrangePrimary
import com.example.joopjoop.ui.theme.TextPrimary

@Composable
fun MyPageScreen(
    viewModel: MyPageViewModel,
    onSettingClick: () -> Unit = {},
    postContent: @Composable () -> Unit,
    scrapContent: @Composable () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // 화면이 처음 그려지거나 다른 화면(글쓰기 등)에 갔다 돌아올 때마다 새로고침
    LaunchedEffect(Unit) {
        viewModel.refreshAllData()
    }

    MyPageContent(
        uiState = uiState,
        onSettingClick = onSettingClick,
        onTabSelected = { viewModel.onTabSelected(it) },
        postContent = postContent,
        scrapContent = scrapContent
    )
}

@Composable
fun MyPageContent(
    uiState: MyPageUiState,
    onSettingClick: () -> Unit,
    onTabSelected: (MyPageTab) -> Unit,
    postContent: @Composable () -> Unit,
    scrapContent: @Composable () -> Unit
) {
    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        containerColor = BgDarkest,
        topBar = {
            MyPageTopAppBar(onSettingClick = onSettingClick)
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // [F-MY-01] 프로필 (AuthRepository 기반 데이터)
            ProfileHeader(user = uiState.user, noteCount = uiState.noteCount)

            // 2. 탭 선택 바
            MyPageTabRow(
                selectedTab = uiState.selectedTab,
                onTabSelected = onTabSelected
            )

            // 3. 컨텐츠 영역
            Box(modifier = Modifier.fillMaxSize()) {
                when (uiState.selectedTab) {
                    MyPageTab.POSTS -> postContent()
                    MyPageTab.SCRAPS -> scrapContent()
                }
            }
        }
    }
}

@Composable
fun MyPageTopAppBar(onSettingClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        IconButton(onClick = onSettingClick) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = OrangePrimary
            )
        }
    }
}

@Composable
fun ProfileHeader(user: User?, noteCount: Int) {
    LaunchedEffect(user) {
        android.util.Log.d("UI_DATA", "현재 UI에 전달된 유저: $user")
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // [F-MY-01] 실제 사용자의 프로필 이미지를 불러옵니다.
        JoopJoopImage(
            model = user?.profileImageUrl, // 하드코딩된 URL 삭제
            contentDescription = "프로필 이미지",
            isProfile = true,
            modifier = Modifier
                .size(80.dp)
                .background(BgDark, CircleShape)
                .border(2.dp, OrangePrimary, CircleShape)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            // [F-MY-01] 실제 닉네임과 게시물 수 표시
            Text(
                text = user?.nickname ?: "사용자",
                color = TextPrimary
            )
            Text(
                text = "게시물 $noteCount", // user?.noteCount 대신 전달받은 noteCount 사용
                color = OrangePrimary
            )
        }
    }
}

@Composable
fun MyPageTabRow(
    selectedTab: MyPageTab,
    onTabSelected: (MyPageTab) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = BgDarkest,
        contentColor = OrangePrimary,
        divider = { HorizontalDivider(color = DividerColor) }
    ) {
        MyPageTab.values().forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = if (tab == MyPageTab.POSTS) "쪽지목록" else "스크랩 목록"
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyPageTopAppBarPreview() {
    JoopJoopTheme {
        Box(modifier = Modifier.background(BgDarkest)) {
            MyPageTopAppBar(onSettingClick = {})
        }
    }
}
