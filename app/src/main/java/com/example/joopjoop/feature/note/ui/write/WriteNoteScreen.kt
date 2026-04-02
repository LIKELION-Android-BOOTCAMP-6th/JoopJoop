package com.example.joopjoop.feature.note.ui.write

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.joopjoop.R
import com.example.joopjoop.core.common.util.JoopJoopImage
import com.example.joopjoop.feature.note.viewmodel.WriteNoteViewModel
import com.example.joopjoop.ui.theme.BgDark
import com.example.joopjoop.ui.theme.BgDarkest
import com.example.joopjoop.ui.theme.BgSurface
import com.example.joopjoop.ui.theme.OrangePrimary
import com.example.joopjoop.ui.theme.TextPrimary
import com.example.joopjoop.ui.theme.TextTertiary

@Composable
fun WriteNoteScreen(
    navController: NavController,
    viewModel: WriteNoteViewModel, // 여러 람다 대신 뷰모델을 주입 받음
//    uiState: WriteNoteUiState, // 상태 추가
    modifier: Modifier = Modifier, // 유지
//    onCategorySelected: (String) -> Unit = {},
//    onContentChange: (String) -> Unit = {},
//    onIncreaseHours: () -> Unit = {},
//    onDecreaseHours: () -> Unit = {},
//    onBackClick: () -> Unit = {},
//    onLeaveNoteClick: () -> Unit = {}
) {
    // 뷰모델로부터 현재 UI 상태(글 내용, 선택된 카테고리 등)를 가져옴
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val categories = listOf(
        stringResource(R.string.category_daily),
        stringResource(R.string.category_emotion),
        stringResource(R.string.category_memory),
        stringResource(R.string.category_restaurant)
    )

    // 갤러리 실행 런쳐
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val contentResolver = context.contentResolver
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(it, takeFlags)
                viewModel.onImageSelected(it, context)
            } catch (e: Exception) {
                Log.e("PhotoDebug", "URI 권한 획득 실패")
            }
        }
    }

    // 1. 포커스 매니저 가져오기
    val focusManager = LocalFocusManager.current


    // --- 뒤로가기 로직 추가 ---
    var backPressedTime by remember { mutableStateOf(0L) }

    // 뒤로가기 동작을 처리하는 공통 함수
    val handleBackNavigation = {
        val currentTime = System.currentTimeMillis()
        // 2초(2000ms) 이내에 다시 누르면 뒤로가기 실행
        if (currentTime - backPressedTime < 2000) {
            navController.popBackStack()
        } else {
            backPressedTime = currentTime
            Toast.makeText(context, "내용이 저장되지 않고 메인화면으로 돌아갑니다", Toast.LENGTH_SHORT).show()
        }
    }

    // 시스템 뒤로가기 버튼 처리 (하단 내비게이션 바/제스처)
    BackHandler(enabled = true) {
        handleBackNavigation()
    }
    LaunchedEffect(uiState.isSubmitSuccess) {
        if (uiState.isSubmitSuccess) {
            navController.popBackStack()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) { // 2. 터치 이벤트 감지 추가
                detectTapGestures(onTap = {
                    focusManager.clearFocus() // 배경 터치 시 키보드 내림
                })
            }, containerColor = BgDarkest, topBar = {
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
                        // 람다 대신 navController를 직접 사용하여 뒤로가기 실행
                        .clickable { handleBackNavigation() }
                        .padding(8.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // 쪽지 수정시 note id 값 가져오기
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val noteId = navBackStackEntry?.arguments?.getString("noteId")
                val title =
                    if (noteId == null) stringResource(R.string.write_note_title) else stringResource(
                        R.string.edit_note
                    )

                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. 카테고리 선택 부분
            Text(
                text = stringResource(R.string.select_category),
                color = OrangePrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories) { category ->
                    CategorySelection(
                        text = category,
                        isSelected = uiState.selectedCategory == category,
                        onClick = { viewModel.onCategorySelected(category) }) // 뷰모델 함수 호출
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. 쪽지 입력 부분 (Box 내부)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .background(BgDark, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                OutlinedTextField(
                    value = uiState.noteContent, // state 사용
                    onValueChange = { viewModel.onContentChange(it) }, // 뷰모델 콜백 사용
                    modifier = Modifier.fillMaxSize(), placeholder = {
                        Text(
                            text = stringResource(R.string.note_placeholder),
                            color = TextTertiary,
                            fontSize = 16.sp
                        )
                    }, colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = OrangePrimary
                    )
                )

                Text(
                    text = "${uiState.noteContent.length} / 300",
                    color = if (uiState.noteContent.length >= 300) OrangePrimary else TextTertiary,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 8.dp, end = 8.dp)
                )

                val isImageAdded = !uiState.selectedImageUri.isNullOrBlank()

                // 사진 추가 버튼 (기능은 나중에)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 8.dp)
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp)) // 클릭 영역 제한
                        .border(1.dp, TextTertiary, RoundedCornerShape(16.dp))
                        .clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (isImageAdded) {
                        JoopJoopImage(
                            model = uiState.selectedImageUri, // 뷰모델의 상태값 (Uri 또는 URL)
                            contentDescription = "선택된 이미지",
                            modifier = Modifier.fillMaxSize()
                        )
                        // 이미지 삭제 버튼
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(20.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                .clickable { viewModel.onImageRemoved() }, // 이미지 제거 로직
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_delete_24),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_camera),
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = stringResource(R.string.add_photo),
                                color = TextTertiary,
                                fontSize = 10.sp
                            )
                        }
                    }
                    if (uiState.isImageUploading) {
                        ImageUploadIndicator(modifier = Modifier.fillMaxSize())
                    }
                }
            }
            /*Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_camera),
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = stringResource(R.string.add_photo),
                                color = TextTertiary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }*/
            Spacer(modifier = Modifier.height(24.dp))

            // 3. 보관 기간 조절 Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgDark, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(OrangePrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_visibility),
                            contentDescription = null,
                            tint = BgDarkest,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.storage_period),
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.storage_period_desc),
                            color = TextTertiary,
                            fontSize = 12.sp
                        )
                    }
                }

                // 시간 조절 버튼
                Row(
                    modifier = Modifier
                        .background(BgDarkest, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "-",
                        color = TextTertiary,
                        modifier = Modifier.clickable { viewModel.decreaseHours() }) // viewModel 함수로 호출
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "${uiState.storageHours}h",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "+",
                        color = TextTertiary,
                        modifier = Modifier.clickable { viewModel.increaseHours() }) // viewModel 함수로 호출
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 4. 쪽지 남기기 버튼
            Button(
                onClick = {
                    viewModel.submitNote(context) {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("SHOULD_REFRESH", true)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                enabled = uiState.noteContent.isNotBlank() && !uiState.isSubmitting,
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary, contentColor = TextPrimary,
                    disabledContainerColor = OrangePrimary.copy(alpha = 0.3f),
                    disabledContentColor = TextTertiary
                )
            ) {
                Text(
                    text = "> ${stringResource(R.string.leave_note_button)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if (uiState.isSubmitting) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = OrangePrimary)
                }
            }
        }
    }
}

@Composable
fun CategorySelection(
    text: String, isSelected: Boolean, onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) OrangePrimary else BgSurface)
            .clickable { onClick() }
            .padding(horizontal = 20.dp), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            color = if (isSelected) TextPrimary else TextTertiary,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun ImageUploadIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(30.dp),
            color = OrangePrimary,
            strokeWidth = 3.dp
        )
    }
}


//프리뷰 사용하시려면 수정이 필요합니다.

//@Preview(showBackground = true)
//@Composable
//fun WriteNoteScreenPreview() { // 더미데이터
//    JoopJoopTheme {
//        WriteNoteScreen(
//            navController = rememberNavController(), uiState = WriteNoteUiState(
//                selectedCategory = "감성", noteContent = "오늘 날씨가 너무 좋네요~!", storageHours = 12
//            )
//        )
//    }
//}
