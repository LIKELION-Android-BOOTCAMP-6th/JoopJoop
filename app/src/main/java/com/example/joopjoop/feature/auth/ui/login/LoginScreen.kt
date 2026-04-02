package com.example.joopjoop.feature.auth.ui.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.joopjoop.R
import com.example.joopjoop.feature.auth.viewmodel.LoginViewModel
import com.example.joopjoop.feature.notification.viewmodel.NotificationViewModel
import com.example.joopjoop.ui.theme.JoopJoopTheme

@Composable
fun LoginRoute(
    viewModel: LoginViewModel, // NavGraph에서 주입받은 뷰모델
    notificationViewModel: NotificationViewModel,
    onLoginSuccess: () -> Unit,
    onBackClick: () -> Unit,
    onCreateAccountClick: () -> Unit
) {
    // 아래 로직 NavGraph에서 생성하도록 수정함 추후에 코드 참고할 필요 없으면 삭제
    // AppContainer에서 Repository 가져오기
//    val context = androidx.compose.ui.platform.LocalContext.current
//    val appContainer =
//        (context.applicationContext as com.example.joopjoop.JoopJoopApplication).container
//
//    // 팩토리를 사용해서 ViewModel 생성
//    val notificationViewModel: NotificationViewModel =
//        androidx.lifecycle.viewmodel.compose.viewModel()
//    val viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
//        factory = com.example.joopjoop.feature.auth.viewmodel.AuthViewModelFactory(
//            appContainer.authRepository, notificationViewModel = notificationViewModel
//        )
//    )

    // 매개변수에 viewModel추가해서 NavGraph에서 생성하도록 수정했음 - 원화
    // 그러므로 아래는 주석처리 해둠
    // AppContainer에서 Repository 가져오기
//    val context = androidx.compose.ui.platform.LocalContext.current
//    val appContainer = (context.applicationContext as com.example.joopjoop.JoopJoopApplication).container
//    // 팩토리를 사용해서 ViewModel 생성
//    val viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
//        factory = com.example.joopjoop.feature.auth.viewmodel.AuthViewModelFactory(appContainer.authRepository)
//    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 권한 요청용 런처
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // 권한 허용 시 알림 시작 (Worker 등록)
            notificationViewModel.startPeriodicNotification()
        }
        // 권한이 거부되어도 로그인은 진행시켜야 하므로 여기서 onLoginSuccess()를 호출하지는 않음
        onLoginSuccess()
    }

    // 로그인 성공 감지
    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            // Android 13 (API 33) 이상인 경우에만 런타임 권한 요청
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // 13 미만은 이미 Manifest 선언으로 권한이 있으므로 바로 시작 후 이동
                notificationViewModel.startPeriodicNotification()
                onLoginSuccess()
            }
        }
    }

    // 로그인 실패(에러 메시지) 감지
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            // 토스트 띄우기
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

            // 에러 메시지 초기화 (ViewModel에 함수 생성 필요)
            // 이걸 안 하면 화면 회전 시 토스트가 또 뜸
            viewModel.clearErrorMessage()
        }
    }
    // POST_NOTIFICATIONS는 **Android 13(API 33) 이상부터 위험 권한으로 분류되어 확인 필요

    LoginScreen(
        uiState = uiState,
        onEmailInput = viewModel::onEmailInput,
        onPasswordInput = viewModel::onPasswordInput,
        onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
        onBackClick = onBackClick,
        onSignInClick = viewModel::login,
        onCreateAccountClick = onCreateAccountClick
    )
}

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    modifier: Modifier = Modifier,
    onEmailInput: (String) -> Unit = {},
    onPasswordInput: (String) -> Unit = {},
    onTogglePasswordVisibility: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onSignInClick: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                        .padding(8.dp, 0.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = stringResource(R.string.login_title),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 로그인 배경 카드
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.tertiary,
                                    MaterialTheme.colorScheme.primary
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.welcome_back),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.sign_in_message),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 이메일 입력
            Text(
                text = stringResource(R.string.email_address),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailInput,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.email_placeholder)) },
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.ic_email),
                        null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 비밀번호 입력
            Text(
                text = stringResource(R.string.password),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordInput,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("••••••••") },
                leadingIcon = {
                    Icon(painterResource(R.drawable.ic_lock), null, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            painter = painterResource(
                                id = if (uiState.isPasswordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                            ),
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 로그인 버튼
            Button(
                onClick = onSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !uiState.isLoading // 로딩 중일 때 버튼 비활성화
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        stringResource(R.string.sign_in),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 계정 만들기 링크
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                        append(stringResource(R.string.dont_have_account))
                    }
                    append(" ")
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(stringResource(R.string.create_account))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .clickable { onCreateAccountClick() },
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    JoopJoopTheme {
        LoginScreen(uiState = LoginUiState()) // 기본 state 전달
    }
}