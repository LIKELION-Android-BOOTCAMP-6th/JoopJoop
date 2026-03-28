package com.example.joopjoop

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.joopjoop.core.designsystem.components.BottomNavigation

@Composable
fun MainScreen(rootNavController: NavController) {
    // Main 내부에서만 사용할 네비게이션 컨트롤러
    val mainNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            // 네비게이션 바
            BottomNavigation(mainNavController, rootNavController)
        }
    ) { innerPadding ->
        // Scaffold가 계산해준 여백(innerPadding)을
        // MainNavHost에 전달
        MainNavHost(
            mainNavController = mainNavController,
            rootNavController = rootNavController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}