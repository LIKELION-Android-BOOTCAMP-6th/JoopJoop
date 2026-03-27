package com.example.joopjoop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.joopjoop.ui.theme.JoopJoopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Edge-to-Edge 설정 (상태바까지 화면 확장 - 선택 사항)
        // WindowCompat.setDecorFitsSystemWindows(window, false)

//        enableEdgeToEdge()

        setContent {
            JoopJoopTheme {
                RootNavHost()
            }
        }

        // note 화면 출력 테스트를 위한 코드
        // note 실행화면 보고싶으면 위 코드 주석 후
        // 아래 코드 주석 해제하기
//        setContent {
//            val navController = rememberNavController()
//            NavHost(navController = navController, startDestination = "noteList") {
//                composable("noteList") { NoteListScreen(navController = navController) }
//                composable("noteDetail/{noteId}") { backStackEntry ->
//                    val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
//                    NoteDetailScreen(navController = navController, noteId = noteId) }
//                composable("writeNote") {
//                    val viewModel: WriteNoteViewModel = viewModel()
//                    val uiState by viewModel.uiState.collectAsState()
//
//                    LaunchedEffect(uiState.isSubmitSuccess) {
//                        if (uiState.isSubmitSuccess && uiState.createdNoteId != null) {
//                            val newId = uiState.createdNoteId
//                            viewModel.resetNote() // 상태 초기화
//
//                            // 작성 화면은 스택에서 제거하고 상세 화면으로 이동
//                            navController.navigate("noteDetail/$newId") {
//                                popUpTo("writeNote") { inclusive = true }
//                            }
//                        }
//                    }
//                    WriteNoteScreen(
//                        navController = navController,
//                        uiState = uiState,
//                        onCategorySelected = viewModel::onCategorySelected,
//                        onContentChange = viewModel::onContentChange,
//                        onIncreaseHours = viewModel::increaseHours,
//                        onDecreaseHours = viewModel::decreaseHours,
//                        onBackClick = { navController.popBackStack() },
//                        onLeaveNoteClick = viewModel::submitNote
//                    )
//                }
//            }
//        }
    }
}