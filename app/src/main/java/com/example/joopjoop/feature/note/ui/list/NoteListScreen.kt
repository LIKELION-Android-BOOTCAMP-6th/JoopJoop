package com.example.joopjoop.feature.note.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.joopjoop.R
import com.example.joopjoop.core.common.util.JoopJoopImage
import com.example.joopjoop.core.common.util.OnSingleClickListener
import com.example.joopjoop.core.model.Note
import com.example.joopjoop.feature.map.viewmodel.MapViewModel
import com.example.joopjoop.ui.theme.BgDark
import com.example.joopjoop.ui.theme.BgDarkest
import com.example.joopjoop.ui.theme.JoopJoopTheme
import com.example.joopjoop.ui.theme.OrangePrimary
import com.example.joopjoop.ui.theme.TextPrimary
import com.example.joopjoop.ui.theme.TextTertiary

@Composable
fun NoteListScreen(
    navController: NavController,
    // 팩토리가 아니라 NavGraph에서 뷰모델을 받도록 수정.
    viewModel: MapViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // savedStateHandle에 저장된 신호(쪽지 삭제) 받기
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isNoteRefresh =
        navBackStackEntry?.savedStateHandle?.get<Boolean>("SHOULD_REFRESH") ?: false

    // 쪽지 신호 감지시 쪽지 재검색
    LaunchedEffect(isNoteRefresh) {
        if (isNoteRefresh) {
            // 현재 내 위치를 기준으로 다시 로드 (viewModel에 현재 위치 정보가 있다고 가정)
            uiState.currentUserLocation?.let { location ->
                viewModel.loadNotes(location)
            }
            // 위 작업 후 마무리 (안바꾸면 계속 새로고침하게됨)
            navController.currentBackStackEntry?.savedStateHandle?.set("SHOULD_REFRESH", false)
        }
    }

    JoopJoopTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            containerColor = BgDarkest,
            topBar = { ListTopBar(navController) },
//            bottomBar = { ListBottomBar(navController) } // 바텀네비게이션바 삭제
        ) { innerPadding ->

            NoteList(
                pickableNotes = uiState.pickableNotes,
                distantNotes = uiState.distantNotes,
                isLoading = uiState.isLoading,
                modifier = Modifier.padding(innerPadding),
                navController = navController
            )
        }
    }
}

@Composable
fun NoteList(
    pickableNotes: List<Note>,
    distantNotes: List<Note>,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDarkest)
            .padding(10.dp, 0.dp)
    ) {
        // 로딩 화면 표시
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = OrangePrimary)
            }
        } else if (pickableNotes.isEmpty() && distantNotes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_mail_24),
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.empty_note_list),
                        color = TextTertiary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // 쪽지 리스트
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 한 줄 두개씩
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                // 가까운 쪽지
                items(pickableNotes, key = { it.id }) { note ->
                    NoteCard(
                        item = note,
                        isLocked = false,
                        onClick = { navController.navigate("noteDetail/${note.id}") }
                    )
                }
                // 먼 쪽지
                items(distantNotes, key = { it.id }) { note ->
                    NoteCard(
                        item = note,
                        isLocked = true,
                        onClick = { } // 클릭 막기
                    )
                }
            }
        }
    }
}

@Composable
fun ListTopBar(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgDarkest),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 0.dp),
            contentAlignment = Alignment.Center
        ) {
            // 뒤로가기 아이콘
            IconButton(
                onClick = {
                    OnSingleClickListener.onclick {
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
            // 제목
            Text(
                text = "주변 쪽지 목록",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 개별 쪽지 형태
@Composable
fun NoteCard(item: Note, isLocked: Boolean, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable(enabled = !isLocked) { onClick() }
    ) {
        //쪽지 아이콘 배경 영역
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(BgDark),
            contentAlignment = Alignment.Center
        ) {
            when {
                // 멀리 있고 + 이미지 있음 → blur 이미지
                isLocked && !item.thumbnailUrl.isNullOrBlank() -> {
                    JoopJoopImage(
                        model = item.thumbnailUrl,
                        contentDescription = "쪽지 썸네일",
                        modifier = Modifier
                            .fillMaxSize(),
                        isBlurred = true
                    )
                }
                // 멀리 있고 + 이미지 없음 → lock만
                isLocked -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(40.dp)
                    )
                }
                // 가까움 + 이미지 있음
                !item.thumbnailUrl.isNullOrBlank() -> {
                    JoopJoopImage(
                        model = item.thumbnailUrl,
                        contentDescription = "쪽지 썸네일",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                // 가까움 + 이미지 없음
                else -> {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_mail_24),
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            // blur 이미지 위에 lock 아이콘
            if (isLocked && !item.thumbnailUrl.isNullOrBlank()) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_lock),
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        /*when {
            // 1. 잠금 상태 (30m 밖)
            isLocked -> {
                Icon(
                    painter = painterResource(id = R.drawable.ic_lock),
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(40.dp)
                )
            }
            // 2. 30m 이내 & 사진 있음 (썸네일 노출)
            !item.thumbnailUrl.isNullOrBlank() -> {
                JoopJoopImage(
                    model = item.thumbnailUrl,
                    contentDescription = "쪽지 썸네일",
                    modifier = Modifier.fillMaxSize()
                )
            }
            // 3. 30m 이내 & 사진 없음 (기존 아이콘)
            else -> {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_mail_24),
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }*/
        /*if (isLocked) {
            // 잠금 상태일 때 잠금 아이콘
            Icon(
                painter = painterResource(id = R.drawable.ic_lock),
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(40.dp)
            )
        } else {
            // 열린 상태일 때 (기존 메일 아이콘 혹은 사진)
            Icon(
                painter = painterResource(id = R.drawable.baseline_mail_24),
                contentDescription = null,
                tint = OrangePrimary,
                modifier = Modifier.size(48.dp)
            )
        }
    }*/

        Spacer(modifier = Modifier.height(10.dp))

        // 내용 - 본문 일부 노출(한줄만, 넘어가면 ... 처리)
        Text(
            text = if (isLocked) "가까이 이동해보세요" else item.contentText, color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 16.sp,
            modifier = Modifier.padding(10.dp, 0.dp)
        )

        // 거리 정보 표시 영역(아이콘 + 텍스트)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp, 0.dp)
        ) {//나침반 화살표 아이콘(45도 회전, 수평 맞추기 위해 윗쪽으로 미세 조정)
            Icon(
                painter = painterResource(id = R.drawable.baseline_navigation_24),
                contentDescription = null,
                tint = if (isLocked) TextTertiary else OrangePrimary, modifier = Modifier
                    .size(14.dp)
                    .rotate(45f) // 회전
                    .offset(y = (-2).dp) // 미세조정
            )
            Spacer(modifier = Modifier.width(4.dp))

            //거리 텍스트
            Text(
                text = item.location.distance.ifEmpty { "-" },
                color = TextTertiary,
                fontSize = 12.sp
            )
        }
    }
}

// 공통네비게이션바가 적용되므로 주석으로 막음 (추후 삭제해도 됨)

//// 화면 하단 네비게이션 바
//@Composable
//fun ListBottomBar(navController: NavController) {
//
//    var selectedTab by remember { mutableStateOf("MAP") }
//
//    val selectedColor = OrangePrimary
//    val unselectedColor = TextTertiary
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(BgDarkest)
//            .navigationBarsPadding()
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 8.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            // MAP 탭
//            Column(
//                modifier = Modifier
//                    .weight(1f)
//                    .clickable { selectedTab = "MAP" },
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.baseline_map_24),
//                    contentDescription = null,
//                    tint = if (selectedTab == "MAP") selectedColor else unselectedColor,
//                )
//                Text(
//                    text = "MAP",
//                    color = if (selectedTab == "MAP") selectedColor else unselectedColor,
//                    fontSize = 8.sp,
//                    textAlign = TextAlign.Center
//                )
//            }
//
//            // WRITE 탭
//            Column(
//                modifier = Modifier
//                    .weight(1f)
//                    .clickable {
//                        selectedTab = "WRITE"
//                        navController.navigate("writeNote")
//                    },
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.outline_edit_square_24),
//                    contentDescription = null,
//                    tint = if (selectedTab == "WRITE") selectedColor else unselectedColor,
//
//                    )
//                Text(
//                    text = "WRITE",
//                    color = if (selectedTab == "WRITE") selectedColor else unselectedColor,
//                    fontSize = 8.sp,
//                    textAlign = TextAlign.Center
//                )
//            }
//
//            // MY PAGE 탭
//            Column(
//                modifier = Modifier
//                    .weight(1f)
//                    .clickable { selectedTab = "MY PAGE" },
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.baseline_person_24),
//                    contentDescription = null,
//                    tint = if (selectedTab == "MY PAGE") selectedColor else unselectedColor,
//                )
//                Text(
//                    text = "MY PAGE",
//                    color = if (selectedTab == "MY PAGE") selectedColor else unselectedColor,
//                    fontSize = 8.sp,
//                    textAlign = TextAlign.Center
//                )
//            }
//
//        }
//    }
//}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun GreetingPreview() {
////    JoopJoopTheme {
////        NoteListScreen(navController = rememberNavController())
////    }
//}

