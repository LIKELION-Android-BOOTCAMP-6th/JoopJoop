package com.example.joopjoop

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.joopjoop.core.designsystem.components.BottomNavigation
import com.example.joopjoop.feature.map.viewmodel.MapViewModel
import com.example.joopjoop.ui.theme.BgDarkest

@Composable
fun MainScreen(rootNavController: NavController, mapViewModel: MapViewModel) {
    // Main 내부에서만 사용할 네비게이션 컨트롤러
    val mainNavController = rememberNavController()

    Scaffold(
        containerColor = BgDarkest, // 번쩍임을 없애기 위해 검은 배경으로 변경
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            BottomNavigation(mainNavController, rootNavController)
        }
    ) { innerPadding ->
        // Scaffold가 계산해준 여백(innerPadding)을
        // MainNavHost에 전달
        MainNavHost(
            mainNavController = mainNavController,
            rootNavController = rootNavController,
            mapViewModel = mapViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}