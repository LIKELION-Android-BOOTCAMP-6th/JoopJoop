package com.example.joopjoop.feature.note.ui.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.joopjoop.R
import com.example.joopjoop.core.common.util.showToast
import com.example.joopjoop.feature.note.viewmodel.NoteDetailViewModel
import com.example.joopjoop.ui.theme.BgDark
import com.example.joopjoop.ui.theme.BgDarkest
import com.example.joopjoop.ui.theme.DividerColor
import com.example.joopjoop.ui.theme.JoopJoopTheme
import com.example.joopjoop.ui.theme.OrangePrimary
import com.example.joopjoop.ui.theme.TextPrimary
import com.example.joopjoop.ui.theme.TextSecondary
import com.example.joopjoop.ui.theme.TextTertiary

@Composable
fun NoteDetailScreen(
    navController: NavController,
    noteId: String = "1", // 나중에 NavArgs에서 받아올 ID
    viewModel: NoteDetailViewModel // NavGraph에서 뷰모델 주입
) {
//    val viewModel: NoteDetailViewModel = viewModel()
//    val viewModel: NoteDetailViewModel = viewModel(factory = factory)
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 화면 진입 시 데이터 불러오기
    LaunchedEffect(noteId) {
        viewModel.loadNoteDetail(noteId)
    }

    // 에러 메시지 감시 및 토스트 출력
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            context.showToast(message) // 공통 유틸리티 호출!

            // 데이터가 없으면 상세 화면에 있을 이유가 없으므로 이전 화면으로 이동
            if (message == "쪽지를 찾을 수 없습니다.") {
                navController.popBackStack()
            }
        }
    }

    JoopJoopTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = BgDarkest,
            topBar = {
                DetailTopBar(navController)
            },
            // 바텀네비게이션바 삭제
//            bottomBar = {
//                DetailBottomBar(navController)
//            }
        ) { innerPadding ->
            NoteDetail(
                uiState = uiState,
                modifier = Modifier.padding(innerPadding),
                // 버튼 클릭 이벤트 ViewModel로 연결
                onLikeClick = { viewModel.toggleLike(noteId) },
                onBookmarkClick = { viewModel.toggleBookmark() }
            )
        }
    }
}


@Composable
fun NoteDetail(
    modifier: Modifier = Modifier,
    uiState: NoteDetailUiState = NoteDetailUiState(),
    onLikeClick: () -> Unit = {}, // 좋아요 클릭
    onBookmarkClick: () -> Unit = {} // 스크랩 클릭
) {

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDarkest)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        // 유저 정보
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 프로필 이미지 (기본 아이콘)
            Icon(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BgDark),
                painter = painterResource(id = R.drawable.baseline_person_24),
                contentDescription = null,
                tint = TextTertiary
            )
            //이름
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(1.dp)
            )
            {

                Text(
                    text = uiState.authorName,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp
                )
                //작성일-조회수-좋아요수
                Text(
                    text = "${uiState.createdAt} 조회 ${uiState.viewCount} 좋아요 ${uiState.likeCount}",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 14.sp
                )
                // 위치
                Text(
                    text = uiState.location,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 14.sp
                )

            }
        }
        val isImageAdded = uiState.imageUri != null
        val blurRadius = if (isImageAdded) 0.dp else 16.dp
        // 메인 이미지 카드
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // 정사각형 비율
                .clip(RoundedCornerShape(12.dp))
                .background(BgDark) // 이미지 로딩 전 배경색
        ) {
            if (isImageAdded) {
                Image(
                    painter = painterResource(id = R.drawable.note_detail_image),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(OrangePrimary.copy(alpha = 0.2f), BgDarkest)
                            )
                        )
                        .blur(20.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.outline_edit_square_24),
                    contentDescription = null,
                    tint = TextTertiary.copy(alpha = 0.4f),
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center)
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        // 본문 텍스트 영역
        Text(
            text = uiState.content,
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 좋아요 & 스크랩 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DetailActionButton(
                icon = R.drawable.outline_thumb_up_24,
                label = "좋아요",
                isActive = uiState.isLiked,
                onClick = onLikeClick,
                modifier = Modifier.weight(1f)
            )
            DetailActionButton(
                icon = R.drawable.outline_bookmark_24,
                label = "스크랩",
                isActive = uiState.isBookmarked,
                onClick = onBookmarkClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
fun DetailTopBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .statusBarsPadding()
            .background(BgDarkest),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
            contentDescription = null,
            tint = TextPrimary,
            modifier = Modifier
                .size(24.dp)
                .clickable { navController.popBackStack() }
        )
        Text(
            text = "쪽지 상세",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// 공통으로 쓰일 하단 버튼
@Composable
fun DetailActionButton(
    modifier: Modifier = Modifier,
    icon: Int,
    label: String,
    isActive: Boolean = false,
    onClick: () -> Unit = {}
) {

    Box(
        modifier = modifier
            .height(48.dp)
            .border(1.dp, DividerColor, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painterResource(id = icon),
                contentDescription = null,
                tint = if (isActive) OrangePrimary else TextPrimary,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = label,
                color = if (isActive) OrangePrimary else TextPrimary,
                fontSize = 14.sp
            )
        }
    }
}

// 임시로 사용하던 네비게이션 바 (추후 삭제 해도 됨)

//// 화면 하단 네비게이션 바
//@Composable
//fun DetailBottomBar(navController: NavController) {
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
//    JoopJoopTheme {
//        NoteDetailScreen(navController = rememberNavController())
//    }
}