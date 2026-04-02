package com.example.joopjoop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark( // 배경이 어두우므로 'dark' 스타일 적용 (글씨는 밝게 나옴)
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT
            )
        )

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
}