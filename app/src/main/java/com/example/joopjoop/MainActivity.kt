package com.example.joopjoop

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.composable
import com.example.joopjoop.feature.auth.ui.login.LoginRoute
import com.example.joopjoop.feature.auth.ui.signup.SignupRoute
import com.example.joopjoop.feature.auth.ui.signup.SignupScreen
import com.example.joopjoop.ui.theme.JoopJoopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JoopJoopTheme {
                // 1. 네비게이션의 핸들(Controller)을 만듭니다.
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
                }
            }
        }
    }
}