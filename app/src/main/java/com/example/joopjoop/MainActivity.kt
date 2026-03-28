package com.example.joopjoop

import android.os.Bundle
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.joopjoop.feature.note.ui.detail.NoteDetailScreen
import com.example.joopjoop.feature.note.ui.list.NoteListScreen
import com.example.joopjoop.feature.note.ui.write.WriteNoteScreen
import com.example.joopjoop.feature.note.ui.write.WriteNoteUiState
import com.example.joopjoop.feature.note.viewmodel.WriteNoteViewModel
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.composable
import com.example.joopjoop.core.repository.AuthRepository
import com.example.joopjoop.feature.auth.data.repository.AuthRepositoryImpl
import com.example.joopjoop.feature.auth.data.source.FirebaseAuthSource
import com.example.joopjoop.feature.auth.data.source.FirestoreUserSource
import com.example.joopjoop.feature.auth.ui.login.LoginRoute
import com.example.joopjoop.feature.auth.ui.signup.SignupRoute
import com.example.joopjoop.feature.notification.viewmodel.NotificationViewModel
import com.example.joopjoop.feature.setting.ui.SettingRoute
import com.example.joopjoop.ui.theme.JoopJoopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authRepository: AuthRepository = AuthRepositoryImpl(
            FirebaseAuthSource(),
            FirestoreUserSource()
        )

        // 1. Edge-to-Edge 설정 (상태바까지 화면 확장 - 선택 사항)
        // WindowCompat.setDecorFitsSystemWindows(window, false)

//        enableEdgeToEdge()

        setContent {
            JoopJoopTheme {
                // 1. 네비게이션의 핸들(Controller)을 만듭니다.
                val notificationViewModel: NotificationViewModel = viewModel()

                val navController = androidx.navigation.compose.rememberNavController()

                // 2. 어떤 화면들이 있는지 지도를 그립니다(NavHost).
                androidx.navigation.compose.NavHost(
                    navController = navController,
                    startDestination = "login" // 시작은 로그인 화면!
                ) {
                    // 로그인 화면 경로 설정
                    composable("login") {
                        LoginRoute(
                            onLoginSuccess = {
                                Log.d("MainActivity", "로그인 성공 -> 지도로 이동")
                                // navController.navigate("main_map") // 나중에 추가!
//                                navController.navigate("setting") // 테스트용으로 설정 화면으로 이동
                            },
                            onBackClick = { finish() },
                            onCreateAccountClick = {
                                Log.d("MainActivity", "회원가입으로 이동")
                                navController.navigate("signup") // 회원가입 화면으로 슝!
                            }
                        )
                    }

                    // 회원가입 화면 경로 설정
                    composable("signup") {
                        SignupRoute(
                            onBackClick = {
                                // 뒤로가기 버튼 누르면 로그인 화면으로 이동
                                navController.popBackStack()
                            },
                            onSignupSuccess = {
                                // 회원가입 성공 시 로그 찍고 로그인 화면으로 돌아가기
                                android.util.Log.d("MainActivity", "회원가입 성공! 이제 로그인 해주세요.")
                                navController.popBackStack()
                            }
                        )
                    }
                    // 설정 화면 경로 설정
                    composable("setting") {
                        SettingRoute(
                            authRepository = authRepository, // 미리 생성된 repository 주입
                            notificationViewModel = notificationViewModel, // 사용 중인 notificationViewModel 주입
                            onNavigateToLogin = {
                                navController.navigate("login") {
                                    popUpTo(0) // 전체 스택 비우고 로그인으로
                                }
                            },
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
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