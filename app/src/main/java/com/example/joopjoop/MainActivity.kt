package com.example.joopjoop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.joopjoop.ui.theme.JoopJoopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Edge-to-Edge 설정 (상태바까지 화면 확장 - 선택 사항)
        // WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            // 2. 팀원들과 약속한 테마 적용
            JoopJoopTheme {
                // 3. 앱 전체의 배경색과 영역 설정
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 4. 우리가 만든 설계도(RootNavHost) 실행
                    RootNavHost()
                }
            }
        }
    }
}