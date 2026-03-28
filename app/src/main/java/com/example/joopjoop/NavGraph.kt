package com.example.joopjoop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.joopjoop.feature.auth.ui.intro.IntroScreen
import com.example.joopjoop.feature.auth.ui.login.LoginRoute
import com.example.joopjoop.feature.auth.ui.signup.SignupRoute
import com.example.joopjoop.feature.auth.viewmodel.AuthViewModelFactory
import com.example.joopjoop.feature.auth.viewmodel.LoginViewModel
import com.example.joopjoop.feature.auth.viewmodel.SignupViewModel
import com.example.joopjoop.feature.auth.viewmodel.SignupViewModelFactory
import com.example.joopjoop.feature.map.ui.MapScreen
import com.example.joopjoop.feature.map.viewmodel.MapViewModel
import com.example.joopjoop.feature.mypage.ui.main.MyPageScreen
import com.example.joopjoop.feature.mypage.ui.post.MyPostListContent
import com.example.joopjoop.feature.mypage.ui.scrap.MyScrapListContent
import com.example.joopjoop.feature.mypage.viewmodel.MyPageViewModel
import com.example.joopjoop.feature.note.ui.detail.NoteDetailScreen
import com.example.joopjoop.feature.note.ui.list.NoteListScreen
import com.example.joopjoop.feature.note.ui.write.WriteNoteScreen
import com.example.joopjoop.feature.note.viewmodel.NoteDetailViewModel
import com.example.joopjoop.feature.note.viewmodel.NoteListViewModel
import com.example.joopjoop.feature.note.viewmodel.WriteNoteViewModel
import com.example.joopjoop.feature.notification.viewmodel.NotificationViewModel

// Routes 정의
object Routes {
    // 1. 인증 그래프 (Auth Graph)
    const val AUTH = "auth"       // 인증 관련 화면들의 묶음
    const val INTRO = "intro"     // 앱 진입 시 첫 소개 화면
    const val LOGIN = "login"     // 로그인 입력 화면
    const val SIGNUP = "signup"   // 회원가입 입력 화면

    // 2. 메인 그래프 (Main Graph - BottomNav 포함 영역)
    const val MAIN = "main"       // 하단 탭바가 존재하는 메인 컨테이너
    const val MAP = "map"         // [메인 탭 1] 지도 탐색 화면
    const val MYPAGE = "mypage"   // [메인 탭 2] 내 활동 및 프로필 화면

    // 3. 서브/상세 화면 (Sub Graph - BottomNav 없음)
    const val WRITE = "write"           // 새로운 쪽지 작성 화면
    const val NOTE_LIST = "noteList"    // 주변 쪽지들을 리스트로 보는 화면
    const val NOTE_DETAIL = "noteDetail/{noteId}" // 특정 쪽지의 상세 내용을 보는 화면
    const val SETTINGS = "settings"      // 알림 설정, 계정 관리 등 설정 화면
}

/**
 * 앱의 최상위 네비게이션 컨트롤러
 * * 역할: Auth(인증), Main(탭), Sub(상세) 사이의 거대한 흐름을 제어
 * * 새로운 상세 화면이 추가되면 여기에 composable을 등록
 */
@Composable
fun RootNavHost() {
    val rootNavController = rememberNavController()

    // 공통 재료 가져오기
    val context = LocalContext.current
    val appContainer = (context.applicationContext as JoopJoopApplication).container

// 로그인 여부에 따라 화면분기를 위해서는 아래의 authRepository.currentUserUid 필요

//    // 리포지토리를 통해 현재 로그인 상태 확인
//    val isLoggedIn = appContainer.authRepository.currentUserUid != null
//
//    // 로그인 상태면 메인(지도), 아니면 인증(인트로) 화면으로!
//    val startDest = if (isLoggedIn) Routes.MAIN else Routes.AUTH

    NavHost(
        navController = rootNavController,
        // [개발 단계 전용] 구글 로그인 연동 전까지는 바로 메인으로 진입.
        startDestination = Routes.AUTH
    ) {
        // 1. 인증 그래프 (Auth Graph)
        // Intro, Login, Signup 등을 포함하며 로그인 완료 시 스택에서 제거됩니다.
        navigation(startDestination = Routes.INTRO, route = Routes.AUTH) {
            composable(Routes.INTRO) {
                // 인트로 화면 (필요 시 뷰모델 주입)
                IntroScreen(
                    onLoginClick = { rootNavController.navigate(Routes.LOGIN) },
                    onSignupClick = { rootNavController.navigate(Routes.SIGNUP) }
                )
            }

            composable(Routes.LOGIN) {
                val notificationViewModel: NotificationViewModel = viewModel()

                val loginViewModel: LoginViewModel = viewModel(
                    // notification 기능을 추가하면서 LoginViewModel 생성 시 NotificationViewModel 사용이 필요한 로직이라서 주석처리함
//                    factory = appContainer.authViewModelFactory
                    factory = AuthViewModelFactory(
                        authRepository = appContainer.authRepository,
                        notificationViewModel = notificationViewModel
                    )
                )

                LoginRoute(
                    viewModel = loginViewModel,
                    notificationViewModel = notificationViewModel,  // 권한 요청 및 알림 시작 로직을 위해 직접 전달
                    onLoginSuccess = {
                        rootNavController.navigate(Routes.MAIN) {
                            popUpTo(Routes.AUTH) { inclusive = true }
                        }
                    },
                    onBackClick = { rootNavController.popBackStack() },
                    onCreateAccountClick = { rootNavController.navigate(Routes.SIGNUP) }
                )
            }

            composable(Routes.SIGNUP) {
                // 마찬가지로 상단의 appContainer를 사용합니다.
                val signupViewModel: SignupViewModel = viewModel(
                    factory = SignupViewModelFactory(
                        authRepository = appContainer.authRepository
                    )
                )

                SignupRoute(
                    viewModel = signupViewModel,
                    onSignupSuccess = {
                        rootNavController.popBackStack()
                    },
                    onBackClick = {
                        rootNavController.popBackStack()
                    }
                )
            }
        }

        // 2. 메인 그래프 (Main Graph)
        // 바텀 네비게이션이 존재하는 '그릇' 화면.
        composable(Routes.MAIN) {
            MainScreen(rootNavController = rootNavController)
        }

        // 3. 서브 그래프 (Sub Graph / 상세 화면)
        // 바텀바가 보이지 않아야 하는 독립적인 상세 페이지

        composable(Routes.NOTE_LIST) {
            val viewModel: NoteListViewModel = viewModel(
                factory = appContainer.noteViewModelFactory
            )
            NoteListScreen(
                navController = rootNavController,
                viewModel = viewModel
            )
        }

        composable(Routes.WRITE) {
            // AppContainer 내부에서 이미 FusedLocationClient를 주입한 팩토리를 가져옵니다.
            val viewModel: WriteNoteViewModel = viewModel(
                factory = appContainer.noteViewModelFactory
            )

            WriteNoteScreen(
                navController = rootNavController,
                viewModel = viewModel
            )
        }

        composable(
            route = Routes.NOTE_DETAIL,
            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            val viewModel: NoteDetailViewModel = viewModel(
                factory = appContainer.noteViewModelFactory
            )

            // 화면이 처음 뜰 때 데이터를 로드하도록 설정
            LaunchedEffect(noteId) {
                viewModel.loadNoteDetail(noteId)
            }

            NoteDetailScreen(
                navController = rootNavController,
                noteId = noteId,
                viewModel = viewModel
            )
        }

        // [추가] 설정 화면 주소 등록
        composable(Routes.SETTINGS) {
            PlaceholderScreen("설정 화면")
        }
    }
}

/**
 * 메인 내부 네비게이션 (MainNavHost)
 * MainScreen(Scaffold)의 안쪽 영역에서 MAP/MYPAGE만 교체하는 역할
 */
@Composable
fun MainNavHost(
    mainNavController: NavHostController,
    rootNavController: NavController,
    modifier: Modifier = Modifier
) {
    // Context와 AppContainer를 미리 가져옵니다.
    val context = LocalContext.current
    val appContainer = (context.applicationContext as JoopJoopApplication).container

    NavHost(
        navController = mainNavController,
        startDestination = Routes.MAP,
        modifier = modifier
    ) {
        composable(Routes.MAP) {
            // 팩토리를 사용하여 MapViewModel 생성
            val mapViewModel: MapViewModel = viewModel(
                factory = appContainer.mapViewModelFactory
            )

            // 실제 제작한 MapScreen으로 교체
            MapScreen(viewModel = mapViewModel)
        }

        // [수정] 마이페이지 경로에 실제 뷰모델과 화면을 연결
        composable(Routes.MYPAGE) {
            val myPageViewModel: MyPageViewModel = viewModel(
                factory = appContainer.myPageViewModelFactory
            )

            // 실제 마이페이지 화면으로 교체
            MyPageScreen(
                viewModel = myPageViewModel,
                // [F-MY-02] 내가 쓴 쪽지 리스트 부품 주입
                postContent = {
                    MyPostListContent(
                        viewModel = myPageViewModel,
                        onNoteClick = { noteId ->
                            // 상세 화면은 BottomNav가 없는 RootNavHost 영역이므로 rootNavController 사용
                            rootNavController.navigate("note_detail/$noteId")
                        }
                    )
                },
                scrapContent = {
                    MyScrapListContent(
                        viewModel = myPageViewModel,
                        onNoteClick = { noteId ->
                            rootNavController.navigate("note_detail/$noteId")
                        }
                    )
                }
            )
        }
    }
}

// 테스트용 임시화면
@Composable
fun PlaceholderScreen(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            color = Color.Gray
        )
    }
}