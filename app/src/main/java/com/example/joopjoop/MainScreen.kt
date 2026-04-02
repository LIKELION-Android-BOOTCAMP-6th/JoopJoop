package com.example.joopjoop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
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
        containerColor = BgDarkest,
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            Box(
                modifier = Modifier.navigationBarsPadding()
            ) {
                BottomNavigation(mainNavController, rootNavController)
            }
        }
    ) { innerPadding ->
        MainNavHost(
            mainNavController = mainNavController,
            rootNavController = rootNavController,
            mapViewModel = mapViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}