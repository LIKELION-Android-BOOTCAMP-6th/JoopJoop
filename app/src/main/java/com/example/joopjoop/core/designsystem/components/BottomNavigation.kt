package com.example.joopjoop.core.designsystem.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.joopjoop.Routes
import com.example.joopjoop.ui.theme.*

@Composable
fun BottomNavigation(
    mainNavController: NavController, // 지도/마이페이지 전환용
    rootNavController: NavController // 작성 화면 점프용
) {
    // 현재 네비게이션의 '백스택(화면 쌓임 기록)' 상태를 관찰 가능한 State로 가져옴
    // 화면이 바뀔 때마다 이 변수가 Recomposition 되어 UI를 최신 상태로 유지
    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()

    // 백스택의 최상단(현재 화면)에 있는 대상(destination)의 '경로(route)' 문자열을 추출
    // 예: 현재 지도를 보고 있다면 "map", 마이페이지라면 "mypage"가 할당
    val currentRoute = navBackStackEntry?.destination?.route

    // 중앙 버튼이 튀어나오게
    // 전체를 Box로 감싸고 하단 바와 중앙 버튼을 겹치게 배치
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // NavigationBar (딱 바 높이만 차지)
        CompositionLocalProvider(
            LocalAbsoluteTonalElevation provides 0.dp
        ) {
            // 1. 실제 바텀 네비게이션 바 (지도, 마이페이지)
            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp,
                contentColor = TextSecondary,
                modifier = Modifier.height(80.dp) // 하단 바 자체 높이
            ) {
                // 왼쪽: 지도 탭
                NavigationBarItem(
                    icon = { Icon(Icons.Default.LocationOn, contentDescription = "지도") },
                    label = { Text("MAP") },
                    // 현재 경로(currentRoute)가 이 아이템의 경로와 일치하면 '선택됨' 상태로 표시
                    selected = currentRoute == Routes.MAP,
                    onClick = {
                        mainNavController.navigate(Routes.MAP) {
                            // 스택 관리: 앱의 시작 지점(Start Destination) 위로 쌓인 모든 화면을 비움
                            // 이렇게 하면 어떤 탭에서든 뒤로가기 시 앱이 종료되거나 홈(지도)으로 이동
                            popUpTo(mainNavController.graph.findStartDestination().id) {
                                // 이전 탭에서 입력하던 내용이나 스크롤 위치 등을 상태로 저장
                                saveState = true
                            }
                            // 중복 방지: 동일한 화면이 이미 스택 최상단에 있다면 새로 생성하지 않는다
                            launchSingleTop = true
                            // 상태 복구: 이전에 저장했던 해당 탭의 상태(saveState)를 다시 불러옵니다.
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OrangePrimary,
                        selectedTextColor = OrangePrimary,
                        indicatorColor = BgElevated,
                        unselectedIconColor = TextTertiary,
                        unselectedTextColor = TextTertiary
                    )
                )

                // 중앙: 쪽지 쓰기 버튼을 위한 빈 공간을 확보
                Spacer(modifier = Modifier.weight(1f))

                // 오른쪽: 마이페이지 탭
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "마이페이지") },
                    label = { Text("MY PAGE") },
                    selected = currentRoute == Routes.MYPAGE,
                    onClick = {
                        mainNavController.navigate(Routes.MYPAGE) {
                            popUpTo(mainNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OrangePrimary,
                        selectedTextColor = OrangePrimary,
                        indicatorColor = BgElevated,
                        unselectedIconColor = TextTertiary,
                        unselectedTextColor = TextTertiary
                    )
                )
            }

        }

        // 중앙에 튀어나온 'WRITE' 버튼
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-20).dp), // ← 여기로 띄움 정도 조절
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 주황색 원형 버튼
            FloatingActionButton(
                onClick = {
                    // 'WRITE' 버튼은 rootNavController을 사용해
                    // 바텀바가 없는 전체 화면으로 이동
                    rootNavController.navigate(Routes.WRITE)
                },
                shape = CircleShape,
                containerColor = OrangePrimary,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
            ) {
                Icon(Icons.Default.Create, contentDescription = "작성")
            }

            // 버튼 아래 'WRITE' 텍스트 (UI 예시 반영)
            Text(
                text = "WRITE",
                fontSize = 12.sp,
                color = TextPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}