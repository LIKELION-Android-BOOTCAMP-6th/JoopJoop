package com.example.joopjoop.feature.note.ui.list

import android.R.attr.text
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.joopjoop.R
import com.example.joopjoop.feature.note.viewmodel.NoteListViewModel
import com.example.joopjoop.ui.theme.BgDark
import com.example.joopjoop.ui.theme.BgDarkest
import com.example.joopjoop.ui.theme.JoopJoopTheme
import com.example.joopjoop.ui.theme.OrangePrimary
import com.example.joopjoop.ui.theme.TextPrimary
import com.example.joopjoop.ui.theme.TextTertiary

@Composable
fun NoteListScreen(navController: NavController){

    val viewModel: NoteListViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    JoopJoopTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = BgDarkest,
            topBar = { ListTopBar(navController) },
            bottomBar = { ListBottomBar(navController) }
        ) { innerPadding ->
            NoteList(
                uiState = uiState,
                modifier = Modifier.padding(innerPadding),
                navController = navController
            )
        }
    }
}

@Composable
fun NoteList(uiState: NoteListUiState, modifier: Modifier = Modifier, navController: NavController,) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDarkest)
    ) {
        // 로딩 화면 표시
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = OrangePrimary)
            }
        } else if(uiState.notes.isEmpty()){
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_mail_24),
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "주변에 쪽지가 없어요",
                        color = TextTertiary,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
    // 쪽지 리스트
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 한 줄 두개씩
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(uiState.notes) { note ->
                    NoteCard(
                        item = note,
                        onClick = { navController.navigate("noteDetail") }
                    ) // 카드 형태로...
                }
            }}}}

@Composable
fun ListTopBar(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgDarkest)
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    )
    {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            // 뒤로가기 아이콘
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { navController.popBackStack() }
            )
            // 제목
            Text(
                text = "주변 쪽지 목록",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 개별 쪽지 형태
@Composable
fun NoteCard(item: NoteItem, onClick: () -> Unit = {}){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .clickable { onClick() }
        ) {
            //쪽지 아이콘 배경 영역
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(BgDark),
                contentAlignment = Alignment.Center
            ) {
                // 쪽지 아이콘
                Icon(
                    painter = painterResource(id = R.drawable.baseline_mail_24),
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            // 쪽지 본문 일부 노출(한줄만, 넘어가면 ... 처리)
            Text(
                text = item.content,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 거리 정보 표시 영역(아이콘 + 텍스트)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                //나침반 화살표 아이콘(45도 회전, 수평 맞추기 위해 윗쪽으로 미세 조정)
                Icon(
                    painter = painterResource(id = R.drawable.baseline_navigation_24),
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier
                        .size(16.dp)
                        .rotate(45f) // 회전
                        .offset(y = (-2).dp) // 미세조정
                )
                Spacer(modifier = Modifier.width(4.dp))

                //거리 텍스트
                Text(
                    text = item.distance,
                    color = TextTertiary, // 컬러
                    fontSize = 12.sp
                )
            }
        }
    }

// 화면 하단 네비게이션 바
@Composable
fun ListBottomBar(navController: NavController) {

    var selectedTab by remember { mutableStateOf("MAP") }

    val selectedColor = OrangePrimary
    val unselectedColor = TextTertiary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgDarkest)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // MAP 탭
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedTab = "MAP" },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_map_24),
                    contentDescription = null,
                    tint = if (selectedTab == "MAP") selectedColor else unselectedColor,
                )
                Text(
                    text = "MAP",
                    color = if (selectedTab == "MAP") selectedColor else unselectedColor,
                    fontSize = 8.sp,
                    textAlign = TextAlign.Center
                )
            }

                // WRITE 탭
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = "WRITE"
                            navController.navigate("writeNote")},
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_edit_square_24),
                        contentDescription = null,
                        tint = if (selectedTab == "WRITE") selectedColor else unselectedColor,

                        )
                    Text(
                        text = "WRITE",
                        color = if (selectedTab == "WRITE") selectedColor else unselectedColor,
                        fontSize = 8.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // MY PAGE 탭
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable{ selectedTab = "MY PAGE"},
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_person_24),
                        contentDescription = null,
                        tint = if (selectedTab == "MY PAGE") selectedColor else unselectedColor,
                    )
                    Text(
                        text = "MY PAGE",
                        color = if (selectedTab == "MY PAGE") selectedColor else unselectedColor,
                        fontSize = 8.sp,
                        textAlign = TextAlign.Center
                    )
                }

            }
        }
    }

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    JoopJoopTheme {
        NoteListScreen(navController = rememberNavController())
    }
}

