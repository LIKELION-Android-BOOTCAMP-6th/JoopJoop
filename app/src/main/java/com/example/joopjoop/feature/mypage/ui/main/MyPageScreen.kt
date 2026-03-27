package com.example.joopjoop.feature.mypage.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.joopjoop.core.model.User
import com.example.joopjoop.feature.mypage.viewmodel.MyPageViewModel
import com.example.joopjoop.ui.theme.BgDark
import com.example.joopjoop.ui.theme.BgDarkest
import com.example.joopjoop.ui.theme.DividerColor
import com.example.joopjoop.ui.theme.OrangePrimary
import com.example.joopjoop.ui.theme.TextPrimary

@Composable
fun MyPageScreen(
    viewModel: MyPageViewModel, // 수동 DI로 주입받음
    postContent: @Composable () -> Unit, // post/ 폴더의 부품
    scrapContent: @Composable () -> Unit  // scrap/ 폴더의 부품
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = BgDarkest,
        topBar = { /* MyPageTopAppBar 구현 */ }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // [F-MY-01] 프로필 (AuthRepository 기반 데이터)
            // 1. 프로필 헤더 (직접 구현 필요)
            ProfileHeader(user = uiState.user)

            // 2. 탭 선택 바 (직접 구현 필요)
            MyPageTabRow(
                selectedTab = uiState.selectedTab,
                onTabSelected = { viewModel.onTabSelected(it) }
            )

            // 3. 컨텐츠 영역 (주입받은 부품 사용)
            when (uiState.selectedTab) {
                MyPageTab.POSTS -> postContent()
                MyPageTab.SCRAPS -> scrapContent()
            }
        }
    }
}

@Composable
fun ProfileHeader(user: User?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 프로필 이미지 영역 (일단 원형 박스로 배치)
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(BgDark, CircleShape)
                .border(2.dp, OrangePrimary, CircleShape) // 주황색 포인트
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = user?.nickname ?: "사용자",
                color = TextPrimary
            )
            Text(
                text = "게시물 ${user?.noteCount ?: 0}",
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