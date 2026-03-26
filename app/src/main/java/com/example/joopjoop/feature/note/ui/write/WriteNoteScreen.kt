package com.example.joopjoop.feature.note.ui.write

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.joopjoop.R
import com.example.joopjoop.ui.theme.BgDark
import com.example.joopjoop.ui.theme.BgDarkest
import com.example.joopjoop.ui.theme.BgSurface
import com.example.joopjoop.ui.theme.JoopJoopTheme
import com.example.joopjoop.ui.theme.OrangePrimary
import com.example.joopjoop.ui.theme.TextPrimary
import com.example.joopjoop.ui.theme.TextTertiary

@Composable
fun WriteNoteScreen(
    navController: NavController,
    uiState: WriteNoteUiState, // 상태 추가
    modifier: Modifier = Modifier, // 유지
    onCategorySelected: (String) -> Unit = {},
    onContentChange: (String) -> Unit = {},
    onIncreaseHours: () -> Unit = {},
    onDecreaseHours: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onLeaveNoteClick: () -> Unit = {}
) {
    val categories = listOf(
        stringResource(R.string.category_daily),
        stringResource(R.string.category_emotion),
        stringResource(R.string.category_memory),
        stringResource(R.string.category_restaurant)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BgDarkest,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onBackClick() }
                        .padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = stringResource(R.string.write_note_title),
                    color = TextPrimary,
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

            // 1. 카테고리 선택 부분
            Text(
                text = stringResource(R.string.select_category),
                color = OrangePrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories) { category ->
                    CategorySelection(
                        text = category,
                        isSelected = uiState.selectedCategory == category,
                        onClick = { onCategorySelected(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. 쪽지 입력 부분 (Box 내부)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .background(BgDark, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                OutlinedTextField(
                    value = uiState.noteContent, // state 사용
                    onValueChange = onContentChange, // 콜백 사용
                    modifier = Modifier.fillMaxSize(),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.note_placeholder),
                            color = TextTertiary,
                            fontSize = 16.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = OrangePrimary
                    )
                )

                // 사진 추가 버튼 (기능은 나중에)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 8.dp)
                        .size(80.dp)
                        .border(1.dp, TextTertiary, RoundedCornerShape(16.dp))
                        .clickable { /* 사진 추가 로직 */ },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = stringResource(R.string.add_photo),
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. 보관 기간 조절 Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgDark, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(32.dp).background(OrangePrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_visibility),
                            contentDescription = null,
                            tint = BgDarkest,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = stringResource(R.string.storage_period), color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = stringResource(R.string.storage_period_desc), color = TextTertiary, fontSize = 12.sp)
                    }
                }

                // 시간 조절 버튼
                Row(
                    modifier = Modifier.background(BgDarkest, RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("-", color = TextTertiary, modifier = Modifier.clickable { onDecreaseHours() })
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("${uiState.storageHours}h", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("+", color = TextTertiary, modifier = Modifier.clickable { onIncreaseHours() })
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 4. 쪽지 남기기 버튼
            Button(
                onClick = onLeaveNoteClick, // 이제 인자 없이 깔끔하게!
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary, contentColor = TextPrimary)
            ) {
                Text(text = "> ${stringResource(R.string.leave_note_button)}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CategorySelection(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) OrangePrimary else BgSurface)
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) TextPrimary else TextTertiary,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WriteNoteScreenPreview() { // 더미데이터
    JoopJoopTheme {
        WriteNoteScreen(
            navController = rememberNavController(),
            uiState = WriteNoteUiState(
                selectedCategory = "감성",
                noteContent = "오늘 날씨가 너무 좋네요~!",
                storageHours = 12
            )
        )
    }
}
