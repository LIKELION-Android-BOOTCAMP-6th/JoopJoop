package com.example.joopjoop

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.joopjoop.core.di.AppContainer
import com.example.joopjoop.feature.note.ui.detail.NoteDetailScreen
import com.example.joopjoop.feature.note.ui.list.NoteListScreen
import com.example.joopjoop.feature.note.ui.write.WriteNoteScreen
import com.example.joopjoop.feature.note.viewmodel.WriteNoteViewModel


class MainActivity : ComponentActivity() {
    private val appContainer by lazy { AppContainer(applicationContext) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Edge-to-Edge 설정 (상태바까지 화면 확장 - 선택 사항)
        // WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "noteList") {

                // 노트 리스트
                composable("noteList") {
                    NoteListScreen(
                        navController = navController,
                        factory = appContainer.noteViewModelFactory
                    )
                }

                // 노트 상세 화면
                composable(route = "noteDetail/{noteId}") { backStackEntry ->
                    // URL에서 noteId를 추출
                    val noteId = backStackEntry.arguments?.getString("noteId") ?: ""

                    NoteDetailScreen(
                        navController = navController,
                        noteId = noteId,
                        factory = appContainer.noteViewModelFactory // 같은 팩토리를 사용합니다.
                    )
                }

                // 노트 작성 화면
                composable("writeNote") {
                    val context = LocalContext.current
                    val viewModel: WriteNoteViewModel = viewModel(
                        factory = appContainer.writeNoteViewModelFactory
                    )
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    WriteNoteScreen(
                        navController = navController,
                        uiState = uiState,
                        // 1. 카테고리 변경 연결
                        onCategorySelected = { category ->
                            viewModel.onCategorySelected(category)
                        },
                        // 2. 내용 입력 연결
                        onContentChange = { content ->
                            if (content.length <= 300) { // 300자 제한 로직 추가
                                viewModel.onContentChange(content)
                            }
                        },
                        // 3. 보관 시간 조절 연결
                        onIncreaseHours = { viewModel.increaseHours() },
                        onDecreaseHours = { viewModel.decreaseHours() },
                        // 4. 뒤로가기
                        onBackClick = { navController.popBackStack() },
                        // 5. 쪽지 남기기 실행
                        onLeaveNoteClick = {
                            viewModel.submitNote() // ViewModel 내부에서 uiState의 값을 사용해 저장
                        }
                    )

                    // 저장 성공 시 화면 닫기 (이전 화면인 지도로 이동)
                    LaunchedEffect(uiState.isSubmitSuccess) {
                        if (uiState.isSubmitSuccess) {
                            Toast.makeText(context, "쪽지를 남겼습니다!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    }
                }
            }
        }

//        setContent {
//            JoopJoopTheme {
//                // 1. 네비게이션의 핸들(Controller)을 만듭니다.
//                val navController = androidx.navigation.compose.rememberNavController()
//
//                // 2. 어떤 화면들이 있는지 지도를 그립니다(NavHost).
//                androidx.navigation.compose.NavHost(
//                    navController = navController,
//                    startDestination = "login" // 시작은 로그인 화면!
//                ) {
//                    // 로그인 화면 경로 설정
//                    composable("login") {
//                        LoginRoute(
//                            onLoginSuccess = {
//                                Log.d("MainActivity", "로그인 성공 -> 지도로 이동")
//                                // navController.navigate("main_map") // 나중에 추가!
//                            },
//                            onBackClick = { finish() },
//                            onCreateAccountClick = {
//                                Log.d("MainActivity", "회원가입으로 이동")
//                                navController.navigate("signup") // 회원가입 화면으로 슝!
//                            }
//                        )
//                    }
//
//                    // 회원가입 화면 경로 설정
//                    composable("signup") {
//                        SignupRoute(
//                            onBackClick = {
//                                // 뒤로가기 버튼 누르면 로그인 화면으로 이동
//                                navController.popBackStack()
//                            },
//                            onSignupSuccess = {
//                                // 회원가입 성공 시 로그 찍고 로그인 화면으로 돌아가기
//                                android.util.Log.d("MainActivity", "회원가입 성공! 이제 로그인 해주세요.")
//                                navController.popBackStack()
//                            }
//                        )
//                    }
//                }
//            }
//        }

        // note 화면 출력 테스트를 위한 코드
        // note 실행화면 보고싶으면 위 코드 주석 후
        // 아래 코드 주석 해제하기
//        setContent {
//            val navController = rememberNavController()
//            NavHost(navController = navController, startDestination = "noteList") {
//                composable("noteList") { NoteListScreen(navController = navController) }
//                composable("noteDetail/{noteId}") { backStackEntry ->
//                    val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
//                    NoteDetailScreen(navController = navController, noteId = noteId)
//                }
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
//                    )
//                }
//        }
    }
}