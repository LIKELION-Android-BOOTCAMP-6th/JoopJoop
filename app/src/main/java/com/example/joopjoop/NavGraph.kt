package com.example.joopjoop

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.joopjoop.Routes.NOTE_EDIT
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
import com.example.joopjoop.feature.note.viewmodel.WriteNoteViewModel
import com.example.joopjoop.feature.notification.viewmodel.NotificationViewModel
import com.example.joopjoop.feature.setting.ui.SettingRoute
import com.example.joopjoop.ui.theme.BgDarkest
import kotlinx.coroutines.delay

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
    const val NOTE_EDIT = "write_note?noteId={noteId}"      // 쪽지 수정을 위한 화면 전환
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

    val notificationViewModel: NotificationViewModel = viewModel()

    // MainViewModel을 통해 로그인 상태 구독
    val mainViewModel: MainViewModel = viewModel(
        factory = appContainer.mainViewModelFactory
    )

    val mapViewModel: MapViewModel = viewModel(
        factory = appContainer.mapViewModelFactory
    )

    val isLoggedIn by mainViewModel.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == true) {
            // 자동 로그인 시에도 알림 작업이 등록되도록 호출
            notificationViewModel.startPeriodicNotification()

            delay(300) // 화면 전환 전 로딩 애니메이션이 자연스럽게 끝나도록 약간 지연
            rootNavController.navigate(Routes.MAIN) {
                popUpTo(Routes.AUTH) { inclusive = true }
            }
        }
    }

    Scaffold(
        containerColor = BgDarkest,
        contentWindowInsets = WindowInsets.systemBars
    ) { _ ->

        Box( // 번쩍임을 없애기 위해 검은 배경을 깔아둠
            modifier = Modifier
                .fillMaxSize()
                .background(BgDarkest)
        ) {
            // isLoggedIn == null → 로그인 상태 로딩 중 (IntroScreen에서 로딩 UI 처리)
//    if (isLoggedIn == null) return
            NavHost(
                navController = rootNavController,
                // 인증 그래프를 시작점으로 사용 (로그인 여부는 내부 상태로 처리)
                startDestination = Routes.AUTH
            ) {
                // 인증 관련 화면 그룹 (Intro, Login, Signup)
                // 로그인 완료 시 전체 스택에서 제거됨
                navigation(startDestination = Routes.INTRO, route = Routes.AUTH) {
                    composable(Routes.INTRO) {
                        // 인트로 화면 (필요 시 뷰모델 주입)
                        IntroScreen(
                            // 로그인 상태 로딩 중(null)일 때 로딩 UI 유지
                            isLoading = isLoggedIn == null,
                            // 로그인 성공 여부 (Root에서 화면 전환 트리거로 사용)
                            isLoginSuccess = isLoggedIn == true,
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
                                notificationViewModel = notificationViewModel,
                                imageProcessor = appContainer.imageProcessor
                            )
                        )

                        LoginRoute(
                            viewModel = loginViewModel,
                            notificationViewModel = notificationViewModel,  // 권한 요청 및 알림 시작 로직을 위해 직접 전달
                            onLoginSuccess = {
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
                    MainScreen(
                        rootNavController = rootNavController,
                        mapViewModel = mapViewModel
                    )
                }

                // 3. 서브 그래프 (Sub Graph / 상세 화면)
                // 바텀바가 보이지 않아야 하는 독립적인 상세 페이지

                composable(Routes.NOTE_LIST) {
                    NoteListScreen(
                        navController = rootNavController,
                        viewModel = mapViewModel
                    )
                }

                composable(Routes.WRITE) {
                    val context = LocalContext.current

                    // 1. 권한 상태를 '상태(State)'로 관리 (매우 중요!)
                    var hasPermission by remember {
                        val locationPermissions = arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                        mutableStateOf(
                            locationPermissions.all {
                                androidx.core.content.ContextCompat.checkSelfPermission(
                                    context,
                                    it
                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                            }
                        )
                    }

                    // 2. 권한 요청 런처
                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestMultiplePermissions()
                    ) { permissions ->
                        // 권한 허용 여부에 따라 '상태' 업데이트 -> UI가 자동으로 다시 그려짐
                        hasPermission = permissions.values.all { it }
                        if (!hasPermission) {
                            rootNavController.popBackStack()
                        }
                    }

                    // 3. 조건부 화면 렌더링
                    if (hasPermission) {
                        // [권한 있음] 이제 이 블록이 실행됩니다!
                        val viewModel: WriteNoteViewModel = viewModel(
                            factory = appContainer.noteViewModelFactory
                        )

                        LaunchedEffect(Unit) {
                            viewModel.prepareNewNote()
                        }

                        WriteNoteScreen(
                            navController = rootNavController,
                            viewModel = viewModel
                        )
                    } else {
                        // [권한 없음] 처음에 여기 들어왔다가, 허용하는 순간 위쪽 if문으로 갈아탑니다.
                        LaunchedEffect(Unit) {
                            launcher.launch(
                                arrayOf(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(BgDarkest)
                        )
                    }
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

                // 설정 화면 주소 등록
                composable(Routes.SETTINGS) {
                    // SettingRoute를 호출하여 의존성(Repository, ViewModel)을 주입합니다.
                    SettingRoute(
                        authRepository = appContainer.authRepository,
                        notificationViewModel = viewModel(), // 필요 시 appContainer에서 가져올 수도 있음.
                        onNavigateToLogin = {
                            // 로그아웃 성공 시 AUTH 화면으로 이동하며 스택 정리
                            rootNavController.navigate(Routes.AUTH) {
                                popUpTo(Routes.MAIN) { inclusive = true }
                            }
                        },
                        onBackClick = {
                            rootNavController.popBackStack()
                        }
                    )
                }

                // 쪽지 내용 수정
                composable(
                    route = NOTE_EDIT,
                    arguments = listOf(navArgument("noteId") { nullable = true })
                ) { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getString("noteId")
                    val viewModel: WriteNoteViewModel = viewModel(
                        factory = appContainer.noteViewModelFactory
                    )

                    LaunchedEffect(noteId) {
                        viewModel.loadNoteForEdit(noteId)
                    }

                    WriteNoteScreen(
                        navController = rootNavController,
                        viewModel = viewModel
                    )
                }

                // 설정 화면 주소 등록
                composable(Routes.SETTINGS) {
                    // SettingRoute를 호출하여 의존성(Repository, ViewModel)을 주입합니다.
                    SettingRoute(
                        authRepository = appContainer.authRepository,
                        notificationViewModel = viewModel(), // 필요 시 appContainer에서 가져올 수도 있음.
                        onNavigateToLogin = {
                            // 로그아웃 성공 시 AUTH 화면으로 이동하며 스택 정리
                            rootNavController.navigate(Routes.AUTH) {
                                popUpTo(Routes.MAIN) { inclusive = true }
                            }
                        },
                        onBackClick = {
                            rootNavController.popBackStack()
                        }
                    )
                }
            }
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
    mapViewModel: MapViewModel,
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
            // 실제 제작한 MapScreen으로 교체
            MapScreen(
                viewModel = mapViewModel,
                onNavigateToNoteList = {
                    // 쪽지 리스트 화면
                    rootNavController.navigate(Routes.NOTE_LIST)
                },
                onNavigateToNoteDetail = { noteId ->
                    // 쪽지 상세 화면으로 이동 (Routes.NOTE_DETAIL 형태에 맞춰 argument 전달)
                    rootNavController.navigate("noteDetail/$noteId")
                }
            )
        }

        // [수정] 마이페이지 경로에 실제 뷰모델과 화면을 연결
        composable(Routes.MYPAGE) {
            val myPageViewModel: MyPageViewModel = viewModel(
                factory = appContainer.myPageViewModelFactory
            )

            // 실제 마이페이지 화면으로 교체
            MyPageScreen(
                viewModel = myPageViewModel,
                // 설정화면 진입 버튼 연결
                onSettingClick = {
                    rootNavController.navigate(Routes.SETTINGS)
                },
                // [F-MY-02] 내가 쓴 쪽지 리스트 부품 주입
                postContent = {
                    MyPostListContent(
                        viewModel = myPageViewModel,
                        onNoteClick = { noteId ->
                            // 상세 화면은 BottomNav가 없는 RootNavHost 영역이므로 rootNavController 사용
                            rootNavController.navigate("noteDetail/$noteId")
                        }
                    )
                },
                scrapContent = {
                    MyScrapListContent(
                        viewModel = myPageViewModel,
                        onNoteClick = { noteId ->
                            rootNavController.navigate("noteDetail/$noteId")
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