package com.example.joopjoop.feature.mypage.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.joopjoop.feature.mypage.ui.components.MyPageBottomNavigationSection
import com.example.joopjoop.feature.mypage.ui.components.MyPageContentCard
import com.example.joopjoop.feature.mypage.ui.components.MyPageProfileSection
import com.example.joopjoop.feature.mypage.ui.components.MyPageTabMenu
import com.example.joopjoop.feature.mypage.ui.components.MyPageTopAppBarSection
import com.example.joopjoop.feature.mypage.viewmodel.MyPageNoteListViewModel
import com.example.joopjoop.feature.mypage.viewmodel.MyPageProfileViewModel
import com.example.joopjoop.feature.mypage.viewmodel.MyPageScrapListViewModel

@Composable
fun MyPageMainScreen(
    profileViewModel: MyPageProfileViewModel,
    noteListViewModel: MyPageNoteListViewModel,
    scrapListViewModel: MyPageScrapListViewModel,
    showBottomNavigation: Boolean = true
) {
    val profileState = profileViewModel.uiState
    val noteState = noteListViewModel.uiState
    val scrapState = scrapListViewModel.uiState

    var selectedTab by remember { mutableStateOf(MyPageTab.SCRAP) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MyPageColors.Surface)
    ) {
        MyPageTopAppBarSection()

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                MyPageProfileSection(
                    nickname = profileState.nickname,
                    noteCount = profileState.noteCount
                )
            }

            item(span = { GridItemSpan(2) }) {
                MyPageTabMenu(
                    selectedTab = selectedTab,
                    onSelectTab = { selectedTab = it }
                )
            }

            if (selectedTab == MyPageTab.SCRAP) {
                when {
                    scrapState.errorMessage != null -> {
                        item(span = { GridItemSpan(2) }) {
                            Text(
                                text = scrapState.errorMessage,
                                color = MyPageColors.OnSurfaceHigh,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }

                    scrapState.isLoading -> {
                        item(span = { GridItemSpan(2) }) {
                            Text(
                                text = "로딩 중...",
                                color = MyPageColors.OnSurfaceHigh,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }

                    scrapState.scraps.isEmpty() -> {
                        item(span = { GridItemSpan(2) }) {
                            Text(
                                text = "스크랩한 항목이 없습니다.",
                                color = MyPageColors.OnSurfaceHigh,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }

                    else -> {
                        items(scrapState.scraps) { item ->
                            MyPageContentCard(
                                previewText = item.previewText,
                                createdAt = item.createdAt,
                                imageRatio = CardImageRatio.OneToOne
                            )
                        }
                    }
                }
            } else {
                when {
                    noteState.errorMessage != null -> {
                        item(span = { GridItemSpan(2) }) {
                            Text(
                                text = noteState.errorMessage,
                                color = MyPageColors.OnSurfaceHigh,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }

                    noteState.isLoading -> {
                        item(span = { GridItemSpan(2) }) {
                            Text(
                                text = "로딩 중...",
                                color = MyPageColors.OnSurfaceHigh,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }

                    noteState.notes.isEmpty() -> {
                        item(span = { GridItemSpan(2) }) {
                            Text(
                                text = "작성한 쪽지가 없습니다.",
                                color = MyPageColors.OnSurfaceHigh,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }

                    else -> {
                        items(noteState.notes) { item ->
                            MyPageContentCard(
                                previewText = item.previewText,
                                createdAt = item.createdAt,
                                imageRatio = CardImageRatio.FourToFive
                            )
                        }
                    }
                }
            }
        }

        if (showBottomNavigation) {
            MyPageBottomNavigationSection()
        }
    }
}
