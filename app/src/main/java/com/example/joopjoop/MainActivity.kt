package com.example.joopjoop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.joopjoop.ui.theme.BgDarkest
import com.example.joopjoop.ui.theme.JoopJoopTheme

class MainActivity : ComponentActivity() {
    // 쪽지(note) 화면 테스트 시에 사용했던 appContainer
//    private val appContainer by lazy { AppContainer(applicationContext) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Edge-to-Edge 설정 (상태바까지 화면 확장 - 선택 사항)
        // WindowCompat.setDecorFitsSystemWindows(window, false)
//        enableEdgeToEdge()

        setContent {
            JoopJoopTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BgDarkest) // 번쩍임을 없애기 위해 검은 배경으로 변경
                ) {
                    RootNavHost()
                }
            }
        }
    }


    // 쪽지(note) 목록, 쪽지 상세, 쪽지 작성 환경만 테스트 하기 위한 코드
    // 추후에 화면 연결 완료시 삭제 필요
    // 해당 코드 사용하고 싶으면 상단 appContainer 변수 주석 해제 후 필요한 파일 import 하면됨
//        setContent {
//            val navController = rememberNavController()
//            NavHost(navController = navController, startDestination = "noteList") {
//
//                // 노트 리스트
//                composable("noteList") {
//                    NoteListScreen(
//                        navController = navController,
//                        factory = appContainer.noteViewModelFactory
//                    )
//                }
//
//                // 노트 상세 화면
//                composable(route = "noteDetail/{noteId}") { backStackEntry ->
//                    // URL에서 noteId를 추출
//                    val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
//
//                    NoteDetailScreen(
//                        navController = navController,
//                        noteId = noteId,
//                        factory = appContainer.noteViewModelFactory // 같은 팩토리를 사용합니다.
//                    )
//                }
//
//                // 노트 작성 화면
//                composable("writeNote") {
//                    val context = LocalContext.current
//                    val viewModel: WriteNoteViewModel = viewModel(
//                        factory = appContainer.noteViewModelFactory
//                    )
//                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//
//                    WriteNoteScreen(
//                        navController = navController,
//                        uiState = uiState,
//                        // 1. 카테고리 변경 연결
//                        onCategorySelected = { category ->
//                            viewModel.onCategorySelected(category)
//                        },
//                        // 2. 내용 입력 연결
//                        onContentChange = { content ->
//                            if (content.length <= 300) { // 300자 제한 로직 추가
//                                viewModel.onContentChange(content)
//                            }
//                        },
//                        // 3. 보관 시간 조절 연결
//                        onIncreaseHours = { viewModel.increaseHours() },
//                        onDecreaseHours = { viewModel.decreaseHours() },
//                        // 4. 뒤로가기
//                        onBackClick = { navController.popBackStack() },
//                        // 5. 쪽지 남기기 실행
//                        onLeaveNoteClick = {
//                            viewModel.submitNote(context) // ViewModel 내부에서 uiState의 값을 사용해 저장
//                        }
//                    )
//
//                    // 저장 성공 시 화면 닫기 (이전 화면인 지도로 이동)
//                    LaunchedEffect(uiState.isSubmitSuccess) {
//                        if (uiState.isSubmitSuccess) {
//                            Toast.makeText(context, "쪽지를 남겼습니다!", Toast.LENGTH_SHORT).show()
//                            navController.popBackStack()
//                        }
//                    }
//                }
//            }
//        }
}