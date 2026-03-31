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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.joopjoop.core.common.util.JoopJoopImage

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
        if (noteId.isNotEmpty()) {
            viewModel.loadNoteDetail(noteId)
        }
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
            Box(modifier = Modifier.padding(innerPadding)) {
                when {
                    uiState.isLoading -> {
                        // 로딩 중일 때는 로딩 컴포저블만 표시
                        LoadingScreen()
                    }

                    uiState.errorMessage != null -> {
                        // 에러 발생 시 처리 (예: 빈 화면 또는 에러 문구)
                    }

                    else -> {
                        // 로딩이 완료된 후에만 실제 상세 내용을 그림
                        NoteDetail(
                            uiState = uiState,
                            navController = navController,
                            viewModel = viewModel,
                            noteId = noteId
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgDarkest), // 배경색 통일
        contentAlignment = Alignment.Center
    ) {
        // 주황색 로딩 스피너
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = OrangePrimary,
            strokeWidth = 4.dp
        )
    }
}

@Composable
fun NoteDetail(
    modifier: Modifier = Modifier,
    navController: NavController,
    uiState: NoteDetailUiState = NoteDetailUiState(),
    viewModel: NoteDetailViewModel,
    noteId: String = "1"
) {
    val scrollState = rememberScrollState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDarkest)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        // 1. 유저 정보 영역
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(BgDark),
                painter = painterResource(id = R.drawable.baseline_person_24),
                contentDescription = null,
                tint = TextTertiary
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(
                    text = uiState.userNickName,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${uiState.createdAt} 조회 ${uiState.viewCount} 좋아요 ${uiState.likeCount}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = uiState.location,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 2. 이미지 영역 (사진이 있을 때만 노출)
        val isImageAdded = !uiState.imageUri.isNullOrBlank()
        if (isImageAdded) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BgDark)
            ) {
                JoopJoopImage(
                    model = uiState.imageUri,
                    contentDescription = "쪽지 이미지",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        } else {
            // 사진 없을 때 간격
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. 본문 텍스트 영역
        Text(
            text = uiState.content,
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 4. 하단 버튼 (수정/삭제 또는 좋아요/스크랩)
        NoteDetailBottomButton(
            uiState = uiState,
            onEdit = { viewModel.editNote(noteId) },
            onDelete = { showDeleteDialog = true },
            onLikeClick = { viewModel.toggleLike(noteId) },
            onBookmarkClick = { viewModel.toggleBookmark(noteId) }
        )

        Spacer(modifier = Modifier.height(40.dp))
    } // Column 끝

    // 5. 삭제 확인 다이얼로그 (Column 바깥)
    if (showDeleteDialog) {
        DeleteNoteDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                viewModel.deleteNote(noteId) {
                    showDeleteDialog = false
                    navController.popBackStack()
                }
            }
        )
    }
}

/*@Composable
fun NoteDetail(
    modifier: Modifier = Modifier,
    navController: NavController,
    uiState: NoteDetailUiState = NoteDetailUiState(),
    viewModel: NoteDetailViewModel,
    noteId: String = "1"
) {
    val scrollState = rememberScrollState()
    var showDeleteDialog by remember { mutableStateOf(false) }  // 쪽지 삭제시 다이얼로그

    val isImageAdded = !uiState.imageUri.isNullOrBlank()
    if (isImageAdded) {
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(BgDark)
        ) {
            JoopJoopImage(
                model = uiState.imageUri,
                contentDescription = "쪽지 이미지",
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
    } else {
        // 사진 없을 때 간격
        Spacer(modifier = Modifier.height(16.dp))
    }

    // 3. 본문 텍스트 영역
    Text(
        text = uiState.content,
        color = TextSecondary,
        fontSize = 14.sp,
        lineHeight = 22.sp
    )

    Spacer(modifier = Modifier.height(32.dp))}

    *//*Column(
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
            ) {
                Text(
                    text = uiState.userNickName,
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
        val isImageAdded = uiState.imageUrl != null
        val blurRadius = if (isImageAdded) 0.dp else 16.dp
        // 메인 이미지 카드
        if (!uiState.imageUri.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // 사진이 있을 때만 정사각형 비율 유지
                    .clip(RoundedCornerShape(12.dp))
                    .background(BgDark)
            ) {
                JoopJoopImage(
                    model = uiState.imageUri,
                    contentDescription = "쪽지 이미지",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        } else {
            // 사진이 없을 때 유저 정보와 본문 사이의 최소한의 여백만 추가
            Spacer(modifier = Modifier.height(8.dp))
        }
        *//**//*Column(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // 정사각형 비율
                .clip(RoundedCornerShape(12.dp))
                .background(BgDark) // 이미지 로딩 전 배경색
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // 정사각형 비율
                    .clip(RoundedCornerShape(12.dp))
                    .background(BgDark) // 이미지 로딩 전 배경색
            ) {
                val isImageAdded = !uiState.imageUrl.isNullOrBlank()
                if (isImageAdded) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BgDark)
                    ){
                    JoopJoopImage(
                        model = uiState.imageUrl,
                        contentDescription = "쪽지 이미지",
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
        }
        Spacer(modifier = Modifier.height(20.dp))*//**//*

        // 본문 텍스트 영역
        Text(
            text = uiState.contentText,
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 22.sp
        )*//*

        Spacer(modifier = Modifier.height(24.dp))

        // 수정&삭제 / 좋아요&스크랩
        NoteDetailBottomButton(
            uiState = uiState,
            onEdit = { viewModel.editNote(noteId) },
            onDelete = { showDeleteDialog = true },
            onLikeClick = { viewModel.toggleLike(noteId) },
            onBookmarkClick = { viewModel.toggleBookmark(noteId) }
        )
    }

    if (showDeleteDialog) {
        DeleteNoteDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                viewModel.deleteNote(noteId) {
                    showDeleteDialog = false
                    navController.popBackStack()
                }
            }
        )
    }
}*/

@Composable
fun DeleteNoteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "쪽지 삭제", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Text(
                text = "정말 이 쪽지를 삭제하시겠습니까?\n삭제된 데이터는 복구할 수 없습니다.",
                color = TextSecondary,
                fontSize = 15.sp
            )
        },
        containerColor = BgDark,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "삭제", color = OrangePrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "취소", color = TextTertiary)
            }
        }
    )
}


@Composable
fun NoteDetailBottomButton(
    uiState: NoteDetailUiState,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onLikeClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (uiState.isAuthor) {
            DetailActionButton(
                icon = R.drawable.ic_edit,
                label = "수정하기",
                isActive = uiState.isLiked,
                onClick = onEdit,
                modifier = Modifier.weight(1f)
            )
            DetailActionButton(
                icon = R.drawable.ic_delete,
                label = "삭제하기",
                isActive = uiState.isBookmarked,
                onClick = onDelete,
                modifier = Modifier.weight(1f)
            )
        } else {
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