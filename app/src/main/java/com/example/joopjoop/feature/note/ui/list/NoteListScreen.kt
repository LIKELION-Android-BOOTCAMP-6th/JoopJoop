package com.example.joopjoop.feature.note.ui.list

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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.joopjoop.R
import com.example.joopjoop.ui.theme.JoopJoopTheme


@Composable
fun NoteList(items: List<String>, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color(0xff1a1208))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                contentDescription = null,
                tint = Color(0xffffffff),
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                text = "주변 쪽지 목록",
                color = Color(0xffffffff),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }


        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(items) {
                NoteCard()
            }
        }
    }
}

@Composable
fun NoteCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xff251A0E)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_mail_24),
                contentDescription = null,
                tint = Color(0xffE07B2A),
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "여기에 맛있는 빵집이...", // 테스트용 쪽지 내용
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_navigation_24),
                contentDescription = null,
                tint = Color(0xFFE67E22),
                modifier = Modifier
                    .size(16.dp)
                    .rotate(45f)
                    .offset(y = (-2).dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "100m", // 테스트용 거리
                color = Color.Gray, // 컬러
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun MyBottomBar() {

    var selectedTab by remember { mutableStateOf("MAP") }
    val selectedColor = Color(0xffE07B2A)
    val unselectedColor = Color(0xff7a6552)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xff1a1208))
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
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

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedTab = "WRITE" },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_edit_square_24),
                    contentDescription = null,
                    tint = if (selectedTab == "WRITE") selectedColor else unselectedColor
                )
                Text(
                    text = "WRITE",
                    color = if (selectedTab == "WRITE") selectedColor else unselectedColor,
                    fontSize = 8.sp,
                    textAlign = TextAlign.Center
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedTab = "MY PAGE" },
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JoopJoopTheme {
        NoteList(items = List(1) { "Item $it" })
    }
}

